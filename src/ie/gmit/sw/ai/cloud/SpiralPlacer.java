package ie.gmit.sw.ai.cloud;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

public class SpiralPlacer {
	private Graphics2D g; //The "canvas" to draw the word cloud on
	private BufferedImage img; //Rasterises the "canvas" to a PNG
	private java.util.List<Rectangle> placed = new ArrayList<>(); //The list of placed words
	private CollisionDetector detector = new CollisionDetector(); //Detects overlapping words
	private int width; //Image width. The bigger the canvas, the easier it is to place a word.
	private int height; //Image height
	private Rectangle imageRect;
	private int turn = 5; //The weight of the turn in the spiral

	public SpiralPlacer(int w, int h) {
		this.width = w;
		this.height = h;
		img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		g = (Graphics2D) img.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		imageRect = new Rectangle(width, height);

		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
	}

	public void placeAll(TermWeight[] words) {
		for (TermWeight wf : words) {
			place(wf);
		}
		g.dispose();
	}

	private void place(TermWeight wf) {
		Font font = new Font("Verdana", Font.PLAIN, wf.getFontSize()); //Create a font with a size proportional to the word frequency
		g.setFont(font); //Set the font of the graphics "brush"
		
		//Get the "size" of the word string as a rectangle
		Rectangle2D bounds = g.getFontMetrics(font).getStringBounds(wf.getTerm(), g);
		int halfWordWidth = (int) Math.round(bounds.getWidth() / 2);
		int halfWordHeight = (int) Math.round(bounds.getHeight() / 2);
		int wordWidth = (int) Math.round(bounds.getWidth());
		int wordHeight = (int) Math.round(bounds.getHeight());
		int quarterWordHeight = (int) Math.round(bounds.getHeight() / 4);

		int wordBorder = 3;
		int doubleWordborder = wordBorder * 2;

		int i = width / 2; //Get the horizontal centre
		int j = height / 2; //Get the vertical centre
		int k = 0; //Step to move along spiral
		double d = 0; //Distance from the centre
		int l = 0;

		//Start with the word placed at the centre of the spiral
		Rectangle word = new Rectangle(i - halfWordWidth - wordBorder,
									   j - halfWordHeight - wordBorder,
				                          wordWidth + doubleWordborder,
				                          wordHeight + doubleWordborder);
		
		//If the word collides with any existing words, move it along the spiral
		while (detector.collides(imageRect, word, placed)) {
			l = k * turn % 360;
			d = k * 0.02d;
			int x = (int) Math.round(i + d * Math.cos(l * Math.PI / 180.0d));
			int y = (int) Math.round(j + d * Math.sin(l * Math.PI / 180.0d));
			i = x;
			j = y;

			if (Math.abs(x) > width * 2) {
				// failed to place this worth within the bounds of the image
				return;
			}

			//Start with the word placed at the centre of the spiral
			word = new Rectangle(i - halfWordWidth - wordBorder,
								 j - halfWordHeight - wordBorder,
									wordWidth + doubleWordborder,
									wordHeight + doubleWordborder);
			k++;
		}

		// rainbow colours and fade with distance to centre
		float dist = Math.min(Math.max(1.2f - (float) (d / (width / 8d)), 0.4f), 1f);
		float hue =  l / 360f * (dist * 2) % 1f;
		g.setColor(new Color(Color.HSBtoRGB(hue, 1, dist))); //Set the colour of the graphics "brush"
		g.drawString(wf.getTerm(), i - halfWordWidth, j + quarterWordHeight);//Draw the word on the graphics canvas=

		placed.add(word); //Add the word to the list of placed words
	}

	public BufferedImage getImage() {
		return img;
	}
}