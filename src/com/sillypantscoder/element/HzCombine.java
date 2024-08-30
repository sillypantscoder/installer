package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class HzCombine extends Element {
	public Color background;
	public Element[] children;
	public HzCombine(Color background, Element[] children) {
		this.background = background;
		this.children = children;
	}
	public int getMinWidth() {
		int width = 0;
		for (Element child : children) {
			width += child.getMinWidth();
		}
		return width;
	}
	public int getMinHeight() {
		int height = 0;
		for (Element child : children) {
			height = Math.max(height, child.getMinHeight());
		}
		return height;
	}
	public Surface[] renderChildren(int maxWidth, int maxHeight) {
		Surface[] rendered = new Surface[children.length];
		int spaceRemaining = maxWidth;
		for (int i = 0; i < children.length; i++) {
			rendered[i] = children[i].draw(spaceRemaining, maxHeight);
			spaceRemaining -= rendered[i].get_width();
		}
		return rendered;
	}
	public Surface draw(int maxWidth, int maxHeight) {
		Surface[] rendered = renderChildren(maxHeight, maxHeight);
		Surface result = new Surface(maxWidth, getMinHeight(), this.background);
		int x = 0;
		for (Surface childSurface : rendered) {
			result.blit(childSurface, x, 0);
			x += childSurface.get_width();
		}
		return result;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		Surface[] rendered = renderChildren(maxWidth, maxHeight);
		for (int i = 0; i < children.length; i++) {
			Element child = children[i];
			Surface childSurface = rendered[i];
			if (x < childSurface.get_width()) {
				if (y < childSurface.get_height()) {
					return child.elementAtPoint(childSurface.get_width(), childSurface.get_height(), x, y);
				}
				return Optional.empty();
			}
			x -= childSurface.get_width();
		}
		return Optional.empty();
	}
}
