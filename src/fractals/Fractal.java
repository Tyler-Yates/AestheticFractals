package fractals;

import graphics.GraphicalInterface;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * Represents a single fractal
 */
public class Fractal implements Serializable {
    //The mathematical Equations that define the structure of the Fractal
    private Equation x, y;
    //The image representing the visualization of the fractal. This variable is transient to keep it from being
    // Serialized when the program state is saved.
    private transient BufferedImage img;
    //Represents whether the image of the fractal has finished rendering
    private boolean isGenerating;

    //Create the directory to store the image files
    static {
        try {
            Files.createDirectories(Paths.get(("images")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Defines the path to the image folder
    static final String IMAGE_PATH = "images" + File.separator;

    //Define the ID for the Fractal. This ID is randomly generated
    String id = "Fractal_" + ((int) (Math.random() * Integer.MAX_VALUE));

    /**
     * Constructs a new Fractal with randomly generated X and Y Equations
     */
    public Fractal() {
        x = Equation.generateRandomXEquation();
        y = Equation.generateRandomYEquation();
    }

    /**
     * Constructs a new Fractal with the given X and Y equations
     *
     * @param x
     * @param y
     */
    public Fractal(Equation x, Equation y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Crosses the current Fractal (Parent 1) with the given Fractal f (Parent 2) to generate a new Fractal.
     *
     * Crossing is defined as such:
     * The current Fractal and Fractal f are parents for the new Fractal.
     *
     * Each of the parents is represented by an expression tree. Take the two corresponding expression trees for the
     * parents and pick a random node in each tree. The subtrees rooted at these nodes in each expression tree are
     * swapped, yielding a single new Fractal.
     *
     * For example:
     * |Parent 1:      Parent 2:
     * |    a             d
     * |   / \           / \
     * |  b   c         e   f
     * |                   / \
     * |                  g   h
     *
     * Assume node b is picked in Parent 1 and node f in parent 2.
     *
     * |Resulting new Fractal:
     * |     a
     * |    / \
     * |   f   c
     * |  / \
     * | g   h
     *
     * The result of Fractal f (Parent 2) crossed with the current Fractal (Parent 1) is not computed.
     *
     * @param f
     *
     * @return
     */
    public Fractal cross(Fractal f) {
        //Clone the Equations to prevent changes from altering the original Equations
        Equation cloneX = x.clone();
        Equation cloneY = y.clone();
        Equation otherCloneX = f.x.clone();
        Equation otherCloneY = f.y.clone();

        //Cross the cloned X and Y Equations
        cloneX.cross(otherCloneX);
        cloneY.cross(otherCloneY);

        //Return a new Fractal defined by the new crossed Equations
        return new Fractal(cloneX, cloneY);
    }

    /**
     * Mutates the given Fractal.
     *
     * Mutation is defined as such:
     * Loop through every node in the expression tree of the current Fractal. For every node that represents a
     * constant,
     * there is a fixed chance that this constant is altered by a set amount.
     *
     * @return
     */
    public Fractal mutate() {
        //Clone the Equations to prevent changes from altering the original Equations.
        Equation cloneX = x.clone();
        Equation cloneY = y.clone();

        //Mutate each of the Equations
        cloneX.mutate();
        cloneY.mutate();

        //Return a new Fractal defined by the mutated Equations
        return new Fractal(cloneX, cloneY);
    }

    /**
     * Returns a cloned copy of the current Fractal. Any changes to a cloned fractal will not affect the Fractal which
     * it was cloned from.
     *
     * @return
     */
    public Fractal clone() {
        return new Fractal(x.clone(), y.clone());
    }

    /**
     * Generates the image for the current Fractal.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateImage() {
        //Get the dimensions of the screen in order to determine how large to render the image
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //Define the width and height of the image
        int image_width = (int) (screenSize.getWidth() / 3);
        int image_height = (int) (screenSize.getHeight() / 3);

        /*
        For reasons as yet unknown, the Java PNG reader freaks out when it tries to read PNG files whose dimensions
        are not divisible by 4. Thus, add a few pixels to the width and height if necessary to enforce divisibility
        by 4.
         */
        if (image_width % 4 != 0)
            image_width += 4 - image_width % 4;
        if (image_height % 4 != 0)
            image_height += 4 - image_height;

        //Call the C-Backend to render the image and save it to a file
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{
                "C-Genetics/aesthetics", "-save", "-p", "100000",
                "-s", "" + image_width, "" + image_height,
                IMAGE_PATH + id,
                x.toString(), y.toString(), "0",
                "1", "1", "1"});
        try {
            Process p = processBuilder.start();
            p.waitFor();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Load the image file. If it fails, retry a few times.
        int tries = 3;
        while (tries-- > 0) {
            try {
                File f = new File(IMAGE_PATH + id + ".png");
                img = ImageIO.read(f);
                //Delete the image file on disk to prevent the image folder from filling up the disk
                f.delete();
                
                //If we successfully read in the image, we are done with the method
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls the C-Backend to render the current Fractal in 3D.
     *
     * @throws IOException
     */
    public void renderInGL() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{
                "C-Genetics/aesthetics", x.toString(), y.toString(), "x", "x", "y", "z"
        });
        processBuilder.start();
    }

    /**
     * Draws the image of the current Fractal at the given coordinate (x,y). The coordinate represents the upper-left
     * corner of the image.
     *
     * @param g
     * @param x
     * @param y
     */
    public void drawImage(Graphics g, int x, int y) {
        //If no image has been rendered and we are not currently rendering, start the rendering
        if (img == null && !isGenerating) {
            isGenerating = true;
            //Call the ImageManager method to render this image in a Thread
            ImageManager.renderImage(this);
        }

        //If no image has been rendered, just draw loading text to the window
        if (img == null) {
            g.drawString("Loading...", x + 20, y + 20);
        }
        //If the image has been rendered, draw it to the screen
        else {
            //We are no longer generating the image
            isGenerating = false;
            g.drawImage(img, x, y, GraphicalInterface.frame.getWidth() / 3,
                    GraphicalInterface.frame.getHeight() / 3,
                    GraphicalInterface.frame);
        }
    }
}