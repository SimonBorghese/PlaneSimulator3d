package App;

import Data.DataDriver;
import Data.WorldCoordinate;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import Graphics.GLTransform;
import Graphics.GraphicsDriver;
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
     * The number of levels to zoom out by
     */
    private int zoom_out;

    /**
     * A list holding tile locations and their WorldGenerationThread
     * If no key exist, it hasn't loaded
     * If the key exist and the thread isn't running, it is ready
     * If the key exist and the thread is running then the data is still being parsed
     */
    //private HashMap<Map.Entry<Integer, Integer>, WorldGenerationThread> coordinateThreads;
            // Zoom level, Y offset, X offset, Thread
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, WorldGenerationThread>>> coordinateThreads;

    /**
     * TODO FIX OR FIND BETTER SOLUTION!
     */
    private HashMap<WorldCoordinate, Float> globalElevations;

    /**
     * Construct this WorldProcess with a provided base location
     * @param initial The initial world coordinate we start from
     * @param zoom The 0 level zoom for this world
     * @param zoom_out The number of zoom levels to move up
     * @throws InvalidParameterException If initial is null
     */
    public WorldProcess(WorldCoordinate initial, int zoom, int zoom_out) {
        if (initial == null){
            throw new InvalidParameterException("Initial world coordinate for a WorldProcess must not be null");
        }
        this.initial = initial;
        this.zoom = zoom;
        this.zoom_out = zoom_out;
        coordinateThreads = new HashMap<>();

        // TODO: REMOVE ME
        globalElevations = new HashMap<>();
    }

    // App Process related methods

    /**
     * Empty as there is nothing to initialize
     * @param context Unused
     * @throws InvalidParameterException Never
     */
    @Override
    public void init(AppContext context) throws InvalidParameterException {
        WorldCoordinate initial_15 = new WorldCoordinate(initial.getWorldCoordinate().getX(),
                initial.getWorldCoordinate().getY(), 256, zoom);
        WorldCoordinate initial_14 = new WorldCoordinate(initial.getWorldCoordinate().getX(),
                initial.getWorldCoordinate().getY(), 256, zoom-1);
        WorldCoordinate initial_13 = new WorldCoordinate(initial.getWorldCoordinate().getX(),
                initial.getWorldCoordinate().getY(), 256, zoom-2);
        WorldCoordinate initial_12 = new WorldCoordinate(initial.getWorldCoordinate().getX(),
                initial.getWorldCoordinate().getY(), 256, zoom-3);


        for (int x = 0; x <= 0; x++){
            for (int y = 0; y <= 0; y++){
                spawnMesh(context.getDataDriver(), initial_15, new Vector(x ,y ,0), zoom);
            }
        }
        //spawnMesh(context.getDataDriver(), initial_14, new Vector(0,0,0), zoom-1);
        //spawnMesh(context.getDataDriver(), initial_13, new Vector(0,0,0), zoom-2);
        //spawnMesh(context.getDataDriver(), initial_12, new Vector(0,0,0), zoom - 3);
    }

    /**
     * A private method to load a mesh from a ready thread
     * @param gDriver The graphics driver to push the mesh to
     * @param thread The target thread to load from
     * @param zoom The zoom level of this mesh
     * @throws InvalidParameterException If the thread is null or not ready
     */
    private void loadMesh(GraphicsDriver gDriver, WorldGenerationThread thread, int zoom){
        if (thread == null || !thread.isReady){
            throw new InvalidParameterException("Provided thread for throwing mesh is either null or not-ready!");
        }
        Vector thread_offset = thread.getOffset();

        WorldGenerationThread.HeightmapMesh[] meshes = thread.result_meshes;
        Image[] images = thread.result_image;

        int mesh_resolution = (int) Math.sqrt((double) Math.min(meshes.length, images.length));

        int mesh_res_range = (int) Math.floor((double) mesh_resolution / 2.0);
        for (int y = -mesh_res_range; y <= mesh_res_range; y++) {
            for (int x = -mesh_res_range; x <= mesh_res_range; x++) {
                int index = ((y + mesh_res_range) * mesh_resolution) + (x+mesh_res_range);
                Image img = images[index];
                WorldGenerationThread.HeightmapMesh mesh = meshes[index];

                WorldCoordinate point = thread.generateAdjacentTiles()[((mesh_res_range) * mesh_resolution) + mesh_res_range];

                gDriver.pushTexture(img);

                Graphics.GLHeightmap test_mesh = new Graphics.GLHeightmap(10);

                test_mesh.bindElementsForUse();

                test_mesh.uploadVertices(mesh.vertices);

                test_mesh.uploadElements(mesh.indices);

                test_mesh.configureVertexArray();

                // Scale this transform and move it down by the zoom level

                int zoom_offset = this.zoom - zoom;
                int zoom_scale2 = (int) Math.pow(2, 16 - zoom);

                Transform transform = new Transform();



                transform.getPos().setX((((thread_offset.getX()  + (x)))));
                transform.getPos().setZ((((thread_offset.getY()  + (y)))));

                transform.getScale().setScalar(zoom_scale2*10);

                //transform.getScale().setY(((zoom_scale * 10 * 4) - (zoom_offset)) * 0.3);

                transform.getPos().setY(-0.2);

                GLTransform glTransform = new GLTransform(transform);

                gDriver.pushObject(glTransform);

                gDriver.pushObject(test_mesh);
            }
        }

        thread.isFinished = true;
    }

    /**
     * This method spawns a thread at a position with zoom
     * @param dDriver The data driver to pass to copy to the thread
     * @param location A vector based offset.
     * @param zoom The zoom to create a tile from
     */
    private void spawnMesh(DataDriver dDriver, WorldCoordinate location, Vector offset, int zoom){
        if (!coordinateThreads.containsKey(zoom)){
            coordinateThreads.put(zoom, new HashMap<>());
        }

        HashMap<Integer, HashMap<Integer, WorldGenerationThread>> zoom_group = coordinateThreads.get(zoom);

        if (!zoom_group.containsKey((int) offset.getX())){
            zoom_group.put((int) offset.getX(), new HashMap<>());
        }
        HashMap<Integer, WorldGenerationThread> horizontal_lines = zoom_group.get((int) offset.getX());

        WorldGenerationThread thread = new WorldGenerationThread(dDriver);

        Vector initial_pos = location.getTile();
        thread.setLocation(new WorldCoordinate((int) initial_pos.getX(), (int)initial_pos.getY(), zoom),
                new Vector(offset.getX(), offset.getY(), 0), zoom ,2);
        thread.start();

        horizontal_lines.put((int) offset.getY(), thread);
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


        // Iterate through each zoom level, determine the offset
        //for (int z = zoom; z >= (zoom - zoom_out); z--){
            int scale = (int) Math.pow(2, zoom / 2.0);

            int x_offset = (int) ((cam.getGLCamera().getTransform().getPos().getX() / scale));
            int y_offset = (int) ((cam.getGLCamera().getTransform().getPos().getZ() / scale));

            //System.out.printf("Z: %d X: %d Y: %d\n", z, x_offset, y_offset);

        WorldCoordinate initial_12 = new WorldCoordinate(initial.getWorldCoordinate().getX(),
                initial.getWorldCoordinate().getY(), 256, zoom);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int x_o = x_offset + x;
                int y_o = y_offset + y;
                if (!coordinateThreads.get(zoom).containsKey(x_o) ||
                                !coordinateThreads.get(zoom).get(x_o).containsKey(y_o)) {

                    WorldCoordinate initial_wc = new WorldCoordinate(
                            initial_12.getTile().getX() + x_o,
                            initial_12.getTile().getY() + y_o,
                            (double) zoom);


                    spawnMesh(context.getDataDriver(), initial_wc, new Vector(x_o, y_o, 0), zoom);

                }
            }
        }



        for (Map.Entry<Integer, HashMap<Integer, HashMap<Integer, WorldGenerationThread>>> zoom_layer : coordinateThreads.entrySet()) {
            for (HashMap<Integer, WorldGenerationThread> horizontal_rows : zoom_layer.getValue().values()) {
                for (WorldGenerationThread thread : horizontal_rows.values()) {
                    if (thread.isReady && !thread.isFinished) {
                        loadMesh(context.getGraphicsDriver(), thread, zoom_layer.getKey());
                    }
                }
            }
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
        private Image[] result_image;

        /**
         * Our resultant meshes, should only be used after the thread finishes
         */
        private HeightmapMesh[] result_meshes;

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
            result_image = new Image[9];
            result_meshes = new HeightmapMesh[9];
            result_elevation = new HashMap<>();
        }

        /**
         * Start reading the data from the Google API. Finished when data is read and interpreted
         * @throws IllegalStateException If location is null, zoom is zero, or elevation_res is <= 0
         * (likely due to the thread's parameters not being set)
         */
        @Override
        public void run() {
            WorldCoordinate[] tiles = generateAdjacentTiles();

            for (int i = 0; i < tiles.length; i++){
                // Append the tile's elevation to the list
                queryWorldElevation(tiles[i]);

                try{
                    // Append the coordinate's satellite images
                    result_image[i] = dataDriver.getSatalliteImage(tiles[i], zoom);
                } catch (ConfigurationException e) {
                    throw new IllegalStateException("Failed to load a tile's satellite image!");
                }
            }

            for (int i = 0; i < tiles.length; i++){
                // Append the tile's elevation to the list
                result_meshes[i] = new HeightmapMesh(10, tiles[i]);

                result_meshes[i].generateMesh(result_elevation.entrySet());
            }

            isReady = true;
        }

        /**
         * A private method to generate 2x2 adjacent locations from the center location
         * @return An array of four coordinates in the 4 tiles adjacent from the center
         */
        private WorldCoordinate[] generateAdjacentTiles(){
            WorldCoordinate[] offsets = new WorldCoordinate[9];
            Vector base_location = this.location.getTile().plus(offset);

            for (int y = 0; y < 3; y++){
                for (int x = 0; x < 3; x++){
                    offsets[(y*3)+(x)] = new WorldCoordinate( base_location.getX() - (x-1),
                            base_location.getY() + (y-1), zoom);
                }
            }

            return offsets;
        }

        /**
         * A private method to query then append to the elevation list for a specific world coordinate
         * @param cord The world coordinate to query
         * @throws InvalidParameterException If cord is null
         */
        private void queryWorldElevation(WorldCoordinate cord){
            if (cord == null){
                throw new InvalidParameterException("Provided coordinate for elevation is null!");
            }

            // Find the base tile location
            Vector base_loc = cord.getTile();
            WorldCoordinate up = new WorldCoordinate(base_loc.getX(), base_loc.getY() + 1, zoom);
            WorldCoordinate down = new WorldCoordinate(base_loc.getX(), base_loc.getY() - 1, zoom);
            WorldCoordinate left = new WorldCoordinate(base_loc.getX() - 1, base_loc.getY(), zoom);
            WorldCoordinate right = new WorldCoordinate(base_loc.getX() + 1, base_loc.getY(), zoom);

            double max_long = up.getWorldCoordinate().getY();
            double min_long = down.getWorldCoordinate().getY();
            double max_lat = right.getWorldCoordinate().getX();
            double min_lat = left.getWorldCoordinate().getX();

            double base_lat = cord.getWorldCoordinate().getX();
            double base_long = cord.getWorldCoordinate().getY();

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

            result_elevation.putAll(dataDriver.getElevationData(cords));
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
         * 0 = bottom left
         * 1 = bottom right
         * 2 = top left
         * 3 = top right
         * @return An Image from the satellite API
         */
        public Image[] getImages(){
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

        /**
         * A class holding a heightmap mesh
         */
        private class HeightmapMesh{
            /**
             * Our buffer of vertices
             */
            private float[] vertices;

            /**
             * Our indices for our heightmaps
             */
            private int[] indices;

            /**
             * The resolution of the height map
             */
            private int resolution;

            /**
             * The base world coordinate for this mesh
             */
            private WorldCoordinate base;

            /**
             * Constructor to initialize our heightmap with a specific resolution
             * @param res The resolution of the heightmap, determines the length of vertices and indices
             * @param location The base location of this mesh
             */
            public HeightmapMesh(int res, WorldCoordinate location){
                this.resolution = res;
                this.base = location;
                this.indices = new int[(res - 1) * res * 2];
                this.vertices = new float[res * res * 5];
            }

            /**
             * Generate this mesh using a list of elevations, derived from the hash map of elevations for all adjacent
             * tiles
             * @param elevations A Set of WorldCoordinates and Elevations
             */
            public void generateMesh(Set<Map.Entry<WorldCoordinate, Float>> elevations){
                initializeElements();

                double elevation_min = 0.0;
                double elevation_max = 0.0;

                if (!elevations.stream().findFirst().isEmpty()) {
                    elevation_min = elevations.stream().findFirst().get().getValue();
                    elevation_max = elevation_min;

                    for (Map.Entry<WorldCoordinate, Float> cord : elevations) {
                        elevation_min = Math.min(elevation_min, cord.getValue());
                        elevation_max = Math.max(elevation_max, cord.getValue());
                    }
                }

                int index = 0;
                for (int i = 0; i < resolution; i++) {
                    for (int j = 0; j < resolution; j++) {
                        vertices[index] = (float) (-resolution / 2.0 + i) / (float) (resolution -1);

                        double result_y = 0.0;

                        // Radius of our own location
                        Vector location_bounds = base.findBounds();
                        Vector world_location = base.getWorldCoordinate();

                        double constant_factor = 0.1;
                        double dist_factor = 100.0;

                        // First, find the position of this tile, relative to itself (i.e., 0,0 is the center)

                        double radius_x = (((-resolution / 2.0 + i) / resolution) - 0.5) * 2.0;
                        double radius_y = (((-resolution / 2.0 + j) / resolution) - 0.5) * 2.0;

                        double lat_offset = radius_x * Math.abs(location_bounds.getX());
                        double lng_offset = radius_y * Math.abs(location_bounds.getY());

                        double latitude = world_location.getX() + (lat_offset);
                        double longitude = world_location.getY() + (lng_offset);


                        // Construct the latitude and longitude
                        Vector latlng = new Vector(latitude,
                                longitude, 0);

                        for (Map.Entry<WorldCoordinate, Float> cord : elevations) {
                            double dist = latlng.getDistance(cord.getKey().getWorldCoordinate());

                            double final_dist = constant_factor *
                                    (Math.abs((elevation_max - cord.getValue()) / (elevation_max - elevation_min))
                                            * (1 / Math.max(1.0, Math.pow(dist_factor * dist,2))));

                            result_y += final_dist;
                        }

                        vertices[index+1] = (float) (result_y / (double) elevation_res);
                        vertices[index+2] = (float) (-resolution / 2.0 + j) / (float) (resolution -1);

                        vertices[index+3] = (float) (-((float) (-resolution / 2.0 + i)
                                + (resolution / 2.0)) / (float) (resolution -1));
                        vertices[index+4] = (float) -((float) (-resolution / 2.0 + j)
                                + (resolution / 2.0)) / (float) (resolution -1);

                        index += 5;
                    }
                }
            }

            /**
             * Create the list of elements for this heightmap
             */
            private void initializeElements(){
                for (int i = 0; i < resolution - 1; i++) {
                    for (int j = 0; j < resolution; j++) {
                        for (int k = 0; k < 2; k++) {
                            indices[(i * resolution * 2) + (j * 2) + k] = (j + resolution * (i + k));
                        }
                    }
                }
            }
        }
    }


}
