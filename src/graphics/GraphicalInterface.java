package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fractals.Equation;
import fractals.Generator;

/**
 * Opens JFrame with nine boxes.
 * 
 */
public class GraphicalInterface extends JPanel implements MouseMotionListener,
        MouseListener
{
    private static final long serialVersionUID = 749344840243728058L;

    public static JFrame frame;

    static int mouseX, mouseY; // Mouse location on the screen

    static PPMImageReader reader = new PPMImageReader(null); // Responsible for
                                                             // reading in the
                                                             // fractal images

    private static final double VERSION = 0.00;

    /**
     * Initializes the JFrame
     */
    public GraphicalInterface()
    {
        frame = new JFrame("Aesthetic Fractal v" + VERSION);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1900, 1060);
        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
        frame.add(this);
    }

    public static void main(String args[]) throws IOException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[] {
                "C-Backend/aesthetics", "-s", "768", "1024", "TestFractal",
                new Equation("sin(-1.4 * y) + cos(-1.4 * x)").toString(),
                new Equation("sin(1.6 * x) + 0.7 * cos(1.6 * y)").toString() });
        // Equation.generateRandomXEquation().toString(),
        // Equation.generateRandomYEquation().toString()});
        processBuilder.start();

        new GraphicalInterface();
    }

    public void drawInterface(Graphics g)
    {
        int boxWidth = frame.getWidth() / 3;
        int boxHeight = frame.getHeight() / 3;

        for (int i = 0; i < 9; i++)
        {
            int x = i % 3 * boxWidth;
            int y = i / 3 * boxHeight;

            drawBox(g, x, y, boxWidth, boxHeight);
            drawImage(g, i, x, y);
            drawSelectedBox(g);
        }
    }

    public void drawBox(Graphics g, int x, int y, int width, int height)
    {
        g.setColor(Color.black);
        g.drawRect(x, y, width, height);
    }
    
    public void drawImage(Graphics g, int index, int x, int y)
    {
        Generator.drawImage(index, g, x, y);
    }

    public void drawSelectedBox(Graphics g)
    {
        int l = mouseX / (frame.getWidth() / 3) * (frame.getWidth() / 3);
        int t = mouseY / (frame.getHeight() / 3) * (frame.getHeight() / 3);

        g.setColor(new Color(0, 0, 255, 15));
        g.fillRect(l + 1, t + 1, frame.getWidth() / 3 - 2,
                frame.getHeight() / 3 - 2);
    }

    public void paint(Graphics g)
    {
        ImageInputStream inputStream;
        try
        {
            File f = new File("TestFractal.ppm");
            System.out.println(f.getAbsolutePath());
            inputStream = new FileImageInputStream(new File("TestFractal.ppm"));
            BufferedImage image = reader.read(inputStream);
            g.drawImage(image, 0, 0, null);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {

    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        mouseX = x - frame.getInsets().left;
        mouseY = y - frame.getInsets().top;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // TODO Auto-generated method stub

    }
}
