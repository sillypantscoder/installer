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
	public GitWindow window;
	public String id;
	public String name;
	public LocalDateTime date;
	public String extraInfo;
	public Update(GitWindow window, String id, String name, LocalDateTime date, String extraInfo) {
		this.window = window;
		this.id = id;
		this.name = name;
		this.date = date;
		this.extraInfo = extraInfo;
	}
	public Element makeElement() {
		return new BorderBox(8, new Color(0, 0, 200), 2, 8, new Color(200, 200, 255), new VCombine(new Element[] {
			new Text("Update is available:", 16, false),
			new Text(name, 20, true),
			new WrappingText(extraInfo, 16, false),
			new Button(this::applyUpdate, "Download Update", true)
		}));
	}
	public void applyUpdate() {
		window.element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Updating...", 24, true)
			}),
			new Text("Please wait...", 16, false)
		});
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runProcess(window.path, true, new String[] { "git", "merge", id });
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			window.makeElement();
		}, "git-update-thread").start();
	}
	// Check For Updates
	public static Optional<Update> checkForUpdates(GitWindow window) {
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
		String[] commitData = runProcess(window.path, false, new String[] { "git", "log", commitID, "-1", "--date=iso-strict" }).split("\n");
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
		// Finish
		return Optional.ofNullable(new Update(window, commitID, title, date, bodyString));
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
