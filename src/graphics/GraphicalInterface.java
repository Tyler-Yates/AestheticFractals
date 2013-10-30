package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
    
    public static boolean needsRedraw = true;

    static int mouseX, mouseY; // Mouse location on the screen

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

    public static void main(String args[]) throws IOException, InterruptedException
    {  
        Generator.generateNewGeneration();
        new GraphicalInterface();
    }

    public void drawInterface(Graphics g)
    {
        g.setColor(Color.white);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
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
        if(needsRedraw)
        {
            drawInterface(g);
            needsRedraw=false;
        }
        repaint();
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
        
        needsRedraw=true;
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
