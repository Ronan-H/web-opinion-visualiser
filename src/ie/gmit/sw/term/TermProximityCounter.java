package ie.gmit.sw.term;

import java.util.*;

// counts up occurrences of terms that are near the search query
public class TermProximityCounter {
    // maximum number of words to count before and after the query
    private static final int maxCountBefore = 4;
    private static final int maxCountAfter = 10;
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

        // make arrays of individual terms found before and after, ignoring "stop words"
        String[] beforeParts = ignorer.getUsefulTerms(
                beforeText.equals("") ? new String[0] : beforeText.split(" ")
        );
        String[] afterParts = ignorer.getUsefulTerms(
                afterText.equals("") ? new String[0] : afterText.split(" ")
        );

        // count term occurrences before the query
        for (int i = 0; i < Math.min(beforeParts.length, maxCountBefore); i++) {
            countTerm(wordScores, beforeParts[beforeParts.length - i  - 1]);
        }

        // count term occurrences after the query
        for (int i = 0; i < Math.min(afterParts.length, maxCountAfter); i++) {
            countTerm(wordScores, afterParts[i]);
        }

        return wordScores;
    }

    private void countTerm(Map<String, Integer> termCounts, String term) {
        termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
    }
}
