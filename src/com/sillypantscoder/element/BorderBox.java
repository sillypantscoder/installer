package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class BorderBox extends Element {
	public int margin;
	public Color borderColor;
	public int borderWidth;
	public int padding;
	public Color background;
	public Element child;
	public BorderBox(int margin, Color borderColor, int borderWidth, int padding, Color background, Element child) {
		this.margin = margin;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		this.padding = padding;
		this.background = background;
		this.child = child;
	}
	public int getMinWidth() {
		return this.child.getMinWidth() + (this.padding * 2) + (this.margin * 2);
	}
	public int getMinHeight() {
		return this.child.getMinHeight() + (this.padding * 2) + (this.margin * 2);
	}
	public Surface draw(int maxWidth, int maxHeight) {
		// Draw child
		int allPadding = (this.padding * 2) + (this.margin * 2);
		Surface childSurface = child.draw(maxWidth - allPadding, child.getMinHeight());
		// Create surface
		Surface s = new Surface(childSurface.get_width() + allPadding, childSurface.get_height() + allPadding, new Color(0, 0, 0, 0));
		// Draw background
		s.drawRect(background, margin, margin, s.get_width() - (margin + padding), s.get_height() - (margin + padding));
		// Draw border
		s.drawRect(borderColor, margin, margin, s.get_width() - (margin + padding), s.get_height() - (margin + padding), borderWidth);
		// Draw child
		s.blit(childSurface, margin + padding, margin + padding);
		// Return
		return s;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		if (x < margin || x >= getMinWidth() - margin || y < margin || y >= getMinHeight() - margin) {
			return Optional.empty();
		}
		return child.elementAtPoint(maxWidth, maxHeight, x - margin, y - margin);
	}
}
