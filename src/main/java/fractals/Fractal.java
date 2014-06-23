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
    private Equation x, y, z, r, g, b;
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
    private String fileName, filterFileName;

    /**
     * Constructs a new Fractal with randomly generated Equations
     */
    public Fractal() {
        this(Equation.generateRandomXEquation(), Equation.generateRandomYEquation(),
                Equation.generateRandomZEquation(), new Equation("x"), new Equation("y"), new Equation("z"));
    }

    /**
     * Constructs a new Fractal with the given X, Y, and Z equations
     *
     * @param x
     * @param y
     * @param z
     */
    public Fractal(Equation x, Equation y, Equation z) {
        this(x, y, z, new Equation("x"), new Equation("y"), new Equation("z"));
    }

    /**
     * Contructs a new Fractal with the given X, Y, Z, R, G, and B equations
     *
     * @param x
     * @param y
     * @param z
     * @param r
     * @param g
     * @param b
     */
    public Fractal(Equation x, Equation y, Equation z, Equation r, Equation g, Equation b) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;

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
        filterFileName = IMAGE_PATH + id + "_filter.png";
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
            r = newFractal.getR();
            g = newFractal.getG();
            b = newFractal.getB();
        }
        else if (operation.equals("mutate")) {
            inPlaceMutate();
        }
        else if (operation.equals("introduce") && parent1 != null) {
            Fractal newFractal = parent1.introduce();

            x = newFractal.getX();
            y = newFractal.getY();
            z = newFractal.getZ();
            r = newFractal.getR();
            g = newFractal.getG();
            b = newFractal.getB();
        }
        else {
            x = Equation.generateRandomXEquation();
            y = Equation.generateRandomYEquation();
            z = Equation.generateRandomZEquation();
            r = new Equation("x");
            g = new Equation("y");
            b = new Equation("z");
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
        Equation cloneR = r.clone();
        Equation cloneG = g.clone();
        Equation cloneB = b.clone();
        Equation otherCloneX = f.x.clone();
        Equation otherCloneY = f.y.clone();
        Equation otherCloneZ = f.z.clone();
        Equation otherCloneR = r.clone();
        Equation otherCloneG = g.clone();
        Equation otherCloneB = b.clone();

        //Cross the cloned Equations
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            cloneX.cross(otherCloneX);
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            cloneY.cross(otherCloneY);
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            cloneZ.cross(otherCloneZ);
        }
        if (GraphicalInterface.selector.colorEquation.isSelected()) {
            cloneR.cross(otherCloneR);
            cloneG.cross(otherCloneG);
            cloneB.cross(otherCloneB);
        }

        //Return a new Fractal defined by the new crossed Equations
        return new Fractal(cloneX, cloneY, cloneZ, cloneR, cloneG, cloneB);
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
        Equation cloneR = r.clone();
        Equation cloneG = g.clone();
        Equation cloneB = b.clone();

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
        if (GraphicalInterface.selector.colorEquation.isSelected()) {
            cloneR.introduce();
            cloneG.introduce();
            cloneB.introduce();
        }

        //Return a new Fractal defined by the mutated Equations
        return new Fractal(cloneX, cloneY, cloneZ, cloneR, cloneG, cloneB);
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
        if (GraphicalInterface.selector.colorEquation.isSelected()) {
            r.mutate();
            g.mutate();
            b.mutate();
        }
    }

    /**
     * Returns a cloned copy of the current Fractal. Any changes to a cloned fractal will not affect the Fractal which
     * it was cloned from.
     *
     * @return
     */
    public Fractal clone() {
        return new Fractal(x.clone(), y.clone(), z.clone(), r.clone(), g.clone(), b.clone());
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
        Equation cloneR = r.clone();
        Equation cloneG = g.clone();
        Equation cloneB = b.clone();

        //Cross the cloned Equations based on evolutionary selection
        if (GraphicalInterface.selector.xEquation.isSelected()) {
            cloneX.introduce();
        }
        if (GraphicalInterface.selector.yEquation.isSelected()) {
            cloneY.introduce();
        }
        if (GraphicalInterface.selector.zEquation.isSelected()) {
            cloneZ.introduce();
        }
        if (GraphicalInterface.selector.colorEquation.isSelected()) {
            cloneR.introduce();
            cloneG.introduce();
            cloneB.introduce();
        }

        //Return a new Fractal defined by the new crossed Equations
        return new Fractal(cloneX, cloneY, cloneZ, cloneR, cloneG, cloneB);
    }

    /**
     * Generates the image for the current Fractal.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void generateImage() {
        int image_width, image_height;
        try {
            //Get the dimensions of the screen in order to determine how large to render the image
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            //Define the width and height of the image
            image_width = (int) (screenSize.getWidth() / 3);
            image_height = (int) (screenSize.getHeight() / 3);
        } catch (Exception e) {
            //If there was an error getting the screen size, set default width and height values
            image_width = 1920;
            image_height = 1080;
        }

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
                r.toString(), g.toString(), b.toString()});
        try {
            Process p = processBuilder.start();
            p.waitFor();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

        }
    }

    /**
     * Returns whether or not the generated image is 'sparse' and thus aesthetically unpleasing.
     *
     * This method REQUIRES that generateImage() already be called.
     *
     * @return
     */
    public boolean isSparseImage() {
        File f = new File(filterFileName);
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
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{
                "C-Genetics/aesthetics", x.toString(), y.toString(), z.toString(), r.toString(), g.toString(),
                b.toString()
        });
        Process p = processBuilder.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
     * Returns the R equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getR() {
        return r;
    }

    /**
     * Returns the G equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getG() {
        return g;
    }

    /**
     * Returns the B equation of the current Fractal. The returned Equation is not a clone so any changes made to the
     * returned Equation will affect the current Fractal.
     *
     * @return
     */
    public Equation getB() {
        return b;
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
