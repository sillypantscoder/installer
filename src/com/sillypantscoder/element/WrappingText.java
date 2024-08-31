package com.sillypantscoder.element;

import java.awt.Color;
import java.util.ArrayList;

import com.sillypantscoder.windowlib.Surface;

public class WrappingText extends Text {
	public int maxWidth = Integer.MAX_VALUE;
	public WrappingText(String text, int size, boolean bold) {
		super(text, size, bold);
	}
	public Surface draw(int maxWidth, int maxHeight) {
		if (this.maxWidth != maxWidth) {
			this.maxWidth = maxWidth;
			this.updateText();
		}
		return super.draw(maxWidth, maxHeight);
	}
	public void updateText() {
		image = renderWrappedTextWithNewlines();
	}
	public ArrayList<Surface> renderWrappedText(String line) {
		ArrayList<Surface> finishedLines = new ArrayList<Surface>();
		String currentText = "";
		String[] words = line.split("(?=[ ])");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (Surface.renderText(size, bold, currentText + word, Color.BLACK).get_width() > maxWidth) {
				finishedLines.add(Surface.renderText(size, bold, currentText, Color.BLACK));
				currentText = "";
			}
			currentText += word;
		}
		finishedLines.add(Surface.renderText(size, bold, currentText, Color.BLACK));
		return finishedLines;
	}
	public Surface renderWrappedTextWithNewlines() {
		ArrayList<Surface> lines = new ArrayList<Surface>();
		String[] textLines = text.split("\n");
		for (int i = 0; i < textLines.length; i++) {
			lines.addAll(renderWrappedText(textLines[i]));
		}
		Surface result = Surface.combineVertically(lines, new Color(0, 0, 0, 0));
		return result;
	}
}
