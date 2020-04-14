package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.WeightedFont;
import ie.gmit.sw.ai.cloud.TermWeight;
import ie.gmit.sw.comparator.FuzzyComparator;
import ie.gmit.sw.comparator.DFSComparator;
import ie.gmit.sw.comparator.PageNodeEvaluator;
import ie.gmit.sw.comparator.RandomComparator;
import ie.gmit.sw.crawler.CrawlStats;
import ie.gmit.sw.crawler.QueryCrawler;
import ie.gmit.sw.crawler.SearchEngineScraper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryCloudGenerator {
    private String query;
    private int maxPageLoads;
    private int numCrawlers;
    private int numCloudWords;
    private PageNodeEvaluator pageNodeEvaluator;
    private CrawlStats crawlStats;

    private DomainFrequency domainFrequency;

    public QueryCloudGenerator(String query, int maxPageLoads, int numThreads, int numCloudWords, SearchAlgorithm searchAlgorithm) {
        this.query = query.toLowerCase();
        this.maxPageLoads = maxPageLoads;
        this.numCrawlers = numThreads;
        this.numCloudWords = numCloudWords;

        domainFrequency = new DomainFrequency();

        switch (searchAlgorithm) {
            case BFS_FUZZY_HEURISTIC:
                pageNodeEvaluator = new FuzzyComparator(this.query, domainFrequency);
                break;
            case DFS_RELEVANCE_HEURISTIC:
                pageNodeEvaluator = new DFSComparator(this.query);
                break;
            case RANDOM_RELEVANCE_HEURISTIC:
                pageNodeEvaluator = new RandomComparator(this.query);
                break;
        }
    }

    public BufferedImage generateWordCloud() throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        AtomicInteger pageLoadsLeft = new AtomicInteger(maxPageLoads);
        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        PriorityBlockingQueue<PageNode> queue = new PriorityBlockingQueue<>(100, pageNodeEvaluator);

        Set<String> visited = ConcurrentHashMap.newKeySet();
        List<PageNode> resultPages =
                Arrays.stream(new SearchEngineScraper().getResultLinks(query, 10))
                        .map(PageNode::new)
                        .collect(Collectors.toList());
        queue.addAll(resultPages);

        // create crawlers and submit to executor
        ExecutorService executor = Executors.newFixedThreadPool(numCrawlers);
        TfpdfCalculator tfpdfCalculator = new TfpdfCalculator();
        crawlStats = new CrawlStats(domainFrequency);
        for (int i = 0; i < numCrawlers; i++) {
            executor.submit(
                    new QueryCrawler(query, queue, ignorer, crawlStats, visited, pageNodeEvaluator, tfpdfCalculator, pageLoadsLeft)
            );
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TermWeight[] words = new WeightedFont(numCloudWords).getFontSizes(
                new MapToWeightingArray(tfpdfCalculator.getWeights()).convert(numCloudWords));

        System.out.println("\n-- Word frequencies --");
        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %.3f%n", i, words[i].getTerm(), words[i].getWeight());
        }
        System.out.println();

        System.out.println(crawlStats.toString());

        return new WordCloudGenerator(words, 850, 850).generateWordCloud();
    }

    public CrawlStats getCrawlStats() {
        return crawlStats;
    }
}
