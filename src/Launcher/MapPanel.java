package Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

/**
 * An extension of JPanel to have the map and utilities for getting latitude and longitude
 */
public class MapPanel extends JPanel {
    /**
     * The image icon of the map
     */
    private JLabel map;

    /**
     * The x coordinate of the mouse according to the listener, -1 if it hasn't been initialized
     */
    private int mouse_x;

    /**
     * The y coordinate of the mouse according to the listener, -1 if it hasn't been initialized
     */
    private int mouse_y;

    /**
     * Constructor for this specific map panels, creates a reasonably sized image
     * @param width The width of the FRAME
     * @param height The height of the FRAME
     */
    public MapPanel(int width, int height){
        super();
        try {
            // Load image, this is the part that can throw an exception
            BufferedImage map_jpeg = ImageIO.read(new File("Robinson_projection_SW.jpg"));

            // Calculate our desired image size
            double aspect_ratio = (double) map_jpeg.getWidth() / (double) map_jpeg.getHeight();
            int width_fraction = (4*width) / 5;

            // Image size: Width = Width Fraction
            // Height = width / aspect ratio
            map = new JLabel(new ImageIcon(new ImageIcon(map_jpeg)
                    .getImage()
                    .getScaledInstance(width_fraction,
                            (int) ((double) width_fraction / aspect_ratio), Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            System.out.println("FAILED TO CONSTRUCT OUR MAP IMAGE!");
            throw new RuntimeException(e);
        }

        // Add the map icon
        add(map);
        // Add our mouse updater
        addMouseListener(new MouseDetector());

        // Set up the repainter
        new Thread(new Repainter()).start();

        this.mouse_x = -1;
        this.mouse_y = -1;
    }

    /**
     * Return the latitude of the mouse cursor
     * @return A latitude between -90 and 90 which should be relative to the mouse cursor or NaN if mouse is invalid
     */
    public double getLat(){
        double relative_x = (((double) mouse_x / (double) getWidth()) - 0.5) * 2.0;

        if (mouse_x == -1){
            relative_x = Double.NaN;
        }

        return relative_x * 90.0;
    }

    /**
     * Return the longitude of the mouse cursor
     * @return A longitude between -180 and 180 should be relative to the mouse cursor or NaN if mouse is invalid
     */
    public double getLng(){
        double relative_y = (((double) mouse_y / (double) getHeight()) - 0.5) * 2.0;

        if (mouse_y == -1){
            relative_y = Double.NaN;
        }

        // The y is reversed relative to how x is
        return relative_y * -180.0;
    }

    /**
     * Paint this frame, specifically, a clear sphere
     * @param g The graphics context
     */
    @Override
    public void paint(Graphics g){
        super.paint(g);

        // Highlight the mouse cursor
        g.setColor(Color.RED);
        g.fillOval(mouse_x - 5, mouse_y - 5, 10,10);
    }

    /**
     * A listener for mouse clicks to mark locations
     */
    private class MouseDetector implements MouseListener{

        /**
         * Update the mouse coordinates on click
         * @param mouseEvent The event to read from
         */
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            mouse_x = mouseEvent.getX();
            mouse_y = mouseEvent.getY();
        }

        /**
         * Unused
         * @param mouseEvent unused
         */
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        /**
         * Unused
         * @param mouseEvent unused
         */
        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        /**
         * Unused
         * @param mouseEvent unused
         */
        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        /**
         * Unused
         * @param mouseEvent unused
         */
        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }

    /**
     * A runnable object which should run as a separate thread to repaint the panel
     */
    private class Repainter implements Runnable {
        /**
         * Whether to exit this swing worker
         */
        private boolean doExit;

        /**
         * Construct this painter, enabled by default
         */
        public Repainter(){
            super();
            doExit = false;
        }

        /**
         * To run in another thread, repaint the panel every 16 ms (60 FPS)
         */
        @Override
        public void run() {
            while (!doExit) {
                repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
