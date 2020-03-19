package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;
import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;

import java.io.IOException;
import java.util.*;

public abstract class QueryCrawler {
    String query;
    int maxPageLoads;
    int pageLoads;
    Queue<String> urlPool;
    Random random;
    Set<String> visited;
    WordIgnorer ignorer;
    WordProximityScorer scorer;
    PriorityQueue<PageNode> queue;

    public QueryCrawler(String query, int maxPageLoads, Comparator<PageNode> pageComparator, Queue<String> urlPool) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;
        this.urlPool = urlPool;

        queue = new PriorityQueue<>(pageComparator);
        pageLoads = 0;
        random = new Random();
    }

    public Map<String, Integer> getCrawlScores() throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        visited = new HashSet<>();
        List<String> resultUrls = Arrays.asList(new SearchEngineScraper().getResultLinks(query));
        urlPool.addAll(resultUrls);
        //urlPool.add("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_outbreak");

        ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        scorer = new WordProximityScorer(query);

        while (crawlNextPage());

        System.out.println("Finished crawling.");

        return scorer.getWordScores();
    }

    protected void queueNextPage() {
        String nextUrl = urlPool.poll();
        System.out.printf("Loading page: %s%n%n", nextUrl);
        queue.add(new PageNode(nextUrl));
        pageLoads++;
    }

    public abstract boolean crawlNextPage();
}
