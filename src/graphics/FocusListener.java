package graphics;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class FocusListener implements WindowFocusListener {
    /*
    Defines whether the Listener should attempt to regain focus
    True indicates the Listener should regain focus
    False indicates the Listener should not regain focus
     */
    private boolean focus = true;

    /**
     * Tells the current FocusListener to stop trying to regain focus
     */
    public void loseFocus() {
        focus = false;
    }

    /**
     * Tells the current FocusListener to resume trying to regain focus. This method will also attempt to regain focus
     * as soon as this method is called.
     */
    public void regainFocus() {
        focus = true;
        GraphicalInterface.frame.requestFocus();
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        if (focus)
            GraphicalInterface.frame.requestFocus();
    }
}
