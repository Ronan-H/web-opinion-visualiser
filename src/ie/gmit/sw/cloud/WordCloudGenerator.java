package ie.gmit.sw.cloud;

import java.awt.image.BufferedImage;

// generates a word cloud based on a list of term weightings
public class WordCloudGenerator {
    private TermWeight[] words;
    private int width;
    private int height;

    public WordCloudGenerator(TermWeight[] terms, int width, int height) {
        this.words = terms;
        this.width = width;
        this.height = height;
    }

    public BufferedImage generateWordCloud() {
        // place all term on the cloud
        SpiralPlacer placer = new SpiralPlacer(width, height);
        placer.placeAll(words);

        return placer.getImage();
    }
}
