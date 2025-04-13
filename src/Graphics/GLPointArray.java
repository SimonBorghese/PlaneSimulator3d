package Graphics;

/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.InvalidParameterException;

/**
 * A special type of vertex array where there is no elements and it is drawn as points
 */
public class GLPointArray extends GLObject{
    /*
     * The handle int should represent the VAO object
     */

    /**
     * The VBO handle, this handle holds the vertex buffer, the raw vertices sent to the GPU
     */
    private int vbo;

    /**
     * The number of points in the vertex buffer
     */
    private int num_vertices;

    /**
     * The constructor shouldn't do anything because we don't know EXACTLY when it executes
     */
    public GLPointArray(){
        handle = GL33.glGenVertexArrays();
        vbo = GL33.glGenBuffers();
    }

    /**
     * Configure the VAO, generateVertexArray() must be called first
     * and preferably after uploadVertices and uploadElements
     */
    public void configureVertexArray(){
        // Configure the vertex pointers
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 3 * 4,  0L);

        // Enable said vertex pointers
        GL33.glEnableVertexAttribArray(0);
    }

    /**
     * Upload vertex buffer, this will generate the VBO and then upload to it
     * generateVertexArray() must be called before this
     * @param vertex_raw An array of raw bytes for these vertices, must be 5x floats per vertex
     */
    public void uploadVertices(float[] vertex_raw){
        // Convert the float array to a float array
        FloatBuffer fb = MemoryUtil.memAllocFloat(vertex_raw.length);

        // each vertex has 3 floats
        num_vertices = vertex_raw.length / 3;

        fb.put(vertex_raw).flip();

        // Bind the buffer then
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
    }

    /**
     * Bind the VAO so it can be used for modifications
     */
    public void bindElementsForUse(){
        GL33.glBindVertexArray(handle);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);
    }

    /**
     * Bind the VAO so it can be used
     */
    public void useMesh(){
        GL33.glBindVertexArray(handle);
    }

    /**
     * Destroy the Vertex Array object
     */
    @Override
    public void destroy() {
        GL33.glDeleteVertexArrays(handle);
    }

    /**
     * This method will bind the vertex array and draw all the elements within it. This doesn't directly
     * depend on the context however a shader and texture should still be set.
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws java.security.InvalidParameterException If the context shows no shader
     */
    @Override
    public void use(GraphicsContext context) {
        if (!context.hasShader()){
            throw new InvalidParameterException("A meshes attempted render without a bound shader!");
        }

        useMesh();

        GL33.glDrawArrays(GL33.GL_POINTS, 0, num_vertices);
    }
}
