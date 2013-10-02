package fractals;

import graphics.GraphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class AttractorFractal implements Fractal
{
	//Mathematical expressions to define the x and y movement
	private Expression expressionX, expressionY;
	
	//The opacity of the color
	private static final int ALPHA = 255;
	
	//Scale of the drawing
	private static final int SCALE = 100;
	
	//Maps a point to its alpha value
	private ArrayList<DoublePoint> points = new ArrayList<DoublePoint>(100000);
	private double minX, minY, maxX, maxY;
	private boolean isCalculated;  // Has calculated points; prevents recalculation.
	
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
	
	/**
	 * Calculates points of fractal and stores in Hashmap without drawing.
	 */
	public void calculate() throws UnknownFunctionException, UnparsableExpressionException {
		isCalculated = false;
		double x,y;
		
		x = y = 0.0;
		
		//Perform iterations to draw the fractal
		for(int i=0; i<10000; i++)
		{
			double newX = expressionX.evaluate(x, y);
			double newY = expressionY.evaluate(x, y);
			
			x = newX;
			y = newY;
			
			minX = Math.min(x, minX);
			minY = Math.min(y, minY);
			maxX = Math.max(x, maxX);
			maxY = Math.max(y, maxY);
			
			DoublePoint newPoint = new DoublePoint(x, y);
			points.add(newPoint);
			
		}
		
		System.out.println("Num points: "+points.size());

		isCalculated = true;
	}
	
	@Override
	/* *
	 * Draw fractal centered in box specified by l,r,t,b
	 */
	public void paintFractal(Graphics g, int l, int r, int t, int b)
	{
		double fractalWidth = maxX - minX;
		double fractalHeight= maxY - minY;
		
		int frameWidth = r - l;
		int frameHeight = b - t;
		
		double ratioWidth = 1.0 * frameWidth / fractalWidth;
		double ratioHeight = 1.0 * frameHeight / fractalHeight;
		
		double scale = Math.min(ratioHeight, ratioWidth);
		
		double originX = l + (frameWidth) / 2;
		double originY = t + (frameHeight) / 2;
		for(DoublePoint p:points)
		{
			int x = (int) (p.getX() * scale);
			int y = (int) (p.getY() * scale);
			
			//System.out.println(p.getX()+" "+p.getY());
			//System.out.println(x+" "+y);
			
			g.setColor(new Color(0,0,0,ALPHA));
			g.drawLine((int) (x + originX), (int) (y + originY), (int) (x + originX), (int) (y + originY) );
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
