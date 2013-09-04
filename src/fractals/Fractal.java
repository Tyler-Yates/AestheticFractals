package fractals;

import java.awt.Graphics;

public interface Fractal 
{
	public void paintFractal(Graphics g);
	public Fractal cloneFractal();
	public void mutateFractal();
}
