package ie.gmit.sw.crawler;

import ie.gmit.sw.*;
import ie.gmit.sw.comparator.PageNodeEvaluator;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        while (crawlNextPage());
    }

    private PageNode loadNextPage() {
        /*
        // rebuild queue (some elements may be out of order because of the changing domain frequency
        synchronized (queue) {
            Deque<PageNode> temp = new ArrayDeque<>(queue.size());
            while (!queue.isEmpty()) {
                temp.offer(queue.poll());
            }
            while (!temp.isEmpty()) {
                queue.add(temp.poll());
            }
        }
        */

        PageNode nextPage;
        log.format("Polling page URL from the queue...%n");
        try {
            nextPage = queue.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }

        if (nextPage == null) {
            return null;
        }

        log.format("Loading page:      %s%n", nextPage.getUrl());
        log.format("   ...from parent: %s%n",
                nextPage.getParent() == null ? "<search result>" : nextPage.getParent().getUrl());
        log.format("Relative domain visit frequency: %.3f%n",
                crawlStats.getDomainFrequency().getRelativeDomainFrequency(nextPage.getUrl()));
        nextPage.load();
        crawlStats.incPageLoads();

        // increment domain name visit count
        crawlStats.recordDomainVisit(nextPage.getUrl());
        return nextPage;
    }

    public boolean crawlNextPage() {
        if (pageLoads.decrementAndGet() < 0) {
            crawlStats.logEntry("Crawler stopping: max page loads hit");
            return false;
        }

        log = new Formatter();

        PageNode node = loadNextPage();

        if (node == null) {
            crawlStats.logEntry("Crawler stopping: queue is empty");
            return false;
        }

        crawlStats.recordDepth(node.getDepth());
        double nodeRelevancy = node.getRelevanceScore(query);

        if (nodeRelevancy > 0) {
            crawlStats.incPageHaveQuery();
        }

        log.format("Relevance: %.4f%n", nodeRelevancy);
        log.format("Depth: %d%n", node.getDepth());
        visited.add(node.getRootUrl());
        int numChildNodesExpanded = pageNodeEvaluator.numChildExpandHeuristic(node);
        log.format("Adding %d child URLs...%n", numChildNodesExpanded);

        List<String> nextLinks = node.getUnvisitedLinks(visited);

        for (int i = 0; i < numChildNodesExpanded && nextLinks.size() > 0; i++) {
            queue.add(new PageNode(nextLinks.remove(random.nextInt(nextLinks.size())), node));
        }

        while (queue.size() > 1000) {
            queue.poll();
        }

        node.addTermWeights(query, tfpdfCalculator, scorer);

        crawlStats.logEntry(new LogEntry(log.toString(), node.getId()));

        return true;
    }
}
