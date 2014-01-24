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

    //The window
	public static JFrame frame;
	public static JLayeredPane selector;

    /*
    Represents the fractals that have been selected as parents for the next generation.
    [0] is the upper left
    [1] is the upper middle
    [2] is the upper right
    ...
    [8] is the lower right
     */
	public static boolean selectedFractals[] = new boolean[9];

    //Represents the size of the window in pixels
	static int windowWidth = 1024, windowHeight = 768;
	static int mouseX, mouseY; // Mouse location on the screen

    //Program version
	private static final double VERSION = 0.8;

	private static int selectedBoxX, selectedBoxY, menuOpenForFractalNum;

    //Background and Foreground colors
	private static Color bgColor       = Color.black;
	private static Color fgColor       = Color.white;

    //Color of the selected fractals
	private static Color selectedColor = new Color(0, 255, 0, 15);
    //Color of the boxes where the mouse is hovering over
	private static Color hoverColor    = new Color(255, 255, 255, 15);

    //True if the user is in full-screen mode. This is toggled using the 'f' key
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
        //Generate the first generation
		Generator.generateNewGeneration();
        //Initialize the JFrame window
		new GraphicalInterface();
	}

    /**
     * Toggles full-screen mode for the JFrame windows
     */
	public static void toggleFullScreen() {
		if (fullScreen) {
			frame.setExtendedState(Frame.NORMAL);
		} else {
			frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);  
		}
		
		fullScreen = !fullScreen;
	}

    /*
    Draws the 9 boxes on the window
     */
	public void drawInterface(Graphics g) {
        //Fill the window with the background color
		g.setColor(bgColor);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        //Define how big each box is by looking at the current window size
		int boxWidth = frame.getWidth() / 3;
		int boxHeight = frame.getHeight() / 3;

        //Draw each of the 9 boxes
		for (int i = 0; i < 9; i++) {
			int x = i % 3 * boxWidth;
			int y = i / 3 * boxHeight;

			g.setColor(fgColor);
            //Only draw the fractal when the image is completely finished
			if (ImageManager.indexIsReady(i))
				drawImage(g, i, x, y);
            //Otherwise just draw text
			else
				g.drawString("Loading...", x + 20, y + 20);
			drawBox(g, x, y, boxWidth, boxHeight);
		}

        //Highlight the selected boxes
        drawSelectedBoxes(g);

        //Highlight the box that the mouse is hovering over
        drawSelectedBox(g);
	}

    /*
    Highlight the boxes the user has selected
     */
	private void drawSelectedBoxes(Graphics g) {
		g.setColor(selectedColor);
        //Loop through all 9 boxes
		for (int i = 0; i < selectedFractals.length; i++) {
			int x = i % 3 * frame.getWidth() / 3;
			int y = i / 3 * frame.getHeight() / 3;

            //Only highlight the boxes that have been selected
			if (selectedFractals[i]) {
				g.fillRect(x, y, frame.getWidth() / 3, frame.getHeight() / 3);
			}
		}
	}

    /**
     * Draw the box to allow users to render a fractal
     * @param e
     * @param boxIndex
     */
	private void drawPopupForFractal(MouseEvent e, int boxIndex) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem renderItem = new JMenuItem("Render fractal");
		renderItem.addActionListener(this);
		menu.add(renderItem);
        //Draw the menu where the mouse was at time of click
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

    /**
     * Draws a box with the foreground color at position (x,y) with
     * the specified width and height
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     */
	public void drawBox(Graphics g, int x, int y, int width, int height) {
		g.setColor(fgColor);
		g.drawRect(x, y, width, height);
	}

    /**
     * Draw the fractal represented by the given index at position (x,y)
     * @param g
     * @param index
     * @param x
     * @param y
     */
	public void drawImage(Graphics g, int index, int x, int y) {
		Generator.drawImage(index, g, x, y);
	}

    /**
     * Highlight the box that the mouse is hovering over
     * @param g
     */
	public void drawSelectedBox(Graphics g) {
        //Use the mouse to determine which box is highlighted
		int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
		int t = mouseY / (frame.getHeight() / 3) * (frame.getHeight() / 3);

		g.setColor(hoverColor);
		g.fillRect(l + 1, t + 1, frame.getWidth() / 3 - 2,
				frame.getHeight() / 3 - 2);
	}

    /**
     * Draw the interface
     * @param g
     */
	public void paint(Graphics g) {
		drawInterface(g);
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

    /**
     * Triggered when the mouse moves on the window
     * @param e
     */
	@Override
	public void mouseMoved(MouseEvent e) {
        //Get the mouse's position
		int x = e.getX();
		int y = e.getY();

        //Take into account the top and side bars of the window
		mouseX = x - frame.getInsets().left;
		mouseY = y - frame.getInsets().top;

		int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
		int t = mouseY / (frame.getHeight() / 3) * (frame.getHeight() / 3);

        //Repaint the screen if the hover box changes
		if (l != selectedBoxX | t != selectedBoxY) {
			selectedBoxX = l;
			selectedBoxY = t;
			repaint();
		}
	}

    /**
     * Triggered when the mouse is clicked in the window
     * @param e
     */
	@Override
	public void mouseClicked(MouseEvent e) {
        //Get the mouse's position
		int x = e.getX();
		int y = e.getY();

        //Take into account the top and side bars of the window
		mouseX = x - frame.getInsets().left;
		mouseY = y - frame.getInsets().top;

		int boxCol = mouseX / (frame.getWidth() / 3);
		int boxRow = mouseY / (frame.getHeight() / 3);

        //Calculate the index of the box
		int boxIndex = (boxRow * 3) + boxCol;

        //If the click is a right-mouse button click, bring up the render box
        if (SwingUtilities.isRightMouseButton(e)) {
            drawPopupForFractal(e, boxIndex);
            menuOpenForFractalNum = boxIndex;
        }
        //Otherwise, toggle the selection of the box
        else {
        	selectedFractals[boxIndex] = !selectedFractals[boxIndex];
        }

        //Repaint the screen
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

    /**
     * Triggered when the user presses a key
     * @param e
     */
	@Override
	public void keyReleased(KeyEvent e) {
        //Space bar generates a new generation
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Generator.generateNewGeneration();
            //Clear the selected fractals
			selectedFractals = new boolean[9];
			repaint();
		}
        //Left goes back one generation
        else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
		    Generator.decrementGeneration();
		}
        //Right goes forward a generation
        else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
		    Generator.incrementGeneration();
		}
        //F toggles full-screen mode
        else if(e.getKeyCode()==KeyEvent.VK_F) {
			toggleFullScreen();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

    /**
     * Render the fractal if the user presses the menu button
     * @param e
     */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Add menu option to save png and info file with specified filename
		
		if (e.getActionCommand().equals("Render fractal")) {
			try {
				Generator.renderFractalInGL(menuOpenForFractalNum);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
