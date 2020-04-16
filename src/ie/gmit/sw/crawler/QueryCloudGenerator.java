package ie.gmit.sw.crawler;

import ie.gmit.sw.cloud.MapToWeightingArray;
import ie.gmit.sw.cloud.WeightedFont;
import ie.gmit.sw.cloud.TermWeight;
import ie.gmit.sw.cloud.WordCloudGenerator;
import ie.gmit.sw.comparator.FuzzyComparator;
import ie.gmit.sw.comparator.DFSComparator;
import ie.gmit.sw.comparator.PageNodeEvaluator;
import ie.gmit.sw.comparator.RandomComparator;
import ie.gmit.sw.term.TermIgnorer;
import ie.gmit.sw.term.TfpdfCalculator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// manages a pool of QueryCrawlers to crawl the web based on a given search query,
// and generate a word cloud
public class QueryCloudGenerator {
    private String query;
    private int maxPageLoads;
    private int numCrawlers;
    private int numCloudWords;
    private PageNodeEvaluator pageNodeEvaluator;
    private TermIgnorer ignorer;
    private CrawlStats crawlStats;
    private DomainFrequency domainFrequency;

    public QueryCloudGenerator(String query,
                               int maxPageLoads,
                               int numThreads,
                               int numCloudWords,
                               SearchAlgorithm searchAlgorithm,
                               File ignoredTerms,
                               File fclFile) {
        this.query = query.toLowerCase();
        this.maxPageLoads = maxPageLoads;
        this.numCrawlers = numThreads;
        this.numCloudWords = numCloudWords;
        this.ignorer = new TermIgnorer(ignoredTerms, query);

        domainFrequency = new DomainFrequency();

        // choose a PageNode comparator based on the search algorithm selected
        switch (searchAlgorithm) {
            case BFS_FUZZY_HEURISTIC:
                pageNodeEvaluator = new FuzzyComparator(fclFile, this.query, domainFrequency);
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

        // initialise shared objects
        AtomicInteger pageLoadsLeft = new AtomicInteger(maxPageLoads);
        PriorityBlockingQueue<PageNode> queue = new PriorityBlockingQueue<>(100, pageNodeEvaluator);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        TfpdfCalculator tfpdfCalculator = new TfpdfCalculator();
        crawlStats = new CrawlStats(domainFrequency);

        // perform a search for the query on DuckDuckGo
        String[] searchResults = new SearchEngineScraper(query).getResultLinks();

        // store search result links as PageNodes, ignoring URLs that don't match the allowed criteria
        URLFilter urlFilter = URLFilter.getInstance();
        List<PageNode> resultPages =
                Arrays.stream(searchResults)
                        .filter(urlFilter::isURLAllowed)
                        .map(PageNode::new)
                        .collect(Collectors.toList());

        // add search results to queue
        for (int i = 0; i < Math.min(resultPages.size(), pageNodeEvaluator.numSearchResultsToUse()); i++) {
            queue.add(resultPages.get(i));
        }

        // create crawlers and submit to executor
        ExecutorService executor = Executors.newFixedThreadPool(numCrawlers);
        for (int i = 0; i < numCrawlers; i++) {
            executor.submit(
                    new QueryCrawler(query, queue, ignorer, crawlStats, visited, pageNodeEvaluator, tfpdfCalculator, pageLoadsLeft)
            );
        }

        // wait for all threads to terminate
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // generate a list of terms sorted by weight
        TermWeight[] terms = new WeightedFont(numCloudWords).getFontSizes(
                new MapToWeightingArray(tfpdfCalculator.getWeights()).convert(numCloudWords));

        // print term frequencies
        System.out.println("\n-- Term frequencies --");
        for (int i = terms.length - 1; i >= 0; i--) {
            System.out.printf("Term %d: %15s - Score: %.3f%n", i, terms[i].getTerm(), terms[i].getWeight());
        }
        System.out.println();

        // print crawl stats, the stats shown on the servlet page under the word cloud
        System.out.println(crawlStats.toString());

        // generate word cloud image based on term occurrences
        return new WordCloudGenerator(terms, 850, 850).generateWordCloud();
    }

    public CrawlStats getCrawlStats() {
        return crawlStats;
    }
}
