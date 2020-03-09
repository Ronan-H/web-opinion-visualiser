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
    private boolean errored;

    public PageNode(String url) {
        this.url = url;

        try {
            System.out.println("Connecting to URL: " + url);
            pageDoc = Jsoup.connect(url).get();
            Thread.sleep(750);
        } catch (IOException | InterruptedException e) {
            errored = true;
        }
    }

    public String getUrl() {
        return url;
    }

    public Collection<String> getUnvisitedLinks(Set<String> visited) {
        if (errored) return new ArrayList<>();

        return pageDoc.select("a")
                .stream()
                .map(e -> e.absUrl("href"))
                .map(String::toLowerCase)
                .filter(u -> u.startsWith("http"))
                .filter(u -> !visited.contains(u))
                .filter(u -> !URLBlacklist.getInstance().isUrlBlacklisted(u))
                .collect(Collectors.toList());
    }

    public double getRelevanceScore(String query) {
        if (errored) return -1;

        System.out.println("Getting relevance score for url " + url);

        TagWeights tagWeights = TagWeights.getInstance();
        int scoreTotal = 0;
        int totalOccurences = 0;
        Elements elems;
        String elemText;
        int tagScore;
        int totalTextLength = 0;

        for (String scoringTag : tagWeights.getScoringTags()) {
            tagScore = tagWeights.getScoreFor(scoringTag);
            elems = pageDoc.select(scoringTag);
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();
                totalTextLength += elemText.length();
                if (elemText.contains(query)) {
                    // counting occurrences of a substring in a string: https://stackoverflow.com/a/770069
                    int numOccurances = elemText.split(query, -1).length - 1;
                    scoreTotal += tagScore * numOccurances;
                    totalOccurences += numOccurances;
                }
            }
        }

        return scoreTotal * ((double) (totalOccurences * query.length()) / totalTextLength);
    }

    public void addWordScores(String query, WordProximityScorer scorer, WordIgnorer ignorer) {
        if (errored) return;

        TagWeights tagWeights = TagWeights.getInstance();
        Elements elems;
        String elemText;
        int tagScore;

        for (String scoringTag : tagWeights.getScoringTags()) {
            tagScore = tagWeights.getScoreFor(scoringTag);
            elems = pageDoc.select(scoringTag);
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();
                if (elemText.contains(query)) {
                    scorer.addWordScores(elemText, 1, ignorer);
                }
            }
        }
    }
}
