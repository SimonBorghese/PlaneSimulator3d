package Launcher;

import App.AppDriver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
     * The panel for the launching portion
     */
    private LaunchPanel launch_panel;

    /**
     * The button to launch the program
     */
    private JButton launch_button;

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

        launch_panel = new LaunchPanel();

        // Create the launch button
        launch_button = new JButton("Launch!");
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

        JLabel instructions = new JLabel(" Click somewhere!");
        instructions.setFont(smallTextFont);
        frame.add(instructions);
        frame.add(map_panel);

        launch_button.addActionListener(new AppLauncher());
        launch_panel.add(launch_button);


        frame.add(launch_panel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        map_panel.addMouseMotionListener(new MouseUpdater());
        frame.setVisible(true);
    }

    private class AppLauncher implements ActionListener{
        /**
         * Empty constructpr
         */
        public AppLauncher(){

        }

        /**
         * When this action is called, the app is launched
         * @param actionEvent unused
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            double lat = map_panel.getLat();
            double lng = map_panel.getLng();

            // Santiy check for NaN
            if ((lat < -91.0 && lat > 91.0) || lat == Double.NaN) {
                new Utils.ErrorPopup("SELECT A VALID LOCATION!");
                return;
            }

            int window_width = launch_panel.getWindowWidth();
            int window_height = launch_panel.getWindowHeight();

            if (window_width <= 0 || window_height <= 0){
                new Utils.ErrorPopup("ENTER VALID WINDOW DIMENSIONS!");
                return;
            }

            // Spawn a thread for this process. We don't really need to do anything for the thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDriver app = new AppDriver(window_width, window_height);

                    app.init(lat,lng);

                    app.loop();
                }
            }).start();
        }
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
            // Santiy check for NaN
            if (map_panel.getLat() > -91.0 && map_panel.getLat() < 91.0) {
                System.out.printf("Lat: %f Lng: %f\n", map_panel.getLat(), map_panel.getLng());
                launch_panel.setLocation(map_panel.getLat(), map_panel.getLng());
            }
        }
    }

}
