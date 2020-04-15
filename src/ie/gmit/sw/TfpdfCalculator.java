package ie.gmit.sw;

import java.util.*;

// calculates the tf*pdf weights for a set of terms
// see the README for more information about tf*pdf
public class TfpdfCalculator {
    private Map<String, List<Map<String, Integer>>> domainPageCounts;

    public TfpdfCalculator() {
        domainPageCounts = new HashMap<>();
    }

    // add term occurrence counter for a page
    public synchronized void addTermCounts(String domain, Map<String, Integer> scores) {
        if (!domainPageCounts.containsKey(domain)) {
            domainPageCounts.put(domain, new ArrayList<>());
        }

        domainPageCounts.get(domain).add(scores);
    }

    public synchronized Map<String, Double> getWeights() {
        Map<String, Double> allTermScores = new HashMap<>();
        Set<String> termsUsed = new HashSet<>();

        for (String domain : domainPageCounts.keySet()) {
            List<Map<String, Integer>> domainScores = domainPageCounts.get(domain);

            for (Map<String, Integer> pageScores : domainScores) {
                for (String term : pageScores.keySet()) {
                    if (!termsUsed.contains(term)) {
                        allTermScores.put(term, getTfpdfWeight(term));
                        termsUsed.add(term);
                    }
                }
            }
        }

        return allTermScores;
    }

    private double getTfpdfWeight(String term) {
        int njc = 0;
        int nc = domainPageCounts.size();

        for (String domain : domainPageCounts.keySet()) {
            for (Map<String, Integer> pageScores : domainPageCounts.get(domain)) {
                if (pageScores.containsKey(term)) {
                    njc++;
                    break;
                }
            }
        }

        double sum = 0;
        for (String domain : domainPageCounts.keySet()) {
            sum += getNormalizedFjc(term, domain) * Math.exp((double) njc / nc);
        }

        return sum;
    }

    private int getFjc(String term, String domain) {
        int freq = 0;

        for (Map<String, Integer> pageScores : domainPageCounts.get(domain)) {
            freq += pageScores.getOrDefault(term, 0);
        }

        return freq;
    }

    private double getNormalizedFjc(String term, String domain) {
        double fjc = getFjc(term, domain);
        int sum = 0;

        for (Map<String, Integer> pageScores : domainPageCounts.get(domain)) {
            for (int f : pageScores.values()) {
                sum += Math.pow(f, 2);
            }
        }

        if (sum == 0) {
            return 0;
        }

        return fjc / Math.sqrt(sum);
    }
}
