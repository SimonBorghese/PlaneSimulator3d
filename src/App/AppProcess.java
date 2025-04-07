package App;

import java.security.InvalidParameterException;

/**
 * This interface represents a process that the app driver will execute each frame. Each process has access to the
 * Data and Graphics Drivers and has an object that should persist across frames
 */
public interface AppProcess {
    /**
     * This method is called every frame on each app process.
     * @param dt The time, in seconds, since the last call (Delta Time)
     * @param context The current app context this process is running in
     * @throws java.security.InvalidParameterException If context is null and/or if dt is negative
     */
    void frame(double dt, AppContext context) throws InvalidParameterException;
}
