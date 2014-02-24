package graphics;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Class used to display dialogs for file interactions
 */
public class FileChooser {
    /**
     * Opens a save file dialog.
     * Returns the String representing the absolute path of the user-selected file. If the user did not choose a file,
     * this method returns null.
     *
     * The dialog will only show directories and files with the given extension.
     *
     * @return
     */
    public static String showSaveDialog(String extension) {
        JFileChooser fileChooser = new JFileChooser();

        //Limit the files shown to only those of the given extension
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(extension+" files", extension);
        fileChooser.setFileFilter(fileNameExtensionFilter);

        //Set the default directory for the FileChooser to the AestheticFractals directory
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        //Determine if the user pressed the 'Save' or 'Cancel' button
        int status = fileChooser.showSaveDialog(GraphicalInterface.frame);
        //If the user pressed 'Save', return the path of the file
        if (status == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            String path = f.getAbsolutePath();
            //Ensure that typed file names still include the given extension
            if (!path.endsWith(extension))
                path += "." + extension;
            return path;
        }
        //If the user pressed 'Cancel' return null
        return null;
    }

    /**
     * Opens a load file dialog.
     * Returns the String representing the absolute path of the user-selected file. If the user did not choose a file,
     * this method returns null.
     *
     * The dialog will only show directories and files with the given extension.
     *
     * @return
     */
    public static String showLoadDialog(String extension) {
        JFileChooser fileChooser = new JFileChooser();

        //Limit the files shown to only those of the given extension
        FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter(extension+" files", extension);
        fileChooser.setFileFilter(fileNameExtensionFilter);

        //Set the default directory for the FileChooser to the AestheticFractals directory
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        //Determine if the user pressed the 'Save' or 'Cancel' button
        int status = fileChooser.showOpenDialog(GraphicalInterface.frame);
        //If the user pressed 'Save', return the path of the file
        if (status == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            return f.getAbsolutePath();
        }
        //If the user pressed 'Cancel' return null
        return null;
    }
}
