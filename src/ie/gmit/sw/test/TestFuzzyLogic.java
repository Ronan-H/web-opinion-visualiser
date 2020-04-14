package ie.gmit.sw.test;

import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class TestFuzzyLogic {
    // based on tipper example: http://jfuzzylogic.sourceforge.net/html/example_java.html
    public static void main(String[] args) {
        // Load from 'FCL' file
        String fileName = "./res/page-scoring.fcl";
        FIS fis = FIS.load(fileName,true);
        // Error while loading?
        if(fis == null) {
            System.err.printf("Can't load file: '%s'%n", fileName);
            return;
        }

        // Show
        FunctionBlock fb = fis.getFunctionBlock("page_scoring");
        JFuzzyChart.get().chart(fb);

        // Set inputs
        fis.setVariable("relevance", 0.1);
        fis.setVariable("domain_usage", 0.2);
        fis.setVariable("depth", 5);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable out = fis.getVariable("score");
        JFuzzyChart.get().chart(out, out.getDefuzzifier(), true);
        System.out.println("Score: " + out.getLatestDefuzzifiedValue());


        // Print ruleSet
        System.out.println(fis);
    }
}
