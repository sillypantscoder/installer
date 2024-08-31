package com.sillypantscoder.installer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.sillypantscoder.element.BorderBox;
import com.sillypantscoder.element.Button;
import com.sillypantscoder.element.Element;
import com.sillypantscoder.element.HzCombine;
import com.sillypantscoder.element.Text;
import com.sillypantscoder.element.VCombine;
import com.sillypantscoder.element.WrappingText;

public class Update {
	public static enum UpdateMode {
		MERGE,
		RESET,
		YOU_ARE_HERE;
		public String getButtonText() {
			if (this == RESET) return "Reset to here";
			if (this == YOU_ARE_HERE) return "(You are here)";
			return "Download Update";
		}
		public boolean isFull() {
			return this != YOU_ARE_HERE;
		}
		public void apply(File path, String commitID) {
			if (this == YOU_ARE_HERE) return;
			if (this == RESET) {
				runProcess(path, true, new String[] { "git", "reset", "--hard", commitID });
				return;
			} else {
				runProcess(path, true, new String[] { "git", "merge", commitID });
			}
		}
	}
	public GitWindow window;
	public String id;
	public UpdateMode mode;
	public String name;
	public LocalDateTime date;
	public String extraInfo;
	public Update(GitWindow window, String id, UpdateMode mode, String name, LocalDateTime date, String extraInfo) {
		this.window = window;
		this.id = id;
		this.mode = mode;
		this.name = name;
		this.date = date;
		this.extraInfo = extraInfo;
	}
	public Element makeElement() {
		return new BorderBox(8, new Color(0, 0, 200), 2, 8, new Color(200, 200, 255), new VCombine(new Element[] {
			new Text("Update is available:", 16, false),
			new Text(name, 20, true),
			new WrappingText(extraInfo, 16, false),
			new Button(this::applyUpdate, mode.getButtonText(), mode.isFull())
		}));
	}
	public void applyUpdate() {
		if (mode.isFull() == false) return;
		window.element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Updating...", 24, true)
			}),
			new Text("Please wait...", 16, false)
		});
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mode.apply(window.path, id);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.update = Update.checkForNextUpdate(window);
			window.makeElement();
		}, "git-update-thread").start();
	}
	// Check For Updates
	public static Optional<Update> checkForNextUpdate(GitWindow window) {
		runProcess(window.path, false, new String[] { "git", "fetch" });
		String logData = runProcess(window.path, false, new String[] { "git", "log", "main..origin/main", "--format=oneline" });
		// Examine log data for updates
		if (logData.length() == 0) {
			// there are no updates
			return Optional.empty();
		}
		String[] commits = logData.split("\n");
		String commitID = commits[commits.length - 1].split("\n")[0].split(" ")[0];
		// Get data about most recent commit
		return Optional.ofNullable(getUpdateFromID(window, commitID, UpdateMode.MERGE));
	}
	public static Update[] getAllUpdates(GitWindow window) {
		runProcess(window.path, false, new String[] { "git", "fetch" });
		String logData = runProcess(window.path, false, new String[] { "git", "log", "origin/main", "--format=oneline" });
		String currentCommitID = runProcess(window.path, false, new String[] { "git", "show", "--format=oneline", "-s" }).split(" ")[0];
		// Examine log data for updates
		String[] commits = logData.split("\n");
		Update[] updates = new Update[commits.length];
		for (int i = 0; i < commits.length; i++) {
			String commitID = commits[i].split("\n")[0].split(" ")[0];
			updates[i] = getUpdateFromID(window, commitID, commitID.equals(currentCommitID) ? UpdateMode.YOU_ARE_HERE : UpdateMode.RESET);
		}
		// Get data about most recent commit
		return updates;
	}
	public static Update getUpdateFromID(GitWindow window, String commitID, UpdateMode mode) {
		String[] commitData = runProcess(window.path, false, new String[] { "git", "log", commitID, "-1", "--date=iso-strict" }).split("\n");
		if (commitData.length == 0 && commitData[0].length() == 0) return null;
		// - Parse date
		LocalDateTime date = LocalDateTime.parse(commitData[2].substring(8), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		// - Find title and body
		String[] titleAndBody = new String[commitData.length - 4];
		System.arraycopy(commitData, 4, titleAndBody, 0, commitData.length - 4);
		String title = titleAndBody[0].strip();
		String[] body = new String[0];
		if (titleAndBody.length >= 3) {
			body = new String[titleAndBody.length - 2];
			for (int i = 2; i < titleAndBody.length; i++) {
				body[i - 2] = titleAndBody[i].strip();
			}
		}
		String bodyString = String.join("\n", body);
		return new Update(window, commitID, mode, title, date, bodyString);
	}
	private static String runProcess(File path, boolean inheritIO, String[] args) {
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.directory(path);
		if (inheritIO) builder.inheritIO();
		try {
			Process p = builder.start();
			p.waitFor();
			if (!inheritIO) {
				InputStream stream = p.getInputStream();
				String data = new String(stream.readAllBytes());
				return data;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}
}
