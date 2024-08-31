package com.sillypantscoder.element;

import java.awt.Color;

import com.sillypantscoder.windowlib.Surface;

public class Text extends Image {
	public String text;
	public int size;
	public boolean bold;
	public Text(String text, int size, boolean bold) {
		super(new Surface(1, 1, Color.RED));
		this.text = text;
		this.size = size;
		this.bold = bold;
		this.updateText();
	}
	public void updateText() {
		image = Surface.renderText(size, bold, text, Color.BLACK);
	}
}
