package ie.gmit.sw.comparator;

import ie.gmit.sw.PageNode;

import java.util.Random;

public class RandomComparator extends PageNodeEvaluator {
    private Random random;

    public RandomComparator(String query) {
        super(query);
        this.random = new Random();
    }

    @Override
    public int compare(PageNode a, PageNode b) {
        return random.nextBoolean() ? 1 : -1;
    }
}
