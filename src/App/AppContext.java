package App;

import Data.DataDriver;
import Graphics.GraphicsDriver;

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
     * A constructor to create this context with the provided, existing drivers.
     * @param graphicsDriver The graphics driver to use for this context, must not be null
     * @param dataDriver The data driver to use for this context, must not be null
     * @throws java.security.InvalidParameterException If either parameter is null
     */
    public AppContext(Graphics.GraphicsDriver graphicsDriver, Data.DataDriver dataDriver) {
        if (graphicsDriver == null || dataDriver == null) {
            throw new IllegalArgumentException("Neither driver can be null!");
        }
        this.graphicsDriver = graphicsDriver;
        this.dataDriver = dataDriver;
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

}
