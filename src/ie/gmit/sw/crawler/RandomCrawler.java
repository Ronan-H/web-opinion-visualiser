package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;
import ie.gmit.sw.RandomComparator;

import java.util.List;
import java.util.PriorityQueue;

public class RandomCrawler extends QueryCrawler {
    public RandomCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads, new RandomComparator(), new PriorityQueue<String>(new RandomComparator()));
    }

    @Override
    public boolean crawlNextPage() {
        if ((queue.isEmpty() && urlPool.isEmpty()) || pageLoads >= maxPageLoads) {
            return false;
        }

        PageNode node;
        if (queue.isEmpty()) {
            queueNextPage();
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

            for (int i = 0; i < numLinksAdd && nextLinks.size() > 0 && urlPool.size() < 250; i++) {
                urlPool.add(nextLinks.remove(random.nextInt(nextLinks.size())));
            }
        }

        System.out.println("Adding word scores...\n");
        node.addWordScores(query, scorer, ignorer);

        return true;
    }
}
