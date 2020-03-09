package ie.gmit.sw;

public class URLBlacklist {
    private static URLBlacklist urlBlacklist;

    public static URLBlacklist getInstance() {
        if (urlBlacklist == null) {
            urlBlacklist = new URLBlacklist();
        }

        return urlBlacklist;
    }

    private String[] blacklist;
    private URLBlacklist() {
        blacklist = new String[]
        {
                "facebook.com",
                "youtube.com"
        };
    }

    public boolean isUrlBlacklisted(String url) {
        for (String s : blacklist) {
            if (url.contains(s)) {
                return true;
            }
        }

        return false;
    }
}
