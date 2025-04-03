/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * A class wrapping around Vertex Arrays and provides an interface to use them
 */
public class GLVertexArray extends GLObject{
    /*
     * The handle int should represent the VAO object
     */

    /**
     * The VBO handle, this handle holds the vertex buffer, the raw vertices sent to the GPU
     */
    private int vbo;

    /**
     * The EBO handle, this handle holds the element array, the array of indices for this array
     */
    private int ebo;

    /**
     * The constructor shouldn't do anything because we don't know EXACTLY when it executes
     */
    public GLVertexArray(){
        handle = GL33.glGenVertexArrays();
        vbo = GL33.glGenBuffers();
        ebo = GL33.glGenBuffers();
    }

    /**
     * Configure the VAO, generateVertexArray() must be called first
     * and preferably after uploadVertices and uploadElements
     */
    public void configureVertexArray(){
        // Configure the vertex pointers
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 5 * 4,  0L);
        GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 5 * 4, 0xC);

        // Enable said vertex pointers
        GL33.glEnableVertexAttribArray(0);
        GL33.glEnableVertexAttribArray(1);
    }

    /**
     * Upload vertex buffer, this will generate the VBO and then upload to it
     * generateVertexArray() must be called before this
     * @param vertex_raw An array of raw bytes for these vertices, must be 5x floats per vertex
     */
    public void uploadVertices(float[] vertex_raw){
        // Convert the float array to a float array
        FloatBuffer fb = MemoryUtil.memAllocFloat(vertex_raw.length);

        fb.put(vertex_raw).flip();

        // Bind the buffer then
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
    }

    /**
     * Upload element buffer, this will generate the EBO and then upload to it
     * generateVertexArray() must be called before this
     * @param elements An array of raw bytes for these vertices, must be 5x floats per vertex
     */
    public void uploadElements(int[] elements){
        // Convert the elemnts to an int array
        IntBuffer ib = MemoryUtil.memAllocInt(elements.length);

        ib.put(elements).flip();

        // Bind the buffer then
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);
    }

    /**
     * Bind the VAO so it can be used for modifications
     */
    public void bindElementsForUse(){
        GL33.glBindVertexArray(handle);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, ebo);
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
}
