package ie.gmit.sw.crawler;

public class HeuristicDFSCrawler extends QueryCrawler {
    public HeuristicDFSCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads, new LIFOComparator());
    }
}
