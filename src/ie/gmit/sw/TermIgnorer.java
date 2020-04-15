package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

// decides if a term should be ignored, based on a number of criteria
public class TermIgnorer {
    private Set<String> ignoredSet;
    private String query;
    private Pattern termPattern;

    public TermIgnorer(File ignoredTerms, String query) {
        this.query = query;
        ignoredSet = new HashSet<>();

        try {
            // load in ignored terms from file
            BufferedReader ignoreIn = new BufferedReader(new FileReader(ignoredTerms));
            String term;
            while ((term = ignoreIn.readLine()) != null) {
                ignoredSet.add(term);
            }
            ignoreIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // basic regex pattern which all terms must match
        termPattern = Pattern.compile("[a-z]+(-[a-z0-9]+)*");
    }

    // filter out ignored terms from a list
    public String[] getUsefulTerms(String[] input) {
        List<String> usefulTerms = new ArrayList<>(input.length);
        for (String word : input) {
            if (!isIgnored(word)) {
                usefulTerms.add(word);
            }
        }

        return usefulTerms.toArray(new String[0]);
    }

    // returns true if a given term should be ignored
    public boolean isIgnored(String term) {
        // ignore terms that...
        return term.length() > 12 // ...are too long
            || term.length() < 4 // ...are too short
            || term.contains(query) // ...contain the query
            || query.contains(term) // ...are in the query
            || ignoredSet.contains(term) // ...are in the ignored terms file
            || !termPattern.matcher(term).matches(); // ...do not conform to the regex
    }
}
