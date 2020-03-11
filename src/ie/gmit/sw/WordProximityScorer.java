package ie.gmit.sw;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Map;

public class WordProximityScorer {
    private static final Integer[] beforeScoring = {2, 1, 1, 1, 1};
    private static final Integer[] afterScoring = {3, 2, 2, 1, 1, 1, 1, 1};
    private Map<String, Integer> wordScores;
    private String query;

    public WordProximityScorer(Map<String, Integer> wordScores, String query) {
        this.wordScores = wordScores;
        this.query = query;
    }

    public void addWordScores(String text, WordIgnorer ignorer) {
        addWordScores(text, ignorer, 1);
    }

    public void addWordScores(String text, WordIgnorer ignorer, int weighting) {
        int queryPos = text.indexOf(query);
        String beforeText = text.substring(0, queryPos).trim();
        String[] beforeParts = (beforeText.equals("") ? new String[0] : beforeText.split(" "));
        String afterText = text.substring(queryPos + query.length()).trim();
        String[] afterParts = (afterText.equals("") ? new String[0] : afterText.split(" "));

        Deque<Integer> scoreQueue = new ArrayDeque<>(Arrays.asList(beforeScoring));
        for (int i = 0; i < beforeParts.length && !scoreQueue.isEmpty(); i++) {
            String word = beforeParts[beforeParts.length - i  - 1];
            if (ignorer.isIgnored(word)) {
                continue;
            }
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, wordScores.get(word) + (scoreQueue.poll() * weighting));
        }

        scoreQueue = new ArrayDeque<>(Arrays.asList(afterScoring));
        for (int i = 0; i < afterParts.length && !scoreQueue.isEmpty(); i++) {
            String word = afterParts[i];
            if (ignorer.isIgnored(word)) {
                continue;
            }
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, wordScores.get(word) + (scoreQueue.poll() * weighting));
        }
    }
}
