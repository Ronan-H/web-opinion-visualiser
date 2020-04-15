package ie.gmit.sw.term;

import java.util.*;

// calculates the tf*pdf weights for a set of terms
// see the README for more information about tf*pdf
public class TfpdfCalculator {
    // domain name -> (map of terms -> num occurrences)
    private Map<String, Map<String, Integer>> domainCountsMap;
    // domain name -> (map of terms -> number of pages containing that term)
    private Map<String, Map<String, Integer>> pageCountsMap;
    // domain name -> number of pages on that domain
    private Map<String, Integer> numDomainPages;

    public TfpdfCalculator() {
        domainCountsMap = new HashMap<>();
        pageCountsMap = new HashMap<>();
        numDomainPages = new HashMap<>();
    }

    // add term occurrence counter for a page
    public synchronized void addTermCounts(String domain, Map<String, Integer> counts) {
        if (!domainCountsMap.containsKey(domain)) {
            // initialise domain maps/counts
            domainCountsMap.put(domain, new HashMap<>());
            pageCountsMap.put(domain, new HashMap<>());
            numDomainPages.put(domain, 0);
        }

        // merge term counts
        Map<String, Integer> domainCounts = domainCountsMap.get(domain);
        Map<String, Integer> pageCounts = pageCountsMap.get(domain);
        for (String term : counts.keySet()) {
            domainCounts.put(term, domainCounts.getOrDefault(term, 0) + counts.get(term));
            pageCounts.put(term, pageCounts.getOrDefault(term, 0) + 1);
        }

        // increment page count for domain
        numDomainPages.put(domain, numDomainPages.get(domain) + 1);
    }

    // compute term weights, according to the tf*pdf implementation
    public synchronized Map<String, Double> getWeights() {
        Map<String, Double> allTermScores = new HashMap<>();
        Set<String> termsUsed = new HashSet<>();

        // compute weighting for all terms
        for (Map<String, Integer> domainCounts : domainCountsMap.values()) {
            for (String term : domainCounts.keySet()) {
                if (!termsUsed.contains(term)) {
                    allTermScores.put(term, getTfpdfWeight(term));
                    termsUsed.add(term);
                }
            }
        }

        return allTermScores;
    }

    // get tf*pdf weighting for a single term
    private double getTfpdfWeight(String term) {
        double sum = 0;
        for (String domain : domainCountsMap.keySet()) {
            // njc: number of "documents" (pages) in "channel" (domain) c where the term j occurs
            int njc = pageCountsMap.get(domain).getOrDefault(term, 0);
            // Nc: total number of "documents" (pages) in "channel" (domain) c
            int nc = numDomainPages.get(domain);

            // |Fjc| * exp(njc / Nc)
            sum += getNormalizedFjc(term, domain) * Math.exp((double) njc / nc);
        }

        return sum;
    }

    // frequency of term j in "channel" (domain) c
    private int getFjc(String term, String domain) {
        return domainCountsMap.get(domain).getOrDefault(term, 0);
    }

    // normalised Fjc value, based on the sum of all terms occurrences for the domain
    private double getNormalizedFjc(String term, String domain) {
        double fjc = getFjc(term, domain);

        // sum of squares of term occurrences
        int sum = domainCountsMap.get(domain)
                .values()
                .stream()
                .map(fkc -> (int) Math.pow(fkc, 2))
                .mapToInt(Integer::intValue)
                .sum();

        if (sum == 0) {
            // avoid division by 0
            return 0;
        }

        return fjc / Math.sqrt(sum);
    }
}
