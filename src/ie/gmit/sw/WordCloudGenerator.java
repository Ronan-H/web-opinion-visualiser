package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.LogarithmicSpiralPlacer;
import ie.gmit.sw.ai.cloud.WordFrequency;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class WordCloudGenerator {
    private WordFrequency[] words;
    private int width;
    private int height;
    private int maxWords;

    public WordCloudGenerator(WordFrequency[] words, int width, int height, int maxWords) {
        this.words = words;
        this.width = width;
        this.height = height;
        this.maxWords = maxWords;
    }

    public BufferedImage generateWordCloud() {
        Arrays.sort(words, Comparator.comparing(WordFrequency::getFrequency, Comparator.reverseOrder()));

        LogarithmicSpiralPlacer placer = new LogarithmicSpiralPlacer(width, height);
        for (int i = 0; i < Math.min(words.length, maxWords); i++) {
            placer.place(words[i]); //Place each word on the canvas starting with the largest
        }

        return placer.getImage(); //Get a handle on the word cloud graphic
    }
}
