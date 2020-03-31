package ie.gmit.sw.crawler;

import ie.gmit.sw.DomainFrequency;
import ie.gmit.sw.PageNode;
import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class QueryCrawler {
    private String query;
    private int maxPageLoads;
    private int pageLoads;
    private Random random;
    private Set<String> visited;
    private WordIgnorer ignorer;
    private WordProximityScorer scorer;
    private PriorityQueue<PageNode> queue;
    private DomainFrequency domainFrequency;


    public QueryCrawler(String query, int maxPageLoads, Comparator<PageNode> pageComparator) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;

        queue = new PriorityQueue<>(pageComparator);
        pageLoads = 0;
        random = new Random();
        domainFrequency = new DomainFrequency();

        // TODO find a way to do this properly
        ((FuzzyScoreComparator) pageComparator).setDomainFrequencies(domainFrequency);
    }

    private PageNode loadNextPage() {
        PageNode nextPage = queue.poll();
        System.out.printf("Loading page: %s%n%n", nextPage.getUrl());
        System.out.printf("Relative domain visit frequency: %.3f%n", domainFrequency.getRelativeDomainFrequency(nextPage.getUrl()));
        nextPage.load();
        pageLoads++;

        // increment domain name visit count
        domainFrequency.recordVisit(nextPage.getUrl());
        return nextPage;
    }

    public boolean crawlNextPage() {
        if (queue.isEmpty() || pageLoads >= maxPageLoads) {
            return false;
        }

        PageNode node;

        node = loadNextPage();
        System.out.println("Polling page from the queue: " + node.getUrl());
        double nodeRelevancy = node.getRelevanceScore(query);
        System.out.printf("Relevance: %.2f%n", nodeRelevancy);
        System.out.printf("Depth: %d%n", node.getDepth());
        visited.add(node.getRootUrl());

        if (nodeRelevancy > 0.5) {
            List<String> nextLinks = node.getUnvisitedLinks(visited);

            // add a few random links from this page to the URL pool
            int numLinksAdd = (int)Math.ceil(nodeRelevancy / 3);
            if (numLinksAdd > 5) numLinksAdd = 5;

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
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        visited = new HashSet<>();
        List<PageNode> resultPages =
                Arrays.stream(new SearchEngineScraper().getResultLinks(query))
                .map(PageNode::new)
                .collect(Collectors.toList());
        queue.addAll(resultPages);
        //urlPool.add("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_outbreak");

        ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        scorer = new WordProximityScorer(query);

        while (crawlNextPage());

        System.out.println("Finished crawling.");

        return scorer.getWordScores();
    }

    public DomainFrequency getDomainFrequencies() {
        return domainFrequency;
    }
}
