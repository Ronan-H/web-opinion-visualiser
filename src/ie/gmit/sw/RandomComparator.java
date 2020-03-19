package ie.gmit.sw;

import java.util.Comparator;
import java.util.Random;

public class RandomComparator<T> implements Comparator<T> {
    private Random random;

    public RandomComparator() {
        this.random = new Random();
    }

    @Override
    public int compare(T a, T b) {
        return random.nextBoolean() ? 1 : -1;
    }
}
