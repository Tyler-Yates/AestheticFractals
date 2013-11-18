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
        crossRatio = 0.3;
    }
    
    public static void generateNewGeneration()
    {
        for(int i=0; i<GraphicalInterface.selectedFractals.length; i++)
        {
            if(GraphicalInterface.selectedFractals[i])
                selectedFractals.add(fractals.get(i));
        }
        
        fractals.clear();
        
        Fractal newFractal;
        for(int i=0; i<9; i++)
        {
            if (selectedFractals.isEmpty()) {
                newFractal = new Fractal();
            }
            else if (selectedFractals.size() == 1) {
                newFractal = selectedFractals.get(0).mutate();
            } else {
                Fractal Parent1 = selectedFractals.get((int)(Math.random()*selectedFractals.size()));
                Fractal Parent2 = selectedFractals.get((int)(Math.random()*selectedFractals.size()));
            
                if(i<=9*crossRatio) {
                    System.out.println("crossing i=" + i);
                    newFractal = Parent1.cross(Parent2);
                }
                else {
                //else if (i<=9*mutateRatio) {
                    System.out.println("mutating i=" + i);
                    newFractal = Parent1.mutate();
                }
                /*else {
                    System.out.println("cloning i=" + i);
                    newFractal = Parent1.clone();
                }*/
            }
            //(new ImageGenerator(newFractal)).start();
            try
            {
                newFractal.generateImage();
            } catch (IOException | InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            fractals.add(newFractal);
        }
        
        selectedFractals.clear();
    }

    public static void drawImage(int index, Graphics g, int x, int y)
    {
        fractals.get(index).drawImage(g, x, y);
        
    }
}

class ImageGenerator extends Thread
{
    Fractal fractal;
    
    public ImageGenerator(Fractal f)
    {
        fractal=f;
    }
    
    @Override
    public void run()
    {
        try
        {
            fractal.generateImage();
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}