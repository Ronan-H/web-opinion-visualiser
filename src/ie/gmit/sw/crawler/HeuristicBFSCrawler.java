package ie.gmit.sw.crawler;

public class HeuristicBFSCrawler extends QueryCrawler {
    public HeuristicBFSCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads, new FuzzyScoreComparator(query));
    }
}
