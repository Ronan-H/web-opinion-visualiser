package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PageNode {
    private String url;
    private Document pageDoc;

    public PageNode(String url) {
        this.url = url;

        try {
            System.out.println("Connecting to URL: " + url);
            pageDoc = Jsoup.connect(url).get();
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public Collection<String> getUnvisitedLinks(Set<String> visited) {
        return pageDoc.select("a")
                .stream()
                .map(e -> e.absUrl("href"))
                .map(String::toLowerCase)
                .filter(u -> u.startsWith("http"))
                .filter(u -> !visited.contains(u))
                .filter(u -> !URLBlacklist.getInstance().isUrlBlacklisted(u))
                .collect(Collectors.toList());
    }

    public int getRelevanceScore(String query) {
        System.out.println("Getting relevance score for url " + url);

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
                    //System.out.printf("Found query in %s tag on page %s, score added: %d%n", scoringTag, url, tagScore);
                    scoreTotal += tagScore;
                }
            }
        }

        return scoreTotal;
    }
}
