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
        double expandFraction = childExpandFraction(node);
        int numExpand = (int)Math.round(expandFraction * 15);

        if (node.getParent() == null) {
            // root node; assume search results are quite relevant, regardless of heuristic score
            return Math.max(numExpand, 5);
        }

        if (node.getRelevanceScore(query) <= 0) {
            // no query strings on this page, don't expand any child nodes
            return 0;
        }

        // expand a number of child nodes according to the childExpandFraction
        return numExpand;
    }

    // fraction of the max number of child nodes to expand
    // (subclasses can override this)
    protected double childExpandFraction(PageNode node) {
        // default: use page relevancy
        return Math.min(node.getRelevanceScore(query) * 50, 1);
    }

    // number of search results to use from DuckDuckGo
    public int numSearchResultsToUse() {
        return 5;
    }
}
