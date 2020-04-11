package ie.gmit.sw;

import java.util.*;

public class Tfpdf {
    private Map<String, List<Map<String, Integer>>> domainPageScores;

    public Tfpdf() {
        domainPageScores = new HashMap<>();
    }

    public synchronized Map<String, Integer> getWeights() {
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

        Map<String, Integer> intScores = new HashMap<>();

        for (String word : allTermScores.keySet()) {
            intScores.put(word,
                    (int) Math.round(allTermScores.get(word) * 10000));
        }

        return intScores;
    }

    public double getTfpdfWeight(String term) {
        double sum = 0;

        int nc = 0;
        int njc = 0;
        for (String domain : domainPageScores.keySet()) {
            List<Map<String, Integer>> domainScores = domainPageScores.get(domain);

            for (Map<String, Integer> pageScores : domainScores) {
                if (pageScores.containsKey(term)) {
                    njc++;
                }
            }

            nc += domainScores.size();
        }

        for (String domain : domainPageScores.keySet()) {
            double val = getNormalizedFjc(term, domain) * Math.exp((double) njc / nc);
            System.out.printf("Tfpdf single domain score for %s: %.3f%n", term, val);
            sum += val;
        }

        System.out.printf("Tfpdf for %s: %.3f%n", term, sum);

        return sum;
    }

    public double getFjc(String term, String domain) {
        List<Map<String, Integer>> domainScores = domainPageScores.get(domain);
        double freq = 0;

        for (Map<String, Integer> pageScores : domainScores) {
            freq += pageScores.getOrDefault(term, 0);
        }

        return freq;
    }

    public double getNormalizedFjc(String term, String domain) {
        double fjc = getFjc(term, domain);
        List<Map<String, Integer>> domainScores = domainPageScores.get(domain);

        double sum = 0;

        for (Map<String, Integer> pageScores : domainScores) {
            for (String t : pageScores.keySet()) {
                sum += Math.pow(pageScores.get(t), 2);
            }
        }

        if (sum == 0) {
            return 0;
        }

        return fjc / Math.sqrt(sum);
    }

    public synchronized void addPageScores(String domain, Map<String, Integer> scores) {
        if (!domainPageScores.containsKey(domain)) {
            domainPageScores.put(domain, new ArrayList<>());
        }

        domainPageScores.get(domain).add(scores);
    }
}
