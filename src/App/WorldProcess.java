package App;

import Data.DataDriver;
import Data.WorldCoordinate;

/**
 * This process manages the loading of satellite data based on the camera position. Uniquely, this process will
 * spawn threads from an implemented Runner child class.
 */
public class WorldProcess {
    /**
     * This is the initial coordinate of the scene, everything is relative to this point
     */
    private WorldCoordinate initial;

    /**
     * A class which implements threading for loading in world data
     */
    private class WorldGenerationThread implements Runnable{

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
         * Start reading the data from the Google API. Finished when data is read and interpreted
         */
        @Override
        public void run() {

        }
    }
}
