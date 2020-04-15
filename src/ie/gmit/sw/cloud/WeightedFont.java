package ie.gmit.sw.cloud;

import java.util.HashMap;
import java.util.Map;

public class WeightedFont {
	// min/max font size presets
	private static final Map<Integer, Double[]> fontSizesForNumWords;
	static {
		fontSizesForNumWords = new HashMap<>();
		fontSizesForNumWords.put(20, new Double[]{30d, 60d});
		fontSizesForNumWords.put(60, new Double[]{21d, 50d});
		fontSizesForNumWords.put(80, new Double[]{10d, 50d});
	}

	private double maxFontSize;
	private double minFontSize;

	public WeightedFont(double minFontSize, double maxFontSize) {
		this.minFontSize = minFontSize;
		this.maxFontSize = maxFontSize;
	}

	public WeightedFont(int numCloudWords) {
		// use a font size preset
		this(fontSizesForNumWords.get(numCloudWords)[0],
			 fontSizesForNumWords.get(numCloudWords)[1]);
	}

	public TermWeight[] getFontSizes(TermWeight[] words) {
		//Get the max and min frequencies and scale these to a natural log scale to smooth out the range  
		double max = Math.log(words[0].getWeight());
		double min = Math.log(words[words.length - 1].getWeight());

		for (TermWeight wf : words) {
			//Use a log scale and word frequency to compute the font size for the word
			wf.setFontSize((int)Math.round(getScaledFontSize(Math.log(wf.getWeight()), min, max)));
		}
		return words;
	}
	
	//Compute the initial font size for the word 
	private double getScaledFontSize(double value, double min, double max){
		double normalized = (value - min) / (max - min);
		return minFontSize + ((maxFontSize - minFontSize) * normalized);
	}
}