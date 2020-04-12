package ie.gmit.sw.crawler;

import ie.gmit.sw.*;
import ie.gmit.sw.comparator.PageNodeEvaluator;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryCrawler implements Runnable {
    private String query;
    private Random random;
    private Set<String> visited;
    private WordIgnorer ignorer;
    private WordProximityScorer scorer;
    private PriorityBlockingQueue<PageNode> queue;
    private DomainFrequency domainFrequency;
    private PageNodeEvaluator pageNodeEvaluator;
    private AtomicInteger pageLoads;
    private TfpdfCalculator tfpdfCalculator;

    public QueryCrawler(String query,
                        PriorityBlockingQueue<PageNode> queue,
                        WordIgnorer ignorer,
                        DomainFrequency domainFrequency,
                        Set<String> visited,
                        PageNodeEvaluator pageNodeEvaluator,
                        AtomicInteger pageLoads,
                        TfpdfCalculator tfpdfCalculator) {
        this.query = query;
        this.queue = queue;
        this.ignorer = ignorer;
        this.domainFrequency = domainFrequency;
        this.visited = visited;
        this.pageNodeEvaluator = pageNodeEvaluator;
        this.pageLoads = pageLoads;

        scorer = new WordProximityScorer(query);
        this.tfpdfCalculator = tfpdfCalculator;
        random = new Random();
    }

    @Override
    public void run() {
        while (crawlNextPage());
    }

    private PageNode loadNextPage() {
        PageNode nextPage = queue.poll();
        System.out.printf("Loading page: %s%n%n", nextPage.getUrl());
        System.out.printf("Relative domain visit frequency: %.3f%n", domainFrequency.getRelativeDomainFrequency(nextPage.getUrl()));
        nextPage.load();

        // increment domain name visit count
        domainFrequency.recordVisit(nextPage.getUrl());
        return nextPage;
    }

    public boolean crawlNextPage() {
        if (queue.isEmpty()) {
            System.out.println("Crawler stopping: queue is empty");
            return false;
        }

        if (pageLoads.decrementAndGet() < 0) {
            System.out.println("Crawler stopping: max page loads hit");
            return false;
        }

        System.out.println("Page loads remaining: " + pageLoads.get());

        PageNode node;

        node = loadNextPage();
        System.out.println("Polling page from the queue: " + node.getUrl());
        double nodeRelevancy = node.getRelevanceScore(query);
        System.out.printf("Relevance: %.4f%n", nodeRelevancy);
        System.out.printf("Depth: %d%n", node.getDepth());
        visited.add(node.getRootUrl());
        int numChildNodesExpanded = pageNodeEvaluator.numChildExpandHeuristic(node);
        System.out.printf("Adding %d child URLs...%n", numChildNodesExpanded);

        List<String> nextLinks = node.getUnvisitedLinks(visited);

        for (int i = 0; i < numChildNodesExpanded && nextLinks.size() > 0 && queue.size() < 1000; i++) {
            queue.add(new PageNode(nextLinks.remove(random.nextInt(nextLinks.size())), node));
        }

        System.out.println("Adding word scores...\n");
        node.addWordScores(query, tfpdfCalculator, scorer, ignorer);

        return true;
    }
}
