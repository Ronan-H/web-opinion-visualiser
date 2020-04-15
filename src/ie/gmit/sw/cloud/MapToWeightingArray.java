package ie.gmit.sw.cloud;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class MapToWeightingArray {
    private Map<String, Double> wordScores;

    public MapToWeightingArray(Map<String, Double> wordScores) {
        this.wordScores = wordScores;
    }

    public TermWeight[] convert(int sizeLimit) {
        // convert to frequency array
        TermWeight[] words = new TermWeight[wordScores.size()];
        int wordIndex = 0;
        for (String word : wordScores.keySet()) {
            words[wordIndex++] = new TermWeight(word, wordScores.get(word));
        }

        // sort (by frequency, highest to lowest)
        Arrays.sort(words, Comparator.comparing(TermWeight::getWeight, Comparator.reverseOrder()));

        return Arrays.copyOfRange(words, 0, Math.min(words.length, sizeLimit));
    }
}
