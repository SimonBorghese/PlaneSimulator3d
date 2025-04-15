package Launcher;

import javax.swing.*;

import Math.Vector;
import org.intellij.lang.annotations.Flow;

import java.awt.*;

/**
 * This panel contains the inputs for launching the app portion
 */
public class LaunchPanel extends JPanel {
    /**
     * A text field to hold a visual indication of the currently selected latitude and longitude
     * Should be immutable
     */
    private JTextField location_selection;

    /**
     * A vector where X and Y are the selected latitude and longitude
     * If either number is NaN, no location has been selected yet
     */
    private Vector location;

    /**
     * The width of the window input
     */
    private LabelInput width;

    /**
     * The height of the window input
     */
    private LabelInput height;

    /**
     * Construct this panel by constructing all objects
     */
    public LaunchPanel(){
        super(new FlowLayout());
        location_selection = new JTextField(25);
        location_selection.setText("No Location");
        location_selection.setEnabled(false);

        location = new Vector(Float.NaN, Float.NaN, Float.NaN);

        JTextField default_width = new JTextField(4);
        default_width.setText("800");

        JTextField default_height = new JTextField(4);
        default_height.setText("600");


        width = new LabelInput(new JLabel("Width"), default_width);
        height = new LabelInput(new JLabel("Height"), default_height);

        add(location_selection);
        add(width);
        add(height);
    }

    /**
     * Sets the latitude and longitude values
     * @param lat Latitude
     * @param lng Longitude
     */
    public void setLocation(double lat, double lng){
        location = new Vector(lat,lng,0);
        location_selection.setText(String.format("Lat: %.6f Lng: %.6f", lat,lng));
    }

    /**
     * Gets the currently set width
     * @return The width, parsed as an int, 0 if it failed
     */
    public int getWindowWidth(){
        int result;
        try {
            result = Integer.parseInt(width.getInput());
        } catch (Exception e){
            result = 0;
        }

        return result;
    }

    /**
     * Gets the currently set height
     * @return The height, parsed as an int, 0 if it failed
     */
    public int getWindowHeight(){
        int result;
        try {
            result = Integer.parseInt(height.getInput());
        } catch (Exception e){
            result = 0;
        }

        return result;
    }

    /**
     * This class provides a wrapper for making a Label right next to an Input
     */
    private class LabelInput extends JPanel{
        /**
         * The label
         */
        private JLabel label;

        /**
         * The input
         */
        private JTextField input;

        /**
         * The programmer must create the label and input then this constructor will add this to the panek
         * @param label A pre-constructed label to use
         * @param input A pre-constructed input field to use
         */
        public LabelInput(JLabel label, JTextField input){
            super(new FlowLayout());

            this.label = label;
            this.input = input;

            add(label);
            add(input);
        }

        /**
         * Get the current input string
         * @return A string which has been input into the input field
         */
        public String getInput(){
            return input.getText();
        }

    }
}
