package fractals;

import graphics.GraphicalInterface;

import java.awt.Graphics;
import java.io.IOException;
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
            Fractal newFractal = new Fractal();
            try
            {
                newFractal.generateImage();
            } catch (IOException | InterruptedException e)
            {
                System.out.println("error");
                
                e.printStackTrace();
            }
            fractals.add(newFractal);
        }
        
        GraphicalInterface.needsRedraw=true;
    }

    public static void drawImage(int index, Graphics g, int x, int y)
    {
        fractals.get(index).drawImage(g, x, y);
        
    }
}
