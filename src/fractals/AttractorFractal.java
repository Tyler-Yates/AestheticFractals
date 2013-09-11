package fractals;

import graphics.GraphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class AttractorFractal implements Fractal
{
	private Expression expressionX, expressionY;
	
	private static final int ALPHA = 2;
	
	public AttractorFractal(Expression expressionX, Expression expressionY)
	{
		this.expressionX = expressionX;
		this.expressionY = expressionY;
	}

	@Override
	public void paintFractal(Graphics g) throws UnknownFunctionException, UnparsableExpressionException 
	{
		double x,y;
		
		HashMap<Point, Integer> points = new HashMap<Point, Integer>();
		
		x = y = 0.0;
		
		for(int i=0; i<5000000; i++)
		{
			double newX = expressionX.evaluate(x, y);
			double newY = expressionY.evaluate(x, y);
			
			x = newX;
			y = newY;
			
			int roundX = (int) Math.round(x * 200 + GraphicalInterface.frame.getWidth()/2);
			int roundY = (int) Math.round(y * 200 + GraphicalInterface.frame.getHeight()/2);
			
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
			
			//g.drawLine(roundX, roundY, roundX, roundY);
		}
		
		for(Point p:points.keySet())
		{
			g.setColor(new Color(0,0,0,points.get(p)));
			g.drawLine(p.x, p.y, p.x, p.y);
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
