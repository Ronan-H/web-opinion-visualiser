package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;

import java.util.List;

public class RandomCrawler extends QueryCrawler {
    public RandomCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads, new RandomComparator<>());
    }
}
