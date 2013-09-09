package fractals;

import java.awt.Graphics;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public interface Fractal 
{
	public void paintFractal(Graphics g) throws UnknownFunctionException, UnparsableExpressionException;
	public Fractal cloneFractal();
	public void mutateFractal();
}
