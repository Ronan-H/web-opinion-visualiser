package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class WordIgnorer {
    private Set<String> ignoredSet;
    private String query;
    private Pattern wordPattern;

    public WordIgnorer(String ignoredPath, String query) {
        this.query = query;
        ignoredSet = new HashSet<>();

        try {
            BufferedReader ignoreIn = new BufferedReader(new FileReader(ignoredPath));
            String word;
            while ((word = ignoreIn.readLine()) != null) {
                ignoredSet.add(word);
            }
            ignoreIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        wordPattern = Pattern.compile("[a-z]+(-[a-z])*");
    }

    public WordIgnorer() {
        ignoredSet = new HashSet<>();
    }

    public String[] getUsefulWords(String[] input) {
        List<String> usefulWords = new ArrayList<>(input.length);
        for (String word : input) {
            if (!isIgnored(word)) {
                usefulWords.add(word);
            }
        }

        return usefulWords.toArray(new String[0]);
    }

    public boolean isIgnored(String word) {
        return word.length() > 12
                || word.equals(query)
                || ignoredSet.contains(word)
                || !wordPattern.matcher(word).matches();
    }
}
