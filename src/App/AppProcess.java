package App;

import java.security.InvalidParameterException;

/**
 * This interface represents a process that the app driver will execute each frame. Each process has access to the
 * Data and Graphics Drivers and has an object that should persist across frames. All processes should act
 * independently of each other but within the same Context. Not necessarily threaded. The purpose is more so
 * to divide up each task of initialization and updating to its own class/object.
 */
public interface AppProcess {

    /**
     * This method should be called on creation to allow for creation of subobjects.
     * @param context The current app context, should be initialized before this
     * @throws InvalidParameterException If the context is null
     */
    void init(AppContext context) throws InvalidParameterException;

    /**
     * This method is called every frame on each app process.
     * @param dt The time, in seconds, since the last call (Delta Time)
     * @param context The current app context this process is running in
     * @throws java.security.InvalidParameterException If context is null and/or if dt is negative
     */
    void frame(double dt, AppContext context) throws InvalidParameterException;
}
