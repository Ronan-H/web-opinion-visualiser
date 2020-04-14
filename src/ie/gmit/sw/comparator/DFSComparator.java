package ie.gmit.sw.comparator;

import ie.gmit.sw.PageNode;

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
}