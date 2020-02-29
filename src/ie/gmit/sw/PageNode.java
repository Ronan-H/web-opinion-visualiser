package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class PageNode {
    private String url;
    private Document pageDoc;
    private PageNode[] children;

    public PageNode(String url) {
        this.url = url;

        try {
            pageDoc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public PageNode[] getChildren() {
        return children;
    }

    public void findChildren() throws IOException {
        children = pageDoc.select("a")
                            .stream()
                            .map(e -> e.absUrl("href"))
                            .map(PageNode::new)
                            .toArray(PageNode[]::new);
    }

    public int getRelevanceScore(String query) {
        TagWeights tagWeights = TagWeights.getInstance();
        int scoreTotal = 0;
        Elements elems;
        String elemText;
        int tagScore;

        for (String scoringTag : tagWeights.getScoringTags()) {
            tagScore = tagWeights.getScoreFor(scoringTag);
            elems = pageDoc.select(scoringTag);
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();
                if (elemText.contains(query)) {
                    System.out.printf("Found query in %s tag, score added: %d%n", scoringTag, tagScore);
                    scoreTotal += tagScore;
                }
            }
        }


        return scoreTotal;
    }
}
