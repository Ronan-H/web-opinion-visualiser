package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;
import ie.gmit.sw.RelevanceComparator;
import ie.gmit.sw.WordIgnorer;
import ie.gmit.sw.WordProximityScorer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public abstract class QueryCrawler {
    private int maxPageLoads;
    private Deque<String> urlPool;
    private Random random;
    private Set<String> visited;

    public QueryCrawler(int maxPageLoads) {
        this.maxPageLoads = maxPageLoads;

        random = new Random();
    }

    public Map<String, Integer> getCrawlScores(String query) throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        visited = new HashSet<>();
        PriorityQueue<PageNode> queue = new PriorityQueue<>(maxPageLoads, new RelevanceComparator(query));

        List<String> resultUrls = Arrays.asList(new SearchEngineScraper().getResultLinks(query));
        urlPool = new ArrayDeque<>(resultUrls);
        //urlPool.add("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_outbreak");

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        Map<String, Integer> wordScores = new HashMap<>();
        WordProximityScorer scorer = new WordProximityScorer(wordScores, query);

        int pageLoads = 0;
        PageNode node;

        while ((!queue.isEmpty() || !urlPool.isEmpty()) && pageLoads < maxPageLoads) {
            if (queue.isEmpty()) {
                String nextUrl = urlPool.poll();
                System.out.printf("Loading page: %s%n%n", nextUrl);
                node = new PageNode(nextUrl);
                queue.add(node);
                pageLoads++;
            }

            node = queue.poll();
            System.out.println("Polling page from the queue: " + node.getUrl());
            double nodeRelevancy = node.getRelevanceScore(query);
            System.out.printf("Relevance: %.2f%n", nodeRelevancy);
            visited.add(node.getRootUrl());

            if (nodeRelevancy > 0.5) {
                List<String> nextLinks = node.getUnvisitedLinks(visited);

                // add a few random links from this page to the URL pool
                int numLinksAdd = (int)Math.ceil(nodeRelevancy / 3);
                //if (numLinksAdd < 2) numLinksAdd = 2;
                if (numLinksAdd > 5) numLinksAdd = 5;

                System.out.printf("Adding %d child URLs...%n", numLinksAdd);

                for (int i = 0; i < numLinksAdd && nextLinks.size() > 0; i++) {
                    urlPool.add(nextLinks.remove(random.nextInt(nextLinks.size())));
                }
            }

            // prune urlPool
            while (urlPool.size() > 250) {
                urlPool.pop();
            }

            System.out.println("Adding word scores...\n");
            node.addWordScores(query, scorer, ignorer);
        }

        System.out.println("Finished crawling.");

        return wordScores;
    }

    public abstract boolean crawlNextPage();
}
