package ie.gmit.sw.cloud;

import java.awt.Rectangle;
import java.util.List;

public class CollisionDetector {
	/*
	 * Checks if the rectangle created around the new word overlaps any of the existing
	 * rectangles that have already been placed in the word cloud.
	 */
	public boolean collides(Rectangle imageRect, Rectangle word, List<Rectangle> existing) {
		if (!imageRect.contains(word)) return true;

		for (Rectangle rectangle : existing) {
			if (word.intersects(rectangle) || word.contains(rectangle) || rectangle.contains(word)) {
				return true;
			}
		}
		return false;
	}
}