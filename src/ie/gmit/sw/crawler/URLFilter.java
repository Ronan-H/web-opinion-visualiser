package ie.gmit.sw.crawler;

// simple filter for URLs (some may be blacklisted etc)
public class URLFilter {
    // singleton design pattern
    private static URLFilter urlFilter;

    public static URLFilter getInstance() {
        if (urlFilter == null) {
            urlFilter = new URLFilter();
        }

        return urlFilter;
    }

    private String[] blacklist;
    private String[] whitelist;
    private URLFilter() {
        // list of terms that URLs must not contain
        blacklist = new String[]
        {
                "facebook.com",
                "youtube.com",
                "linkedin.com",
                "tripadvisor",
                "booking.com"
        };

        // list of terms that URLs must contain
        // (just domain extensions, to avoid non-english pages)
        whitelist = new String[] {
                ".com",
                ".ie",
                ".net",
                ".info",
                ".gov"
        };
    }

    // returns true if the URL contains all of the whitelisted terms and none of the blacklisted terms
    public boolean isURLAllowed(String url) {
        // no blacklisted URLs
        for (String s : blacklist) {
            if (url.contains(s)) {
                return false;
            }
        }

        // URL must contain all whitelist terms
        for (String s : whitelist) {
            if (!url.contains(s)) {
                return false;
            }
        }

        return true;
    }
}
