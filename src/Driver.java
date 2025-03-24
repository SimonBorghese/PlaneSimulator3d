import Data.DataDriver;
import Data.WorldCoordinate;

import javax.imageio.ImageIO;
import javax.naming.ConfigurationException;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Math.Vector;
/**
 * PlaneSimulator3d
 * This is a flight tracker/simulator created by Simon Borghese for CS 220
 * All files in src were authored entirely by Simon Borghese
 * This code will be uploaded at a later date to Github under an Open Source License.
 * @author Simon Borghese
 */

public class Driver {
    public static void main(String[] args){
        /*
        Graphics.Window win = new Graphics.Window(800, 600, 4,1);

        win.init();

        win.loop();

        win.destroy();

         */

            try {
                Data.DataDriver dataDriver = new DataDriver();

                ArrayList<WorldCoordinate> requested_locations = new ArrayList<>();

                requested_locations.add(new WorldCoordinate(39.7391536,
                        -104.9847034));

                HashMap<WorldCoordinate, Float> elevations = dataDriver.getElevationData(requested_locations);

                for (Map.Entry<WorldCoordinate, Float> coordinate : elevations.entrySet()) {
                    System.out.printf("At lat: %f and Long: %f, the elevation is: %f\n", coordinate.getKey().getLatitude(),
                            coordinate.getKey().getLongitude(), coordinate.getValue());
                }

                byte[] raw_image = dataDriver.getSatalliteImage(new WorldCoordinate(39.7391536,
                        -104.9847034), 15);

                JFrame frame = new JFrame();

                    //BufferedImage image = new BufferedImage(512,512, BufferedImage.TYPE_INT_RGB);

                    //Graphics2D g = image.createGraphics();

                    //g.drawBytes(raw_image, 0, raw_image.length, 512, 512);

                    //g.dispose();

                    JPanel panel = new JPanel();

                    //frame.add(new JLabel(new ImageIcon(image)));


                    frame.setSize(800,600);

                    frame.setLayout(null);

                frame.setVisible(true);

            } catch (ConfigurationException e) {
                throw new RuntimeException(e);
            }
    }
}
