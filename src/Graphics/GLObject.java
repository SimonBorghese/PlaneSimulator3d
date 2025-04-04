/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/28/2025
 */

package Graphics;

/**
 * An abstract class for implementing OpenGL objects. It's intended to provide an interface for destroying OpenGL
 * objects since Java doesn't do it for us with the API I'm using
 */
public abstract class GLObject {
    /**
     * All OpenGL objects were representing usually have a primary handle, there may be more but there's always one
     */
    int handle;

    /**
     * Destroy this OpenGL object, this method WILL result in the object being inoperable after executing
     */
    public abstract void destroy();

    /**
     * "Use" this OpenGL object, this method must either prepare or execute rendering
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     */
    public abstract void use(GraphicsContext context);
}
