package ie.gmit.sw;

import java.util.Comparator;

public class RelevanceComparator implements Comparator<PageNode> {
    private String query;

    public RelevanceComparator(String query) {
        this.query = query;
    }

    @Override
    public int compare(PageNode nodeA, PageNode nodeB) {
        return -Double.compare(nodeA.getRelevanceScore(query), nodeB.getRelevanceScore(query));
    }
}
