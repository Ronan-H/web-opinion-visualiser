package ie.gmit.sw.crawler;

// simple URL blacklist
public class URLBlacklist {
    // singleton design pattern
    private static URLBlacklist urlBlacklist;

    public static URLBlacklist getInstance() {
        if (urlBlacklist == null) {
            urlBlacklist = new URLBlacklist();
        }

        return urlBlacklist;
    }

    private String[] blacklist;
    private URLBlacklist() {
        // list of terms to ignore in URLs
        blacklist = new String[]
        {
                "facebook.com",
                "youtube.com",
                "linkedin.com",
                "tripadvisor",
                "booking.com",
                ".ru",
        };
    }

    // returns true if the given URL contains any of the blacklisted terms
    public boolean isUrlBlacklisted(String url) {
        for (String s : blacklist) {
            if (url.contains(s)) {
                return true;
            }
        }

        return false;
    }
}
