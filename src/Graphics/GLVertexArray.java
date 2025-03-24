/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Graphics;

import org.lwjgl.opengl.GL33;

/**
 * A class wrapping around Vertex Arrays and provides an interface to use them
 */
public class GLVertexArray {
    /**
     * The VAO handle, this handle holds the vertex array buffer and element buffer
     */
    private int vao;

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
        vao = -1;
        vbo = -1;
        ebo = -1;
    }

    /**
     * Generate Vertex Array Object then configure the array
     * The current format is:
     * vec3 - aPos
     * vec2 - aTex
     */
    public void generateVertexArray(){
        // Generate the Vertex Array
        vao = GL33.glGenVertexArrays();
    }

    /**
     * Configure the VAO, generateVertexArray() must be called first
     * and preferably after uploadVertices and uploadElements
     */
    public void configureVertexArray(){
        useMesh();
        GL33.glBindBuffer(GL33.GL_VERTEX_ARRAY, vbo);
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, ebo);

        // Configure the vertex pointers
        GL33.glVertexAttribPointer(0, 3 * 4, GL33.GL_FLOAT, false, 5*4, 0L);
        GL33.glVertexAttribPointer(1, 2 * 4, GL33.GL_FLOAT, false, 5*4, (3*4));

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
        // Generate the Vertex Buffer
        vbo = GL33.glGenBuffers();

        // Bind the buffer then
        GL33.glBindBuffer(GL33.GL_VERTEX_ARRAY, vbo);
        GL33.glBufferData(GL33.GL_VERTEX_ARRAY, vertex_raw, GL33.GL_STATIC_DRAW);
    }

    /**
     * Upload element buffer, this will generate the EBO and then upload to it
     * generateVertexArray() must be called before this
     * @param elements An array of raw bytes for these vertices, must be 5x floats per vertex
     */
    public void uploadElements(int[] elements){

        // Generate the Vertex Buffer
        ebo = GL33.glGenBuffers();

        // Bind the buffer then
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, elements, GL33.GL_STATIC_DRAW);
    }

    /**
     * Bind the VAO so it can be used
     */
    public void useMesh(){
        GL33.glBindVertexArray(vao);
    }
}
