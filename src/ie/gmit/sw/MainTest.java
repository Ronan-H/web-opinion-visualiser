package ie.gmit.sw;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainTest {
    public static void main(String[] args) throws IOException {
        /*
        String query = "GMIT Software";
        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
        Elements res = doc.getElementById("links").getElementsByClass("results_links");
        for(Element r: res){
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            System.out.println("URL:\t" + title.attr("href"));
            System.out.println("Title:\t" + title.text());
            System.out.println("Text:\t" + r.getElementsByClass("result__snippet").first().wholeText());
        }
        */

        int searchDepth = 10;
        PageNode node = new PageNode("https://projecteuler.net/");
        Set<String> visited = new HashSet<>();
        String query = "problems";
        int rootHeuristic = node.getRelevanceScore(query);
        System.out.println("rootHeuristic = " + rootHeuristic);
        int minHeuristic = (int) (rootHeuristic * 0.7f);
        node.findChildren(query, searchDepth, minHeuristic, visited);

        System.out.println("======================================================================");

        int score = node.getRelevanceScore(query);
        System.out.printf("%nRelevance for query \"%s\": %d%n", query, score);
    }
}
