package com.sillypantscoder.windowlib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
// import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class Surface {
	public static Font FONT = null;
	public BufferedImage img;
	public Surface(int width, int height, Color color) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.fill(color);
	}
	public Surface(BufferedImage image) {
		img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(image, 0, 0, new DummyImageObserver());
		g2d.dispose();
	}
	public void fill(Color color) {
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(color);
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
		graphics.dispose();
	}
	public void blit(Surface other, int x, int y) {
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(other.img, x, y, new DummyImageObserver());
		g2d.dispose();
	}
	public void blit(Surface other, int centerX, int centerY, double rotation) {
		Graphics2D g2d = img.createGraphics();
		g2d.translate(centerX, centerY);
		g2d.rotate(Math.toRadians(rotation));
		g2d.translate(-centerX, -centerY);
		g2d.drawImage(other.img, centerX - other.get_width() / 2, centerY - other.get_height() / 2, new DummyImageObserver());
		g2d.dispose();
	}
	public int get_width() {
		return img.getWidth();
	}
	public int get_height() {
		return img.getHeight();
	}
	public Surface copy() {
		Surface r = new Surface(get_width(), get_height(), new Color(0, 0, 0, 0));
		r.blit(this, 0, 0);
		return r;
	}
	public void set_at(int x, int y, Color color) {
		if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
			img.setRGB(x, y, color.getRGB());
		}
	}
	public Color get_at(int x, int y) {
		if (x >= 0 && x < img.getWidth() && y >= 0 && y < img.getHeight()) {
			return new Color(img.getRGB(x, y));
		}
		return Color.BLACK;
	}
	public void drawLine(Color color, int x1, int y1, int x2, int y2, int thickness) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		BasicStroke bs = new BasicStroke(thickness);
		g2d.setStroke(bs);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.dispose();
	}
	public void drawRect(Color color, int x, int y, int width, int height) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(x, y, width, height);
		g2d.dispose();
	}
	public void drawRect(Color color, int x, int y, int width, int height, int lineWidth) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(lineWidth));
		g2d.drawRect(x, y, width, height);
		g2d.dispose();
	}
	public void drawEllipse(Color color, int cx, int cy, int rx, int ry) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.fillOval(cx - rx, cy - ry, rx*2, ry*2);
		g2d.dispose();
	}
	public void drawEllipse(Color color, int cx, int cy, int rx, int ry, int lineWidth) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(lineWidth));
		g2d.drawOval(cx - rx, cy - ry, rx*2, ry*2);
		g2d.dispose();
	}
	public void drawCircle(Color color, int cx, int cy, int r) {
		this.drawEllipse(color, cx, cy, r, r);
	}
	public void drawCircle(Color color, int cx, int cy, int r, int lineWidth) {
		this.drawEllipse(color, cx, cy, r, r, lineWidth);
	}
	public void drawCircle(Color color, double cx, double cy, double r) {
		drawCircle(color, (int)(cx), (int)(cy), (int)(r));
	}
	public Surface scaleValues(float amount) {
		RescaleOp op = new RescaleOp(new float[] { amount, amount, amount, amount }, new float[] { 0, 0, 0, 0 }, null);
		BufferedImage newImg = op.filter(this.img, null);
		return new Surface(newImg);
	}
	public Surface scale_size(int amount) {
		int newWidth = this.img.getWidth() * amount;
		int newHeight = this.img.getHeight() * amount;
		return this.resize(newWidth, newHeight);
	}
	public Surface resize(int newWidth, int newHeight) {
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = newImg.createGraphics();
		g2d.drawImage(this.img, 0, 0, newWidth, newHeight, 0, 0, this.img.getWidth(), this.img.getHeight(), null);
		g2d.dispose();
		return new Surface(newImg);
	}
	public Surface crop(int x, int y, int w, int h) {
		BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = newImg.createGraphics();
		g2d.drawImage(this.img, 0, 0, w, h, x, y, x + w, y + h, null);
		g2d.dispose();
		return new Surface(newImg);
	}
	public Surface flipVertically() {
		BufferedImage newImg = new BufferedImage(this.img.getWidth(), this.img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = newImg.createGraphics();
		g2d.drawImage(this.img, 0, this.img.getHeight(), this.img.getWidth(), 0, 0, 0, this.img.getWidth(), this.img.getHeight(), null);
		g2d.dispose();
		return new Surface(newImg);
	}
	public void writeToFile(String filename) throws IOException {
		File outputfile = new File(filename);
		ImageIO.write(img, "png", outputfile);
	}
	public void save(String name) {
		int n = 1;
		while (new File(name + n + ".png").exists()) {
			n += 1;
		}
		try {
			this.writeToFile(name + n + ".png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Font getFont(int size) {
		// Get base font
		if (FONT == null) {
			FONT = new Font("Monospaced", Font.BOLD, 30);
			// try {
			// 	FONT = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("Segoe UI Bold.ttf"));
			// } catch (FileNotFoundException e) {
			// 	e.printStackTrace();
			// } catch (FontFormatException e) {
			// 	e.printStackTrace();
			// } catch (IOException e) {
			// 	e.printStackTrace();
			// }
		}
		// Derive font of specific size
		return FONT.deriveFont((float)(size));
	}
	public static Surface renderText(int size, String text, Color color) {
		// Measure the text
		Font font = getFont(size);
		Surface measure = new Surface(1, 1, Color.BLACK);
		Graphics2D big = (Graphics2D)(measure.img.getGraphics());
		big.setFont(font);
		FontMetrics fm = big.getFontMetrics();
		Surface ret = new Surface(fm.stringWidth(text), fm.getHeight(), new Color(0, 0, 0, 0));
		// Draw the text
		try {
			Graphics2D g2d = ret.img.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setFont(font);
			g2d.setColor(color);
			g2d.drawString(text, 0, fm.getAscent());
			g2d.dispose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		// Finish
		return ret;
	}
	public static Surface combineVertically(Surface[] surfaces, Color background) {
		int width = 1;
		for (int i = 0; i < surfaces.length; i++) { int w = surfaces[i].get_width(); if (w > width) { width = w; } }
		int height = 1;
		for (int i = 0; i < surfaces.length; i++) { int h = surfaces[i].get_height(); height += h; }
		Surface total = new Surface(width, height, background);
		int cum_y = 0;
		for (int i = 0; i < surfaces.length; i++) {
			total.blit(surfaces[i], 0, cum_y);
			cum_y += surfaces[i].get_height();
		}
		return total;
	}
	public static Surface combineVertically(ArrayList<Surface> surfaces, Color background) {
		return combineVertically(surfaces.toArray(new Surface[surfaces.size()]), background);
	}
	public static class DummyImageObserver implements ImageObserver {
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			return false;
		}
	}
}