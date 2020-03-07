package ie.gmit.sw;


import ie.gmit.sw.ai.cloud.WordFrequency;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class QueryFrequencyGenerator {
    public WordFrequency[] generateWordFrequencies() throws IOException {
        String query = "ireland";
        int maxPageVisits = 40;
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

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        Map<String, Integer> wordScores = new HashMap<>();
        WordProximityScorer scorer = new WordProximityScorer(wordScores, query, ignorer);

        int pageVisits = 0;
        //int minRelevancy = Math.min((int) (highestRootRelevance * 0.8), 10);
        int minRelevancy = 5;

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
                System.out.println("Page not relevant, ignoring links: " + next.getUrl());
            }

            next.addWordScores(scorer, query);
        }

        WordFrequency[] frequencies = new WordFrequency[wordScores.size()];
        int wordIndex = 0;
        for (String word : wordScores.keySet()) {
            frequencies[wordIndex++] = new WordFrequency(word, wordScores.get(word));
        }

        return frequencies;
    }
}
