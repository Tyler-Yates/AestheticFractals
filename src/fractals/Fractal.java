package fractals;

import graphics.GraphicalInterface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents a single fractal
 */
public class Fractal implements Serializable {
    //The mathematical Equations that define the structure of the Fractal
    private Equation x, y, z;
    //The image representing the visualization of the fractal. This variable is transient to keep it from being
    // Serialized when the program state is saved.
    private transient BufferedImage img;
    //Represents whether the image of the fractal has finished rendering
    public boolean isGenerating;

    //Defines what operation produced this fractal: crossing, mutation, cloning, etc.
    private String operation = "";
    //Defines the parent Fractals if this Fractal's operation was crossing
    private Fractal parent1, parent2;

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

    //Define the ID for the Fractal
    private String id;
    //Define the path to the image of the Fractal
    private String fileName;

    /**
     * Constructs a new Fractal with randomly generated X and Y Equations
     */
    public Fractal() {
        this(Equation.generateRandomXEquation(), Equation.generateRandomYEquation(),
                Equation.generateRandomZEquation());
    }

    /**
     * Constructs a new Fractal with the given X and Y equations
     *
     * @param x
     * @param y
     */
    public Fractal(Equation x, Equation y) {
        this(x, y, Equation.generateRandomZEquation());
    }

    /**
     * Constructs a new Fractal with the given X, Y, and Z equations
     *
     * @param x
     * @param y
     * @param z
     */
    public Fractal(Equation x, Equation y, Equation z) {
        this.x = x;
        this.y = y;
        this.z = z;

        setIdentification();
    }

    /**
     * Sets the ID and fileName parameters of the current Fractal
     */
    private void setIdentification() {
        if (x == null || y == null) {
            id = "null";
        }
        else {
            //The ID for the fractal is the hash code of the Equations
            id = "f_" + x.hashCode() + y.hashCode();
        }

        //Use the ID to create the filename for the image
        fileName = IMAGE_PATH + id + ".png";
    }

    /**
     * Sets the operation that created this Fractal.
     * For example, if this Fractal was created as a result of crossing two other Fractals, the operation should be
     * "cross".
     *
     * @param op
     */
    public void setOperation(String op) {
        operation = op.toLowerCase();
    }

    /**
     * Sets the parents of the current Fractal.
     *
     * @param p1
     * @param p2
     */
    public void setParents(Fractal p1, Fractal p2) {
        parent1 = p1;
        parent2 = p2;
    }

    /**
     * Creates the current Fractal over again.
     */
    public void redo() {
        if (operation.equals("cross") && parent1 != null && parent2 != null) {
            Fractal newFractal = parent1.cross(parent2);

            x = newFractal.getX();
            y = newFractal.getY();
            z = newFractal.getZ();
        }
        else if (operation.equals("mutate")) {
            inPlaceMutate();
        }
        else {
            x = Equation.generateRandomXEquation();
            y = Equation.generateRandomYEquation();
            z = Equation.generateRandomZEquation();
        }
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
     * This method returns a copy of the crossed Fractal. The current Fractal is unmodified.
     *
     * @param f
     *
     * @return
     */
    public Fractal cross(Fractal f) {
        //Clone the Equations to prevent changes from altering the original Equations
        Equation cloneX = x.clone();
        Equation cloneY = y.clone();
        Equation cloneZ = z.clone();
        Equation otherCloneX = f.x.clone();
        Equation otherCloneY = f.y.clone();
        Equation otherCloneZ = f.z.clone();

        //Cross the cloned X and Y Equations
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            cloneX.cross(otherCloneX);
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            cloneY.cross(otherCloneY);
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            cloneZ.cross(otherCloneZ);
        }

        //Return a new Fractal defined by the new crossed Equations
        return new Fractal(cloneX, cloneY, cloneZ);
    }

    /**
     * Mutates the given Fractal.
     *
     * Mutation is defined as such:
     * Loop through every node in the expression tree of the current Fractal. For every node that represents a
     * constant,
     * there is a fixed chance that this constant is altered by a set amount.
     *
     * This method returns a copy of the newly mutated Fractal. The current Fractal is unmodified.
     *
     * @return
     */
    public Fractal mutate() {
        //Clone the Equations to prevent changes from altering the original Equations.
        Equation cloneX = x.clone();
        Equation cloneY = y.clone();
        Equation cloneZ = z.clone();

        //Mutate each of the Equations
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            cloneX.mutate();
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            cloneY.mutate();
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            cloneZ.mutate();
        }

        //Return a new Fractal defined by the mutated Equations
        return new Fractal(cloneX, cloneY, cloneZ);
    }

    /**
     * Mutates the current Fractal in place such that the current Fractal is altered.
     */
    public void inPlaceMutate() {
        //Mutate each of the Equations
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            x.mutate();
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            y.mutate();
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            z.mutate();
        }
    }

    /**
     * Returns a cloned copy of the current Fractal. Any changes to a cloned fractal will not affect the Fractal which
     * it was cloned from.
     *
     * @return
     */
    public Fractal clone() {
        return new Fractal(x.clone(), y.clone(), z.clone());
    }

    /**
     * Performs introduction on the current Fractal. This method returns a copy of the new Fractal. The current Fractal
     * is unmodified.
     *
     * @return
     */
    public Fractal introduce() {
        Equation cloneX = x.clone();
        Equation cloneY = y.clone();
        Equation cloneZ = z.clone();

        //Cross the cloned X and Y Equations
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            cloneX.introduce();
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            cloneY.introduce();
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            cloneZ.introduce();
        }

        //Return a new Fractal defined by the new crossed Equations
        return new Fractal(cloneX, cloneY, cloneZ);
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
        if (image_width % 4 != 0) {
            image_width += 4 - image_width % 4;
        }
        if (image_height % 4 != 0) {
            image_height += 4 - image_height;
        }

        //Call the C-Backend to render the image and save it to a file
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{
                "C-Genetics/aesthetics", "-save", "-p", "100000",
                "-s", "" + image_width, "" + image_height,
                IMAGE_PATH + id,
                x.toString(), y.toString(), z.toString(),
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
    }

    /**
     * Returns whether or not the generated image is 'sparse' and thus aesthetically unpleasing
     *
     * @return
     */
    public boolean isSparseImage() {
        File f = new File(fileName);
        //Get the size of the image file in kilobytes
        double sizeOfImage = f.length() / 1024.0;

        //If the image size is less than the threshold value, it is sparse
        if (sizeOfImage <= 1.0) {
            return true;
        }
        return false;
    }

    /**
     * Loads the image file corresponding to the given fractal
     */
    public void loadImage() throws InterruptedException {
        //Load the image file. If it fails, retry a few times.
        int tries = 3;
        while (tries-- > 0) {
            try {
                File f = new File(fileName);
                img = ImageIO.read(f);

                //Force a repaint of the window to draw the newly rendered Fractal
                GraphicalInterface.frame.getContentPane().repaint();

                //Delete the image file on disk to prevent the image folder from filling up the disk
                discard();

                //If we successfully read in the image, we are done with the method
                return;
            } catch (IOException e) {
                System.err.println("File name: " + fileName);
                e.printStackTrace();
                Thread.sleep(500);
            }
        }
    }

    /**
     * Calls the C-Backend to render the current Fractal in 3D.
     *
     * @throws IOException
     */
    public void renderInGL() throws IOException {
        //TODO Add proper R, G, and B equations
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{
                "C-Genetics/aesthetics", x.toString(), y.toString(), z.toString(), "x", "y", "z"
        });
        //Tell the FocusListener to stop trying to regain focus
        GraphicalInterface.focusListener.loseFocus();
        Process p = processBuilder.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //Now that the render window has closed, allow the FocusListener to regain focus
            GraphicalInterface.focusListener.regainFocus();
        }
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
                    (GraphicalInterface.frame.getHeight() - GraphicalInterface.SELECTOR_HEIGHT) / 3,
                    GraphicalInterface.frame);
        }
    }

    /**
     * Returns the X equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getX() {
        return x;
    }

    /**
     * Returns the Y equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getY() {
        return y;
    }

    /**
     * Returns the Z equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getZ() {
        return z;
    }

    /**
     * Deletes files created on the disk by the current Fractal
     */
    public void discard() {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
    }
}