package fractals;

import java.awt.Graphics;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public interface Fractal 
{
	public void paintFractal(Graphics g, int l, int r, int t, int b);
	public Fractal cloneFractal();
	public void mutateFractal();
	public void calculate() throws UnknownFunctionException, UnparsableExpressionException;
	public boolean isCalculated();
}
