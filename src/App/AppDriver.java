package App;

import Data.DataDriver;
import Data.WorldCoordinate;
import Graphics.GLCamera;
import Graphics.GLTransform;
import Graphics.GraphicsDriver;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Math.Image;
import Math.Transform;
import Math.Vector;

/**
 * This class provides a driver for the app in which the main driver can call to
 * The app folder contains wrappers around various elements of this program.
 * The app driver should keep a list of all processes to run, in no particular order
 */
public class AppDriver {
    /**
     * The current context for this driver
     */
    private AppContext context;

    /**
     * The camera for this driver
     * TODO: REMOVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */
    private AppCamera camera;

    /**
     * An arraylist for all the processes within this app
     */
    private ArrayList<AppProcess> appProcesses;

    /**
     * A constructor for the App Driver which constructs objects but doesn't configure them
     * @param window_width The width of the window to construct
     * @param window_height The height of the window to construct
     */
    public AppDriver(int window_width, int window_height){
        try {
            // Construct the app processes array
            appProcesses = new ArrayList<>();

            // Create a graphics driver for our context, OpenGL 3.3 is embedded
            GraphicsDriver graphicsDriver = new GraphicsDriver(window_width, window_height, 3,3);

            // The data driver may through a configuration exception
            DataDriver dataDriver = new DataDriver();

            context = new AppContext(graphicsDriver, dataDriver, appProcesses);
        } catch (ConfigurationException e) {
            System.out.println("No Google API key provided. Please provide one in a .google_api_key file");
            System.exit(1);
        }
    }

    /**
     * Initialize the AppDriver with the constructed objects. This should be called before any other method
     */
    public void init(){
        // Initialize the graphics driver
        context.getGraphicsDriver().init();

        // Note: The data driver is always configured from the constructor of the data driver

        // Add our processes to our AppDriver
        camera = new AppCamera(100.0f, 100.0f);
        initializeAndAppend(camera);

        // TODO REMOVE ME!!!

        double[][] heightmap = new double[10][10];
        double lat = 37.80187;
        double lng = -79.27461;
        ArrayList<WorldCoordinate> cords = new ArrayList<>();

        for (int x = -2; x < 2; x++){
            for (int y = -2; y < 2; y++){
                double lat_p = (double) x * (0.001) + lat;
                double lng_p = (double) y * (0.001) + lng;
                cords.add(
                        new WorldCoordinate(lat_p, lng_p)
                );
            }
        }

        // Add our world process
        WorldProcess worldProcess = new WorldProcess(new WorldCoordinate(lat,lng), 15, 3);
        initializeAndAppend(worldProcess);

        //HeightMapProcess heightMapProcess = new HeightMapProcess();
        //initializeAndAppend(heightMapProcess);

    }

    /**
     * The loop method for the app driver. This is called by the primary driver and the method takes care of the looping
     */
    public void loop(){
        long time = System.currentTimeMillis();

        camera.getGLCamera().getTransform().getPos().setZ(0.0);

        camera.getGLCamera().getTransform().getPos().setX(0.0);

        camera.getGLCamera().getTransform().getPos().setY(10.0);

        camera.getGLCamera().getTransform().getRotation().setX(0.0);

        while (context.getGraphicsDriver().loop()){
            long current_time = System.currentTimeMillis();

            double dt = (double) (current_time - time) / 1000.0;

            time = System.currentTimeMillis();

            // Iterate through our processes
            for (AppProcess process : appProcesses){
                process.frame(dt, context);
            }

        }
    }

    /**
     * Initializes a provided AppProcess then add it to our list
     * @param process A constructed, but not yet configured, process
     * @throws IllegalStateException If the process is null. This is an illegal state because this method is only
     * being called within this class, so the parameters are clearly defined at compile time so if something did
     * break, it was likely due to the state of the program or something
     */
    private void initializeAndAppend(AppProcess process){
        if (process == null){
            throw new IllegalStateException("AppDriver tried to initialize a null process!");
        }

        process.init(context);
        appProcesses.add(process);
    }
}
