package ie.gmit.sw.crawler;

import ie.gmit.sw.comparator.PageNodeEvaluator;
import ie.gmit.sw.term.TermIgnorer;
import ie.gmit.sw.term.TermProximityCounter;
import ie.gmit.sw.term.TfpdfCalculator;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


// crawls the web on it's own thread, based on a given search query
public class QueryCrawler implements Runnable {
    private String query;
    private Random random;
    private Set<String> visited;
    private TermProximityCounter scorer;
    private PriorityBlockingQueue<PageNode> queue;
    private CrawlStats crawlStats;
    private PageNodeEvaluator pageNodeEvaluator;
    private AtomicInteger pageLoads;
    private TfpdfCalculator tfpdfCalculator;
    private Formatter log;

    public QueryCrawler(String query,
                        PriorityBlockingQueue<PageNode> queue,
                        TermIgnorer ignorer,
                        CrawlStats crawlStats,
                        Set<String> visited,
                        PageNodeEvaluator pageNodeEvaluator,
                        TfpdfCalculator tfpdfCalculator,
                        AtomicInteger pageLoads) {
        this.query = query;
        this.queue = queue;
        this.crawlStats = crawlStats;
        this.visited = visited;
        this.pageNodeEvaluator = pageNodeEvaluator;
        this.pageLoads = pageLoads;

        scorer = new TermProximityCounter(query, ignorer);
        this.tfpdfCalculator = tfpdfCalculator;
        random = new Random();
    }

    @Override
    public void run() {
        // keep crawling until one of the stop conditions is met
        while (crawlNextPage());
    }

    // polls and loads a page from the shared queue
    private PageNode loadNextPage() throws InterruptedException {
        PageNode nextPage;
        log.format("Polling page URL from the queue...%n");
        nextPage = queue.poll(3, TimeUnit.SECONDS);

        if (nextPage == null) {
            // timeout on poll
            return null;
        }

        // record visit to URL
        visited.add(nextPage.getRootUrl());

        // log page load, and some stats
        nextPage.load();
        log.format("Loaded page:      %s%n", nextPage.getUrl());
        log.format("   ...from parent: %s%n",
                nextPage.getParent() == null ? "<search result>" : nextPage.getParent().getUrl());
        log.format("Relative domain visit frequency: %.3f%n",
                crawlStats.getDomainFrequency().getRelativeDomainFrequency(nextPage.getUrl()));
        crawlStats.incPageLoads();

        // increment domain name visit count
        crawlStats.recordDomainVisit(nextPage.getUrl());
        return nextPage;
    }

    // loads and processes the next page in the queue
    public boolean crawlNextPage() {
        if (pageLoads.decrementAndGet() < 0) {
            // stop condition: hit the max number of allowed page loads
            crawlStats.logEntry("Crawler stopping: max page loads hit");
            return false;
        }

        log = new Formatter();
        // load next page
        PageNode node;
        try {
             node = loadNextPage();
        } catch (InterruptedException e) {
            // skip this page and continue the search
            return true;
        }

        if (node == null) {
            // queue poll timed out
            crawlStats.logEntry("Crawler stopping: queue is empty");
            return false;
        }

        // record some stats to display with the cloud on the web page
        crawlStats.recordDepth(node.getDepth());
        double nodeRelevancy = node.getRelevanceScore(query);

        if (nodeRelevancy > 0) {
            crawlStats.incPageHaveQuery();
        }

        log.format("Relevance: %.4f%n", nodeRelevancy);
        log.format("Depth: %d%n", node.getDepth());
        int numChildNodesExpanded = pageNodeEvaluator.numChildExpandHeuristic(node);
        log.format("Adding %d child URLs...%n", numChildNodesExpanded);

        // add random selection of child links to queue
        List<String> nextLinks = node.getUnvisitedLinks(visited);
        for (int i = 0; i < numChildNodesExpanded && nextLinks.size() > 0; i++) {
            queue.add(new PageNode(nextLinks.remove(random.nextInt(nextLinks.size())), node));
        }

        // record term weights for the word cloud
        node.addTermWeights(query, tfpdfCalculator, scorer);

        // submit crawl log
        crawlStats.logEntry(new LogEntry(log.toString(), node.getId()));

        // ready to crawl the next page
        return true;
    }
}
