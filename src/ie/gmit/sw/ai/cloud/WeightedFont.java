package ie.gmit.sw.ai.cloud;

public class WeightedFont {
	private static final double MAX_FONT_SIZE = 45.00d;
	private static final double MIN_FONT_SIZE = 25.00d;
	
	public TermWeight[] getFontSizes(TermWeight[] words) {
		//Get the max and min frequencies and scale these to a natural log scale to smooth out the range  
		double max = Math.log(words[0].getWeight());
		double min = Math.log(words[words.length - 1].getWeight());

		for (TermWeight wf : words) {
			//Use a log scale and word frequency to compute the font size for the word
			wf.setFontSize((int)getScaledFontSize(Math.log(wf.getWeight()), min, max));
		}
		return words;
	}
	
	//Compute the initial font size for the word 
	public double getScaledFontSize(double value, double min, double max){
		double normalized = (value - min) / (max - min);
		return MIN_FONT_SIZE + ((MAX_FONT_SIZE - MIN_FONT_SIZE) * normalized);
	}
}