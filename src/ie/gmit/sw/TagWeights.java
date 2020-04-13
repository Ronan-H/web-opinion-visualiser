package ie.gmit.sw;

import java.util.HashMap;
import java.util.Map;

public class TagWeights {
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

        WEIGHTS.put("p", 2);

        WEIGHTS.put("li", 3);
        WEIGHTS.put("ul", 3);

        WEIGHTS.put("h6", 4);
        WEIGHTS.put("h5", 4);
        WEIGHTS.put("h4", 5);
        WEIGHTS.put("h3", 5);
        WEIGHTS.put("h2", 5);
        WEIGHTS.put("h1", 6);

        WEIGHTS.put("title", 8);

        SCORING_TAGS = WEIGHTS.keySet().toArray(new String[0]);
    }

    public String[] getScoringTags() {
        return SCORING_TAGS;
    }

    public int getScoreFor(String tag) {
        return WEIGHTS.get(tag);
    }
}
