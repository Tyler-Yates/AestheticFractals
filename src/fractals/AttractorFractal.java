package fractals;

import graphics.GraphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class AttractorFractal implements Fractal
{
	//Mathematical expressions to define the x and y movement
	private Expression expressionX, expressionY;
	
	//The opacity of the color
	private static final int ALPHA = 2;
	
	//Scale of the drawing
	private static final int SCALE = 100;
	
	//Maps a point to its alpha value
	private HashMap<Point, Integer> points = new HashMap<Point, Integer>();
	private int minX, minY, maxX, maxY;
	private boolean isCalculated;
	
	public AttractorFractal(Expression expressionX, Expression expressionY)
	{
		this.expressionX = expressionX;
		this.expressionY = expressionY;
		
		minX = minY = Integer.MAX_VALUE;
		maxX = maxY = Integer.MIN_VALUE;
	}

	public boolean isCalculated() {
		return isCalculated;
	}
	
	public void calculate() throws UnknownFunctionException, UnparsableExpressionException {
		isCalculated = false;
		double x,y;
		
		x = y = 0.0;
		
		//Perform iterations to draw the fractal
		for(int i=0; i<5000000; i++)
		{
			double newX = expressionX.evaluate(x, y);
			double newY = expressionY.evaluate(x, y);
			
			x = newX;
			y = newY;
			
			int roundX = (int) Math.round(x * SCALE + GraphicalInterface.frame.getWidth()/2);
			int roundY = (int) Math.round(y * SCALE + GraphicalInterface.frame.getHeight()/2);
			minX = Math.min(roundX, minX);
			minY = Math.min(roundY, minY);
			maxX = Math.max(roundX, maxX);
			maxY = Math.max(roundY, maxY);
			
			Point newPoint = new Point(roundX, roundY);
			
			if(points.containsKey(newPoint))
			{
				if(points.get(newPoint)<255)
				{
					int newAlpha = Math.min(255, points.get(newPoint)+ALPHA);
					points.put(newPoint, newAlpha);
				}
			}
			else
			{
				points.put(newPoint, ALPHA);
			}
			
		}

		isCalculated = true;
	}
	
	@Override
	public void paintFractal(Graphics g, int l, int r, int t, int b)
	{
		int fractalWidth = maxX - minX;
		int fractalHeight= maxY - minY;
		int frameWidth = r - l;
		int frameHeight = b - t;
		
		int originX = l + (frameWidth - fractalWidth) / 2 - minX;
		int originY = t + (frameHeight - fractalHeight) / 2 - minY;
		for(Point p:points.keySet())
		{
			g.setColor(new Color(0,0,0,points.get(p)));
			g.drawLine(p.x + originX, p.y + originY, p.x + originX, p.y + originY);
		}
	}

	@Override
	public Fractal cloneFractal() 
	{
		try {
			return (Fractal) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void mutateFractal() 
	{
		
	}
}
