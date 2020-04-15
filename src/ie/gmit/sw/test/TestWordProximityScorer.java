package ie.gmit.sw.test;

import ie.gmit.sw.term.TermIgnorer;
import ie.gmit.sw.term.TermProximityCounter;

import java.io.File;
import java.util.Map;

public class TestWordProximityScorer {
    public static void main(String[] args) {
        String text = "Most people who catch coronavirus will experience mild symptoms. They should make a full recovery without needing to go to hospital.";
        String query = "coronavirus";

        File ignoredWords = new File("./res/ignorewords.txt");
        TermIgnorer ignorer = new TermIgnorer(ignoredWords, query);
        TermProximityCounter scorer = new TermProximityCounter(query, ignorer);

        Map<String, Integer> scores = scorer.getTermCounts(text);
        for (String word : scores.keySet()) {
            long score = scores.get(word);
            System.out.printf("%s: %d%n", word, score);
        }

    }
}
