package graphics;

import javax.swing.*;
import java.awt.*;

public class EvolutionSelector extends JPanel {
    private static JFrame frame;

    //Used to allow JLabel to word-wrap
    private static final String HTML_Formatting = "<html><body style='width: 100px'>";

    public static JCheckBox xEquation = new JCheckBox("x");
    public static JCheckBox yEquation = new JCheckBox("y");
    public static JCheckBox colorEquation = new JCheckBox("color");
    private static JLabel title = new JLabel(HTML_Formatting + "Select Equations to Evolve:");


    public EvolutionSelector(int x, int y) {
        frame = new JFrame("Evolution Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setSize(150, 300);
        frame.setResizable(false);
        frame.setVisible(true);
        //Don't let this window take focus away from the GraphicalInterface window
        frame.setFocusableWindowState(false);
        frame.getContentPane().setLayout(new GridLayout(4, 2));

        //Add the title text to the windows
        frame.getContentPane().add(title);

        //Add the evolution selectors
        frame.getContentPane().add(xEquation);
        frame.getContentPane().add(yEquation);
        frame.getContentPane().add(colorEquation);
    }
}
