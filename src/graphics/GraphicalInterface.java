package graphics;

import javax.swing.*;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import fractals.AttractorFractal;
import fractals.Expression;
import fractals.Fractal;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Opens JFrame with nine boxes.
 *
 */
public class GraphicalInterface extends JPanel implements MouseMotionListener, MouseListener
{
	private static final long serialVersionUID = 749344840243728058L;

	public static JFrame frame;
	
	static Fractal fractal;
	
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
	
	public static void main(String args[])
	{
		new GraphicalInterface();
		
		fractal = new AttractorFractal(new Expression("sin(1.7 * y) + cos(1.7* x)"), new Expression("sin(1.6 * x) + 0.7 * cos(1.6 * y)"));
		try {
			fractal.calculate();
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			e.printStackTrace();
		}
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
		if(fractal == null || !fractal.isCalculated()) {
			repaint();
			return;
		}
		
		//Draw the background
		g.setColor(Color.white);
		g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		fractal.paintFractal(g, frame.getWidth()/3, frame.getWidth()*2/3, frame.getHeight()/3, frame.getHeight()*2/3);
		
		drawBoxes(g);
		drawSelectedBox(g);
		
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
		fractal = new AttractorFractal(Expression.rand(), Expression.rand());
		try {
			fractal.calculate();
		} catch (UnknownFunctionException | UnparsableExpressionException ee) {
			ee.printStackTrace();
		}
		
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
