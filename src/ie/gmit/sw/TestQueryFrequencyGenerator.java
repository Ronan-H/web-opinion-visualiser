package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.WordFrequency;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class TestQueryFrequencyGenerator {
    public static void main(String[] args) throws IOException {
        String query = "fishing";

        QueryFrequencyGenerator frequencyGenerator = new QueryFrequencyGenerator();
        WordFrequency[] frequencies = frequencyGenerator.generateWordFrequencies(query);
        Arrays.sort(frequencies, Comparator.comparing(WordFrequency::getFrequency, Comparator.reverseOrder()));

        for (int i = Math.min(frequencies.length - 1, 40); i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %d%n", i, frequencies[i].getWord(), frequencies[i].getFrequency());
        }
    }
}
