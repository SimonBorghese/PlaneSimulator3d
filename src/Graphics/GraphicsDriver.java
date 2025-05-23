/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/21/2025
 */

package Graphics;

import Utils.GraphicsStack;
import Utils.Stack.GraphicsNode;
import org.lwjgl.opengl.GL33;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import Math.Image;
import org.lwjgl.system.MemoryUtil;

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
     * Add a GLCamera to the stack. This is restricted to GLCamera as external objects
     * shouldn't be adding anything else to the stack.
     * @param camera The GLCamera to add to the stack
     * @throws NullPointerException If the provided camera is null
     */
    public void addCamera(GLCamera camera){
        if (camera == null){
            throw new NullPointerException("Provided Camera to stack is null!");
        }

        stack.push(camera);
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

        // Enable the depth buffer
        GL33.glEnable(GL33.GL_DEPTH_TEST);
    }

    /**
     * This method handles rendering in the graphics driver
     */
    private void render(){
        // Clear the color buffer to black
        GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Clear both the depth and color buffers
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

        // Create a graphics context to then populate while going up the stack
        // We create a new context every time we go through the stack
        GraphicsContext ctx = new GraphicsContext();
        // Go up the stack and use all objects
        // It's important to follow the order of the stack such that dependencies are met
        GraphicsNode node = stack.getRoot();
        while (node != null){
            if (node.hasElement()) {
                node.getElement().use(ctx);
            }
            node = node.next();
        }
    }

    /**
     * Adds a texture to the stack, the programmer provides the raw image data which is then decoded and turned
     * into a GLTexture which is then pushed to the stack. The programmer has no context of how the texture
     * actually works, all the programmer must take care of is ensuring correct order of pushing data
     * @param data The PNG or JPG of the image we're loading from
     * @throws java.security.InvalidParameterException If the image provided fails to decode
     */
    public void pushTexture(Image data){
        GLTexture texture = new GLTexture();

        ByteBuffer img_data = MemoryUtil.memAlloc(data.getDataSize());

        // Copy the data to the buffer
        for (Byte b : data.getData()) {
            img_data.put(b);
        }

        img_data.flip();

        texture.uploadTexture(img_data, data.getWidth(), data.getHeight());


        // Push our texture to the stack
        stack.push(texture);
    }

    /**
     * Temporary method, delete later
     */
    @Deprecated
    public void pushObject(GLObject obj){
        stack.push(obj);
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
            stack.pop().getElement().destroy();
        }

        window.destroy();
    }

    /**
     * Get the set Window Width
     * @return The Window Width
     */
    public int getWindowWidth(){
        return window_width;
    }

    /**
     * Get the set Window Height
     * @return The Window Height
     */
    public int getWindowHeight(){
        return window_height;
    }

    /**
     * Return a pointer to the Window object
     * @return The pointer to the window object
     */
    public Window getWindow(){
        return window;
    }
}
