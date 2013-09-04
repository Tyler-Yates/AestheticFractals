package fractals;

import java.awt.Color;
import java.awt.Graphics;

public class AttractorFractal implements Fractal
{
	private Expression expressionX, expressionY;
	
	public AttractorFractal(Expression expressionX, Expression expressionY)
	{
		this.expressionX = expressionX;
		this.expressionY = expressionY;
	}

	@Override
	public void paintFractal(Graphics g) 
	{
		double x,y;
		
		g.setColor(Color.white);
		
		x = y = 100.0;
		
		for(int i=0; i<1000; i++)
		{
			double newX = expressionX.evaluate(x, y);
			double newY = expressionY.evaluate(x, y);
			
			x = newX;
			y = newY;
			
			int roundX = (int) Math.round(x);
			int roundY = (int) Math.round(y);
			
			g.drawLine(roundX, roundY, roundX, roundY);
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
