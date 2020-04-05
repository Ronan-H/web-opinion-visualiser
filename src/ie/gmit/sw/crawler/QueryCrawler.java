package ie.gmit.sw.crawler;

import ie.gmit.sw.DomainFrequency;
import ie.gmit.sw.PageNode;
import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryCrawler implements Runnable{
    private String query;
    private int maxPageLoads;
    private Random random;
    private Set<String> visited;
    private WordIgnorer ignorer;
    private WordProximityScorer scorer;
    private PriorityBlockingQueue<PageNode> queue;
    private DomainFrequency domainFrequency;
    private AtomicInteger pageLoads;

    // TODO find a way to do this properly
    private FuzzyScoreComparator fuzzyScoreComparator;


    public QueryCrawler(String query,
                        int maxPageLoads,
                        PriorityBlockingQueue queue,
                        WordIgnorer ignorer,
                        DomainFrequency domainFrequency,
                        Set<String> visited,
                        FuzzyScoreComparator fuzzyScoreComparator,
                        AtomicInteger pageLoads) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;
        this.queue = queue;
        this.ignorer = ignorer;
        this.domainFrequency = domainFrequency;
        this.visited = visited;
        this.pageLoads = pageLoads;
        // TODO find a way to do this properly
        this.fuzzyScoreComparator = fuzzyScoreComparator;

        scorer = new WordProximityScorer(query);
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
        System.out.printf("Relevance: %.2f%n", nodeRelevancy);
        System.out.printf("Depth: %d%n", node.getDepth());
        visited.add(node.getRootUrl());
        double fuzzyScore = fuzzyScoreComparator.getScoreForPage(node);
        System.out.printf("Fuzzy score: %.2f%n", fuzzyScore);

        if (fuzzyScore > 5.5) {
            List<String> nextLinks = node.getUnvisitedLinks(visited);

            // add a few random links from this page to the URL pool
            int numLinksAdd = (int)Math.ceil(fuzzyScore / 7);

            System.out.printf("Adding %d child URLs...%n", numLinksAdd);

            for (int i = 0; i < numLinksAdd && nextLinks.size() > 0 && queue.size() < 250; i++) {
                queue.add(new PageNode(nextLinks.remove(random.nextInt(nextLinks.size())), node));
            }
        }

        System.out.println("Adding word scores...\n");
        node.addWordScores(query, scorer, ignorer);

        return true;
    }

    public Map<String, Integer> getCrawlScores() throws IOException {
        return scorer.getWordScores();
    }

    public DomainFrequency getDomainFrequencies() {
        return domainFrequency;
    }
}
