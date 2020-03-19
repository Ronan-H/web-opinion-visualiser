package ie.gmit.sw.crawler;

import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;

import java.io.IOException;
import java.util.*;

public abstract class QueryCrawler {
    protected String query;
    protected int maxPageLoads;
    protected int pageLoads;
    protected Deque<String> urlPool;
    protected Random random;
    protected Set<String> visited;
    protected WordIgnorer ignorer;
    protected WordProximityScorer scorer;

    public QueryCrawler(String query, int maxPageLoads) {
        this.query = query;
        this.maxPageLoads = maxPageLoads;

        pageLoads = 0;
        random = new Random();
    }

    public Map<String, Integer> getCrawlScores() throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        visited = new HashSet<>();
        List<String> resultUrls = Arrays.asList(new SearchEngineScraper().getResultLinks(query));
        urlPool = new ArrayDeque<>(resultUrls);
        //urlPool.add("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_outbreak");

        ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        scorer = new WordProximityScorer(query);

        while (crawlNextPage());

        System.out.println("Finished crawling.");

        return scorer.getWordScores();
    }

    public abstract boolean crawlNextPage();
}
