package com.sillypantscoder.element;

import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class Clickable extends Element {
	public Runnable onClick;
	public Element child;
	public Clickable(Runnable onClick, Element child) {
		this.onClick = onClick;
		this.child = child;
	}
	public int getMinWidth() {
		return this.child.getMinWidth();
	}
	public int getMinHeight() {
		return this.child.getMinHeight();
	}
	public Surface draw(int maxWidth, int maxHeight) {
		return this.child.draw(maxWidth, maxHeight);
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		this.onClick.run();
		return this.child.elementAtPoint(maxWidth, maxHeight, x, y);
	}
}
