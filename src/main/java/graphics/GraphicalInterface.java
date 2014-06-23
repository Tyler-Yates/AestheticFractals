package graphics;

import fractals.Generator;
import fractals.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Opens JFrame with nine boxes.
 */
public class GraphicalInterface extends JPanel implements MouseMotionListener, MouseListener, KeyListener, ActionListener {
    private static final long serialVersionUID = 749344840243728058L;

    //The window
    public static JFrame frame;

    /*
    Generator controls the generation of new Fractals. It is an instantiation because we need to save
    this Object to a file through Serialization in order to save the state of the program.
     */
    public static Generator generator;

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
    static int windowWidth = 1024, windowHeight = 868;
    static int mouseX, mouseY; // Mouse location on the screen

    //Program version
    private static final double VERSION = 1.0;

    private static int selectedBoxX, selectedBoxY, menuOpenForFractalNum;

    //Background and Foreground colors
    private static final Color bgColor = Color.black;
    private static final Color fgColor = Color.white;

    //Color of the selected fractals
    private static final Color selectedColor = new Color(255, 255, 255, 35);
    //Color of the boxes where the mouse is hovering over
    private static final Color hoverColor = new Color(255, 255, 255, 35);

    //True if the user is in full-screen mode. This is toggled using the 'f' key
    private static boolean fullScreen = false;

    //The file extensions for save states
    private static final String EXTENSION = "ser";

    //Defines the height of the evolution selector at the bottom of the screen
    public static final int SELECTOR_HEIGHT = 100;
    public static EvolutionSelector selector;

    /**
     * Initializes the JFrame
     */
    public GraphicalInterface() {
        frame = new JFrame("Aesthetic Fractal v" + VERSION);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(windowWidth, windowHeight);
        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.add(this);
    }

    public static void main(String args[]) throws IOException, InterruptedException {
        //Generate the first generation
        generator = new Generator();
        generator.generateNewGeneration();

        //Initialize the Evolution Selector to the immediate right of the JFrame window
        selector = new EvolutionSelector(windowWidth, 0);

        //Initialize the JFrame window
        new GraphicalInterface();
    }

    /**
     * Returns the total height of the area where the 3x3 grid of fractals are drawn.
     *
     * @return the height in pixels
     */
    public static int getFractalWindowHeight() {
        return frame.getHeight() - SELECTOR_HEIGHT;
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

    /**
     * Draw the 9 fractal boxes on the window
     *
     * @param g the graphics object to draw on
     */
    public void drawInterface(Graphics g) {
        //Fill the window with the background color
        g.setColor(bgColor);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        //Define how big each box is by looking at the current window size
        final int boxWidth = frame.getWidth() / 3;
        final int boxHeight = getFractalWindowHeight() / 3;

        //Draw each of the 9 boxes
        for (int i = 0; i < 9; i++) {
            final int x = i % 3 * boxWidth;
            final int y = i / 3 * boxHeight;

            g.setColor(fgColor);
            //Draw the fractal
            drawImage(g, i, x, y);
            drawBox(g, x, y, boxWidth, boxHeight);
        }

        //Highlight the selected boxes
        drawSelectedBoxes(g);

        //Highlight the box that the mouse is hovering over
        drawSelectedBox(g);

        //Draw the evolution selector bar
        drawSelectorBar(g);
    }

    /**
     * Draw the selector bar at the bottom of the window
     *
     * @param g the graphics object to draw on
     */
    private void drawSelectorBar(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, getFractalWindowHeight() + 1, frame.getWidth(), SELECTOR_HEIGHT);
        g.setColor(Color.white);
        //Draw the generation information
        g.drawString("Generation: " + generator.getGeneration() + "/" + generator.getTotalGenerations(), 15, getFractalWindowHeight() + 15);
    }

    /**
     * Highlight the boxes the user has selected
     *
     * @param g the graphics object to draw on
     */
    private void drawSelectedBoxes(Graphics g) {
        g.setColor(selectedColor);
        //Loop through all 9 boxes
        for (int i = 0; i < selectedFractals.length; i++) {
            final int x = i % 3 * frame.getWidth() / 3;
            final int y = i / 3 * getFractalWindowHeight() / 3;

            //Only highlight the boxes that have been selected
            if (selectedFractals[i]) {
                g.fillRect(x, y, frame.getWidth() / 3, getFractalWindowHeight() / 3);
            }
        }
    }

    /**
     * Draw the box to allow users to render a fractal
     *
     * @param e the mouse event
     */
    private void drawPopupForFractal(MouseEvent e) {
        final JPopupMenu menu = new JPopupMenu();
        final JMenuItem renderItem = new JMenuItem("Render fractal");
        renderItem.addActionListener(this);
        menu.add(renderItem);
        //Draw the menu where the mouse was at time of click
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Draws a box with the foreground color at position (x,y) with
     * the specified width and height
     *
     * @param g      the graphics object to draw on
     * @param x      the x location of the upper-left corner of the box in pixels
     * @param y      the y location of the upper-left corner of the box in pixels
     * @param width  the width of the box
     * @param height the height of the box
     */
    public void drawBox(Graphics g, int x, int y, int width, int height) {
        g.setColor(fgColor);
        g.drawRect(x, y, width, height);
    }

    /**
     * Draw the fractal represented by the given index at position (x,y)
     *
     * @param g     the graphics object to draw on
     * @param index the index of the fractal
     * @param x     the x location of the upper-left corner of the image in pixels
     * @param y     the y location of the upper-left corner of the image in pixels
     */
    public void drawImage(Graphics g, int index, int x, int y) {
        generator.drawImage(index, g, x, y);
    }

    /**
     * Highlight the box that the mouse is hovering over
     *
     * @param g the graphics object to draw on
     */
    public void drawSelectedBox(Graphics g) {
        //Use the mouse to determine which box is highlighted
        final int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
        final int t = mouseY / (getFractalWindowHeight() / 3) * (getFractalWindowHeight() / 3);

        g.setColor(hoverColor);
        g.fillRect(l + 1, t + 1, frame.getWidth() / 3 - 2, getFractalWindowHeight() / 3 - 2);
    }

    /**
     * Draw the interface
     *
     * @param g the graphics object to draw on
     */
    public void paint(Graphics g) {
        drawInterface(g);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //Get the mouse's position
        final int x = e.getX();
        final int y = e.getY();

        //Take into account the top and side bars of the window
        mouseX = x - frame.getInsets().left;
        mouseY = y - frame.getInsets().top;

        final int boxX = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
        final int boxY = mouseY / (getFractalWindowHeight() / 3) * (getFractalWindowHeight() / 3);

        //Repaint the screen if the hover box changes
        if (boxX != selectedBoxX | boxY != selectedBoxY) {
            selectedBoxX = boxX;
            selectedBoxY = boxY;
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Get the mouse's position
        final int x = e.getX();
        final int y = e.getY();

        //Take into account the top and side bars of the window
        mouseX = x - frame.getInsets().left;
        mouseY = y - frame.getInsets().top;

        final int boxCol = mouseX / (frame.getWidth() / 3);
        final int boxRow = mouseY / (getFractalWindowHeight() / 3);

        //Calculate the index of the box
        final int boxIndex = (boxRow * 3) + boxCol;

        //If the click is a right-mouse button click, bring up the render box
        if (SwingUtilities.isRightMouseButton(e)) {
            drawPopupForFractal(e);
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

    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Space bar generates a new generation
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            generator.generateNewGeneration();
            //Clear the selected fractals
            selectedFractals = new boolean[9];
            repaint();
        }
        //Left goes back one generation
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            generator.decrementGeneration();
        }
        //Right goes forward a generation
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            generator.incrementGeneration();
        }
        //F toggles full-screen mode
        else if (e.getKeyCode() == KeyEvent.VK_F) {
            toggleFullScreen();
        }
        //S saves the state of the program
        else if (e.getKeyChar() == 's') {
            save();
        }
        //L loads a previously saved state
        else if (e.getKeyChar() == 'l') {
            load();
        }
    }

    /**
     * Saves the state of the program to a file.
     */
    private void save() {
        System.out.print("Saving...");
        //Open a dialog for the user to pick the file to save to
        final String fileName = FileChooser.showSaveDialog(EXTENSION);
        if (fileName == null) {
            return;
        }
        try (
                final OutputStream file = new FileOutputStream(fileName);
                final OutputStream buffer = new BufferedOutputStream(file);
                final ObjectOutput output = new ObjectOutputStream(buffer)
        ) {
            //Stop the current Threads as we are discarding the current program state
            ImageManager.interruptThreads();
            output.writeObject(generator);
            System.out.println("Done!");
        } catch (IOException ex) {
            System.err.println("Error saving the generation");
            ex.printStackTrace();
        }
    }

    /**
     * Loads a previously saved state of the program.
     */
    private void load() {
        System.out.print("Loading...");
        //Open a dialog for the user to pick the file to load from
        final String fileName = FileChooser.showLoadDialog(EXTENSION);
        if (fileName == null) {
            return;
        }
        try (
                final InputStream file = new FileInputStream(fileName);
                final InputStream buffer = new BufferedInputStream(file);
                final ObjectInput input = new ObjectInputStream(buffer)
        ) {
            //Stop the current Threads as we are discarding the current program state
            ImageManager.interruptThreads();
            generator = (Generator) input.readObject();
            System.out.println("Done!");
            repaint();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Render the fractal if the user presses the menu button
     *
     * @param e the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Add menu option to save png and info file with specified filename

        if (e.getActionCommand().equals("Render fractal")) {
            try {
                generator.renderFractalInGL(menuOpenForFractalNum);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
