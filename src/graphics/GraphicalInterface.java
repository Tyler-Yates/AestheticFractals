package graphics;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import fractals.Fractal;
import fractals.Generator;
import fractals.ImageManager;

/**
 * Opens JFrame with nine boxes.
 * 
 */
public class GraphicalInterface extends JPanel implements MouseMotionListener,
		MouseListener, KeyListener, ActionListener {
	private static final long serialVersionUID = 749344840243728058L;

	public static JFrame frame;
	public static JLayeredPane selector;

	public static boolean selectedFractals[] = new boolean[9];

	static int windowWidth = 1024, windowHeight = 768;
	static int mouseX, mouseY; // Mouse location on the screen

	private static final double VERSION = 0.1;

	private static int selectedBoxX, selectedBoxY, menuOpenForFractalNum;
	
	private static Color bgColor       = Color.black;
	private static Color fgColor       = Color.white;
	private static Color selectedColor = new Color(0, 255, 0, 15);
	private static Color hoverColor    = new Color(255, 255, 255, 15);
	
	private static boolean fullScreen = false;
	
	/**
	 * Initializes the JFrame
	 */
	public GraphicalInterface() {
		frame = new JFrame("Aesthetic Fractal v" + VERSION);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(windowWidth, windowHeight);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.addKeyListener(this);
		frame.add(this);
	}

	public static void main(String args[]) throws IOException,
			InterruptedException {
		Generator.generateNewGeneration();
		new GraphicalInterface();
	}

	public static void toggleFullScreen() {
		if (fullScreen) {
			frame.setExtendedState(Frame.NORMAL);
		} else {
			frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);  
		}
		
		fullScreen = !fullScreen;
	}
	
	public void drawInterface(Graphics g) {
		g.setColor(bgColor);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

		int boxWidth = frame.getWidth() / 3;
		int boxHeight = frame.getHeight() / 3;

		for (int i = 0; i < 9; i++) {
			int x = i % 3 * boxWidth;
			int y = i / 3 * boxHeight;

			g.setColor(fgColor);
			if (ImageManager.indexIsReady(i))
				drawImage(g, i, x, y);
			else
				g.drawString("Loading...", x + 20, y + 20);
			drawBox(g, x, y, boxWidth, boxHeight);
			drawSelectedBoxes(g);
		}
	}

	private void drawSelectedBoxes(Graphics g) {
		g.setColor(selectedColor);
		for (int i = 0; i < selectedFractals.length; i++) {
			int x = i % 3 * frame.getWidth() / 3;
			int y = i / 3 * frame.getHeight() / 3;

			if (selectedFractals[i]) {
				g.fillRect(x, y, frame.getWidth() / 3, frame.getHeight() / 3);
			}
		}
	}

	private void drawPopupForFractal(MouseEvent e, int boxIndex) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem renderItem = new JMenuItem("Render fractal");
		renderItem.addActionListener(this);
		menu.add(renderItem);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	public void drawBox(Graphics g, int x, int y, int width, int height) {
		g.setColor(fgColor);
		g.drawRect(x, y, width, height);
	}

	public void drawImage(Graphics g, int index, int x, int y) {
		Generator.drawImage(index, g, x, y);
	}

	public void drawSelectedBox(Graphics g) {
		int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
		int t = mouseY / (frame.getHeight() / 3) * (frame.getHeight() / 3);

		g.setColor(hoverColor);
		g.fillRect(l + 1, t + 1, frame.getWidth() / 3 - 2,
				frame.getHeight() / 3 - 2);
	}

	public void paint(Graphics g) {
		drawInterface(g);
		drawSelectedBox(g);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		mouseX = x - frame.getInsets().left;
		mouseY = y - frame.getInsets().top;

		int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
		int t = mouseY / (frame.getHeight() / 3) * (frame.getHeight() / 3);

		if (l != selectedBoxX | t != selectedBoxY) {
			selectedBoxX = l;
			selectedBoxY = t;
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		mouseX = x - frame.getInsets().left;
		mouseY = y - frame.getInsets().top;

		int boxCol = mouseX / (frame.getWidth() / 3);
		int boxRow = mouseY / (frame.getHeight() / 3);

		int boxIndex = (boxRow * 3) + boxCol;

        if (SwingUtilities.isRightMouseButton(e)) {
            drawPopupForFractal(e, boxIndex);
            menuOpenForFractalNum = boxIndex;
        } else {
        	selectedFractals[boxIndex] = !selectedFractals[boxIndex];
        }
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Generator.generateNewGeneration();
			selectedFractals = new boolean[9];
			repaint();
		} else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
		    Generator.decrementGeneration();
		} else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
		    Generator.incrementGeneration();
		} else if(e.getKeyCode()==KeyEvent.VK_F) {
			toggleFullScreen();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("Render fractal")) {
			try {
				Generator.renderFractalInGL(menuOpenForFractalNum);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
