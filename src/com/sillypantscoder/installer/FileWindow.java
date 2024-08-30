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
	public ArrayList<String> dir;
	public ArrayList<FileEntry> entries;
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
		if (this.scrollContainer != null) scrollContainer.scroll = 0;
		this.entries = new ArrayList<FileEntry>();
		// get list of files
		String[] files = new File(getFolderName()).list();
		Arrays.sort(files, new Comparator<String>() {
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		// convert to entries
		for (int i = 0; i < files.length; i++) {
			String filename = files[i];
			File path = new File(getFolderName() + filename);
			FileEntry entry = FileEntry.create(this, path);
			this.entries.add(entry);
		}
		// convert to element
		HzCombine header = new HzCombine(new Color(200, 200, 200), new Element[] {
			new Clickable(this::clickUpFolder, new Image(FOLDER_UP_ICON)),
			new Divider(FOLDER_UP_ICON.get_height(), 8, 2, new Color(100, 100, 100)),
			new Text(getFolderName(), ROW_HEIGHT)
		});
		VCombine vc = new VCombine(new Element[entries.size() + 1]);
		vc.children[0] = header;
		element = vc;
		for (int i = 0; i < entries.size(); i++) {
			Element e = entries.get(i).makeElement();
			vc.children[i + 1] = e;
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
	public Surface getHeader(int width) {
		Surface header = new Surface(width, ROW_HEIGHT, new Color(0, 0, 0, 0));
		//     (background)
		header.drawRect(new Color(200, 200, 200), 0, 0, width, ROW_HEIGHT);
		//     (up icon)
		header.blit(FOLDER_UP_ICON, 0, 0);
		//     (line)
		header.drawLine(new Color(100, 100, 100), ROW_HEIGHT + 4, 0, ROW_HEIGHT + 4, ROW_HEIGHT - 1, 2);
		//     (title)
		Surface title = Surface.renderText(ROW_HEIGHT - 4, getFolderName(), Color.BLACK);
		header.blit(title, ROW_HEIGHT + 8, -4);
		//     (return)
		return header;
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
		public FileEntry(FileWindow window, FileType type, String name) {
			this.window = window;
			this.type = type;
			this.name = name;
		}
		public Element makeElement() {
			HzCombine row = new HzCombine(new Color(0, 0, 0, 0), new Element[] {
				new Image(type.getIcon()),
				new Text(name, ROW_HEIGHT - 4)
			}) {
				public Surface draw(int maxWidth, int maxHeight) {
					Surface s = super.draw(maxWidth, maxHeight);
					return s;
				}
			};
			return new Clickable(this::click, row);
		}
		public void click() {
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
		public static FileEntry create(FileWindow window, File path) {
			if (path.isFile()) {
				return new FileEntry(window, FileType.FILE, path.getName());
			} else {
				if (Arrays.asList(path.list()).contains(".git")) {
					if (new File(path.getAbsolutePath() + "/.git").isDirectory()) {
						return new FileEntry(window, FileType.GIT, path.getName());
					}
				}
				return new FileEntry(window, FileType.FOLDER, path.getName());
			}
		}
	}
	public void clickUpFolder() {
		this.dir.remove(this.dir.size() - 1);
		this.recalculateEntries();
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		this.element.elementAtPoint(width, height, x, y);
	}
	public void mouseWheel(int amount) {
		this.scrollContainer.scroll += amount;
		if (amount < 0) this.scrollContainer.scroll -= 3;
		if (this.scrollContainer.scroll < 0) this.scrollContainer.scroll = 0;
	}
}
