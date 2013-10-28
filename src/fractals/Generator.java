package fractals;

import java.awt.Graphics;
import java.util.ArrayList;

public class Generator
{
    int generation = 0; //The current generation's number
    
    static ArrayList<Fractal> fractals = new ArrayList<Fractal>(9);
    
    public static void generateNewGeneration()
    {
        fractals.clear();//TODO Keep the existing fractals
        
        for(int i=0; i<9; i++)
        {
            fractals.add(new Fractal());
        }
    }

    public static void drawImage(int index, Graphics g, int x, int y)
    {
        fractals.get(index).drawImage(g, x, y);
    }
}
