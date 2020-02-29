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

        WEIGHTS.put("p", 1);

        WEIGHTS.put("li", 2);
        WEIGHTS.put("ul", 2);

        WEIGHTS.put("h1", 8);
        WEIGHTS.put("h2", 6);
        WEIGHTS.put("h3", 5);
        WEIGHTS.put("h4", 4);
        WEIGHTS.put("h5", 4);
        WEIGHTS.put("h6", 3);

        WEIGHTS.put("title", 10);

        SCORING_TAGS = WEIGHTS.keySet().toArray(new String[0]);
    }

    public String[] getScoringTags() {
        return SCORING_TAGS;
    }

    public int getScoreFor(String tag) {
        return WEIGHTS.get(tag);
    }
}
