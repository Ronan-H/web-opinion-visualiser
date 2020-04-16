package ie.gmit.sw.comparator;

import ie.gmit.sw.crawler.DomainFrequency;
import ie.gmit.sw.crawler.PageNode;
import net.sourceforge.jFuzzyLogic.FIS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

// fuzzy logic comparator; higher scoring pages using the fuzzy control logic go to the front of the queue
public class FuzzyComparator extends PageNodeEvaluator {
    private FIS fis;
    private DomainFrequency frequencies;

    public FuzzyComparator(File fclFile, String query, DomainFrequency domainFrequency) {
        super(query);
        frequencies = domainFrequency;

        // Load from 'FCL' file
        boolean fileError = false;
        try {
            fis = FIS.load(new FileInputStream(fclFile), false);
        } catch (FileNotFoundException e) {
            fileError = true;
        }
        // Error while loading?
        if(fis == null || fileError) {
            System.err.printf("Can't load file: '%s'%n", fclFile.getAbsolutePath());
            System.exit(0);
        }
    }

    @Override
    public int compare(PageNode a, PageNode b) {
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

    @Override
    protected double childExpandFraction(PageNode node) {
        // compute fuzzy score
        double fuzzyScore = getScoreForPage(node);
        // value between 0 and 1 where 1 is max possible fuzzy score
        return (fuzzyScore - 5) / 30;
    }

    // fuzzy logic score for a page
    private synchronized double getScoreForPage(PageNode node) {
        // Set inputs
        PageNode relevanceRef = node.isLoaded() ? node : node.getParent();
        fis.setVariable("relevance", relevanceRef.getRelevanceScore(query));
        fis.setVariable("domain_usage", frequencies.getRelativeDomainFrequency(node.getUrl()));
        fis.setVariable("depth", node.getDepth());

        // Evaluate
        fis.evaluate();
        return fis.getVariable("score").getLatestDefuzzifiedValue();
    }
}
