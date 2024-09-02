package com.sillypantscoder.installer;

import java.awt.Color;

import com.sillypantscoder.element.Button;
import com.sillypantscoder.element.Divider;
import com.sillypantscoder.element.Element;
import com.sillypantscoder.element.HzCombine;
import com.sillypantscoder.element.Image;
import com.sillypantscoder.element.Text;
import com.sillypantscoder.element.VCombine;
import com.sillypantscoder.element.WrappingText;
import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class ErrorWindow extends Window {
	public String message;
	public VCombine element;
	public Button[] buttons;
	public int width = 550;
	public int height = 250;
	public ErrorWindow(String message, Button[] buttons) {
		super();
		this.message = message;
		this.buttons = buttons;
		this.makeElement();
		this.open("Error", width, height);
	}
	public Surface getIcon() {
		return FileWindow.NOT_ALLOWED_ICON;
	}
	public void makeElement() {
		HzCombine header = new HzCombine(new Color(200, 200, 200), new Element[] {
			new Image(FileWindow.NOT_ALLOWED_ICON),
			new Divider(FileWindow.NOT_ALLOWED_ICON.get_height(), 8, 2, new Color(100, 100, 100)),
			new Text("Error!", 24, true)
		});
		WrappingText messageDisplay = new WrappingText(message, 16, false);
		this.element = new VCombine(new Element[buttons.length + 3]);
		this.element.children[0] = header;
		this.element.children[1] = messageDisplay;
		for (int i = 0; i < buttons.length; i++) {
			this.element.children[i + 2] = buttons[i];
		}
		this.element.children[buttons.length + 2] = new Button(this::close, "Close", true);
	}
	public Surface frame(int width, int height) {
		this.width = width;
		this.height = height;
		if (this.element == null) this.element = new VCombine(new Element[] { new Image(FileWindow.NOT_ALLOWED_ICON) });
		Surface s = new Surface(width, height, Color.WHITE);
		// Draw element
		s.blit(this.element.draw(width, height), 0, 0);
		// Finish
		return s;
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		this.element.elementAtPoint(width, height, x, y);
	}
	public void mouseWheel(int amount) {}
}
