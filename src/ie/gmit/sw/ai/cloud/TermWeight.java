package ie.gmit.sw.ai.cloud;

public class TermWeight {
	private String term;
	private double weight;
	private int fontSize = 0;

	public TermWeight(String term, double weight) {
		this.term = term;
		this.weight = weight;
	}

	public String getTerm() {
		return this.term;
	}

	public double getWeight() {
		return this.weight;
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public void setFontSize(int size) {
		this.fontSize = size;
	}
}