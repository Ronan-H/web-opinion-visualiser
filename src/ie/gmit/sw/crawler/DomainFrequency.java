package ie.gmit.sw.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// used to record the visit counts to each domain name
public class DomainFrequency {
    private Map<String, Integer> domainVisits;
    private int totalVisits;

    public DomainFrequency() {
        this.domainVisits = new HashMap<>();
        totalVisits = 0;
    }

    // record a visit to a domain
    public synchronized void recordVisit(String url) {
        String domain = getDomainName(url);
        if (!domainVisits.containsKey(domain)) {
            domainVisits.put(domain, 0);
        }
        domainVisits.put(domain, domainVisits.get(domain) + 1);
        totalVisits++;
    }

    // relative/normalized domain visit frequency
    public synchronized double getRelativeDomainFrequency(String url) {
        String domain = getDomainName(url);

        if (!domainVisits.containsKey(domain)) {
            // no visits to this domain yet
            return 0;
        }

        // return relative frequency (value between 0 and 1)
        return (double) domainVisits.get(domain) / (totalVisits + 3);
    }

    public int getDomainVisits(String domain) {
        return domainVisits.get(domain);
    }

    public String[] getTopN(int n) {
        // convert to string array
        String[] domains = domainVisits.keySet().toArray(new String[0]);

        // sort (by visits, highest to lowest)
        Arrays.sort(domains, Comparator.comparing(domainVisits::get, Comparator.reverseOrder()));

        return Arrays.copyOfRange(domains, 0, Math.min(domains.length, n));
    }

    // taken from https://stackoverflow.com/a/9608008
    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }
        catch (URISyntaxException e) {
            return "invalid";
        }
    }
}
