package ie.gmit.sw;

import java.util.Map;

public class WordProximityScorer {
    private static final int[] scoring = {15, 5, 3, 2, 2, 1};
    private Map<String, Integer> wordScores;
    private String query;
    private WordIgnorer wordIgnorer;

    public WordProximityScorer(Map<String, Integer> wordScores, String query, WordIgnorer wordIgnorer) {
        this.wordScores = wordScores;
        this.query = query;
        this.wordIgnorer = wordIgnorer;
    }

    public void addWordScores(String text) {
        addWordScores(text, 1);
    }

    public void addWordScores(String text, int weighting) {
        int queryPos = text.indexOf(query);
        String beforeText = text.substring(0, queryPos).trim();
        String[] beforeParts = (beforeText.equals("") ? new String[0] : beforeText.split(" "));
        String afterText = text.substring(queryPos + query.length()).trim();
        String[] afterParts = (afterText.equals("") ? new String[0] : afterText.split(" "));

        beforeParts = wordIgnorer.getUsefulWords(beforeParts);
        afterParts = wordIgnorer.getUsefulWords(afterParts);

        for (int i = 0; i < Math.min(beforeParts.length, scoring.length); i++) {
            String word = beforeParts[beforeParts.length - i  - 1];
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, (wordScores.get(word) + scoring[i]) * weighting);
        }

        for (int i = 0; i < Math.min(afterParts.length, scoring.length); i++) {
            String word = afterParts[i];
            if (!wordScores.containsKey(word)) {
                wordScores.put(word, 0);
            }
            wordScores.put(word, (wordScores.get(word) + scoring[i]) * weighting);
        }
    }
}
