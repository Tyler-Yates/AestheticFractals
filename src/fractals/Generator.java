package fractals;

import graphics.GraphicalInterface;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;

public class Generator
{
    int generation = 0; //The current generation's number
    
    static ArrayList<Fractal> fractals = new ArrayList<Fractal>(9);//Current Fractals
    static ArrayList<Fractal> selectedFractals = new ArrayList<Fractal>(9);
    
    static double crossRatio, mutateRatio, cloneRatio;
    
    static {
        crossRatio = mutateRatio = cloneRatio = .3;
    }
    
    public static void generateNewGeneration()
    {
        fractals.clear();
        
        Fractal newFractal;
        for(int i=0; i<9; i++)
        {
            if (selectedFractals.size() < 2) {
                newFractal = new Fractal(Equation.generateRandomXEquation(), Equation.generateRandomYEquation());
            } else {
                Fractal Parent1 = selectedFractals.get((int)(Math.random()*selectedFractals.size()));
                Fractal Parent2 = selectedFractals.get((int)(Math.random()*selectedFractals.size()));
            
                if(i<=9*crossRatio)
                    newFractal = Parent1.cross(Parent2);
                else if (i<=9*mutateRatio)
                    newFractal = Parent1.mutate();
                else
                    newFractal = Parent1.clone();
            }
            
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
