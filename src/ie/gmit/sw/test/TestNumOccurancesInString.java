package ie.gmit.sw.test;

import ie.gmit.sw.crawler.PageNode;

public class TestNumOccurancesInString {
    public static void main(String[] args) {
        String[][] tests = {
                {"no occurences here", "nope"},
                {"this is a test", "is"},
                {"this is a test", "this"},
                {"this is a test", "test"},
                {"hello hello test hello", "hello"}
        };

        for (String[] test : tests) {
            int count = PageNode.numOccurencesInString(test[0], test[1]);
            System.out.printf("Text: %s%n", test[0]);
            System.out.printf("Query: %s%n", test[1]);
            System.out.printf("Occurrences: %s%n%n", count);
        }
    }
}
