package com.sillypantscoder.windowlib;

import java.awt.image.BufferedImage;

public abstract class Window {
	public RepaintingPanel panel;
	public Window() {
		panel = new RepaintingPanel();
		panel.painter = this::painter;
		panel.keyDown = this::keyDown;
		panel.keyUp = this::keyUp;
		panel.mouseMoved = this::mouseMoved;
		panel.mouseDown = this::mouseDown;
		panel.mouseUp = this::mouseUp;
		panel.mouseWheel = this::mouseWheel;
	}
	public abstract Surface getIcon();
	public void open(String title, int width, int height) {
		panel.run(title, getIcon(), width, height);
	}
	public void close() {
		panel.closeWindow();
	}
	public BufferedImage painter(int width, int height) {
		return this.frame(width, height).img;
	}
	public abstract Surface frame(int width, int height);
	public abstract void keyDown(String e);
	public abstract void keyUp(String e);
	public abstract void mouseMoved(int x, int y);
	public abstract void mouseDown(int x, int y);
	public abstract void mouseUp(int x, int y);
	public abstract void mouseWheel(int amount);
}
