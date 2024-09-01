package com.sillypantscoder.element;

import java.awt.Color;

import com.sillypantscoder.windowlib.Surface;

public class PaddedText extends Text {
	public int paddingTop;
	public int paddingBottom;
	public PaddedText(String text, int size, boolean bold, int paddingTop, int paddingBottom) {
		super(text, size, bold);
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.updateText();
	}
	public void updateText() {
		super.updateText();
		Surface padded = new Surface(image.get_width(), image.get_height() + paddingTop + paddingBottom, new Color(0, 0, 0, 0));
		padded.blit(image, 0, paddingTop);
		image = padded;
	}
}
