package ie.gmit.sw.term;

import java.util.*;

// counts up occurrences of terms that are near the search query
public class TermProximityCounter {
    // maximum number of words to count before and after the query
    private static final int maxCountBefore = 3;
    private static final int maxCountAfter = 7;
    private String query;
    private TermIgnorer ignorer;

    public TermProximityCounter(String query, TermIgnorer ignorer) {
        this.query = query;
        this.ignorer = ignorer;
    }

    // get occurrence count mapping of terms around the query
    public Map<String, Integer> getTermCounts(String text) {
        Map<String, Integer> wordScores = new HashMap<>();

        // split text before and after the query
        int queryPos = text.indexOf(query);
        String beforeText = text.substring(0, queryPos).trim();
        String afterText = text.substring(queryPos + query.length()).trim();
        // make arrays of individual terms found before and after
        String[] beforeParts = (beforeText.equals("") ? new String[0] : beforeText.split(" "));
        String[] afterParts = (afterText.equals("") ? new String[0] : afterText.split(" "));

        // count term occurrences before the query
        for (int i = 0; i < Math.min(beforeParts.length, maxCountBefore); i++) {
            countTerms(wordScores, beforeParts[beforeParts.length - i  - 1]);
        }

        // count term occurrences after the query
        for (int i = 0; i < Math.min(afterParts.length, maxCountAfter); i++) {
            countTerms(wordScores, afterParts[i]);
        }

        return wordScores;
    }

    private void countTerms(Map<String, Integer> termCounts, String term) {
        // exclude terms that should be ignored
        if (!ignorer.isIgnored(term)) {
            if (!termCounts.containsKey(term)) {
                termCounts.put(term, 0);
            }
            termCounts.put(term, termCounts.get(term) + 1);
        }
    }
}
