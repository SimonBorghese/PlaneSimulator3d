package App;

import Data.DataDriver;
import Data.WorldCoordinate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import Math.Image;
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
     * Construct this WorldProcess with a provided base location
     * @param initial The initial world coordinate we start from
     * @throws InvalidParameterException If initial is null
     */
    public WorldProcess(WorldCoordinate initial) {
        if (initial == null){
            throw new InvalidParameterException("Initial world coordinate for a WorldProcess must not be null");
        }
        this.initial = initial;
    }

    /**
     * Empty as there is nothing to initialize
     * @param context Unused
     * @throws InvalidParameterException Never
     */
    @Override
    public void init(AppContext context) throws InvalidParameterException {}

    /**
     * Read the camera's position and load in new tiles as needed
     * @param dt The time, in seconds, since the last call (Delta Time)
     * @param context The current app context this process is running in
     * @throws InvalidParameterException
     */
    @Override
    public void frame(double dt, AppContext context) throws InvalidParameterException {
        AppCamera cam = (AppCamera) context.findAppProcess((Class<AppProcess>) AppCamera.class);
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
         * Construct this process from a provided data driver (this'll generate a derivative data driver and not
         * copy it)
         * @param dataDriver The data driver to derive from
         */
        public WorldGenerationThread(DataDriver dataDriver) {
            super();
            this.dataDriver = new DataDriver(dataDriver);
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

            Vector base_loc = location.toPoint(256, zoom);
            WorldCoordinate up = new WorldCoordinate(base_loc.getX(), base_loc.getY() + 1, zoom);
            WorldCoordinate down = new WorldCoordinate(base_loc.getX(), base_loc.getY() - 1, zoom);
            WorldCoordinate left = new WorldCoordinate(base_loc.getX() - 1, base_loc.getY(), zoom);
            WorldCoordinate right = new WorldCoordinate(base_loc.getX() + 1, base_loc.getY(), zoom);

            double max_long = up.getLongitude();
            double min_long = down.getLongitude();
            double max_lat = right.getLatitude();
            double min_lat = left.getLatitude();

            double base_lat = location.getLatitude();
            double base_long = location.getLongitude();

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

            result_elevation = dataDriver.getElevationData(cords);
        }

        /**
         * Sets this thread's coordinate and zoom
         * @param location A WorldCoordinate which represents our location
         * @param zoom A zoom for our satellite data
         * @param elevation_res The resolution for our elevation data, should be  < 3 but not enforced
         * @throws java.security.InvalidParameterException If zoom is < 1 or if location is null or if elevation_res is <= 0
         */
        public void setLocation(WorldCoordinate location, int zoom, int elevation_res) {
            if (zoom < 1 || location == null) {
                throw new InvalidParameterException("Zoom must be greater than zero and location cannot be null");
            }
            this.location = location;
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


    }
}
