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
    private String[] allowedDomainsExts;
    private String[] ignoreEnds;
    private String startWith;

    private URLFilter() {
        // must start with http
        startWith = "http";

        // list of terms that URLs must not contain
        blacklist = new String[]
        {
                "facebook.com",
                "youtube.com",
                "linkedin.com",
                "tripadvisor",
                "booking.com"
        };

        // domain must be one of these extensions
        // (to avoid crawling non-english pages)
        allowedDomainsExts = new String[] {
                ".com",
                ".net",
                ".org",
                ".info",
                ".gov",
                ".uk",
                ".us",
                ".ie",
                ".ca",
                ".au",
                ".nz"
        };

        // ignore URLs ending with (not interested in media files etc)
        ignoreEnds = new String[] {
                ".jpg",
                ".jpeg",
                ".png",
                ".gif",
                ".mp3",
                ".mp4",
                ".pdf"
        };
    }

    // filters a URL based on certain criteria
    public boolean isURLAllowed(String url) {
        // must start with http
        if (!url.startsWith(startWith)) {
            return false;
        }

        // no blacklisted URLs
        for (String s : blacklist) {
            if (url.contains(s)) {
                return false;
            }
        }

        // domain extension must be allowed
        if (!allowedDomainExtension(url)) {
            return false;
        }

        // must not end in certain media extensions
        for (String end : ignoreEnds) {
            if (url.endsWith(end)) {
                return false;
            }
        }

        // matched all criteria
        return true;
    }

    private boolean allowedDomainExtension(String url) {
        // domain must have one of the allowed extensions
        for (String ext : allowedDomainsExts) {
            if (url.contains(ext)) {
                return true;
            }
        }

        return false;
    }
}
