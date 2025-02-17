package ie.gmit.sw.test;

import ie.gmit.sw.term.TermIgnorer;

import java.io.File;

// tests the class that ignores certain unhelpful terms
public class TestWordIgnorer {
    public static void main(String[] args) {
        String text = "this is a test for the term ignorer class";
        String[] parts = text.split(" ");

        File ignoredWords = new File("./res/ignorewords.txt");
        TermIgnorer ignorer = new TermIgnorer(ignoredWords, "query here");

        String[] usefulWords = ignorer.getUsefulTerms(parts);
        for (String word : usefulWords) {
            System.out.println(word);
        }
    }
}
