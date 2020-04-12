package ie.gmit.sw;

import java.util.*;

public class TfpdfCalculator {
    private Map<String, List<Map<String, Integer>>> domainPageScores;

    public TfpdfCalculator() {
        domainPageScores = new HashMap<>();
    }

    public synchronized void addPageScores(String domain, Map<String, Integer> scores) {
        if (!domainPageScores.containsKey(domain)) {
            domainPageScores.put(domain, new ArrayList<>());
        }

        domainPageScores.get(domain).add(scores);
    }

    public synchronized Map<String, Double> getWeights() {
        Map<String, Double> allTermScores = new HashMap<>();
        Set<String> termsUsed = new HashSet<>();

        for (String domain : domainPageScores.keySet()) {
            List<Map<String, Integer>> domainScores = domainPageScores.get(domain);

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
        double sum = 0;
        int njc = 0;
        int nc = 0;

        for (String domain : domainPageScores.keySet()) {
            for (Map<String, Integer> pageScores : domainPageScores.get(domain)) {
                if (pageScores.containsKey(term)) {
                    njc++;
                }

                nc += pageScores.size();
            }

            double val = getNormalizedFjc(term, domain) * Math.exp((double) njc / nc);
            sum += val;
        }

        return sum;
    }

    private double getFjc(String term, String domain) {
        double freq = 0;

        for (Map<String, Integer> pageScores : domainPageScores.get(domain)) {
            freq += pageScores.getOrDefault(term, 0);
        }

        return freq;
    }

    private double getNormalizedFjc(String term, String domain) {
        double fjc = getFjc(term, domain);
        double sum = 0;

        for (Map<String, Integer> pageScores : domainPageScores.get(domain)) {
            for (String t : pageScores.keySet()) {
                sum += Math.pow(pageScores.get(t), 2);
            }
        }

        if (sum == 0) {
            return 0;
        }

        return fjc / Math.sqrt(sum);
    }
}
