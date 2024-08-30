package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class Divider extends Element {
	public int height;
	public int padding;
	public int borderSize;
	public Color borderColor;
	public Divider(int height, int padding, int borderSize, Color borderColor) {
		this.height = height;
		this.padding = padding;
		this.borderSize = borderSize;
		this.borderColor = borderColor;
	}
	public int getMinWidth() {
		return this.padding * 2;
	}
	public int getMinHeight() {
		return height;
	}
	public Surface draw(int maxWidth, int maxHeight) {
		Surface s = new Surface(getMinWidth(), height, new Color(0, 0, 0, 0));
		s.drawLine(borderColor, padding, 0, padding, s.get_height(), borderSize);
		return s;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		return Optional.ofNullable(this);
	}
}
