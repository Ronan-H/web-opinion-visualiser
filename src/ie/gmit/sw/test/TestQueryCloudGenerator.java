package ie.gmit.sw.test;

import ie.gmit.sw.QueryCloudGenerator;
import ie.gmit.sw.SearchAlgorithm;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TestQueryCloudGenerator {
    public static void main(String[] args) throws IOException {
        String query = "coronavirus";
        int maxPageLoads = 100;
        int numThreads = 10;
        SearchAlgorithm searchAlgorithm = SearchAlgorithm.BFS_FUZZY_HEURISTIC;

        QueryCloudGenerator cloudGenerator = new QueryCloudGenerator(query, maxPageLoads, numThreads, searchAlgorithm);

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("\nGenerating word cloud at %s...%n", filePath);
        ImageIO.write(cloudGenerator.generateWordCloud(), "PNG", new File(filePath));
        System.out.println("Finished.");
    }
}
