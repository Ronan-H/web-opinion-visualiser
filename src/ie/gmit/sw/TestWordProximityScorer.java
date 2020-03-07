package ie.gmit.sw;

import java.util.Map;

public class TestWordProximityScorer {
    public static void main(String[] args) {
        WordProximityScorer scorer = new WordProximityScorer("The syntax of java is similar to C and C++", "java");
        Map<String, Integer> scores = scorer.getWordScores();

        for (String word : scores.keySet()) {
            int score = scores.get(word);
            System.out.printf("%s: %d%n", word, score);
        }
    }
}
