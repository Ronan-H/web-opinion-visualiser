package ie.gmit.sw.test;

import ie.gmit.sw.crawler.QueryCloudGenerator;
import ie.gmit.sw.crawler.SearchAlgorithm;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TestQueryCloudGenerator {
    public static void main(String[] args) {
        String query = "fishing";
        int maxPageLoads = 100;
        int numThreads = 10;
        int numCloudWords = 60;
        SearchAlgorithm searchAlgorithm = SearchAlgorithm.BFS_FUZZY_HEURISTIC;
        File ignoredWords = new File("./res/ignorewords.txt");

        QueryCloudGenerator cloudGenerator = new QueryCloudGenerator(
                query, maxPageLoads, numThreads, numCloudWords, searchAlgorithm, ignoredWords
        );

        File cloudsDir = new File ("./clouds/");
        cloudsDir.mkdir();

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("\nGenerating word cloud at %s...%n", filePath);
        try {
            ImageIO.write(cloudGenerator.generateWordCloud(), "PNG", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished.");
    }
}
