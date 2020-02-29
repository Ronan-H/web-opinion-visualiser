package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.stream.Collectors;

public class PageNode {
    private String url;
    private PageNode[] children;

    public PageNode(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public PageNode[] getChildren() {
        return children;
    }

    public void findChildren() throws IOException {
        Document doc = Jsoup.connect(url).get();
        children = doc.select("a")
                            .stream()
                            .map(e -> e.absUrl("href"))
                            .map(PageNode::new)
                            .toArray(PageNode[]::new);
    }
}
