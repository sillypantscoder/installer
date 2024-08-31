package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;

import com.sillypantscoder.windowlib.Surface;

public class Button extends Element {
	public Runnable onClick;
	public int padding = 4;
	public String text;
	public int textSize = 16;
	public boolean full;
	public Button(Runnable onClick, String text, boolean full) {
		this.onClick = onClick;
		this.text = text;
		this.full = full;
	}
	public int getMinWidth() {
		Surface textS = Surface.renderText(16, true, text, Color.WHITE);
		return textS.get_width() + (padding * 4);
	}
	public int getMinHeight() {
		Surface textS = Surface.renderText(16, true, text, Color.WHITE);
		return textS.get_height() + (padding * 4);
	}
	public Surface draw(int maxWidth, int maxHeight) {
		// Render the text
		Surface textS = Surface.renderText(textSize, full, text, full ? Color.WHITE : new Color(150, 150, 150));
		// Create the surface
		Surface docsbtn = new Surface(textS.get_width() + (padding * 4), textS.get_height() + (padding * 4), new Color(0, 0, 0, 0));
		// Draw background
		if (full) docsbtn.drawRect(new Color(0, 0, 200), padding, padding, textS.get_width() + (padding * 2), textS.get_height() + (padding * 2));
		// Draw the text
		docsbtn.blit(textS, padding * 2, padding * 2);
		// Return
		return docsbtn;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		this.onClick.run();
		return Optional.ofNullable(this);
	}
}
