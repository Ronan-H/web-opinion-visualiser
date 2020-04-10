package ie.gmit.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tfidf {
    private List<Map<String, Integer>> pageScores;

    public Tfidf() {
        pageScores = new ArrayList<>();
    }

    public synchronized Map<String, Integer> getTerms() {
        Map<String, Double> allTermScores = new HashMap<>();

        for (Map<String, Integer> scores : pageScores) {
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

        for (Map<String, Integer> scores : pageScores) {
            if (scores.keySet().contains(term)) {
                numDocsHaveTerm++;
            }
        }

        return Math.log((double) pageScores.size() / numDocsHaveTerm);
    }

    public synchronized void addPageScores(Map<String, Integer> scores) {
        pageScores.add(scores);
    }
}
