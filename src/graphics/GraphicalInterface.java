package graphics;

import javax.swing.*;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import fractals.AttractorFractal;
import fractals.Expression;
import fractals.Fractal;

import java.awt.*;

public class GraphicalInterface extends JPanel
{
	private static final long serialVersionUID = 749344840243728058L;

	public static JFrame frame;
	
	static Fractal fractal;
	
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
		
		fractal = new AttractorFractal(new Expression("sin(1.7 * y) + cos(1.7* x)"), new Expression("sin(1.6 * x) + 0.7 * cos(1.6 * y)"));
	}
	
	public void paint(Graphics g)
	{
		//Draw the background
		g.setColor(Color.black);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		try {
			fractal.paintFractal(g);
		} catch (UnknownFunctionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnparsableExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//repaint();
	}
}
