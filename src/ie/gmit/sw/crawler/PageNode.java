package ie.gmit.sw.crawler;

import ie.gmit.sw.term.TermProximityCounter;
import ie.gmit.sw.term.TfpdfCalculator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    private TagWeights tagWeights;

    public PageNode(String url, PageNode parent) {
        this.url = url;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.getDepth() + 1;
        this.rootUrl = getURLRoot(url);

        // set page ID
        synchronized (this) {
            id = idCounter++;
        }

        tagWeights = TagWeights.getInstance();
    }

    public PageNode(String url) {
        this(url, null);
    }

    // load page, i.e. download it using JSoup
    public void load() {
        try {
            pageDoc = Jsoup.connect(url).timeout(2500).get();
        } catch (IOException e) {
            // something went wrong
            errored = true;
        }

        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    // get a list of links from this page that haven't been visited elsewhere yet
    public List<String> getUnvisitedLinks(Set<String> visited) {
        List<String> unvisited = new ArrayList<>();
        if (errored) {
            // no links to go to if there was an error loading this page
            return unvisited;
        }

        // select all links on the page
        Elements links = pageDoc.select("a");
        URLFilter urlFilter = URLFilter.getInstance();
        for (Element link : links) {
            String href = link.absUrl("href").toLowerCase();
            String root = getURLRoot(href);

            if (!visited.contains(root) // unvisited
            && urlFilter.isURLAllowed(href)) { // URL matches allowed criteria
                // add link to list
                unvisited.add(href);
            }
        }

        return unvisited;
    }

    // page relevancy score based on the query,
    // where 0 means the query is not on the page at all,
    // and 1 means the page's text consisted entirely of the query
    public double getRelevanceScore(String query) {
        if (errored) return -1; // Relevance n/a
        if (relevanceComputed) return relevanceScore; // return cached relevance value

        int queryScore = 0;
        double totalScore = 0;
        Elements elems;
        String elemText;
        int tagWeight;

        // for each HTML tag being considered
        for (String scoringTag : tagWeights.getScoringTags()) {
            // get tag weighting
            tagWeight = tagWeights.getScoreFor(scoringTag);
            // find all elements for that tag
            elems = pageDoc.select(scoringTag);
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();
                // add up query score and total term score
                queryScore += numOccurrencesInString(elemText, query) * tagWeight;
                totalScore += ((double) elemText.length() / query.length()) * tagWeight;
            }
        }

        if (totalScore > 0) {
            // return relative query relevance score
            relevanceScore = queryScore / totalScore;
        }
        else {
            // no text (avoiding division by 0)
            relevanceScore = 0;
        }

        relevanceComputed = true;
        return relevanceScore;
    }

    // get the count of a substring in a string
    public static int numOccurrencesInString(String s, String substr) {
        int count = 0;

        for (int i = 0; i <= s.length() - substr.length(); i++) {
            if (s.substring(i, i + substr.length()).equals(substr)) {
                count++;
                i += substr.length();
            }
        }

        return count;
    }

    // add this page's term weights to the tfpdf calculator
    public void addTermWeights(String query, TfpdfCalculator tfpdfCalculator, TermProximityCounter counter) {
        if (errored) return; // nothing to do for an errored page

        Map<String, Integer> termScores = new HashMap<>();
        Elements elems;
        String elemText;

        // for each HTML tag being considered// for each H
        for (String tag : tagWeights.getScoringTags()) {
            // find all elements of that tag
            elems = pageDoc.select(tag);
            // ƒor each element...
            for (Element elem : elems) {
                elemText = elem.text().toLowerCase();

                // keep scoring words around the first query string found, and removing that query string
                // (there may be more than one in a given element)
                while (elemText.contains(query)) {
                    // get number of occurrences of each word in proximity
                    Map<String, Integer> counts = counter.getTermCounts(elemText);

                    // add each term occurrence to a map
                    for (String k : counts.keySet()) {
                        if (!termScores.containsKey(k)) {
                            termScores.put(k, 0);
                        }
                        termScores.put(k, termScores.get(k) + counts.get(k));
                    }

                    // remove the occurrence of the query just used
                    elemText = elemText.replaceFirst(query, "");
                }
            }
        }

        // add term counts to calculator
        tfpdfCalculator.addTermCounts(rootUrl, termScores);
    }

    public int getId() {
        return id;
    }

    // find "root" of URL; stops links to elements on the same page from being considered different pages
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
