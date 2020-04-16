package ie.gmit.sw.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

// scrapes search results from a search engine
public class SearchEngineScraper {
    private String query;

    public SearchEngineScraper(String query) {
        this.query = query;
    }

    public String[] getResultLinks() throws IOException {
        // connect to search on DDG
        Document doc = Jsoup.connect("https://duckduckgo.com/html/?q=" + query).get();

        // aggregate results (only using the top N)
        Elements res = doc.getElementById("links").getElementsByClass("results_links");
        String[] resultLinks = new String[res.size()];
        int resultCounter = 0;

        for (Element r : res){
            Element title = r.getElementsByClass("links_main").first().getElementsByTag("a").first();
            String url = title.attr("href").toLowerCase();

            resultLinks[resultCounter++] = url;
        }

        return resultLinks;
    }
}
