package com.sillypantscoder.element;

import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public abstract class Element {
	public abstract int getMinWidth();
	public abstract int getMinHeight();
	public abstract Surface draw(int maxWidth, int maxHeight);
	public abstract Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y);
}
