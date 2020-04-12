package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.WeightedFont;
import ie.gmit.sw.ai.cloud.TermWeight;
import ie.gmit.sw.comparator.FuzzyComparator;
import ie.gmit.sw.comparator.LIFOComparator;
import ie.gmit.sw.comparator.PageNodeEvaluator;
import ie.gmit.sw.comparator.RandomComparator;
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
    private PageNodeEvaluator pageNodeEvaluator;

    private DomainFrequency domainFrequency;

    public QueryCloudGenerator(String query, int maxPageLoads, int numThreads, SearchAlgorithm searchAlgorithm) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;
        this.numCrawlers = numThreads;

        domainFrequency = new DomainFrequency();

        switch (searchAlgorithm) {
            case BFS_FUZZY_HEURISTIC:
                pageNodeEvaluator = new FuzzyComparator(query, domainFrequency);
                break;
            case DFS_RELEVANCE_HEURISTIC:
                pageNodeEvaluator = new LIFOComparator(query);
                break;
            case RANDOM_RELEVANCE_HEURISTIC:
                pageNodeEvaluator = new RandomComparator(query);
                break;
        }
    }

    public BufferedImage generateWordCloud() throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        AtomicInteger pageLoads = new AtomicInteger(0);
        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        DomainFrequency domainFrequency = new DomainFrequency();
        PriorityBlockingQueue<PageNode> queue = new PriorityBlockingQueue<>(100, new FuzzyComparator(query, domainFrequency));

        Set<String> visited = ConcurrentHashMap.newKeySet();
        List<PageNode> resultPages =
                Arrays.stream(new SearchEngineScraper().getResultLinks(query))
                        .map(PageNode::new)
                        .collect(Collectors.toList());
        queue.addAll(resultPages);

        // create crawlers and submit to executor
        ExecutorService executor = Executors.newFixedThreadPool(numCrawlers);
        TfpdfCalculator tfpdfCalculator = new TfpdfCalculator();
        for (int i = 0; i < numCrawlers; i++) {
            executor.submit(
                    new QueryCrawler(query, maxPageLoads, queue, ignorer, domainFrequency, visited, pageNodeEvaluator, pageLoads, tfpdfCalculator)
            );
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TermWeight[] words = new WeightedFont().getFontSizes(
                new MapToWeightingArray(tfpdfCalculator.getWeights()).convert(60));

        System.out.println("\n-- Word frequencies --");
        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %.3f%n", i, words[i].getTerm(), words[i].getWeight());
        }

        // TODO: this is kind of abusing the purpose of the MaptoWeightingArray class...
        // TODO: domain freqs should really be integer
        System.out.println("\n-- Domain frequencies --");
        TermWeight[] domainFreqs = new MapToWeightingArray(domainFrequency.getVisitMap()).convert(25);
        for (int i = domainFreqs.length - 1; i >= 0; i--) {
            System.out.printf("Domain %d: %15s - Freq: %.0f%n", i, domainFreqs[i].getTerm(), domainFreqs[i].getWeight());
        }

        return new WordCloudGenerator(words, 850, 850).generateWordCloud();
    }
}
