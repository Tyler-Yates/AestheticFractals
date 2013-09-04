package graphics;

import javax.swing.*;
import java.awt.*;

public class GraphicalInterface extends JPanel
{
	private static final long serialVersionUID = 749344840243728058L;

	static JFrame frame;
	
	private static final double VERSION = 0.00;
	
	/**
	 * Initializes the JFrame
	 */
	public GraphicalInterface()
	{
		frame = new JFrame("Aesthetic Fractal v"+VERSION);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.add(this);
	}
	
	public static void main(String args[])
	{
		new GraphicalInterface();
	}
	
	public void paint(Graphics g)
	{
		//Draw the background
		g.setColor(Color.black);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
	}
}
