package ie.gmit.sw.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SearchEngineScraper {
    public String[] getResultLinks(String query) throws IOException {
        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();
        Elements res = doc.getElementById("links").getElementsByClass("results_links");
        String[] resultLinks = new String[res.size()];
        int resultCounter = 0;

        for (Element r : res){
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            String url = title.attr("href").toLowerCase();
            System.out.println("URL:\t" + url);
            System.out.println("Title:\t" + title.text());

            resultLinks[resultCounter++] = url;
        }

        return resultLinks;
    }
}
