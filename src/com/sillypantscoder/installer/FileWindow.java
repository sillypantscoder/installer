package com.sillypantscoder.installer;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import com.sillypantscoder.utils.AssetLoader;
import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class FileWindow extends Window {
	public static final int ROW_HEIGHT = 28;
	public static final int SCROLLBAR_WIDTH = 15;
	public static final int SCROLLBAR_HEIGHT = 80;
	public static final Surface FILE_ICON = loadIcon("file.png");
	public static final Surface FOLDER_ICON = loadIcon("folder.png");
	public static final Surface FOLDER_UP_ICON = loadIcon("folder_up.png");
	public static final Surface GIT_ICON = loadIcon("git.png");
	public ArrayList<String> dir;
	public ArrayList<FileEntry> entries;
	public int scroll;
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
		this.scroll = 0;
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
	}
	public Surface frame(int width, int height) {
		this.width = width;
		this.height = height;
		Surface s = new Surface(width, height, Color.WHITE);
		// Draw entries
		for (int i = 0; i < entries.size(); i++) {
			FileEntry e = entries.get(i);
			int drawY = (ROW_HEIGHT * (i + 1)) - scroll;
			Surface row = e.getRow(width);
			s.blit(row, 0, drawY);
		}
		// Draw header (on top)
		s.blit(getHeader(width), 0, 0);
		// Draw scroll bar
		int barX = width - SCROLLBAR_WIDTH;
		double space = (height - SCROLLBAR_HEIGHT) - ROW_HEIGHT;
		double barpos = ((double)(scroll) / ROW_HEIGHT) / entries.size();
		int barY = (int)(space * barpos) + ROW_HEIGHT;
		s.drawRect(new Color(100, 100, 100), barX, barY, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
		// Return
		return s;
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
		public Surface getRow(int width) {
			Surface s = new Surface(width, ROW_HEIGHT, new Color(0, 0, 0, 0));
			// icon
			s.blit(type.getIcon(), 0, 0);
			// text
			Surface text = Surface.renderText(ROW_HEIGHT - 4, this.name, Color.BLACK);
			s.blit(text, ROW_HEIGHT + 4, -4);
			// return
			return s;
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
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public Optional<Integer> mouseScrollbarOffset = Optional.empty();
	public void mouseMoved(int x, int y) {
		mouseScrollbarOffset.ifPresent((v) -> {
			double targetY = y - v;
			double space = (height - SCROLLBAR_HEIGHT) - ROW_HEIGHT;
			double barpos = (targetY - ROW_HEIGHT) / space;
			scroll = (int)(barpos * entries.size() * ROW_HEIGHT);
			// clamp
			if (this.scroll < 0) this.scroll = 0;
		});
	}
	public void mouseDown(int x, int y) {
		if (x >= width - SCROLLBAR_WIDTH) {
			// find current scrollbar pos
			double space = (height - SCROLLBAR_HEIGHT) - ROW_HEIGHT;
			double barpos = ((double)(scroll) / ROW_HEIGHT) / entries.size();
			int barY = (int)(space * barpos) + ROW_HEIGHT;
			// find offset
			int offset = y - barY;
			if (offset < 0 || offset > SCROLLBAR_HEIGHT) offset = SCROLLBAR_HEIGHT / 2;
			mouseScrollbarOffset = Optional.ofNullable(offset);
			mouseMoved(x, y);
		}
	}
	public void mouseUp(int x, int y) {
		// Check for title bar
		if (y <= ROW_HEIGHT) {
			mouseScrollbarOffset = Optional.empty();
			if (x <= ROW_HEIGHT + 4) {
				// Back button is pressed
				this.dir.remove(this.dir.size() - 1);
				this.recalculateEntries();
			}
			return;
		}
		// Stop scrolling
		if (mouseScrollbarOffset.isPresent()) {
			mouseScrollbarOffset = Optional.empty();
			return;
		}
		// Check items
		int itemY = Math.floorDiv((y + scroll) - ROW_HEIGHT, ROW_HEIGHT);
		if (itemY >= entries.size()) return;
		// Click the item!
		FileEntry e = entries.get(itemY);
		e.click();
	}
	public void mouseWheel(int amount) {
		this.scroll += amount;
		if (amount < 0) this.scroll -= 3;
		if (this.scroll < 0) this.scroll = 0;
	}
}
