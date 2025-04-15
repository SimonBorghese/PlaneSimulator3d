package Utils;

import javax.swing.*;
import java.awt.*;

/**
 * This class provided a utility which can be used anywhere in the program to indicate an error to the user
 */
public class ErrorPopup {
    /**
     * The frame of this popup
     */
    private JFrame popup;

    /**
     * The label for this popup
     */
    private JLabel message;

    /**
     * Make an error popup, spawned immediately
     * @param message_str The message to display
     */
    public ErrorPopup(String message_str){
        popup = new JFrame("ERROR!");
        popup.setSize(400,120);
        popup.setLayout(new FlowLayout());

        message = new JLabel(message_str);

        popup.add(message);

        popup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        popup.setVisible(true);
    }
}
