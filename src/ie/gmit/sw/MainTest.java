package ie.gmit.sw;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class MainTest {
    public static void main(String[] args) throws IOException {
        String query = "software";
        int maxPageVisits = 25;
        int maxResults = 3;

        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
        Elements res = doc.getElementById("links").getElementsByClass("results_links");

        int usingNumResults = Math.min(maxResults, res.size());

        Set<String> visited = new HashSet<>();
        Comparator<PageNode> relevanceComparator = new RelevanceComparator(query);
        PriorityQueue<PageNode> queue = new PriorityQueue<>(10, relevanceComparator);
        Deque<String> urlPool = new ArrayDeque<>();
        int highestRootRelevance = -1;

        for (int i = 0; i < usingNumResults; i++){
            Element r = res.get(i);
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            String url = title.attr("href");
            System.out.println("URL:\t" + url);
            System.out.println("Title:\t" + title.text());
            System.out.println("Text:\t" + r.getElementsByClass("result__snippet").first().wholeText());

            PageNode node = new PageNode(url);
            queue.add(node);
            int nodeRelevance = node.getRelevanceScore(query);
            if (nodeRelevance > highestRootRelevance) {
                highestRootRelevance = nodeRelevance;
            }
        }

        int pageVisits = 0;
        int minRelevancy = Math.min((int) (highestRootRelevance * 0.8), 10);

        while ((!queue.isEmpty() || !urlPool.isEmpty()) && pageVisits < maxPageVisits) {
            if (queue.isEmpty()) {
                PageNode next = new PageNode(urlPool.poll());
                System.out.println("Loading page: " + next.getUrl());
                queue.add(next);
                pageVisits++;
            }

            PageNode next = queue.poll();

            if (next.getRelevanceScore(query) >= minRelevancy) {
                System.out.println("Adding links from: " + next.getUrl());
                System.out.println("\tRelevance: " + next.getRelevanceScore(query));
                visited.add(next.getUrl());
                urlPool.addAll(next.getUnvisitedLinks(visited));
            }
            else {
                System.out.println("Page not relevant, skipping: " + next.getUrl());System.out.println();
            }
        }
    }
}
