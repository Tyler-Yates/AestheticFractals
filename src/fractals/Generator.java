package fractals;

import graphics.GraphicalInterface;

import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Generator
{
    static int generation = 0; // The current generation's number

    static ArrayList<Fractal> fractals;  // Current Fractals
    static ArrayList<Fractal> selectedFractals = new ArrayList<Fractal>(9);

    static Stack<ArrayList<Fractal> > previous = new Stack<ArrayList<Fractal> >();
    static Stack<ArrayList<Fractal> > next = new Stack<ArrayList<Fractal> >();

    public static void renderFractalInGL(int i) throws IOException {
        if (fractals == null || i >= fractals.size()) return;
        fractals.get(i).renderInGL();
    }

    public static void generateNewGeneration()
    {
        if (fractals != null)
            previous.push(fractals);

        generation++;

        for (int i = 0; i < GraphicalInterface.selectedFractals.length; i++) {
            if (GraphicalInterface.selectedFractals[i])
                selectedFractals.add(fractals.get(i));
        }

        fractals = new ArrayList<Fractal>(9);

        Fractal newFractal;
        for (int i = 0; i < 9; i++)
            {
                if (selectedFractals.isEmpty()) {
                    newFractal = new Fractal();
                } else {
                    Fractal Parent1 = selectedFractals.get((int) (Math.random() * selectedFractals.size()));
                    if (i < 3) {
                        Fractal Parent2 = selectedFractals.get((int) (Math.random() * selectedFractals.size()));
                        newFractal = Parent1.cross(Parent2);
                    } else if (i < 6) {
                        newFractal = Parent1.mutate();
                    } else {
                        newFractal = Parent1.clone();
                    }
                }
                fractals.add(newFractal);
            }

        ImageManager.generateNewImages(fractals);

        selectedFractals.clear();
    }

    public static void drawImage(int index, Graphics g, int x, int y)
    {
        fractals.get(index).drawImage(g, x, y);
    }

    public static void decrementGeneration()
    {
        if (previous.isEmpty()) return;
        generation--;

        if (fractals != null)
            next.push(fractals);

        fractals = previous.pop();
        GraphicalInterface.frame.getContentPane().repaint();
    }

    public static void incrementGeneration()
    {
        if (next.isEmpty()) return;
        generation++;

        if (fractals != null)
            previous.push(fractals);

        fractals = next.pop();
        GraphicalInterface.frame.getContentPane().repaint();
    }

}

class ImageGenerator extends Thread
{
    Fractal fractal;

    public ImageGenerator(Fractal f)
    {
        fractal = f;
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
