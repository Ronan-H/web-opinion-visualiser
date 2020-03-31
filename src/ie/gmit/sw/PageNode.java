package ie.gmit.sw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class PageNode {
    private static int idCounter = 0;

    private int id;
    private boolean isLoaded;
    private String url;
    private String rootUrl;
    private PageNode parent;
    private int depth;
    private Document pageDoc;
    private boolean errored;
    private double relevanceScore;
    private boolean relevanceComputed;
    private static final String[] IGNORE_ENDS = {".jpg", ".jpeg", ".png", ".gif", ".mp3", ".mp4", ".pdf"};

    public PageNode(String url, PageNode parent) {
        this.url = url;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.getDepth() + 1;
        this.rootUrl = getURLRoot(url);
        id = idCounter++;
    }

    public PageNode(String url) {
        this(url, null);
    }


    public void load() {
        try {
            System.out.println("Connecting to URL: " + url);
            pageDoc = Jsoup.connect(url).timeout(3 * 1000).get();
            Thread.sleep(1000);
        } catch (Exception e) {
            errored = true;
        }

        if (pageDoc == null) {
            errored = true;
        }

        isLoaded = true;
    }

    public List<String> getUnvisitedLinks(Set<String> visited) {
        List<String> unvisited = new ArrayList<>();
        if (errored) {
            return unvisited;
        }

        Elements links = pageDoc.select("a");
        linkLoop:
        for (Element link : links) {
            String href = link.absUrl("href").toLowerCase();
            String root = getURLRoot(href);

            if (href.startsWith("http") // http/https link
            && !visited.contains(root) // unvisited
            && !URLBlacklist.getInstance().isUrlBlacklisted(href)) { // not blacklisted
                // doesn't end with a common media file extension
                for (String end : IGNORE_ENDS) {
                    if (href.endsWith(end)) {
                        continue linkLoop;
                    }
                }

                unvisited.add(href);
            }
        }

        return unvisited;
/*
        return pageDoc.select("a")
                .stream()
                .map(e -> e.absUrl("href"))
                .map(String::toLowerCase)
                .filter(u -> u.startsWith("http"))
                .filter(u -> !visited.contains(u))
                .filter(u -> !URLBlacklist.getInstance().isUrlBlacklisted(u))
                .collect(Collectors.toList());
*/
    }

    public double getRelevanceScore(String query) {
        if (errored) return -1;
        if (relevanceComputed) return relevanceScore;

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

        if (totalTextLength > 0) {
            relevanceScore = scoreTotal * ((double) (totalOccurences * query.length()) / totalTextLength);
        }
        else {
            relevanceScore = -1;
        }

        relevanceComputed = true;
        return relevanceScore;
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
                    scorer.addWordScores(elemText, ignorer, (int)Math.ceil(tagScore / 2.0));
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    private static String getURLRoot(String url) {
        String[] urlParts = url.split("#");
        return (urlParts.length == 0 ? url : urlParts[0]);
    }

    public String getUrl() {
        return url;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public PageNode getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }
}
