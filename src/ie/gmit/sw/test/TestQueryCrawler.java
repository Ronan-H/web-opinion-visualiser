package ie.gmit.sw.test;

import ie.gmit.sw.*;
import ie.gmit.sw.ai.cloud.*;
import ie.gmit.sw.crawler.HeuristicBFSCrawler;
import ie.gmit.sw.crawler.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestQueryCrawler {
    public static void main(String[] args) throws IOException {
        String query = "eve online";

        QueryCrawler crawler = new HeuristicBFSCrawler(query, 25);
        WordFrequency[] words = new WeightedFont().getFontSizes(
                                new MapToFrequencyArray(
                                crawler.getCrawlScores()).convert(80));

        System.out.println("\n-- Word frequencies --");
        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %d%n", i, words[i].getWord(), words[i].getFrequency());
        }

        System.out.println("\n-- Domain frequencies --");
        WordFrequency[] domainFreqs = new MapToFrequencyArray(crawler.getDomainFrequencies().getVisitMap()).convert(25);
        for (int i = domainFreqs.length - 1; i >= 0; i--) {
            System.out.printf("Domain %d: %15s - Freq: %d%n", i, domainFreqs[i].getWord(), domainFreqs[i].getFrequency());
        }

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("\nGenerating word cloud at %s...%n", filePath);
        BufferedImage cloud = new WordCloudGenerator(words, 850, 850).generateWordCloud();
        ImageIO.write(cloud, "PNG", new File(filePath));
        System.out.println("Finished");
    }
}
