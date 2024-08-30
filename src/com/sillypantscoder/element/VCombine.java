package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class VCombine extends Element {
	public Element[] children;
	public VCombine(Element[] children) {
		this.children = children;
	}
	public int getMinWidth() {
		int width = 0;
		for (Element child : children) {
			width = Math.max(width, child.getMinWidth());
		}
		return width;
	}
	public int getMinHeight() {
		int height = 0;
		for (Element child : children) {
			height += child.getMinHeight();
		}
		return height;
	}
	public Surface[] renderChildren(int maxWidth, int maxHeight) {
		Surface[] rendered = new Surface[children.length];
		int spaceRemaining = maxHeight;
		for (int i = 0; i < children.length; i++) {
			rendered[i] = children[i].draw(maxWidth, spaceRemaining);
			spaceRemaining -= rendered[i].get_height();
		}
		return rendered;
	}
	public Surface draw(int maxWidth, int maxHeight) {
		Surface[] rendered = renderChildren(maxWidth, maxHeight);
		Surface result = new Surface(maxWidth, maxHeight, new Color(0, 0, 0, 0));
		int y = 0;
		for (Surface childSurface : rendered) {
			result.blit(childSurface, 0, y);
			y += childSurface.get_height();
		}
		return result;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		Surface[] rendered = renderChildren(maxWidth, maxHeight);
		for (int i = 0; i < children.length; i++) {
			Element child = children[i];
			Surface childSurface = rendered[i];
			if (y < childSurface.get_height()) {
				if (x < childSurface.get_width()) {
					return child.elementAtPoint(childSurface.get_width(), childSurface.get_height(), x, y);
				}
				return Optional.empty();
			}
			y -= childSurface.get_height();
		}
		return Optional.empty();
	}
}
