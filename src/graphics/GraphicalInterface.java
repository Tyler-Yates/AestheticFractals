package graphics;

import javax.swing.*;

import equations.Equation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

/**
 * Opens JFrame with nine boxes.
 *
 */
public class GraphicalInterface extends JPanel implements MouseMotionListener, MouseListener
{
	private static final long serialVersionUID = 749344840243728058L;

	public static JFrame frame;
	
	static int mouseX, mouseY;
	
	private static final double VERSION = 0.00;
	
	/**
	 * Initializes the JFrame
	 */
	public GraphicalInterface()
	{
		frame = new JFrame("Aesthetic Fractal v"+VERSION);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1900, 1060);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.add(this);
	}
	
	public static void main(String args[]) throws IOException
	{
		new GraphicalInterface();
		
		ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"C-Backend/aesthetics", 
				Equation.generateRandomXEquation().toString(), Equation.generateRandomYEquation().toString()});
		processBuilder.start();
	}
	
	public void drawBoxes(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(0, 0, frame.getWidth(), frame.getHeight());
		g.drawLine(frame.getWidth()/3, 0, frame.getWidth()/3, frame.getHeight());
		g.drawLine(frame.getWidth()/3*2, 0, frame.getWidth()/3*2, frame.getHeight());
		
		g.drawLine(0, frame.getHeight()/3, frame.getWidth(), frame.getHeight()/3);
		g.drawLine(0, frame.getHeight()*2/3, frame.getWidth(), frame.getHeight()*2/3);
	}
	
	public void drawSelectedBox(Graphics g) {
		int l = mouseX / (frame.getWidth()/3) * (frame.getWidth()/3);
		int t = mouseY / (frame.getHeight()/3) * (frame.getHeight()/3);
		
		g.setColor(new Color(0,0,255,15));
		g.fillRect(l+1, t+1, frame.getWidth()/3-2, frame.getHeight()/3-2);
	}
	
	public void paint(Graphics g)
	{

		repaint();
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
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
