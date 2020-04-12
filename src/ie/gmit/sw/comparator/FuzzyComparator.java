package ie.gmit.sw.comparator;

import ie.gmit.sw.DomainFrequency;
import ie.gmit.sw.PageNode;
import net.sourceforge.jFuzzyLogic.FIS;

public class FuzzyComparator extends PageNodeEvaluator {
    private FIS fis;
    private DomainFrequency frequencies;
    private LIFOComparator lifoComparator;

    public FuzzyComparator(String query, DomainFrequency domainFrequency) {
        super(query);
        frequencies = domainFrequency;

        // Load from 'FCL' file
        String fileName = "./res/page-scoring.fcl";
        fis = FIS.load(fileName,false);
        // Error while loading?
        if(fis == null) {
            System.err.printf("Can't load file: '%s'%n", fileName);
            System.exit(0);
        }

        lifoComparator = new LIFOComparator(query);
    }

    @Override
    public synchronized int compare(PageNode a, PageNode b) {
        PageNode aParent = a.getParent();
        PageNode bParent = b.getParent();

        // ensure root pages go to the front of the queue
        if (aParent == null && bParent == null) {
            return -lifoComparator.compare(a, b);
        }

        if (aParent == null) {
            return -Integer.MAX_VALUE;
        }

        if (bParent == null) {
            return Integer.MAX_VALUE;
        }

        double aScore = getScoreForPage(a);
        double bScore = getScoreForPage(b);

        return -Double.compare(aScore, bScore);
    }

    private synchronized double getScoreForPage(PageNode node) {
        if (node.getParent() == null) {
            // root node; assume search results are highly relevant
            return 28;
        }

        // Set inputs
        fis.setVariable("relevance", node.getParent().getRelevanceScore(query));
        fis.setVariable("domain_usage", frequencies.getRelativeDomainFrequency(node.getUrl()));
        fis.setVariable("depth", node.getDepth());

        // Evaluate
        fis.evaluate();

        double score = fis.getVariable("score").getLatestDefuzzifiedValue();
        //System.out.printf("Fuzzy score for URL %s: %.3f%n", node.getParent().getUrl(), score);
        return score;
    }

    @Override
    public int numChildExpandHeuristic(PageNode node) {
        double fuzzyScore = getScoreForPage(node);

        if (fuzzyScore < 5.1) {
            return 0;
        }

        return (int)Math.ceil(fuzzyScore / 4);
    }
}
