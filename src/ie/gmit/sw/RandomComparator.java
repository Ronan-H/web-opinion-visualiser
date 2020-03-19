package ie.gmit.sw;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator implements Comparator<PageNode> {
    private Random random;

    public RandomComparator() {
        this.random = new Random();
    }

    @Override
    public int compare(PageNode nodeA, PageNode nodeB) {
        return random.nextBoolean() ? 1 : -1;
    }
}
