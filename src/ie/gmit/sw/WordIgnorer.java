package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordIgnorer {
    private Set<String> ignoredSet;

    public WordIgnorer(String ignoredPath) {
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
    }

    public WordIgnorer() {
        ignoredSet = new HashSet<>();
    }

    public String[] getUsefulWords(String[] input) {
        List<String> usefulWords = new ArrayList<>(input.length);
        for (String word : input) {
            if (!ignoredSet.contains(word)) {
                usefulWords.add(word);
            }
        }

        return usefulWords.toArray(new String[0]);
    }
}
