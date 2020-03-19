package ie.gmit.sw.crawler;


import ie.gmit.sw.PageNode;
import ie.gmit.sw.RelevanceComparator;

import java.io.IOException;
import java.util.*;

public class BFSQueryCrawler extends QueryCrawler {
    private PriorityQueue<PageNode> queue;

    public BFSQueryCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads);
    }

    @Override
    public Map<String, Integer> getCrawlScores() throws IOException {
        queue = new PriorityQueue<>(maxPageLoads, new RelevanceComparator(query));
        return super.getCrawlScores();
    }

    @Override
    public boolean crawlNextPage() {
        if ((queue.isEmpty() && urlPool.isEmpty()) || pageLoads >= maxPageLoads) {
            return false;
        }

        PageNode node;
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

        return true;
    }
}
