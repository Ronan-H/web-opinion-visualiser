package ie.gmit.sw.test;

import ie.gmit.sw.QueryCloudGenerator;
import ie.gmit.sw.SearchAlgorithm;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TestQueryCloudGenerator {
    public static void main(String[] args) {
        String query = "speedcubing";
        int maxPageLoads = 100;
        int numThreads = 10;
        SearchAlgorithm searchAlgorithm = SearchAlgorithm.BFS_FUZZY_HEURISTIC;

        QueryCloudGenerator cloudGenerator = new QueryCloudGenerator(query, maxPageLoads, numThreads, searchAlgorithm);

        String filePath = String.format("./clouds/%s.png", query);
        System.out.printf("\nGenerating word cloud at %s...%n", filePath);
        try {
            ImageIO.write(cloudGenerator.generateWordCloud(), "PNG", new File(filePath));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished.");
    }
}
