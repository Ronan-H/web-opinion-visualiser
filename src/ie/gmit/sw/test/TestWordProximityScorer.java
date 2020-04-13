package ie.gmit.sw.test;

import ie.gmit.sw.*;

import java.util.Map;

public class TestWordProximityScorer {
    public static void main(String[] args) {
        String text = "Most people who catch coronavirus will experience mild symptoms. They should make a full recovery without needing to go to hospital.";
        String query = "coronavirus";

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        WordProximityScorer scorer = new WordProximityScorer(query);

        Map<String, Integer> scores = scorer.getWordScores(text, ignorer);
        for (String word : scores.keySet()) {
            long score = scores.get(word);
            System.out.printf("%s: %d%n", word, score);
        }

    }
}
