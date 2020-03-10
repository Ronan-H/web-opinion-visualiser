package ie.gmit.sw.test;

import ie.gmit.sw.*;

import java.util.HashMap;
import java.util.Map;

public class TestWordProximityScorer {
    public static void main(String[] args) {
        String text = "the syntax of java is similar to c and c++";
        String query = "java";

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        Map<String, Integer> scores = new HashMap<>();
        WordProximityScorer scorer = new WordProximityScorer(scores, query);
        scorer.addWordScores(text, ignorer);

        for (String word : scores.keySet()) {
            long score = scores.get(word);
            System.out.printf("%s: %d%n", word, score);
        }
    }
}
