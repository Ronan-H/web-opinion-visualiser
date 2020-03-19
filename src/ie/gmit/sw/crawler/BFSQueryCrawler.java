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

public class BFSQueryCrawler extends QueryCrawler {
    private int maxPageLoads;
    private PriorityQueue<PageNode> queue;

    public BFSQueryCrawler(int maxPageLoads) {
        this.maxPageLoads = maxPageLoads;
    }

    public Map<String, Integer> getCrawlScores(String query) throws IOException {
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);

        Random random = new Random();

        Deque<String> urlPool = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Comparator<PageNode> relevanceComparator = new RelevanceComparator(query);

        //urlPool.add("https://en.wikipedia.org/wiki/2019%E2%80%9320_coronavirus_outbreak");

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        WordProximityScorer scorer = new WordProximityScorer(query);

        int pageLoads = 0;
        PageNode node;



        System.out.println("Finished crawling.");

        return scorer.getWordScores();
    }

    @Override
    public boolean crawlNextPage() {
        if ((!queue.isEmpty() || !urlPool.isEmpty()) && pageLoads < maxPageLoads) {
            return false;
        }

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

        return true;
    }
}
