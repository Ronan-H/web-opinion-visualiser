package ie.gmit.sw.crawler;

import ie.gmit.sw.DomainFrequency;
import ie.gmit.sw.PageNode;
import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;
import ie.gmit.sw.comparator.PageNodeEvaluator;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryCrawler implements Callable<Map<String, Integer>> {
    private String query;
    private int maxPageLoads;
    private Random random;
    private Set<String> visited;
    private WordIgnorer ignorer;
    private WordProximityScorer scorer;
    private PriorityBlockingQueue<PageNode> queue;
    private DomainFrequency domainFrequency;
    private PageNodeEvaluator pageNodeEvaluator;
    private AtomicInteger pageLoads;


    public QueryCrawler(String query,
                        int maxPageLoads,
                        PriorityBlockingQueue<PageNode> queue,
                        WordIgnorer ignorer,
                        DomainFrequency domainFrequency,
                        Set<String> visited,
                        PageNodeEvaluator pageNodeEvaluator,
                        AtomicInteger pageLoads) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;
        this.queue = queue;
        this.ignorer = ignorer;
        this.domainFrequency = domainFrequency;
        this.visited = visited;
        this.pageNodeEvaluator = pageNodeEvaluator;
        this.pageLoads = pageLoads;

        scorer = new WordProximityScorer(query);
        random = new Random();
    }

    @Override
    public Map<String, Integer> call() {
        while (crawlNextPage());
        return scorer.getWordScores();
    }

    private PageNode loadNextPage() {
        PageNode nextPage = queue.poll();
        System.out.printf("Loading page: %s%n%n", nextPage.getUrl());
        System.out.printf("Relative domain visit frequency: %.3f%n", domainFrequency.getRelativeDomainFrequency(nextPage.getUrl()));
        nextPage.load();
        pageLoads.incrementAndGet();

        // increment domain name visit count
        domainFrequency.recordVisit(nextPage.getUrl());
        return nextPage;
    }

    public boolean crawlNextPage() {
        if (queue.isEmpty() || pageLoads.get() >= maxPageLoads) {
            if (queue.isEmpty()) {
                System.out.println("Crawler stopping: queue is empty");
            }
            else {
                System.out.println("Crawler stopping: max page loads hit");
            }

            return false;
        }

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

        for (int i = 0; i < numChildNodesExpanded && nextLinks.size() > 0 && queue.size() < 500; i++) {
            queue.add(new PageNode(nextLinks.remove(random.nextInt(nextLinks.size())), node));
        }

        System.out.println("Adding word scores...\n");
        node.addWordScores(query, scorer, ignorer);

        return true;
    }

    public Map<String, Integer> getCrawlScores() {
        return scorer.getWordScores();
    }
}
