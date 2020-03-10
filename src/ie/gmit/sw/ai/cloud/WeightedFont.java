package ie.gmit.sw.ai.cloud;

import java.util.Arrays;

public class WeightedFont {
	private static final double MAX_FONT_SIZE = 37.00d;
	private static final double MIN_FONT_SIZE = 20.00d;
	
	public WordFrequency[] getFontSizes(WordFrequency[] words) {
		//Get the max and min frequencies and scale these to a natural log scale to smooth out the range  
		double max = Math.sqrt(words[0].getFrequency());
		double min = Math.sqrt(words[words.length - 1].getFrequency());

		for (WordFrequency wf : words) {
			//Use a log scale and word frequency to compute the font size for the word
			wf.setFontSize((int)getScaledFontSize(Math.sqrt(wf.getFrequency()), min, max));
		}
		return words;
	}
	
	//Compute the initial font size for the word 
	public double getScaledFontSize(double value, double min, double max){
		double normalized = (value - min) / (max - min);
		return MIN_FONT_SIZE + ((MAX_FONT_SIZE - MIN_FONT_SIZE) * normalized);
	}
}