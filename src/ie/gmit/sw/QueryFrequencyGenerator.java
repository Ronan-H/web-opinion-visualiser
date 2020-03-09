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
        String query = "galway";
        int maxPageLoads = 25;

        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
        Elements res = doc.getElementById("links").getElementsByClass("results_links");

        Set<String> visited = new HashSet<>();
        Comparator<PageNode> relevanceComparator = new RelevanceComparator(query);
        PriorityQueue<PageNode> queue = new PriorityQueue<>(10, relevanceComparator);
        Deque<String> urlPool = new ArrayDeque<>();

        for (Element r : res){
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            String url = title.attr("href");
            System.out.println("URL:\t" + url);
            System.out.println("Title:\t" + title.text());
            System.out.println("Text:\t" + r.getElementsByClass("result__snippet").first().wholeText());

            urlPool.add(url);
        }

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        Map<String, Integer> wordScores = new HashMap<>();
        WordProximityScorer scorer = new WordProximityScorer(wordScores, query);

        int pageLoads = 0;
        double minRelevancy = 0.5;

        while ((!queue.isEmpty() || !urlPool.isEmpty()) && pageLoads < maxPageLoads) {
            if (queue.isEmpty()) {
                PageNode next = new PageNode(urlPool.poll());
                System.out.println("Loading page: " + next.getUrl());
                queue.add(next);
                pageLoads++;
            }

            PageNode next = queue.poll();
            double nextRelevancy = next.getRelevanceScore(query);

            if (nextRelevancy >= minRelevancy) {
                System.out.println("Adding links from: " + next.getUrl());
                System.out.println("\tRelevance: " + nextRelevancy);
                visited.add(next.getUrl());
                urlPool.addAll(next.getUnvisitedLinks(visited));
            }
            else {
                System.out.println("Page not relevant, ignoring links: " + next.getUrl());
            }

            next.addWordScores(query, scorer, ignorer);
        }

        WordFrequency[] frequencies = new WordFrequency[wordScores.size()];
        int wordIndex = 0;
        for (String word : wordScores.keySet()) {
            frequencies[wordIndex++] = new WordFrequency(word, wordScores.get(word));
        }

        return frequencies;
    }
}
