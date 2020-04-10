package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.WordFrequency;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MapToFrequencyArray {
    private Map<String, Integer> wordScores;

    public MapToFrequencyArray(Map<String, Integer> wordScores) {
        this.wordScores = wordScores;
    }

    public MapToFrequencyArray(Map<String, Double> wordScores, boolean fp) {
        Map<String, Integer> intScores = new HashMap<>();

        for (String word : wordScores.keySet()) {
            intScores.put(word, (int) Math.round(wordScores.get(word) * 100));
        }

        this.wordScores = intScores;
    }

    public WordFrequency[] convert(int sizeLimit) {
        // convert to frequency array
        WordFrequency[] words = new WordFrequency[wordScores.size()];
        int wordIndex = 0;
        for (String word : wordScores.keySet()) {
            words[wordIndex++] = new WordFrequency(word, wordScores.get(word));
        }

        // sort (by frequency, highest to lowest)
        Arrays.sort(words, Comparator.comparing(WordFrequency::getFrequency, Comparator.reverseOrder()));

        return Arrays.copyOfRange(words, 0, Math.min(words.length, sizeLimit));
    }
}
