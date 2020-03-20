package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;

import java.util.Comparator;

public class RelevanceComparator implements Comparator<PageNode> {
    private String query;
    private LIFOComparator lifoComparator;

    public RelevanceComparator(String query) {
        this.query = query;

        lifoComparator = new LIFOComparator();
    }

    @Override
    public int compare(PageNode a, PageNode b) {
        PageNode aParent = a.getParent();
        PageNode bParent = b.getParent();

        // ensure root pages go to the front of the queue
        if (aParent == null && bParent == null) {
            return -lifoComparator.compare(a, b);
        }

        if (aParent == null) {
            return -Integer.MAX_VALUE;
        }

        if (bParent == null) {
            return Integer.MAX_VALUE;
        }

        return -Double.compare(
                aParent.getRelevanceScore(query),
                bParent.getRelevanceScore(query)
        );
    }
}
