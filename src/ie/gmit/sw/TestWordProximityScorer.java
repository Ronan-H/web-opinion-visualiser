package ie.gmit.sw;

import java.util.Map;

public class TestWordProximityScorer {
    public static void main(String[] args) {
        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt");
        String text = "the syntax of java is similar to c and c++";
        String query = "java";
        WordProximityScorer scorer = new WordProximityScorer(text, query, ignorer);
        Map<String, Integer> scores = scorer.getWordScores();

        for (String word : scores.keySet()) {
            int score = scores.get(word);
            System.out.printf("%s: %d%n", word, score);
        }
    }
}
