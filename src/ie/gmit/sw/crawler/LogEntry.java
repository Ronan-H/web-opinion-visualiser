package ie.gmit.sw.crawler;

// asynchronous search crawl log entry
public class LogEntry implements Comparable<LogEntry> {
    private String entry;
    private int pageId;

    public LogEntry(String entry, int pageId) {
        this.entry = entry;
        this.pageId = pageId;
    }

    public String getEntry() {
        return entry;
    }

    public int getPageId() {
        return pageId;
    }

    @Override
    public int compareTo(LogEntry otherEntry) {
        return Long.compare(pageId, otherEntry.getPageId());
    }
}
