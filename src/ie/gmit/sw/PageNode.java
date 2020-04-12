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
            pageDoc = Jsoup.connect(url).timeout(5 * 1000).get();
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
            && !URLBlacklist.getInstance().isUrlBlacklisted(href) // not blacklisted
            && link.parent().tagName().equals("p") || link.parent().parent().tagName().equals("p")) { // in a paragraph
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
        int queryScore = 0;
        double totalScore = 0;
        Elements elems;
        String elemText;
        int tagScore;

        for (String scoringTag : tagWeights.getScoringTags()) {
            tagScore = tagWeights.getScoreFor(scoringTag);
            elems = pageDoc.select(scoringTag);
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();
                queryScore += numOccurencesInString(elemText, query) * tagScore;
                totalScore += ((double) elemText.length() / query.length()) * tagScore;
            }
        }

        if (totalScore > 0) {
            relevanceScore = queryScore / totalScore;
        }
        else {
            relevanceScore = 0;
        }

        relevanceComputed = true;
        return relevanceScore;
    }

    public static int numOccurencesInString(String s, String query) {
        int count = 0;

        for (int i = 0; i <= s.length() - query.length(); i++) {
            if (s.substring(i, i + query.length()).equals(query)) {
                count++;
                i += query.length();
            }
        }

        return count;
    }

    public void addWordScores(String query, TfpdfCalculator tfpdfCalculator, WordProximityScorer scorer, WordIgnorer ignorer) {
        if (errored) return;

        Map<String, Integer> termScores = new HashMap<>();
        Elements elems;
        String elemText;

        elems = pageDoc.select("p");
        for (Element elem : elems) {
            elemText = elem.text().toLowerCase();

            while (elemText.contains(query)) {
                Map<String, Integer> scores = scorer.getWordScores(elemText, ignorer, 1);

                for (String k : scores.keySet()) {
                    if (!termScores.containsKey(k)) {
                        termScores.put(k, 0);
                    }
                    termScores.put(k, termScores.get(k) + scores.get(k));
                }
                elemText = elemText.replaceFirst(query, "");
            }
        }

        tfpdfCalculator.addPageScores(rootUrl, termScores);
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

    public PageNode getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }
}
