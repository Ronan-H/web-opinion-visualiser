package ie.gmit.sw;

import java.util.HashMap;
import java.util.Map;

public class WordProximityScorer {
    private static final int[] scoring = {10, 5, 3, 2, 1};
    private String text;
    private String query;
    private WordIgnorer wordIgnorer;

    public WordProximityScorer(String text, String query, WordIgnorer wordIgnorer) {
        this.text = text;
        this.query = query;
        this.wordIgnorer = wordIgnorer;
    }

    public Map<String, Integer> getWordScores() {
        int queryPos = text.indexOf(query);
        String beforeText = text.substring(0, queryPos).trim();
        String[] beforeParts = (beforeText.equals("") ? new String[0] : beforeText.split(" "));
        String afterText = text.substring(queryPos + query.length()).trim();
        String[] afterParts = (afterText.equals("") ? new String[0] : afterText.split(" "));

        beforeParts = wordIgnorer.getUsefulWords(beforeParts);
        afterParts = wordIgnorer.getUsefulWords(afterParts);

        System.out.println("beforeText = " + beforeText);
        System.out.println("afterText = " + afterText);

        for (int i = 0; i < beforeParts.length; i++) {
            System.out.printf("Before %d: %s%n", i, beforeParts[i]);
        }
        for (int i = 0; i < afterParts.length; i++) {
            System.out.printf("After %d: %s%n", i, afterParts[i]);
        }

        Map<String, Integer> wordScores = new HashMap<>();
        for (int i = 0; i < Math.min(beforeParts.length, scoring.length); i++) {
            String word = beforeParts[beforeParts.length - i  - 1];
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, wordScores.get(word) + scoring[i]);
        }

        for (int i = 0; i < Math.min(afterParts.length, scoring.length); i++) {
            String word = afterParts[i];
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, wordScores.get(word) + scoring[i]);
        }

        return wordScores;
    }
}
