package ie.gmit.sw.comparator;

import ie.gmit.sw.PageNode;

import java.util.Comparator;

public abstract class PageNodeEvaluator implements Comparator<PageNode> {
    protected String query;

    public PageNodeEvaluator(String query) {
        this.query = query;
    }

    public int numChildExpandHeuristic(PageNode node) {
        double nodeRelevancy = node.getRelevanceScore(query);

        if (nodeRelevancy <= 0) {
            return 0;
        }

        return (int)Math.ceil(nodeRelevancy * 10);
    }
}
