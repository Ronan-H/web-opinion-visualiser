package ie.gmit.sw.comparator;

import ie.gmit.sw.DomainFrequency;
import ie.gmit.sw.PageNode;
import net.sourceforge.jFuzzyLogic.FIS;

// fuzzy logic comparator; higher scoring pages using the fuzzy control logic go to the front of the queue
public class FuzzyComparator extends PageNodeEvaluator {
    private FIS fis;
    private DomainFrequency frequencies;

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
    }

    @Override
    public synchronized int compare(PageNode a, PageNode b) {
        PageNode aParent = a.getParent();
        PageNode bParent = b.getParent();

        // ensure root pages go to the front of the queue
        if (aParent == null && bParent == null) {
            return Integer.compare(
                    a.getId(),
                    b.getId()
            );
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
            return 20;
        }

        // Set inputs
        PageNode relevanceRef = node.isLoaded() ? node : node.getParent();
        fis.setVariable("relevance", relevanceRef.getRelevanceScore(query));
        fis.setVariable("domain_usage", frequencies.getRelativeDomainFrequency(node.getUrl()));
        fis.setVariable("depth", node.getDepth());

        // Evaluate
        fis.evaluate();
        return fis.getVariable("score").getLatestDefuzzifiedValue();
    }

    @Override
    public int numChildExpandHeuristic(PageNode node) {
        // compute fuzzy score
        double fuzzyScore = getScoreForPage(node);

        if (node.getRelevanceScore(query) <= 0) {
            // no query strings on this page, don't expand any child nodes
            return 0;
        }

        // expand more child nodes if the fuzzy score is high
        return (int)Math.ceil((fuzzyScore - 5) / 2);
    }
}
