package ie.gmit.sw.crawler;

import java.util.HashMap;
import java.util.Map;

// specifies a weighting scheme for HTML tags
// this is for page relevance calculations, where finding the query in a heading is
// more significant than finding it in a paragraph, for example
public class TagWeights {
    // singleton design pattern
    private static TagWeights tagWeights;

    public static TagWeights getInstance() {
        if (tagWeights == null) {
            tagWeights = new TagWeights();
        }

        return tagWeights;
    }

    private final Map<String, Integer> WEIGHTS;
    private final String[] SCORING_TAGS;

    private TagWeights() {
        WEIGHTS = new HashMap<>();

        // a simple weighting scheme for each tag
        WEIGHTS.put("p", 3);

        WEIGHTS.put("li", 4);
        WEIGHTS.put("ul", 4);
        WEIGHTS.put("a", 4);

        WEIGHTS.put("h6", 5);
        WEIGHTS.put("h5", 5);
        WEIGHTS.put("h4", 5);
        WEIGHTS.put("h3", 5);
        WEIGHTS.put("h2", 6);
        WEIGHTS.put("h1", 6);

        SCORING_TAGS = WEIGHTS.keySet().toArray(new String[0]);
    }

    public String[] getScoringTags() {
        return SCORING_TAGS;
    }

    public int getScoreFor(String tag) {
        return WEIGHTS.get(tag);
    }
}
