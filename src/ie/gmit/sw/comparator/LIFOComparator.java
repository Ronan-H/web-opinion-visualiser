package ie.gmit.sw.comparator;

import ie.gmit.sw.PageNode;

public class LIFOComparator extends PageNodeEvaluator {
    public LIFOComparator(String query) {
        super(query);
    }

    @Override
    public int compare(PageNode a, PageNode b) {
        PageNode aParent = a.getParent();
        PageNode bParent = b.getParent();

        // ensure root pages go to the front of the queue
        if (aParent == null && bParent == null) {
            return Integer.compare(
                    a.getId(),
                    b.getId()
            );
        }

        return -Integer.compare(
                a.getId(),
                b.getId()
        );
    }
}