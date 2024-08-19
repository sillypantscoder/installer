package com.sillypantscoder.installer;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class GitWindow extends Window {
	public File path;
	public int hasUpdates;
	public GitWindow(String path) {
		super();
		this.path = new File(path);
		this.open(this.path.getName(), 600, 700);
		this.checkForUpdates();
	}
	public Surface getIcon() {
		return FileWindow.GIT_ICON;
	}
	public Surface frame(int width, int height) {
		Surface s = new Surface(width, height, Color.WHITE);
		int cum_y = 0;
		// Header
		Surface header = getHeader(width);
		s.blit(header, 0, 0);
		cum_y += header.get_height();
		// Docs button
		Surface docsbtn = getDocsButton();
		s.blit(docsbtn, 0, cum_y);
		cum_y += docsbtn.get_height();
		// Finish
		return s;
	}
	public Surface getHeader(int width) {
		Surface header = new Surface(width, 28, new Color(0, 0, 0, 0));
		//     (background)
		header.drawRect(new Color(200, 200, 200), 0, 0, width, 28);
		//     (title)
		Surface title = Surface.renderText(24, path.getName(), Color.BLACK);
		header.blit(title, 0, -4);
		//     (return)
		return header;
	}
	public Surface getDocsButton() {
		boolean hasDocs = new File(path.getAbsolutePath() + "/README.md").exists();
		// Render the text
		String t = hasDocs ? "Open documentation" : "No documentation is available";
		Surface text = Surface.renderText(16, t, hasDocs ? Color.WHITE : new Color(150, 150, 150));
		// Create the surface
		int padding = 4;
		Surface docsbtn = new Surface(text.get_width() + (padding * 4), text.get_height() + (padding * 4), new Color(0, 0, 0, 0));
		// Draw background
		if (hasDocs) docsbtn.drawRect(new Color(0, 0, 200), padding, padding, text.get_width() + (padding * 2), text.get_height() + (padding * 2));
		// Draw the text
		docsbtn.blit(text, padding * 2, padding * 2);
		// Return
		return docsbtn;
	}
	public Surface getUpdateRow() {
		return null;
	}
	public void checkForUpdates() {
		ProcessBuilder builder = new ProcessBuilder("git", "status");
		builder.directory(path);
		try {
			Process p = builder.start();
			p.waitFor();
			InputStream stream = p.getInputStream();
			String data = new String(stream.readAllBytes());
			checkForUpdates_FromGit(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void checkForUpdates_Fetch() {
		ProcessBuilder builder = new ProcessBuilder("git", "fetch");
		builder.directory(path);
		try {
			Process p = builder.start();
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void checkForUpdates_FromGit(String data) {
		// "\nYour branch is behind 'origin\\/main' by ([1-9][0-9]*) commits, and can be fast-forwarded\\.\n"
		Matcher m = Pattern.compile("\nYour branch is behind 'origin\\/main' by ([1-9][0-9]*) commits, and can be fast-forwarded\\.\n").matcher(data);
		if (m.find()) {
			// we can update!
			String amount = m.group(1);
			hasUpdates = Integer.parseInt(amount);
		} else {
			// we can not update!
			hasUpdates = 0;
		}
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		int cum_y = 0;
		// Header
		Surface header = getHeader(10);
		cum_y += header.get_height();
		if (y < cum_y) return;
		// Docs button
		Surface docsbtn = getDocsButton();
		cum_y += docsbtn.get_height();
		if (y < cum_y) { clickDocsButton(); return; }
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
	public void mouseWheel(int amount) {}
	public static Surface makeButton(String text, int textSize, int padding, boolean full) {
		// Render the text
		Surface textS = Surface.renderText(16, text, full ? Color.WHITE : new Color(150, 150, 150));
		// Create the surface
		Surface docsbtn = new Surface(textS.get_width() + (padding * 4), textS.get_height() + (padding * 4), new Color(0, 0, 0, 0));
		// Draw background
		if (full) docsbtn.drawRect(new Color(0, 0, 200), padding, padding, textS.get_width() + (padding * 2), textS.get_height() + (padding * 2));
		// Draw the text
		docsbtn.blit(textS, padding * 2, padding * 2);
		// Return
		return docsbtn;
	}
}
