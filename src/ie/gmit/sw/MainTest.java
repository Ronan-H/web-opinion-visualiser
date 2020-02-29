package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        PageNode node = new PageNode("https://www.aoifesclowndoctors.ie/");
        String query = "clown";
        int score = node.getRelevanceScore(query);
        System.out.printf("%nRelevance for query \"%s\": %d%n", query, score);
    }
}
