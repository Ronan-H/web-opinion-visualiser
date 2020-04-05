package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.WeightedFont;
import ie.gmit.sw.ai.cloud.WordFrequency;
import ie.gmit.sw.comparator.FuzzyComparator;
import ie.gmit.sw.comparator.LIFOComparator;
import ie.gmit.sw.comparator.PageNodeEvaluator;
import ie.gmit.sw.comparator.RandomComparator;
import ie.gmit.sw.crawler.QueryCrawler;
import ie.gmit.sw.crawler.SearchEngineScraper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryCloudGenerator {
    private String query;
    private int maxPageLoads;
    private int numThreads;
    private PageNodeEvaluator pageNodeEvaluator;

    private DomainFrequency domainFrequency;

    public QueryCloudGenerator(String query, int maxPageLoads, int numThreads, SearchAlgorithm searchAlgorithm) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;
        this.numThreads = numThreads;

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

        QueryCrawler[] crawlers = new QueryCrawler[numThreads];
        Thread[] threads = new Thread[numThreads];

        // create crawlers
        for (int i = 0; i < crawlers.length; i++) {
            crawlers[i] = new QueryCrawler(query, 150, queue, ignorer, domainFrequency, visited, new FuzzyComparator(query, domainFrequency), pageLoads);
        }

        // start crawlers on new threads
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);
        for (int i = 0; i < crawlers.length; i++) {
            threads[i] = new Thread(crawlers[i]);
            threads[i].start();
        }

        // wait for all crawlers to finish
        for (int i = 0; i < crawlers.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> combinedScores = new HashMap<>();
        int combinedScore;
        for (int i = 0; i < crawlers.length; i++) {
            Map<String, Integer> crawlScores = crawlers[i].getCrawlScores();
            // combine scores
            for (String k : crawlScores.keySet()) {
                if (!combinedScores.containsKey(k)) {
                    combinedScores.put(k, 0);
                }
                combinedScore = combinedScores.get(k) + crawlScores.get(k);
                combinedScores.put(k, combinedScore);
            }
        }

        WordFrequency[] words = new WeightedFont().getFontSizes(
                new MapToFrequencyArray(combinedScores).convert(60));

        System.out.println("\n-- Word frequencies --");
        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %d%n", i, words[i].getWord(), words[i].getFrequency());
        }

        System.out.println("\n-- Domain frequencies --");
        WordFrequency[] domainFreqs = new MapToFrequencyArray(domainFrequency.getVisitMap()).convert(25);
        for (int i = domainFreqs.length - 1; i >= 0; i--) {
            System.out.printf("Domain %d: %15s - Freq: %d%n", i, domainFreqs[i].getWord(), domainFreqs[i].getFrequency());
        }

        return new WordCloudGenerator(words, 850, 850).generateWordCloud();
    }
}
