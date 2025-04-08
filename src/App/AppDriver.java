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
        camera = new AppCamera(100.0f, 10.0f);
        initializeAndAppend(camera);

        // TODO REMOVE ME!!!

        double[][] heightmap = new double[10][10];
        double lat = 37.837383;
        double lng = -79.068722;
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

        HashMap<WorldCoordinate, Float> result_cords = context.getDataDriver().getElevationData(cords);


        double lat_min = result_cords.keySet().stream().toList().getFirst().getLatitude();
        double lat_max = result_cords.keySet().stream().toList().getFirst().getLatitude();

        double lng_min = result_cords.keySet().stream().toList().getFirst().getLongitude();
        double lng_max = result_cords.keySet().stream().toList().getFirst().getLongitude();

        double min_elevation = result_cords.values().stream().toList().getFirst();

        for (Map.Entry<WorldCoordinate, Float> cord : result_cords.entrySet()){
            lat_min = Math.min(lat_min,cord.getKey().getLatitude());
            lat_max = Math.max(lat_max,cord.getKey().getLatitude());

            lng_min = Math.min(lng_min, cord.getKey().getLongitude());
            lng_max = Math.max(lng_max, cord.getKey().getLongitude());

            min_elevation = Math.min(min_elevation,cord.getValue());
        }

        // Create a tessilated square
        int resolution = 10;
        double constant_factor = 1000.0;

        float[] vertices = new float[resolution*resolution*5];
        int[] elements = new int[(resolution-1)*resolution * 2];

        for (int i =0;  i < resolution - 1; i++){
            for (int j = 0; j < resolution; j++){
                for (int k =0; k < 2; k++){
                    elements[(i*resolution*2) + (j*2) + k] = (j + resolution * (i + k));
                }
            }
        }

        for (int i = 0; i < resolution; i++){
            for (int j  = 0; j < resolution; j++){
                vertices[(i * resolution * 5) + (j * 5)]
                        = (float) ( -resolution / 2.0 + i);
                double result_y = 0.0;
                for (Map.Entry<WorldCoordinate, Float> cord: result_cords.entrySet()){
                    double pos_x = (lat_max - cord.getKey().getLatitude()) / (lat_max - lat_min);
                    double pos_y = (lng_max - cord.getKey().getLongitude()) / (lng_max - lng_min);

                    double radius_x = ((double) j / (double) resolution) - pos_x;
                    double radius_y = ((double) i / (double) resolution) - pos_y;

                    double radius = Math.sqrt(Math.pow(radius_x, 2) + Math.pow(radius_y, 2));
                    double r_sqr = Math.pow(radius, 2);

                    double dist = (cord.getValue() / (min_elevation)) * (1 / (r_sqr * constant_factor));
                    result_y += dist;
                    //System.out.printf("Result cord: %f, Result: %f\n", cord.getValue(), dist);
                }
                vertices[(i * resolution * 5) + (j * 5) + 1]
                        = (float) (0.0);
                vertices[(i * resolution * 5) + (j * 5) + 2]
                        = (float) (-resolution / 2.0 + j);

                vertices[(i * resolution * 5) + (j * 5) + 3] = (float) -(vertices[(i * resolution * 5) + (j * 5)]
                        + (resolution / 2.0)) / (float) (resolution-1);
                vertices[(i * resolution * 5) + (j * 5) + 4] = (float) (vertices[(i * resolution * 5) + (j * 5) + 2]
                        + (resolution / 2.0)) / (float) (resolution-1);
            }
        }

        WorldCoordinate base = new WorldCoordinate(lat,
                lng);
        try {
            Image raw_image = context.getDataDriver().getSatalliteImage(base, 13);

            context.getGraphicsDriver().pushTexture(raw_image);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        Graphics.GLHeightmap test_mesh = new Graphics.GLHeightmap(10);

        test_mesh.bindElementsForUse();

        test_mesh.uploadVertices(vertices);

        test_mesh.uploadElements(elements);

        test_mesh.configureVertexArray();

        Transform transform = new Transform();
        transform.getPos().setX(0.0);
        GLTransform glTransform = new GLTransform(transform);

        context.getGraphicsDriver().pushObject(glTransform);

        context.getGraphicsDriver().pushObject(test_mesh);

        try {
            Vector base_point = base.toPoint(256, 13);

            WorldCoordinate adjacent = new WorldCoordinate(base_point.getX() - 1, base_point.getY(), 13);
            Image raw_image = context.getDataDriver().getSatalliteImage(adjacent, 13);

            context.getGraphicsDriver().pushTexture(raw_image);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        Transform transform2 = new Transform();
        transform.getPos().setX(-9.0);
        GLTransform glTransform2 = new GLTransform(transform2);

        context.getGraphicsDriver().pushObject(glTransform2);
        context.getGraphicsDriver().pushObject(test_mesh);
    }

    /**
     * The loop method for the app driver. This is called by the primary driver and the method takes care of the looping
     */
    public void loop(){
        long time = System.currentTimeMillis();

        camera.getGLCamera().getTransform().getPos().setZ(-2.0);

        camera.getGLCamera().getTransform().getPos().setX(0.0);

        camera.getGLCamera().getTransform().getPos().setY(50.0);

        camera.getGLCamera().getTransform().getRotation().setX(-89.0);

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
