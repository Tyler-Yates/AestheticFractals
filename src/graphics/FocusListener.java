package graphics;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class FocusListener implements WindowFocusListener {
    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        GraphicalInterface.frame.requestFocus();
    }
}
