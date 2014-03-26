package fractals;

import graphics.GraphicalInterface;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Generates new generations of Fractals.
 */
public class Generator implements Serializable {
    private int generation = 0; // The current generation's number

    ArrayList<Fractal> fractals;  // Current generation of Fractals
    ArrayList<Fractal> selectedFractals = new ArrayList<>(9);  //The list of fractals that have been selected
    // by the user

    Stack<ArrayList<Fractal>> previous = new Stack<>(); //The previous generations of Fractals
    Stack<ArrayList<Fractal>> next = new Stack<>(); //The future generations of Fractals (if the
    // user has gone back to previous generations)

    /**
     * Renders the ith Fractal in the C-Backend Render mode.
     *
     * @param i
     *
     * @throws IOException
     */
    public void renderFractalInGL(int i) throws IOException {
        //Ensure that the index is in bounds
        if (fractals == null || i >= fractals.size()) {
            return;
        }
        fractals.get(i).renderInGL();
    }

    /**
     * Generates a new generation. Initially, no Fractals are selected so the first generation
     * will be completely random. Generations after this will be based on the Fractals that the
     * user had selected in the previous generation.
     */
    public void generateNewGeneration() {
        //Halt all of the ImageManager threads from the previous generation to prevent slow-downs in the current
        // generation
        ImageManager.interruptThreads();

        //Add the old generation to the Stack of old generations if this is not the first generation
        if (fractals != null) {
            previous.push(fractals);
        }

        //Increment the generation count
        generation++;

        //Find all of the Fractals that the user has selected
        for (int i = 0; i < GraphicalInterface.selectedFractals.length; i++) {
            if (GraphicalInterface.selectedFractals[i]) {
                selectedFractals.add(fractals.get(i));
            }
        }

        //Clear out the old Fractals
        fractals = new ArrayList<>(9);

        //Generate the new Fractals one by one
        Fractal newFractal;
        for (int i = 0; i < 9; i++) {
            //If there were no selected parents in the previous generation, just generate a new random Fractal
            if (selectedFractals.isEmpty()) {
                newFractal = new Fractal();
            }
            else {
                //Choose a random parent for the new Fractal from the pool of user-selected Fractals
                Fractal parent1 = selectedFractals.get((int) (Math.random() * selectedFractals.size()));
                //For the first row of Fractals, perform Cross-over
                if (i < 3) {
                        /*
                        Cross-over requires a second parent. Choose this second parent from the pool of user-selected
                         Fractals.
                        parent1 and parent2 can refer to the same Fractal.
                         */
                    Fractal parent2 = selectedFractals.get((int) (Math.random() * selectedFractals.size()));
                    newFractal = parent1.cross(parent2);
                    //Fill in information about the Fractal's creation
                    newFractal.setOperation("cross");
                    newFractal.setParents(parent1, parent2);
                }
                else if (i < 6) {
                    //For the second row of Fractals, perform mutation
                    newFractal = parent1.mutate();
                    newFractal.setOperation("mutate");
                }
                else {
                    //For the third row of Fractals, perform introduction
                    newFractal = parent1.introduce();
                    newFractal.setOperation("introduce");
                    newFractal.setParents(parent1, null);
                }
            }
            //Add the new Fractal to the new generation
            fractals.add(newFractal);
        }

        //Clear the list of selected Fractals
        selectedFractals.clear();
    }

    /**
     * Draws the ith Fractal at the given (x,y) coordinate. This coordinate represents the top-left corner of the
     * image.
     *
     * @param index
     * @param g
     * @param x
     * @param y
     */
    public void drawImage(int index, Graphics g, int x, int y) {
        fractals.get(index).drawImage(g, x, y);
    }

    /**
     * Goes back to the previous generation of Fractals. If the user is already at the first generation, this method
     * does nothing.
     */
    public void decrementGeneration() {
        //Don't allow going before the first generation
        if (previous.isEmpty()) {
            return;
        }

        //Halt all of the ImageManager threads from the previous generation to prevent slow-downs in the current
        // generation
        ImageManager.interruptThreads();

        //Decrement the generation count
        generation--;

        //Push the current generation onto the Stack of future generations
        if (fractals != null) {
            next.push(fractals);
        }

        //Fetch the previous generation
        fractals = previous.pop();

        //Repaint the window
        GraphicalInterface.frame.getContentPane().repaint();
    }

    /**
     * Goes forward one generation of Fractals. If the user is already at the last generation, this method does
     * nothing.
     */
    public void incrementGeneration() {
        //Don't allow going past the last generation
        if (next.isEmpty()) {
            return;
        }

        //Halt all of the ImageManager threads from the previous generation to prevent slow-downs in the current
        // generation
        ImageManager.interruptThreads();

        //Increment the generation count
        generation++;

        //Add the current generation to the Stack of previous generations
        if (fractals != null) {
            previous.push(fractals);
        }

        //Fetch the previous generation of Fractals
        fractals = next.pop();

        //Repaint the window
        GraphicalInterface.frame.getContentPane().repaint();
    }

    /**
     * Returns the number current generation
     *
     * @return
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Returns the total number of generations
     *
     * @return
     */
    public int getTotalGenerations() {
        return generation + next.size();
    }
}