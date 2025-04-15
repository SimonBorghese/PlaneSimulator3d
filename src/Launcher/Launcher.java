package Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class provides a swing ui for a launcher for the 3d graphics part of the app
 */
public class Launcher {
    /**
     * The primary JFrame of the launcher
     */
    private JFrame frame;

    /**
     * The panel for the map to select on
     */
    private MapPanel map_panel;

    /**
     * Our font for our big text
     */
    private Font textFont;

    /**
     * Our font for our small text
     */
    private Font smallTextFont;

    /**
     * Constructor for the launcher, creates objects but does not configure or enable them
     * @param width The width of the window to create
     * @param height The height of the window to create
     */
    public Launcher(int width, int height){
        frame = new JFrame("Plane Simulator 3D launcher");
        // This is the one exception to configuration to the constructor
        frame.setSize(width, height);

        // The text size is 1/16th of the width such that it'll scale with size (but not resizing)
        textFont = new Font("Serif", Font.PLAIN, (int) ((1.0 / 16.0) * (double) (width)));

        // The text size is 1/16th of the width such that it'll scale with size (but not resizing)
        smallTextFont = new Font("Serif", Font.PLAIN, (int) ((1.0 / 15.0) * (double) (width)));

        // Create our map panel
        map_panel = new MapPanel(frame.getWidth(), frame.getHeight());
    }

    /**
     * Configure this launcher and create the window
     */
    public void init(){
        // Set the layout to flow
        frame.setLayout(new FlowLayout());

        // Add all of our frame elements
        JLabel title = new JLabel("Launcher/Configurator");
        title.setFont(textFont);
        frame.add(title);

        JLabel instructions = new JLabel("Click somewhere!");
        instructions.setFont(smallTextFont);
        frame.add(instructions);
        frame.add(map_panel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        map_panel.addMouseMotionListener(new MouseUpdater());
        frame.setVisible(true);
    }

    /**
     * A listener to detect mouse motion in a panel to mark position to spawn in
     */
    private class MouseUpdater implements MouseMotionListener{

        /**
         * unused
         * @param mouseEvent unused
         */
        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            System.out.printf("Lat: %f Lng: %f\n", map_panel.getLat(), map_panel.getLng());
        }
    }

}
