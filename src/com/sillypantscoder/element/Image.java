package com.sillypantscoder.element;

import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class Image extends Element {
	public Surface image;
	public Image(Surface image) {
		this.image = image;
	}
	public int getMinWidth() {
		return image.get_width();
	}
	public int getMinHeight() {
		return image.get_height();
	}
	public Surface draw(int maxWidth, int maxHeight) {
		return image;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		if (x >= image.get_width() || y >= image.get_height()) {
			return Optional.empty();
		}
		return Optional.of(this);
	}
}
