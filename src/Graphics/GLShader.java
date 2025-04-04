/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Graphics;

import org.lwjgl.opengl.GL33;

import java.security.InvalidParameterException;

/**
 * This class provides an interface for compiling OpenGL shaders to programs and then using them
 */
public class GLShader extends GLObject{
    /*
     * The shader program, a handle to the OpenGL program created for this object
     * This should be represented as the handle object
     */

    /**
     * The GLShader constructor shouldn't do anything, it's bad practice since we don't know EXACTLY when its executed
     */
    public GLShader(){
        // -1 should be considered invalid in our application
        handle = -1;
    }

    /**
     * Create a shader program from a provided vertex and fragment shader
     * Here's hoping we won't need any other shader stages
     * @param vertex Raw ASCII of the vertex shader. Should NOT BE IN JAVA UTF-16!!!!!11
     * @param fragment Raw ASCII of the vertex shader. Should NOT BE IN JAVA UTF-16!!!!!11
     * @throws InvalidParameterException If the shaders fail to compile or link
     */
    public void createProgram(String vertex, String fragment) throws InvalidParameterException {
        // Create our shader objects
        int vertex_id = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
        int fragment_id = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);

        // Send the source code to the GPU driver
        GL33.glShaderSource(vertex_id, vertex);
        GL33.glShaderSource(fragment_id, fragment);

        // Compile the shader
        GL33.glCompileShader(vertex_id);
        GL33.glCompileShader(fragment_id);

        // Check the compile result
        int vertex_result = getShaderCompile(vertex_id);
        int fragment_result = getShaderCompile(fragment_id);

        if (vertex_result == GL33.GL_FALSE){
            throw throwShaderLog(vertex_id);
        }

        if (fragment_result == GL33.GL_FALSE){
            throw throwShaderLog(fragment_id);
        }

        // Create a GPU program and link our shaders to it
        int program = GL33.glCreateProgram();

        GL33.glAttachShader(program, vertex_id);
        GL33.glAttachShader(program, fragment_id);

        // Link it
        GL33.glLinkProgram(program);

        if (getProgramCompile(program) == GL33.GL_FALSE){
            throw throwProgramLog(program);
        }

        // Destroy the shader object
        GL33.glDeleteShader(vertex_id);
        GL33.glDeleteShader(fragment_id);

        handle = program;
    }

    /**
     * Activate the GPU program
     */
    public void useProgram(){
        GL33.glUseProgram(handle);
    }

    /**
     * Get the uniform location
     * @param name The shader uniform name
     * @return The shader uniform location
     */
    public int getUniformLocation(String name){
        return GL33.glGetUniformLocation(handle, name);
    }

    /**
     * Set an integer uniform
     * @param loc The uniform location
     * @param param The integer to set
     */
    public void setUniformInt(int loc, int param){
        GL33.glUniform1i(loc, param);
    }

    /**
     * Set a mat4x4 uniform
     * @param loc The uniform location
     * @param param The integer to set
     * @throws InvalidParameterException If the parameter isn't an array atleast 16 floats long
     */
    public void setMatrixUniform(int loc, float[] param){
        if (param.length < 16){
            throw new InvalidParameterException("Provided matrix is less than 4x4");
        }
        GL33.glUniformMatrix4fv(loc, false, param);
    }

    /**
     * Get the shader compile status
     * @param handle Shader Handle
     * @return The result from the GPU Driver
     */
    private int getShaderCompile(int handle){
        int[] result = new int[1];
        GL33.glGetShaderiv(handle, GL33.GL_COMPILE_STATUS, result);

        return result[0];
    }

    /**
     * Get the shader info log as a String
     * @param handle Shader Handle
     * @return The GPU log provided by the driver
     */
    private String getShaderLog(int handle){
        return GL33.glGetShaderInfoLog(handle);
    }

    /**
     * Get the program link status
     * @param handle The Program Handle
     * @return The result from the GPU driver
     */
    private int getProgramCompile(int handle){
        int[] result = new int[1];
        GL33.glGetProgramiv(handle, GL33.GL_LINK_STATUS, result);

        return result[0];
    }

    /**
     * Get the program link info log as a String
     * @param handle Program Handle
     * @return The GPU log provided by the driver
     */
    private String getProgramLog(int handle){
        return GL33.glGetProgramInfoLog(handle);
    }

    /**
     * Generate an exception with the shader info log
     * @param shader_id The shader handle
     * @return A instance of an exception with the shader log
     */
    private InvalidParameterException throwShaderLog(int shader_id){
        String log = getShaderLog(shader_id);
        System.out.println("Failed to compile shader!!!!");

        return new InvalidParameterException("Failed to compile shader: " + log);
    }

    /**
     * Generate an exception with the program info log
     * @param shader_id The program handle
     * @return An instance of an exception with the program link log
     */
    private InvalidParameterException throwProgramLog(int shader_id){
        String log = getProgramLog(shader_id);
        System.out.println("Failed to link GPU Program!!!!");

        return new InvalidParameterException("Failed to GPU Program: " + log);
    }


    /**
     * Destroy this shader object
     */
    @Override
    public void destroy() {
        GL33.glDeleteProgram(handle);
    }

    /**
     * Use this shader. We ignore context because any context is effectively destroyed when a new shader is bound.
     * This method binds the currently existing program
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     */
    @Override
    public void use(GraphicsContext context) {
        GL33.glUseProgram(handle);

        // Set ourselves in the context
        context.setShader(this);
    }
}
