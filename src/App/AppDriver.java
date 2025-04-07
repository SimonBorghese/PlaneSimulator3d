package App;

import java.util.ArrayList;

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
     * An arraylist for all the processes within this app
     */
    private ArrayList<AppProcess> appProcesses;
}
