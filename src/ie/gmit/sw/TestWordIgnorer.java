package ie.gmit.sw;

public class TestWordIgnorer {
    public static void main(String[] args) {
        String text = "this is a test for the word ignorer class";
        String[] parts = text.split(" ");
        WordIgnorer ignorer = new WordIgnorer("./res/ignorewords.txt");
        String[] usefulWords = ignorer.getUsefulWords(parts);
        for (String word : usefulWords) {
            System.out.println(word);
        }
    }
}
