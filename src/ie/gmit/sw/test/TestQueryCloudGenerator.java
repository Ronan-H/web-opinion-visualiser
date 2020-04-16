package ie.gmit.sw.test;

import ie.gmit.sw.crawler.QueryCloudGenerator;
import ie.gmit.sw.crawler.SearchAlgorithm;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

// tests query cloud generation (saves the cloud to an image file)
public class TestQueryCloudGenerator {
    public static void main(String[] args) {
        // search parameters
        String query = "coronavirus";
        int maxPageLoads = 250;
        int numThreads = 25;
        int numCloudWords = 80;
        SearchAlgorithm searchAlgorithm = SearchAlgorithm.BFS_FUZZY_HEURISTIC;

        File ignoredWords = new File("./res/ignorewords.txt");
        File fclFile = new File("./res/page-scoring.fcl");

        QueryCloudGenerator cloudGenerator = new QueryCloudGenerator(
                query, maxPageLoads, numThreads, numCloudWords, searchAlgorithm, ignoredWords, fclFile
        );

        // save word cloud to file
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
