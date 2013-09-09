package fractals;

import graphics.GraphicalInterface;

import java.awt.Color;
import java.awt.Graphics;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class AttractorFractal implements Fractal
{
	private Expression expressionX, expressionY;
	
	public AttractorFractal(Expression expressionX, Expression expressionY)
	{
		this.expressionX = expressionX;
		this.expressionY = expressionY;
	}

	@Override
	public void paintFractal(Graphics g) throws UnknownFunctionException, UnparsableExpressionException 
	{
		double x,y;
		
		g.setColor(new Color(255,255,255,60));
		
		x = y = 0.0;
		
		for(int i=0; i<500000; i++)
		{
			double newX = expressionX.evaluate(x, y);
			double newY = expressionY.evaluate(x, y);
			
			x = newX;
			y = newY;
			
			int roundX = (int) Math.round(x * 200 + GraphicalInterface.frame.getWidth()/2);
			int roundY = (int) Math.round(y * 200 + GraphicalInterface.frame.getHeight()/2);
			
			g.drawLine(roundX, roundY, roundX, roundY);
			
			if(i%1000==0)
				System.out.print(".");
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
