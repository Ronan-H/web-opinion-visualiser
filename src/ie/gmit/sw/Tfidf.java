package ie.gmit.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tfidf {
    private Map<String, List<Map<String, Integer>>> domainPageScores;

    public Tfidf() {
        domainPageScores = new HashMap<>();
    }

    public synchronized Map<String, Integer> getTerms() {
        Map<String, Double> allTermScores = new HashMap<>();

        for (Map<String, Integer> scores : domainPageScores) {
            for (String term : scores.keySet()) {
                double tfidf = getTf(scores, term) * getIdf(term);
                if (!allTermScores.containsKey(term)) {
                    allTermScores.put(term, 0d);
                }
                allTermScores.put(term, allTermScores.get(term) + tfidf);
            }
        }

        Map<String, Integer> intScores = new HashMap<>();

        for (String word : allTermScores.keySet()) {
            intScores.put(word,
                    (int) Math.round(allTermScores.get(word) * 100));
        }

        return intScores;
    }

    public double getTf(Map<String, Integer> pageTerms, String term) {
        return pageTerms.get(term);
    }

    public double getIdf(String term) {
        int numDocsHaveTerm = 0;

        for (Map<String, Integer> scores : domainPageScores) {
            if (scores.keySet().contains(term)) {
                numDocsHaveTerm++;
            }
        }

        return Math.log((double) domainPageScores.size() / numDocsHaveTerm);
    }

    public double getFjc(String term, String domain) {
        List<Map<String, Integer>> domainScores = domainPageScores.get(domain);
        double freq = 0;

        for (Map<String, Integer> pageScores : domainScores) {
            freq += pageScores.getOrDefault(term, 0);
        }

        return freq;
    }

    public double getNormalizedFjc(String term, String domain) {
        double fjc = getFjc(term, domain);
        List<Map<String, Integer>> domainScores = domainPageScores.get(domain);

        double sum = 0;

        for (Map<String, Integer> pageScores : domainScores) {
            for (String t : pageScores.keySet()) {
                sum += Math.pow(pageScores.get(t), 2);
            }
        }

        return fjc / Math.sqrt(sum);
    }

    public synchronized void addPageScores(String domain, Map<String, Integer> scores) {
        if (!domainPageScores.containsKey(domain)) {
            domainPageScores.put(domain, new ArrayList<>());
        }

        domainPageScores.get(domain).add(scores);
    }
}
