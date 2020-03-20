package ie.gmit.sw.crawler;

import ie.gmit.sw.PageNode;

import java.util.Comparator;

public class LIFOComparator implements Comparator<PageNode> {
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