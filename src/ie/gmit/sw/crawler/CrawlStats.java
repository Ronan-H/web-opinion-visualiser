package ie.gmit.sw.crawler;

import ie.gmit.sw.DomainFrequency;

import java.util.Formatter;
import java.util.PriorityQueue;

public class CrawlStats {
    private DomainFrequency domainFrequency;
    private int pageLoads;
    private int numPagesHaveQuery;
    private int maxDepth;
    private PriorityQueue<LogEntry> asyncLog;
    private String asString;

    public CrawlStats(DomainFrequency domainFrequency) {
        this.domainFrequency = domainFrequency;
        asyncLog = new PriorityQueue<>();
    }

    public DomainFrequency getDomainFrequency() {
        return domainFrequency;
    }

    public void recordDomainVisit(String domain) {
        domainFrequency.recordVisit(domain);
    }

    public synchronized void recordDepth(int depth) {
        if (depth > maxDepth) {
            maxDepth = depth;
        }
    }

    public synchronized void incPageLoads() {
        pageLoads++;
    }

    public synchronized void incPageHaveQuery() {
        numPagesHaveQuery++;
    }

    public synchronized void logEntry(LogEntry entry) {
        asyncLog.add(entry);
    }

    public synchronized void logEntry(String entry) {
        asyncLog.add(new LogEntry(entry, Integer.MAX_VALUE));
    }

    @Override
    public synchronized String toString() {
        if (asString != null) {
            return asString;
        }

        Formatter builder = new Formatter();

        builder.format("<h2>Top domains</h2>%n");
        builder.format("<ol>%n");
        String[] topDomains = domainFrequency.getTopN(10);
        for (String domain : topDomains) {
            builder.format("<li>%4d visits on <b>%s</b></l1>%n", domainFrequency.getDomainVisits(domain), domain);
        }
        builder.format("</ol>%n");

        double queryPagePercent = (double) numPagesHaveQuery / pageLoads * 100;
        builder.format("<h2>Other stats</h2>%n");
        builder.format("<ul>%n");
        builder.format("<li>Total pages loaded: <b>%d</b></li>%n", pageLoads);
        builder.format("<li>Percentage of pages where the query was found at least once: <b>%.1f%%</b></li>%n", queryPagePercent);
        builder.format("<li>Max search depth: <b>%d</b></li>%n", maxDepth);
        builder.format("</ul>%n");

        builder.format("<h2>Full crawl log</h2>%n");
        builder.format("<textarea rows=\"25\" cols=\"100\" wrap=\"soft\" style=\"overflow: scroll\">%n");
        while (!asyncLog.isEmpty()) {
            builder.format("%s%n", asyncLog.poll().getEntry());
        }
        builder.format("</textarea></br>");

        asString = builder.toString();
        return asString;
    }
}
