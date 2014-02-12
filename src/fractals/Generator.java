package fractals;

import graphics.GraphicalInterface;

import java.awt.Graphics;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class Generator implements Serializable
{
    int generation = 0; // The current generation's number

    ArrayList<Fractal> fractals;  // Current Fractals
    ArrayList<Fractal> selectedFractals = new ArrayList<Fractal>(9);

    Stack<ArrayList<Fractal> > previous = new Stack<ArrayList<Fractal> >();
    Stack<ArrayList<Fractal> > next = new Stack<ArrayList<Fractal> >();

    public void renderFractalInGL(int i) throws IOException {
        if (fractals == null || i >= fractals.size()) return;
        fractals.get(i).renderInGL();
    }

    public void generateNewGeneration()
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

        selectedFractals.clear();
    }

    public void drawImage(int index, Graphics g, int x, int y)
    {
        fractals.get(index).drawImage(g, x, y);
    }

    public void decrementGeneration()
    {
        if (previous.isEmpty()) return;
        generation--;

        if (fractals != null)
            next.push(fractals);

        fractals = previous.pop();
        GraphicalInterface.frame.getContentPane().repaint();
    }

    public void incrementGeneration()
    {
        if (next.isEmpty()) return;
        generation++;

        if (fractals != null)
            previous.push(fractals);

        fractals = next.pop();
        GraphicalInterface.frame.getContentPane().repaint();
    }

}