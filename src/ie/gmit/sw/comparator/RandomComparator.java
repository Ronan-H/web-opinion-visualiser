package ie.gmit.sw.comparator;

import ie.gmit.sw.PageNode;

import java.util.Random;

// random comparator; randomly decides an ordering for a given two PageNodes
public class RandomComparator extends PageNodeEvaluator {
    private Random random;

    public RandomComparator(String query) {
        super(query);
        this.random = new Random();
    }

    @Override
    public int compare(PageNode a, PageNode b) {
        // randomly decide which page should come first in the queue
        return random.nextBoolean() ? 1 : -1;
    }
}
