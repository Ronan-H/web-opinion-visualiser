package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PageNode {
    private String url;
    private Document pageDoc;
    private PageNode[] children;

    public PageNode(String url) {
        this.url = url;
        children = new PageNode[0];

        try {
            System.out.println("Connecting to URL: " + url);
            pageDoc = Jsoup.connect(url).get();
            Thread.sleep(500);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public PageNode[] getChildren() {
        return children;
    }

    public void findChildren(String query, int searchDepth, int minHeuristic, Set<String> visited) throws IOException {
        if (searchDepth == 0) {
            System.out.println("Bailing out, max search depth exceeded");
            return;
        }

        if (getRelevanceScore(query) < minHeuristic) {
            System.out.println("Bailing out, surpassed minimum heuristic limit");
            return;
        }

        visited.add(getUrl());
        System.out.println("Finding children for url " + url);

        String[] unvisitedLinks = pageDoc.select("a")
                .stream()
                .map(e -> e.absUrl("href"))
                .map(String::toLowerCase)
                .filter(u -> u.startsWith("http"))
                .filter(u -> !visited.contains(u))
                .filter(u -> !URLBlacklist.getInstance().isUrlBlacklisted(u))
                .toArray(String[]::new);

        List<PageNode> childrenList = new ArrayList<>();
        for (String link : unvisitedLinks) {
            if (!visited.contains(link)) {
                PageNode child = new PageNode(link);
                visited.add(child.getUrl());
                childrenList.add(child);
                child.findChildren(query, searchDepth - 1, minHeuristic, visited);
            }
        }

        children = childrenList.toArray(new PageNode[0]);
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
                    System.out.printf("Found query in %s tag on page %s, score added: %d%n", scoringTag, url, tagScore);
                    scoreTotal += tagScore;
                }
            }
        }

        return scoreTotal;
    }
}
