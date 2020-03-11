package ie.gmit.sw.test;

import ie.gmit.sw.*;
import ie.gmit.sw.ai.cloud.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestQueryCrawler {
    public static void main(String[] args) throws IOException {
        String query = "fishing";

        WordFrequency[] words = new WeightedFont().getFontSizes(
                                new MapToFrequencyArray(
                                new QueryCrawler(25).getCrawlScores(query)).convert(90));

        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %d%n", i, words[i].getWord(), words[i].getFrequency());
        }

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("Generating word cloud at %s...%n", filePath);
        BufferedImage cloud = new WordCloudGenerator(words, 850, 850).generateWordCloud();
        ImageIO.write(cloud, "PNG", new File(filePath));
        System.out.println("Finished");
    }
}
