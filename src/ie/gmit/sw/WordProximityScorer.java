package ie.gmit.sw;

import java.util.*;

public class WordProximityScorer {
    private static final int maxScoreBefore = 3;
    private static final int maxScoreAfter = 9;
    private String query;
    private WordIgnorer ignorer;

    public WordProximityScorer(String query, WordIgnorer ignorer) {
        this.query = query;
        this.ignorer = ignorer;
    }

    public Map<String, Integer> getWordScores(String text) {
        Map<String, Integer> wordScores = new HashMap<>();

        int queryPos = text.indexOf(query);
        String beforeText = text.substring(0, queryPos).trim();
        String[] beforeParts = (beforeText.equals("") ? new String[0] : beforeText.split(" "));
        String afterText = text.substring(queryPos + query.length()).trim();
        String[] afterParts = (afterText.equals("") ? new String[0] : afterText.split(" "));

        for (int i = 0; i < Math.min(beforeParts.length, maxScoreBefore); i++) {
            scoreWord(wordScores, beforeParts[beforeParts.length - i  - 1]);
        }

        for (int i = 0; i < Math.min(afterParts.length, maxScoreAfter); i++) {
            scoreWord(wordScores, afterParts[i]);
        }

        return wordScores;
    }

    private void scoreWord(Map<String, Integer> wordScores, String word) {
        if (!ignorer.isIgnored(word)) {
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, wordScores.get(word) + 1);
        }
    }
}
