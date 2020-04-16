package ie.gmit.sw.comparator;

import ie.gmit.sw.crawler.PageNode;

// page comparator for depth first search; "deeper" nodes go to the front of the queue
public class DFSComparator extends PageNodeEvaluator {
    public DFSComparator(String query) {
        super(query);
    }

    @Override
    public int compare(PageNode a, PageNode b) {
        return -Integer.compare(
                a.getDepth(),
                b.getDepth()
        );
    }

    // limit DuckDuckGo search results to force a deeper search
    @Override
    public int numSearchResultsToUse() {
        return 2;
    }
}