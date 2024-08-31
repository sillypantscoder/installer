package com.sillypantscoder.installer;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.sillypantscoder.element.Button;
import com.sillypantscoder.element.Clickable;
import com.sillypantscoder.element.Divider;
import com.sillypantscoder.element.Element;
import com.sillypantscoder.element.HzCombine;
import com.sillypantscoder.element.Image;
import com.sillypantscoder.element.ScrollContainer;
import com.sillypantscoder.element.Text;
import com.sillypantscoder.element.VCombine;
import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class GitWindow extends Window {
	public static final Surface BACK_ICON = FileWindow.loadIcon("baseline_west_black_48dp.png");
	public File path;
	public Element element;
	public int width = 600;
	public int height = 700;
	public Optional<Update> update;
	public GitWindow(String path) {
		super();
		this.path = new File(path);
		this.update = Update.checkForNextUpdate(this);
		this.makeElement();
		this.open(this.path.getName(), width, height);
	}
	public Surface getIcon() {
		return FileWindow.GIT_ICON;
	}
	public void makeElement() {
		VCombine main = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text(path.getName(), 24, true)
			}),
			getDocsButton(),
			update.map((v) -> v.makeElement()).orElse(new Button(() -> {}, "No updates are available", false)),
			new Button(this::clickHistoryButton, "View update history", true)
		});
		this.element = main;
	}
	public Surface frame(int width, int height) {
		this.width = width;
		this.height = height;
		if (this.element == null) this.element = new Image(FileWindow.GIT_ICON);
		Surface s = new Surface(width, height, Color.WHITE);
		// Draw element
		s.blit(this.element.draw(width, height), 0, 0);
		// Finish
		return s;
	}
	public Element getDocsButton() {
		boolean hasDocs = new File(path.getAbsolutePath() + "/README.md").exists();
		// Get the settings
		String t = hasDocs ? "Open documentation" : "No documentation is available";
		Runnable onClick = hasDocs ? this::clickDocsButton : () -> {};
		// Create element
		return new Button(onClick, t, hasDocs);
	}
	public Surface getUpdateRow() {
		return null;
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		this.element.elementAtPoint(width, height, x, y);
	}
	public void clickDocsButton() {
		File docsFile = new File(path.getAbsolutePath() + "/README.md");
		if (! docsFile.exists()) return;
		try {
			Desktop.getDesktop().open(docsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void clickHistoryButton() {
		HzCombine header = new HzCombine(new Color(200, 200, 200), new Element[] {
			new Clickable(this::makeElement, new Image(BACK_ICON)),
			new Divider(BACK_ICON.get_height(), 8, 2, new Color(100, 100, 100)),
			new Text("Update history", 24, true)
		});
		Update[] updates = Update.getAllUpdates(this);
		VCombine main = new VCombine(new Element[updates.length]);
		this.element = new VCombine(new Element[] {
			header,
			new ScrollContainer(main)
		});
		for (int i = 0; i < updates.length; i++) {
			main.children[i] = updates[i].makeElement();
		}
	}
	public void mouseWheel(int amount) {
		if (this.element instanceof VCombine vc) {
			if (vc.children.length >= 2) {
				if (vc.children[1] instanceof ScrollContainer scroll) {
					scroll.scrollBy(amount);
				}
			}
		}
	}
}
