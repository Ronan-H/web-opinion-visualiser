package ie.gmit.sw;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator implements Comparator {
    private Random random;

    public RandomComparator() {
        this.random = new Random();
    }

    @Override
    public int compare(Object a, Object b) {
        return random.nextBoolean() ? 1 : -1;
    }
}
