package com.sillypantscoder.installer;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sillypantscoder.element.BorderBox;
import com.sillypantscoder.element.Button;
import com.sillypantscoder.element.Element;
import com.sillypantscoder.element.HzCombine;
import com.sillypantscoder.element.RepositoryTitle;
import com.sillypantscoder.element.Text;
import com.sillypantscoder.element.VCombine;
import com.sillypantscoder.element.WrappingText;
import com.sillypantscoder.windowlib.Surface;
import com.sillypantscoder.windowlib.Window;

public class NewWindow extends Window {
	public File dir;
	public Element element;
	public int width;
	public int height;
	public NewWindow(File dir) {
		super();
		this.width = 400;
		this.height = 400;
		this.dir = dir;
		this.makeElement();
		this.open("Install", width, height);
	}
	public Surface getIcon() {
		return FileWindow.NEW_ICON;
	}
	public void makeElement() {
		element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Install", 20, true)
			}),
			new Text("Paste GitHub URL:", 16, false),
			new Button(this::clickPaste, "Click to paste", true)
		});
	}
	public Surface frame(int width, int height) {
		this.width = width;
		this.height = height;
		// Draw window
		return element.draw(width, height);
	}
	public void clickPaste() {
		String data = getClipboardContents();
		Matcher matcher = Pattern.compile("^https://github.com/[a-zA-Z0-9]+/[a-zA-Z0-9\\-]+$").matcher(data);
		if (matcher.find()) {
			String url = matcher.group(0);
			new Thread(() -> showRepoData(url), "git-clone-thread").start();
		} else {
			element = new VCombine(new Element[] {
				new HzCombine(new Color(200, 200, 200), new Element[] {
					new Text("Install", 20, true)
				}),
				new Text("Paste GitHub URL:", 16, false),
				new Button(() -> {}, "Click to paste", true),
				new Text("Invalid URL", 16, true)
			});
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				makeElement();
			}, "promise").start();
		}
	}
	public void keyDown(String e) {}
	public void keyUp(String e) {}
	public void mouseMoved(int x, int y) {}
	public void mouseDown(int x, int y) {}
	public void mouseUp(int x, int y) {
		this.element.elementAtPoint(width, height, x, y);
	}
	public void mouseWheel(int amount) {}
	public String getClipboardContents() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				String data = (String)(contents.getTransferData(DataFlavor.stringFlavor));
				return data;
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	public void showRepoData(String url) {
		// Find repo name
		String[] parts = url.split("/");
		String user = parts[3];
		String repoName = parts[4];
		// Find repo description
		element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Install", 20, true)
			}),
			new Text("Paste GitHub URL:", 16, false),
			new Button(() -> {}, "Click to paste", true) {
				public Surface draw(int maxWidth, int maxHeight) {
					this.backgroundColor = new Color(128, 128, 128);
					return super.draw(maxWidth, maxHeight);
				}
			},
			new Text("Downloading data...", 16, true)
		});
		String description = getRepoDescription(user, repoName);
		// Show details
		element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Install", 20, true)
			}),
			new BorderBox(8, new Color(0, 0, 200), 2, 8, new Color(200, 200, 255), new VCombine(new Element[] {
				new RepositoryTitle(user, repoName, 20),
				new WrappingText(description, 16, false),
				new HzCombine(new Color(0, 0, 0, 0), new Element[] {
					new Button(this::makeElement, "Cancel", true),
					new Button(() -> new Thread(() -> cloneRepo(user, repoName)).start(), "Install", true)
				})
			}))
		});
	}
	public String getRepoDescription(String user, String repoName) {
		String data = makeGetRequest("https://github.com/" + user + "/" + repoName);
		Matcher matcher = Pattern.compile("<p class=\"f4 mb-3 \">([ \t\n!-~]*)</p>").matcher(data);
		if (matcher.find()) {
			String description = matcher.group(1).strip();
			return description;
		} else {
			return "(Error finding description.)";
		}
	}
	public String makeGetRequest(String urlString) {
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			/* Now read the retrieved document from the stream. */
			byte[] data = is.readAllBytes();
			String d = new String(data);
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}
	public void cloneRepo(String user, String repoName) {
		element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Installing!", 20, true)
			}),
			new Text("Please wait...", 16, false),
		});
		Update.runProcess(dir, true, new String[] { "git", "clone", "https://github.com/" + user + "/" + repoName });
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		element = new VCombine(new Element[] {
			new HzCombine(new Color(200, 200, 200), new Element[] {
				new Text("Successfully installed!", 20, true)
			}),
			new Text("The program has been installed!", 16, false),
			new Button(() -> replaceWithGit(repoName), "Continue", true)
		});
	}
	public void replaceWithGit(String repoName) {
		new GitWindow(dir.getAbsolutePath() + "/" + repoName);
		this.close();
	}
}
