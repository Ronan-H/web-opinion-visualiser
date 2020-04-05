package ie.gmit.sw;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DomainFrequency {
    private Map<String, Integer> domainVisits;
    private int totalVisits;

    public DomainFrequency() {
        this.domainVisits = new HashMap<>();
        totalVisits = 0;
    }

    public synchronized void recordVisit(String url) {
        String domain = getDomainName(url);
        if (!domainVisits.containsKey(domain)) {
            domainVisits.put(domain, 0);
        }
        domainVisits.put(domain, domainVisits.get(domain) + 1);
        totalVisits++;
    }

    public synchronized double getRelativeDomainFrequency(String url) {
        String domain = getDomainName(url);

        if (!domainVisits.containsKey(domain)) {
            // no visits to this domain yet
            return 0;
        }

        // return relative frequency (value between 0 and 1)
        return (double) domainVisits.get(domain) / totalVisits;
    }

    public Map<String, Integer> getVisitMap() {
        return domainVisits;
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
