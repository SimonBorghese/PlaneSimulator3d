package App;

import Data.DataDriver;
import Data.WorldCoordinate;

import java.security.InvalidParameterException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import Graphics.GLTransform;
import Math.Image;
import Math.Transform;
import Math.Vector;


import javax.naming.ConfigurationException;

/**
 * This process manages the loading of satellite data based on the camera position. Uniquely, this process will
 * spawn threads from an implemented Runner child class.
 */
public class WorldProcess implements AppProcess{
    /**
     * This is the initial coordinate of the scene, everything is relative to this point
     */
    private WorldCoordinate initial;

    /**
     * The initial zoom level
     */
    private int zoom;

    /**
     * A list holding tile locations and their WorldGenerationThread
     * If no key exist, it hasn't loaded
     * If the key exist and the thread isn't running, it is ready
     * If the key exist and the thread is running then the data is still being parsed
     */
    //private HashMap<Map.Entry<Integer, Integer>, WorldGenerationThread> coordinateThreads;
    private HashMap<Integer, HashMap<Integer, WorldGenerationThread>> coordinateThreads;

    /**
     * Construct this WorldProcess with a provided base location
     * @param initial The initial world coordinate we start from
     * @param zoom The 0 level zoom for this world
     * @throws InvalidParameterException If initial is null
     */
    public WorldProcess(WorldCoordinate initial, int zoom) {
        if (initial == null){
            throw new InvalidParameterException("Initial world coordinate for a WorldProcess must not be null");
        }
        this.initial = initial;
        this.zoom = zoom;
        coordinateThreads = new HashMap<>();
    }

    /**
     * Empty as there is nothing to initialize
     * @param context Unused
     * @throws InvalidParameterException Never
     */
    @Override
    public void init(AppContext context) throws InvalidParameterException {
        for (int y = -1; y < 2; y++){
            coordinateThreads.put(y, new HashMap<>());
            HashMap<Integer, WorldGenerationThread> horizontal_lines = coordinateThreads.get(y);
            for (int x = -1; x < 2; x++){
                WorldGenerationThread thread = new WorldGenerationThread(context.getDataDriver());

                Vector initial_pos = initial.getTile();
                thread.setLocation(new WorldCoordinate((int) initial_pos.getX() - x, (int)initial_pos.getY() + y, zoom),new Vector(x,y,0), zoom ,1);
                System.out.printf("Looking at: %d %d\n", (int) initial_pos.getX() - x,(int) initial_pos.getY() - y);
                thread.start();

                horizontal_lines.put(x, thread);
            }
        }
    }

    /**
     * Read the camera's position and load in new tiles as needed
     * @param dt The time, in seconds, since the last call (Delta Time)
     * @param context The current app context this process is running in
     * @throws InvalidParameterException If context is null
     * @throws IllegalStateException If no camera could be found in the context
     */
    @Override
    public void frame(double dt, AppContext context) throws InvalidParameterException {
        if (context == null){
            throw new InvalidParameterException("Provided Context to WorldProcess is null!");
        }

        AppCamera cam = (AppCamera) context.findAppProcess(AppCamera.class);
        if (cam == null){
            throw new IllegalStateException("No Camera in provided app process list!");
        }
        Vector pos = cam.getGLCamera().getTransform().getPos();
        int resolution = 10;
        int x = (int) Math.floor(pos.getX() / (double) (resolution/5));
        int y = (int) Math.floor(pos.getZ() / (double) (resolution/5));

        //System.out.printf("Checking: %d %d\n",x,y);

        for (HashMap<Integer, WorldGenerationThread> horzintal_rows : coordinateThreads.values()){
            for (WorldGenerationThread thread : horzintal_rows.values()){
                if (thread.isReady && !thread.isFinished){
                    Vector thread_offset = thread.getOffset();
                    /*
                    HashMap<WorldCoordinate, Float> result_cords = thread.getElevations();

                    double lat_min = result_cords.keySet().stream().toList().getFirst().getLatitude();
                    double lat_max = result_cords.keySet().stream().toList().getFirst().getLatitude();

                    double lng_min = result_cords.keySet().stream().toList().getFirst().getLongitude();
                    double lng_max = result_cords.keySet().stream().toList().getFirst().getLongitude();

                    double min_elevation = result_cords.values().stream().toList().getFirst();

                    for (Map.Entry<WorldCoordinate, Float> cord : result_cords.entrySet()) {
                        lat_min = Math.min(lat_min, cord.getKey().getLatitude());
                        lat_max = Math.max(lat_max, cord.getKey().getLatitude());

                        lng_min = Math.min(lng_min, cord.getKey().getLongitude());
                        lng_max = Math.max(lng_max, cord.getKey().getLongitude());

                        min_elevation = Math.min(min_elevation, cord.getValue());
                    }

                     */
                    // Create a tessilated square
                    double constant_factor = 1000.0;

                    float[] vertices = new float[(resolution+1)* (resolution+1) * 5];
                    ArrayList<Float> vertexes = new ArrayList<>();
                    int[] elements = new int[(resolution - 1) * resolution * 2];

                    for (int i = 0; i < resolution - 1; i++) {
                        for (int j = 0; j < resolution; j++) {
                            for (int k = 0; k < 2; k++) {
                                elements[(i * resolution * 2) + (j * 2) + k] = (j + resolution * (i + k));
                            }
                        }
                    }

                    for (int i = 0; i < resolution; i++) {
                        for (int j = 0; j < resolution; j++) {
                            vertexes.add((float) (-resolution / 2.0 + i));
                            double result_y = 0.0;
                            /*
                            for (Map.Entry<WorldCoordinate, Float> cord : result_cords.entrySet()) {
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

                             */
                            vertexes.add((float) (0.0));
                            vertexes.add((float) (-resolution / 2.0 + j));

                            vertexes.add((float) (-((float) (-resolution / 2.0 + i)
                                    + (resolution / 2.0)) / (float) (resolution -1)));
                            vertexes.add((float) -((float) (-resolution / 2.0 + j)
                                    + (resolution / 2.0)) / (float) (resolution -1));
                        }
                    }

                    context.getGraphicsDriver().pushTexture(thread.getImage());

                    Graphics.GLHeightmap test_mesh = new Graphics.GLHeightmap(10);

                    test_mesh.bindElementsForUse();

                    vertices = new float[vertexes.size()];
                    for (int i =0; i < vertexes.size(); i++){
                        vertices[i] = vertexes.get(i);
                    }

                    test_mesh.uploadVertices(vertices);

                    test_mesh.uploadElements(elements);

                    test_mesh.configureVertexArray();

                    Transform transform = new Transform();
                    transform.getPos().setX(thread_offset.getX() * 9);
                    transform.getPos().setZ(thread_offset.getY() * 9);
                    GLTransform glTransform = new GLTransform(transform);

                    context.getGraphicsDriver().pushObject(glTransform);

                    context.getGraphicsDriver().pushObject(test_mesh);

                    thread.isFinished = true;
                }
            }
        }

        HashMap<Integer, WorldGenerationThread> horizontal_lines = coordinateThreads.get(Integer.valueOf(y));
        if (horizontal_lines != null){
            WorldGenerationThread check_thread = horizontal_lines.get(Integer.valueOf(x));
            // TODO: CHANGE ME TO A METHOD
            if (check_thread != null){
                if (check_thread.isReady && !check_thread.isFinished) {

                }
            } else{
                WorldGenerationThread thread = new WorldGenerationThread(context.getDataDriver());

                Vector initial_pos = initial.getTile();
                thread.setLocation(new WorldCoordinate((int) initial_pos.getX() - x, (int)initial_pos.getY() + y, zoom),new Vector(x,y,0), zoom ,1);

                System.out.printf("Looking at: %d %d\n", (int) initial_pos.getX() - x,(int) initial_pos.getY() - y);

                thread.start();

                horizontal_lines.put(x, thread);
            }
        } else{
            coordinateThreads.put(y, new HashMap<>());
            horizontal_lines = coordinateThreads.get(Integer.valueOf(y));
            WorldGenerationThread thread = new WorldGenerationThread(context.getDataDriver());

            Vector initial_pos = initial.getTile();
            thread.setLocation(new WorldCoordinate((int) initial_pos.getX() - x, (int)initial_pos.getY() + y, zoom),new Vector(x,y,0), zoom ,1);
            System.out.printf("Looking at: %d %d\n", (int) initial_pos.getX() - x,(int) initial_pos.getY() - y);
            thread.start();

            horizontal_lines.put(x, thread);
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * A class which implements threading for loading in world data
     */
    private class WorldGenerationThread extends Thread {

        /**
         * A derived DataDriver from the host app, it needs to be derived as to not potentially break the main
         * thread.
         */
        private DataDriver dataDriver;

        /**
         * The world coordinate to read our data from
         */
        private WorldCoordinate location;

        /**
         * The zoom for our location's tile
         */
        private int zoom;

        /**
         * The resolution of our elevation coordinates
         */
        private int elevation_res;

        /**
         * Our resultant elevations, should only be used after the thread finishes
         */
        private HashMap<WorldCoordinate, Float> result_elevation;

        /**
         * Our resultant image, should only be used after the thread finishes
         */
        private Image result_image;

        /**
         * A simple flag to check whether or not this thread is reasy
         * false always except when run is finished
         */
        private boolean isReady;

        /**
         * Another flag to indicate whether or not this thread has been used
         * false, to be set by the WorldProcess
         */
        private boolean isFinished;

        /**
         * The vector to offset by
         */
        private Vector offset;

        /**
         * Construct this process from a provided data driver (this'll generate a derivative data driver and not
         * copy it)
         * @param dataDriver The data driver to derive from
         */
        public WorldGenerationThread(DataDriver dataDriver) {
            super();
            this.dataDriver = new DataDriver(dataDriver);
            isReady = false;
            isFinished = false;
        }

        /**
         * Start reading the data from the Google API. Finished when data is read and interpreted
         * @throws IllegalStateException If location is null, zoom is zero, or elevation_res is <= 0
         * (likely due to the thread's parameters not being set)
         */
        @Override
        public void run() {
            readWorldElevation();

            // Reads the image from the data driver
            try {
                result_image = dataDriver.getSatalliteImage(location, zoom);
            } catch (ConfigurationException e) {
                System.out.println("DataDriver improperly configured for thread!!!");
            }
        }

        /**
         * This method should be called within a spawned thread.
         * This method generates, reads from the API, then parses our world elevation
         * This is then stored in the result_elevation variable
         */
        private void readWorldElevation(){
            if (location == null || zoom < 1 || elevation_res <= 0) {
                throw new InvalidParameterException("WorldProcess parameters not set before execution");
            }

            Vector base_loc = location.getTile();
            WorldCoordinate up = new WorldCoordinate(base_loc.getX(), base_loc.getY() + 1, zoom);
            WorldCoordinate down = new WorldCoordinate(base_loc.getX(), base_loc.getY() - 1, zoom);
            WorldCoordinate left = new WorldCoordinate(base_loc.getX() - 1, base_loc.getY(), zoom);
            WorldCoordinate right = new WorldCoordinate(base_loc.getX() + 1, base_loc.getY(), zoom);

            double max_long = up.getWorldCoordinate().getY();
            double min_long = down.getWorldCoordinate().getY();
            double max_lat = right.getWorldCoordinate().getX();
            double min_lat = left.getWorldCoordinate().getX();

            double base_lat = location.getWorldCoordinate().getX();
            double base_long = location.getWorldCoordinate().getY();

            double delta_lat = (max_lat - min_lat)  / 2.0;
            double delta_lon = (max_long - min_long) / 2.0;

            // Calculate our needed coordinates
            ArrayList<WorldCoordinate> cords = new ArrayList<>();

            for (int x = -elevation_res; x < elevation_res; x++){
                for (int y = -elevation_res; y < elevation_res; y++){
                    double lat_p = ((double) x * delta_lat / (double) elevation_res) + base_lat;
                    double lng_p = ((double) y * delta_lon / (double) elevation_res) + base_long;
                    cords.add(
                            new WorldCoordinate(lat_p, lng_p)
                    );
                }
            }

            //result_elevation = dataDriver.getElevationData(cords);

            try {
                result_image = dataDriver.getSatalliteImage(location, zoom);
            } catch (ConfigurationException e) {
                // TODO FIX ME!!!!!
                throw new RuntimeException(e);
            }

            System.out.println("THREAD READY!");
            isReady = true;
        }

        /**
         * Sets this thread's coordinate and zoom
         * @param location A WorldCoordinate which represents our location
         * @param offset A 2d vector for the offset in the X-Y
         * @param zoom A zoom for our satellite data
         * @param elevation_res The resolution for our elevation data, should be  < 3 but not enforced
         * @throws java.security.InvalidParameterException If zoom is < 1, if location is null, if offset is null, or if elevation_res is <= 0
         */
        public void setLocation(WorldCoordinate location, Vector offset, int zoom, int elevation_res) {
            if (zoom < 1 || location == null || offset == null || elevation_res <= 0) {
                throw new InvalidParameterException("Zoom must be greater than zero, location and offset cannot be null, and elevation resolution must be > 0");
            }
            this.offset = offset;
            Vector loc_point = location.getTile();
            this.location = new WorldCoordinate(loc_point.getX(), loc_point.getY(), zoom);
            this.zoom = zoom;
            this.elevation_res = elevation_res;
        }

        /**
         * Return our current image, may be null, may be invalid. But wont be either if the thread has run
         * @return An Image from the satellite API
         */
        public Image getImage(){
            return result_image;
        }

        /**
         * Return the world coordinates which have elevation data. May be null or invalid but wont be either if the
         * thread has run.
         * @return A hashmap of world coordinates and their respective elevation
         */
        public HashMap<WorldCoordinate, Float> getElevations(){
            return result_elevation;
        }

        /**
         * Get the assigned offset
         * @return A Vector that was the offset set for this thread
         */
        public Vector getOffset(){
            return offset;
        }


    }
}
