/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/21/2025
 */

package Graphics;

import Utils.GraphicsStack;
import org.lwjgl.opengl.GL33;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
     * The window for the use with this driver
     */
    private Window window;

    /**
     * The graphics stack which hold our objects. Here is how this stack shal be used:
     * Each OpenGL object has a sort of "use" function and within this stack we store the order to use objects
     * At the bottom of the stack we store shaders as they need to be activated before doing anything
     * Next, we store groups of textures and meshes (otherwise known as vertex arrays). After binding a shader
     * the loop will mostly look like:
     * Activate Texture
     * Bind texture to currently bound shader
     * Bind Vertex Array
     * Draw Vertex Array
     * The various objects will be aware of the other objects bound via the GraphicsContext object sent to
     * every use function.
     */
    private GraphicsStack stack;

    /**
     * The primary shader for rendering textured objects
     */
    private GLShader main_shader;

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

        window = new Window(width, height, opengl_major, opengl_minor);

        // Create a stack for our objects
        stack = new GraphicsStack();
    }

    /**
     * Initialize the graphics driver, this calls to several private methods to initialize the window, shaders,
     * and any other subsystems that need to be created
     */
    public void init(){
        // Initialize the window before doing anything
        window.init();

        // Create empty objects for our needed fundamental objects
        main_shader = new GLShader();

        // Create them
        try {
            main_shader.createProgram(Files.readString(Paths.get("shaders/vertex.glsl")),
                    Files.readString(Paths.get("shaders/fragment.glsl")));
        } catch (IOException e) {
            throw new RuntimeException("Shaders not found, working directory likely incorrect!");
        }

        // And then push it to the graphics stack
        stack.push(main_shader);
    }

    /**
     * This method handles rendering in the graphics driver
     */
    private void render(){
        GL33.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Loop the window, return whether this should continue
     * @return True if the driver should continue the loop
     */
    public boolean loop(){
        // Render out the scene
        render();

        return window.loop();
    }

    /**
     * Destroy any remaining graphics objects, then the window
     * This makes this object completely invalid after this
     */
    public void destroy(){
        while (stack.hasElements()){
            stack.pop();
        }

        window.destroy();
    }
}
