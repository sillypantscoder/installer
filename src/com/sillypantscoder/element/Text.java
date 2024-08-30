package com.sillypantscoder.element;

import java.awt.Color;

import com.sillypantscoder.windowlib.Surface;

public class Text extends Image {
	public String text;
	public int size;
	public Text(String text, int size) {
		super(new Surface(1, 1, Color.RED));
		this.text = text;
		this.size = size;
		this.updateText();
	}
	public void updateText() {
		image = Surface.renderText(size, text, Color.BLACK);
	}
}
