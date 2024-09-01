package com.sillypantscoder.element;

import java.awt.Color;

import com.sillypantscoder.windowlib.Surface;

public class RepositoryTitle extends Image {
	public String user;
	public String repoName;
	public int size;
	public RepositoryTitle(String user, String repoName, int size) {
		super(new Surface(1, 1, Color.RED));
		this.user = user;
		this.repoName = repoName;
		this.size = size;
		this.updateText();
	}
	public void updateText() {
		image = Surface.combineHorizontally(new Surface[] {
			Surface.renderText(size, false, user + " / ", Color.BLACK),
			Surface.renderText(size, true, repoName, Color.BLACK)
		}, new Color(0, 0, 0, 0));
	}
}
