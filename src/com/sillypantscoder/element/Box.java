package com.sillypantscoder.element;

import java.awt.Color;
import java.util.Optional;
import java.util.function.Function;

import com.sillypantscoder.windowlib.Surface;

public class Box extends Element {
	public static class Side {
		public int padding;
		public int border;
		public int margin;
		public Side(int padding, int border, int margin) {
			this.padding = padding;
			this.border = border;
			this.margin = margin;
		}
		public Side() {
			this.padding = 0;
			this.border = 0;
			this.margin = 0;
		}
	}
	public Element child;
	public Side top;
	public Side left;
	public Side right;
	public Side bottom;
	public Color backgroundColor;
	public Color borderColor;
	public Optional<Runnable> onClick;
	public Box(Element child, Optional<Runnable> onClick) {
		this.child = child;
		this.top = new Side();
		this.left = new Side();
		this.right = new Side();
		this.bottom = new Side();
		this.backgroundColor = new Color(255, 255, 255, 0);
		this.borderColor = new Color(0, 0, 0, 0);
		this.onClick = onClick;
	}
	public static Box fromCSS(Element child, Runnable onClick, String css) {
		Function<String, Color> toColor = (s) -> {
			String[] data = s.split(" ");
			int r = Integer.parseInt(data[0]);
			int g = Integer.parseInt(data[1]);
			int b = Integer.parseInt(data[2]);
			if (data.length == 4) {
				int a = Integer.parseInt(data[3]);
				return new Color(r, g, b, a);
			} else return new Color(r, g, b);
		};
		Box b = new Box(child, Optional.ofNullable(onClick));
		for (String rule : css.split("; ")) {
			if (rule.length() == 0) continue;
			String name = rule.split(": ")[0];
			String value = rule.split(": ")[1];
			if (value.endsWith(";")) value = value.substring(0, value.length() - 1);
			if (name.equals("background")) b.backgroundColor = toColor.apply(value);
			else if (name.equals("border-color")) b.borderColor = toColor.apply(value);
			else if (name.split("-").length == 1) {
				int intv = Integer.parseInt(value);
				if (name.equals("padding")) {
					b.top.padding = intv;
					b.left.padding = intv;
					b.right.padding = intv;
					b.bottom.padding = intv;
				} else if (name.equals("border")) {
					b.top.border = intv;
					b.left.border = intv;
					b.right.border = intv;
					b.bottom.border = intv;
				} else if (name.equals("margin")) {
					b.top.margin = intv;
					b.left.margin = intv;
					b.right.margin = intv;
					b.bottom.margin = intv;
				}
			} else {
				String attr = name.split("-")[0];
				String side_str = name.split("-")[1];
				// Find correct side
				Side side = null;
				if (side_str.equals("top")) side = b.top;
				else if (side_str.equals("left")) side = b.left;
				else if (side_str.equals("right")) side = b.right;
				else if (side_str.equals("bottom")) side = b.bottom;
				// Apply attribute
				if (attr.equals("padding")) side.padding = Integer.parseInt(value);
				else if (attr.equals("border")) side.border = Integer.parseInt(value);
				else if (attr.equals("margin")) side.margin = Integer.parseInt(value);
			}
		}
		return b;
	}
	public int getMinWidth() {
		return left.margin + left.border + left.padding + child.getMinWidth() + right.padding + right.border + right.margin;
	}
	public int getMinHeight() {
		return top.margin + top.border + top.padding + child.getMinHeight() + bottom.padding + bottom.border + bottom.margin;
	}
	public Surface draw(int maxWidth, int maxHeight) {
		Surface childSurface = child.draw(maxWidth, maxHeight);
		Surface result = new Surface(getMinWidth(), getMinHeight(), new Color(0, 0, 0, 0));
		result.drawRect(backgroundColor, left.margin, top.margin, getMinWidth() - left.margin - right.margin, getMinHeight() - top.margin - bottom.margin);
		// Draw border
		if (top.border > 0) result.drawLine(borderColor, left.margin, top.margin, left.margin + getMinWidth() - left.margin - right.margin, top.margin, top.border);
		if (left.border > 0) result.drawLine(borderColor, left.margin, top.margin, left.margin, top.margin + getMinHeight() - top.margin - bottom.margin, left.border);
		if (right.border > 0) result.drawLine(borderColor, left.margin + getMinWidth() - left.margin - right.margin, top.margin, left.margin + getMinWidth() - left.margin - right.margin, top.margin + getMinHeight() - top.margin - bottom.margin, right.border);
		if (bottom.border > 0) result.drawLine(borderColor, left.margin, top.margin + getMinHeight() - top.margin - bottom.margin, left.margin + getMinWidth() - left.margin - right.margin, top.margin + getMinHeight() - top.margin - bottom.margin, bottom.border);
		// Draw child
		result.blit(childSurface, left.margin + left.border + left.padding, top.margin + top.border + top.padding);
		System.out.println("box" + result.get_width());
		return result;
	}
	public Optional<Element> elementAtPoint(int maxWidth, int maxHeight, int x, int y) {
		if (y > top.margin && y < top.margin + top.padding + top.border + child.getMinHeight() + bottom.padding + bottom.border) {
			if (x > left.margin && x < left.margin + left.padding + left.border + child.getMinWidth() + right.padding + right.border) {
				this.onClick.ifPresent((v) -> v.run());
				return child.elementAtPoint(maxWidth, maxHeight, x - left.margin - left.padding - left.border, y - top.margin - top.padding - top.border);
			}
		}
		return Optional.empty();
	}
}
