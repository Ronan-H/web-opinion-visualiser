package ie.gmit.sw;

import ie.gmit.sw.ai.cloud.SpiralPlacer;
import ie.gmit.sw.ai.cloud.TermWeight;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

public class WordCloudGenerator {
    private TermWeight[] words;
    private int width;
    private int height;

    public WordCloudGenerator(TermWeight[] words, int width, int height) {
        this.words = words;
        this.width = width;
        this.height = height;
    }

    public BufferedImage generateWordCloud() {
        Arrays.sort(words, Comparator.comparing(TermWeight::getWeight, Comparator.reverseOrder()));

        SpiralPlacer placer = new SpiralPlacer(width, height);
        placer.placeAll(words);

        return placer.getImage();
    }
}
