import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * PlaneSimulator3d
 * This is a flight tracker/simulator created by Simon Borghese for CS 220
 * All files in src were authored entirely by Simon Borghese
 * This code will be uploaded at a later date to Github under an Open Source License.
 * @author Simon Borghese
 */

public class Driver {
    public static void main(String[] args){
        Graphics.Window win = new Graphics.Window(800, 600, 4,3);

        win.init();

        win.loop();

        win.destroy();

        try {
            Data.InternetDriver int_dri = new Data.InternetDriver();

            HashMap<String, String> cords = new HashMap<String, String>();
            cords.put("39.7391536", "-104.9847034");
            System.out.println(int_dri.getElevation(cords));
        } catch (FileNotFoundException e) {
            System.out.println("Configuration file not found!");
        }
    }
}
