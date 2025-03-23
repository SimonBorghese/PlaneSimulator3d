/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/21/2025
 */

package Graphics;

/**
 * This is the primary API for the graphics side, this should expose the functions for
 * creating 3d graphics and then integrating them with swing. We shouldn't touch any LWJGL libraries here but in other
 * classes to prevent the code from getting messy
 */
public class GraphicsDriver {
    // The window sizes
    private int window_width;
    private int window_height;

    // OpenGL version
    private int opengl_major;
    private int opengl_minor;

    /**
     * The constructor for the graphics driver. We only need one of these
     * This doesn't create any unsafe objects but rather sets parameters.
     * Although any OpenGL version newer than 3.3 is supported and may desire for maintenance fixes defined in newer
     * APIs but only core 3.3 features will be used.
     * @param width Viewport Width
     * @param height Viewport Height
     * @param opengl_major The OpenGL major version, the graphics implementation here only supports 3 or newer
     * @param opengl_minor The OpenGL minor version. the graphics implementation here only supports 3 for major version
     *                     3 and any version for major version 4
     */
    public GraphicsDriver(int width, int height, int opengl_major, int opengl_minor){
        this.window_width = width;
        this.window_height = height;

        this.opengl_major = opengl_major;
        this.opengl_minor = opengl_minor;
    }
}
