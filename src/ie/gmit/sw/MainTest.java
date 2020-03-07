package ie.gmit.sw;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainTest {
    public static void main(String[] args) throws IOException {
        String query = "software";
        int searchDepth = 1;
        int maxResults = 3;

        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
        Elements res = doc.getElementById("links").getElementsByClass("results_links");

        int usingNumResults = Math.min(maxResults, res.size());

        Set<String> visited = new HashSet<>();

        for (int i = 0; i < usingNumResults; i++){
            Element r = res.get(i);
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            String url = title.attr("href");
            System.out.println("URL:\t" + url);
            System.out.println("Title:\t" + title.text());
            System.out.println("Text:\t" + r.getElementsByClass("result__snippet").first().wholeText());

            PageNode node = new PageNode(url);
            int rootHeuristic = node.getRelevanceScore(query);
            System.out.println("rootHeuristic = " + rootHeuristic);
            int minHeuristic = (int) (rootHeuristic * 0.6f);
            node.findChildren(query, searchDepth, minHeuristic, visited);

            System.out.println("======================================================================");

            int score = node.getRelevanceScore(query);
            System.out.printf("%nRelevance for query \"%s\": %d%n", query, score);
        }
    }
}
