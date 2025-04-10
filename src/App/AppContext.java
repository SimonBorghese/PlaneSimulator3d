package App;

import Data.DataDriver;
import Graphics.GraphicsDriver;

import java.util.List;

/**
 * This class contains all the drivers for each element of the program which runs within app (i.e. data and graphics
 * drivers).
 */
public class AppContext {
    /**
     * The graphics driver in use by this context
     */
    private Graphics.GraphicsDriver graphicsDriver;

    /**
     * The data driver in use by this context
     */
    private Data.DataDriver dataDriver;

    /**
     * A list of every process
     */
    private List<AppProcess> appProcesses;


    /**
     * A constructor to create this context with the provided, existing drivers.
     * @param graphicsDriver The graphics driver to use for this context, must not be null
     * @param dataDriver The data driver to use for this context, must not be null
     * @throws java.security.InvalidParameterException If either parameter is null
     */
    public AppContext(Graphics.GraphicsDriver graphicsDriver, Data.DataDriver dataDriver, List<AppProcess> appProcesses) {
        if (graphicsDriver == null || dataDriver == null || appProcesses == null) {
            throw new IllegalArgumentException("Neither drivers or the app process list can be null!");
        }
        this.graphicsDriver = graphicsDriver;
        this.dataDriver = dataDriver;
        this.appProcesses = appProcesses;
    }

    /**
     * Get the current data driver
     * @return The data driver referred to by this context.
     */
    public DataDriver getDataDriver() {
        return dataDriver;
    }

    /**
     * Get the current graphics driver
     * @return The graphics driver referred to by this context.
     */
    public GraphicsDriver getGraphicsDriver() {
        return graphicsDriver;
    }

    /**
     * Get the current process list
     * @return The process list provided by the AppDriver
     */
    public List<AppProcess> getAppProcesses(){
        return appProcesses;
    }

    /**
     * Checks to see if the context's process list has a specified class
     * @return The desired class provided, null if it doesn't exist
     */
    public AppProcess findAppProcess(Class<? extends AppProcess> processClass) {
        AppProcess foundProcess = null;
        for (AppProcess appProcess : appProcesses) {
            if (foundProcess != null){
                continue;
            }
            if (appProcess.getClass().equals(processClass)) {
                foundProcess = appProcess;
            }
        }

        return foundProcess;
    }

}
