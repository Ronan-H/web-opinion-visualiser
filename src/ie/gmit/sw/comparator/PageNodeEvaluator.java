package ie.gmit.sw.comparator;

import ie.gmit.sw.crawler.PageNode;

import java.util.Comparator;

// abstract PageNode evaluator
// used to compare PageNodes, and decide how many children do expand from a page, based on some heuristic
public abstract class PageNodeEvaluator implements Comparator<PageNode> {
    protected String query;

    public PageNodeEvaluator(String query) {
        this.query = query;
    }

    // heuristic number of children to expand form this node
    public int numChildExpandHeuristic(PageNode node) {
        double nodeRelevancy = node.getRelevanceScore(query);

        if (nodeRelevancy <= 0) {
            // no query strings on this page, don't expand any child nodes
            return 0;
        }

        return Math.min((int)Math.ceil(nodeRelevancy * 100), 10);
    }

    // number of search results to use from DuckDuckGo
    public int numSearchResultsToUse() {
        return 10;
    }
}
