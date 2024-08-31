package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class ScrollContainer extends Element {
	public static final int SCROLLBAR_WIDTH = 15;
	public static final int SCROLLBAR_HEIGHT = 80;
	public Element child;
	public int scroll;
	public Optional<Integer> mouseScrollbarOffset = Optional.empty();
	public ScrollContainer(Element child) {
		this.child = child;
	}
	public int getMinWidth() {
		return 1;
	}
	public int getMinHeight() {
		return 1;
	}
	public Surface draw(int maxWidth, int maxHeight) {
		// Draw child
		Surface s = child.draw(maxWidth - SCROLLBAR_WIDTH, child.getMinHeight());
		Surface result = new Surface(maxWidth, maxHeight, new Color(0, 0, 0, 0));
		result.blit(s, 0, -scroll);
		// Draw scroll bar
		int barX = result.get_width() - SCROLLBAR_WIDTH;
		double space = result.get_height() - SCROLLBAR_HEIGHT;
		double barpos = (double)(scroll) / s.get_height();
		int barY = (int)(space * barpos);
		result.drawRect(new Color(100, 100, 100), barX, barY, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
		// return
		return result;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		if (x >= maxWidth - SCROLLBAR_WIDTH) {
			this.setScrollFromMouse(y, maxWidth, maxHeight);
			return Optional.of(this);
		}
		if (y + scroll >= child.getMinHeight()) {
			return Optional.empty();
		}
		if (y + scroll < 0) {
			return Optional.empty();
		}
		return child.elementAtPoint(maxWidth - SCROLLBAR_WIDTH, child.getMinHeight(), x, y + scroll);
	}
	public void setScroll(int newScroll) {
		this.scroll = newScroll;
		if (this.scroll > child.getMinHeight()) {
			this.scroll = child.getMinHeight();
		}
		if (this.scroll < 0) {
			this.scroll = 0;
		}
	}
	public void scrollBy(int amount) {
		if (amount < 0) this.scroll -= 3;
		this.setScroll(this.scroll + amount);
	}
	public void setScrollFromMouse(int y, int maxWidth, int maxHeight) {
		int innerHeight = child.draw(maxWidth - SCROLLBAR_WIDTH, child.getMinHeight()).get_height();
		// find new scroll value
		double barY = y - (SCROLLBAR_HEIGHT / 2);
		double space = maxHeight - SCROLLBAR_HEIGHT;
		// barY = (space * (scroll / innerHeight))
		// barY / (scroll / innerHeight) = space
		// 1 / (scroll / innerHeight) = space / barY
		// scroll / innerHeight = barY / space
		// scroll = (barY * innerHeight) / space
		int newScroll = (int)((barY * innerHeight) / space);
		setScroll(newScroll);
	}
}
