package com.sillypantscoder.installer;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.sillypantscoder.element.Clickable;
import com.sillypantscoder.element.Divider;
import com.sillypantscoder.element.Element;
import com.sillypantscoder.element.HzCombine;
import com.sillypantscoder.element.Image;
import com.sillypantscoder.element.PaddedText;
import com.sillypantscoder.element.ScrollContainer;
import com.sillypantscoder.element.Text;
import com.sillypantscoder.element.VCombine;
import com.sillypantscoder.utils.AssetLoader;
import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class FileWindow extends Window {
	public static final int ROW_HEIGHT = 28;
	public static final Surface FILE_ICON = loadIcon("file.png");
	public static final Surface FOLDER_ICON = loadIcon("folder.png");
	public static final Surface FOLDER_UP_ICON = loadIcon("folder_up.png");
	public static final Surface GIT_ICON = loadIcon("git.png");
	public static final Surface NOT_ALLOWED_ICON = loadIcon("not_allowed.png");
	public static final Surface NEW_ICON = loadIcon("plus.png");
	public ArrayList<String> dir;
	public ArrayList<FileEntry> repositories;
	public ArrayList<FileEntry> folders;
	public ArrayList<FileEntry> files;
	public Element element;
	public ScrollContainer scrollContainer;
	public int width;
	public int height;
	public FileWindow() {
		super();
		this.width = 800;
		this.height = 400;
		this.open("Files", width, height);
		this.dir = new ArrayList<String>();
		String[] cwd = System.getProperty("user.dir").substring(1).split("/");
		for (String d : cwd) this.dir.add(d);
		this.dir.remove(this.dir.size() - 1);
		this.recalculateEntries();
	}
	public static Surface loadIcon(String filename) {
		final int padding = 6;
		Surface icon = AssetLoader.loadImage(filename).resize(ROW_HEIGHT - (padding * 2), ROW_HEIGHT - (padding * 2));
		Surface padded = new Surface(ROW_HEIGHT, ROW_HEIGHT, new Color(0, 0, 0, 0));
		padded.blit(icon, padding, padding);
		return padded;
	}
	public Surface getIcon() {
		return FOLDER_ICON;
	}
	public void recalculateEntries() {
		// reset
		if (this.scrollContainer != null) scrollContainer.scroll = 0;
		this.repositories = new ArrayList<FileEntry>();
		this.folders = new ArrayList<FileEntry>();
		this.files = new ArrayList<FileEntry>();
		// get list of files
		String[] items = new File(getFolderName()).list();
		Arrays.sort(items, new Comparator<String>() {
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		// convert to entries
		for (int i = 0; i < items.length; i++) {
			String filename = items[i];
			if (filename.startsWith(".")) continue;
			File path = new File(getFolderName() + filename);
			FileEntry entry = FileEntry.create(this, path);
			entry.add();
		}
		// convert to element
		HzCombine header = new HzCombine(new Color(200, 200, 200), new Element[] {
			new Clickable(this::clickUpFolder, new Image(FOLDER_UP_ICON)),
			new Divider(FOLDER_UP_ICON.get_height(), 8, 2, new Color(100, 100, 100)),
			new Clickable(this::clickNew, new Image(NEW_ICON)),
			new Divider(FOLDER_UP_ICON.get_height(), 8, 2, new Color(100, 100, 100)),
			new Text(getFolderName(), ROW_HEIGHT, true)
		});
		VCombine scroll = new VCombine(new Element[0]);
		scrollContainer = new ScrollContainer(scroll);
		element = new VCombine(new Element[] {
			header,
			scrollContainer
		});
		// Add repos
		if (repositories.size() > 0) {
			scroll.appendChild(new PaddedText("Git", 14, true, 12, 4));
			for (int i = 0; i < repositories.size(); i++) {
				Element e = repositories.get(i).makeElement();
				scroll.appendChild(e);
			}
		}
		// Add folders
		if (folders.size() > 0) {
			scroll.appendChild(new PaddedText("Folders", 14, true, 12, 4));
			for (int i = 0; i < folders.size(); i++) {
				Element e = folders.get(i).makeElement();
				scroll.appendChild(e);
			}
		}
		// Add files
		if (files.size() > 0) {
			scroll.appendChild(new PaddedText("Files", 14, true, 12, 4));
			for (int i = 0; i < files.size(); i++) {
				Element e = files.get(i).makeElement();
				scroll.appendChild(e);
			}
		}
	}
	public Surface frame(int width, int height) {
		this.width = width;
		this.height = height;
		// Draw window
		if (element == null) recalculateEntries();
		return element.draw(width, height);
	}
	public String getFolderName() {
		String result = "/";
		for (String v : dir) {
			result += v + "/";
		}
		return result;
	}
	public static class FileEntry {
		public static enum FileType {
			FILE,
			FOLDER,
			GIT;
			public Surface getIcon() {
				if (this == FOLDER) return FOLDER_ICON;
				if (this == GIT) return GIT_ICON;
				return FILE_ICON;
			}
		}
		public FileWindow window;
		public FileType type;
		public String name;
		public boolean error;
		public FileEntry(FileWindow window, FileType type, String name, boolean error) {
			this.window = window;
			this.type = type;
			this.name = name;
			this.error = error;
		}
		public Element makeElement() {
			HzCombine row = new HzCombine(new Color(0, 0, 0, 0), new Element[] {
				new Image(error ? NOT_ALLOWED_ICON : type.getIcon()),
				new Text(name, ROW_HEIGHT - 4, false)
			});
			return new Clickable(this::click, row);
		}
		public void click() {
			if (this.error) return;
			if (this.type == FileType.FOLDER) {
				window.dir.add(this.name);
				window.recalculateEntries();
				return;
			}
			if (this.type == FileType.GIT) {
				new GitWindow(window.getFolderName() + this.name);
				return;
			}
		}
		public void add() {
			if (this.type == FileType.GIT) {
				window.repositories.add(this);
				return;
			}
			if (this.type == FileType.FOLDER) {
				window.folders.add(this);
				return;
			}
			window.files.add(this);
		}
		public static FileEntry create(FileWindow window, File path) {
			if (path.isFile()) {
				return new FileEntry(window, FileType.FILE, path.getName(), false);
			} else {
				String[] paths = path.list();
				if (paths == null) return new FileEntry(window, FileType.FOLDER, path.getName(), true);
				if (Arrays.asList(paths).contains(".git")) {
					if (new File(path.getAbsolutePath() + "/.git").isDirectory()) {
						return new FileEntry(window, FileType.GIT, path.getName(), false);
					}
				}
				return new FileEntry(window, FileType.FOLDER, path.getName(), false);
			}
		}
	}
	public void clickUpFolder() {
		this.dir.remove(this.dir.size() - 1);
		this.recalculateEntries();
	}
	public void clickNew() {
		new NewWindow(new File(this.getFolderName()));
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		this.element.elementAtPoint(width, height, x, y);
	}
	public void mouseWheel(int amount) {
		this.scrollContainer.scrollBy(amount);
	}
}
