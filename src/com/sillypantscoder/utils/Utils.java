package com.sillypantscoder.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class Utils {
	public static String runProcess(File path, boolean inheritIO, String[] args) {
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
	public static void openWebsite(String url) {
		try {
			URI uri = new URI(url);
			Desktop.getDesktop().browse(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
