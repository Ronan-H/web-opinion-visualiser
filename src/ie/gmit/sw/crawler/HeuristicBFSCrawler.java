package ie.gmit.sw.crawler;


import ie.gmit.sw.PageNode;

import java.util.*;

public class HeuristicBFSCrawler extends QueryCrawler {
    public HeuristicBFSCrawler(String query, int maxPageLoads) {
        super(query, maxPageLoads, new RelevanceComparator(query));
    }
}
