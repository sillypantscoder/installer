package com.sillypantscoder.windowlib;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sillypantscoder.windowlib.Surface.DummyImageObserver;

/**
 * A panel that automatically redraws itself.
 */
public class RepaintingPanel extends JPanel {
	private static final long serialVersionUID = 7148504528835036003L;
	public static ArrayList<RepaintingPanel> panelsOpen = new ArrayList<RepaintingPanel>();
	protected JFrame frame;
	public BiFunction<Integer, Integer, BufferedImage> painter;
	public Consumer<String> keyDown;
	public Consumer<String> keyUp;
	public BiConsumer<Integer, Integer> mouseMoved;
	public BiConsumer<Integer, Integer> mouseDown;
	public BiConsumer<Integer, Integer> mouseUp;
	public Consumer<Integer> mouseWheel;
	/**
	* Called by the runtime system whenever the panel needs painting.
	*/
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			g.drawImage(painter.apply(getWidth(), getHeight()), 0, 0, new DummyImageObserver());
		} catch (Throwable t) {
			t.printStackTrace();
			try {
				// set debugger on next line
				Thread.sleep(Duration.ofSeconds(1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void closeWindow() {
		frame.setVisible(false);
		panelsOpen.remove(this);
		if (panelsOpen.size() == 0) {
			System.exit(0);
		}
	}
	public void startAnimation() {
		SwingWorker<Integer, Object> sw = new SwingWorker<Integer, Object>() {
			protected Integer doInBackground() throws Exception {
				while (frame.isVisible()) {
					frame.revalidate();
					frame.getContentPane().repaint();
					Thread.sleep(16);
				}
				return 0;
			}
		};
		sw.execute();
	}
	public void run(String title, Surface icon, int width, int height) {
		frame = new JFrame(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setIconImage(icon.img);
		frame.setVisible(true);
		startAnimation();
		addEventListeners(this);
		panelsOpen.add(this);
	}
	public static void addEventListeners(RepaintingPanel panel) {
		// Add mouse listener
		MouseListener ml = new MouseListener() {
			public void mouseClicked(MouseEvent arg0) { }
			public void mouseEntered(MouseEvent arg0) { }
			public void mouseExited(MouseEvent arg0) { }
			public void mousePressed(MouseEvent arg0) { panel.mouseDown.accept(arg0.getX(), arg0.getY()); }
			public void mouseReleased(MouseEvent arg0) { panel.mouseUp.accept(arg0.getX(), arg0.getY()); }
		};
		panel.addMouseListener(ml);
		// Add mouse motion listener
		MouseMotionListener mml = new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) { panel.mouseMoved.accept(arg0.getX(), arg0.getY()); }
			public void mouseMoved(MouseEvent arg0) { panel.mouseMoved.accept(arg0.getX(), arg0.getY()); }
		};
		panel.addMouseMotionListener(mml);
		// Add keyboard listener
		panel.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e){ panel.keyDown.accept(KeyEvent.getKeyText(e.getKeyCode())); }
			public void keyReleased(KeyEvent e) { panel.keyUp.accept(KeyEvent.getKeyText(e.getKeyCode())); }
			public void keyTyped(KeyEvent e) { }
		});
		// Add mouse wheel listener
		panel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) { panel.mouseWheel.accept((int)(e.getPreciseWheelRotation() * 15)); }
		});
		// Add window listener
		panel.frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) { panel.closeWindow(); }
			public void windowDeactivated(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
		});
	}
}