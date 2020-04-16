package ie.gmit.sw.cloud;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

// converts a Map of terms to their weights to a sorted array of TermWeight objects
public class MapToWeightingArray {
    private Map<String, Double> termWeights;

    public MapToWeightingArray(Map<String, Double> termWeights) {
        this.termWeights = termWeights;
    }

    public TermWeight[] convert(int sizeLimit) {
        // convert to frequency array
        TermWeight[] words = new TermWeight[termWeights.size()];
        int wordIndex = 0;
        for (String word : termWeights.keySet()) {
            words[wordIndex++] = new TermWeight(word, termWeights.get(word));
        }

        // sort (by frequency, highest to lowest)
        Arrays.sort(words, Comparator.comparing(TermWeight::getWeight, Comparator.reverseOrder()));

        return Arrays.copyOfRange(words, 0, Math.min(words.length, sizeLimit));
    }
}
