/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/21/2025
 */

package Graphics;

import java.security.InvalidParameterException;
import org.lwjgl.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

/**
 * The primary driver for the GLFW window where the 3d is being rendered. This doesn't mess with the swing UI as they
 * need to be separate since swing doesn't properly integrate.
 * This class is responsible for all window related management such as keystrokes and OpenGL context management
 */
public class Window implements GLFWKeyCallbackI {
    /**
     * The window sizes provided by the graphic driver class, this also may end up being the viewport size of an
     * invisible window if we end up integrating with swing
     */
    private int width;
    private int height;

    /**
     * OpenGL version information. This class needs this because GLFW makes the OpenGL context for us
     */
    private int ogl_major;
    private int ogl_minor;

    /**
     * The handle for the GLFW window
     */
    private long window;

    /**
     * The glfw key callback which must be destroyed when done
     */
    private GLFWKeyCallbackI key_callback_context;

    /**
     * The constructor for the window, sets parameters and nothing else.
     * @param window_width Window/Viewport width, must be < 1 but realistically should be a reasonable size but also
     *                     less than the resolution of the user's monitor
     * @param window_height Window/Viewport height must be < 1 but realistically should be a reasonable size but also
     *                      less than the resolution of the user's monitor
     * @param major_version The requested OpenGL Major version, will not check for support by the graphics system or
     *                      GPU but must be between 1-4
     * @param minor_version The requested OpenGL Minor version, will not check for support by the graphics system or
     *                      GPU but must be between 0-6 (assuming the major version has that revision)
     *
     */
    public Window(int window_width, int window_height, int major_version, int minor_version) throws InvalidParameterException {
        // Check all values for sane values
        if (window_width <= 0 || window_height <= 0){
            throw new InvalidParameterException("Either width or height is less than 1");
        }
        if (major_version < 1 || major_version > 4){
            throw new InvalidParameterException("Major version too old or new");
        }
        if (minor_version < 0 || minor_version > 6){
            throw new InvalidParameterException("Minor version invalid or too high for any revision");
        }

        this.width = window_width;
        this.height = window_height;
        this.ogl_major = major_version;
        this.ogl_minor = minor_version;
    }

    /**
     * This method properly constructs the GLFW window we can render to
     */
    public void init() throws IllegalStateException{
        boolean glfw_init = GLFW.glfwInit();
        if (!glfw_init){
            throw new IllegalStateException("Failed to initialize GLFW, probably because you're running from the command line with no GUI or something");
        }

        // Tell the GLFW API the OpenGL version we desire
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, ogl_major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, ogl_minor);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);

        // Create the GLFW window
        window = GLFW.glfwCreateWindow(width, height, "PlaneSimulator3d", 0L, 0L);
        if (window == 0L){
            throw new IllegalStateException("Failed to create window, perhaps an invalid parameter but likely no GUI is in use");
        }


        key_callback_context = GLFW.glfwSetKeyCallback(window, this);

        // Set up the OpenGL context for the window
        GLFW.glfwMakeContextCurrent(window);
    }

    /**
     * This method destroys all of the unsafe objects in this class
     */
    public void destroy(){
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    /**
     * This method should be called every frame to update the window surface and poll events
     * @return True if app should continue, false otherwise
     */
    public boolean loop(){
        // Ensure our OpenGL context is current incase swing steals it
        GLCapabilities caps = GL.createCapabilities();

        // Poll the events which may call for the window to be closed
        GLFW.glfwPollEvents();

        // Update the window with the latest render
        GLFW.glfwSwapBuffers(window);

        return !GLFW.glfwWindowShouldClose(window);
    }

    /**
     * Get the window width
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the window height
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Implementation of the GLFW key callback. Used to set the player's movement state and detect window closing
     * @param window   NOT USED
     * @param key      the keyboard key that was pressed or released
     * @param scancode NOT USED
     * @param action   the key action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
     * @param mods     NOT USED
     */
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        switch (key){
            case GLFW.GLFW_KEY_ESCAPE:
                GLFW.glfwSetWindowShouldClose(window, true);
                break;
            default:
                break;
        }
    }
}
