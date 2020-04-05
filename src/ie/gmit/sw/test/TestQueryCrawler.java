package ie.gmit.sw.test;

import ie.gmit.sw.*;
import ie.gmit.sw.ai.cloud.*;
import ie.gmit.sw.crawler.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class TestQueryCrawler {
    public static void main(String[] args) throws IOException {
        String query = "coronavirus";
        int numThreads = 12;

        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt", query);
        DomainFrequency domainFrequency = new DomainFrequency();
        PriorityBlockingQueue<PageNode> queue = new PriorityBlockingQueue<>(100, new FuzzyScoreComparator(query, domainFrequency));

        Set<String> visited = ConcurrentHashMap.newKeySet();
        List<PageNode> resultPages =
                Arrays.stream(new SearchEngineScraper().getResultLinks(query))
                        .map(PageNode::new)
                        .collect(Collectors.toList());
        queue.addAll(resultPages);

        QueryCrawler[] crawlers = new QueryCrawler[numThreads];

        // create crawlers
        for (int i = 0; i < crawlers.length; i++) {
            crawlers[i] = new QueryCrawler(query, 100, queue, ignorer, domainFrequency, visited, new FuzzyScoreComparator(query, domainFrequency));
        }

        // start crawlers on new threads
        System.out.printf("Starting web crawl for query \"%s\"...%n%n", query);
        for (int i = 0; i < crawlers.length; i++) {
            new Thread(crawlers[i]).start();
        }

        // wait for all crawlers to finish
        for (int i = 0; i < crawlers.length; i++) {
            try {
                new Thread(crawlers[i]).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> combinedScores = new HashMap<>();
        int combinedScore;
        for (int i = 0; i < crawlers.length; i++) {
            Map<String, Integer> crawlScores = crawlers[i].getCrawlScores();
            // combine scores
            for (String k : crawlScores.keySet()) {
                if (!combinedScores.containsKey(k)) {
                    combinedScores.put(k, 0);
                }
                combinedScore = combinedScores.get(k) + crawlScores.get(k);
                combinedScores.put(k, combinedScore);
            }
        }

        WordFrequency[] words = new WeightedFont().getFontSizes(
                new MapToFrequencyArray(combinedScores).convert(60));

        // normalize in some way?
        /*
        for (int i = 0; i < words.length; i++) {
            words[i].setFrequency(words[i].getFrequency() * (words.length - i));
        }
        */

        System.out.println("\n-- Word frequencies --");
        for (int i = words.length - 1; i >= 0; i--) {
            System.out.printf("Word %d: %15s - Score: %d%n", i, words[i].getWord(), words[i].getFrequency());
        }

        System.out.println("\n-- Domain frequencies --");
        WordFrequency[] domainFreqs = new MapToFrequencyArray(domainFrequency.getVisitMap()).convert(25);
        for (int i = domainFreqs.length - 1; i >= 0; i--) {
            System.out.printf("Domain %d: %15s - Freq: %d%n", i, domainFreqs[i].getWord(), domainFreqs[i].getFrequency());
        }

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("\nGenerating word cloud at %s...%n", filePath);
        BufferedImage cloud = new WordCloudGenerator(words, 850, 850).generateWordCloud();
        ImageIO.write(cloud, "PNG", new File(filePath));
        System.out.println("Finished.");
    }
}
