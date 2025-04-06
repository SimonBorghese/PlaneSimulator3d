/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/25/2025
 */

package Graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.InvalidParameterException;

/**
 * This class provides an interface for creating, binding, and managing OpenGL Textures
 */
public class GLTexture extends GLObject{
    /*
     * The handle int should represent the texture object
     */

    /**
     * The constructor just creates the texture handle, uploading comes later
     */
    public GLTexture(){
        // Generate a texture handle
        handle = GL33.glGenTextures();
    }

    /**
     * Upload a texture to the GPU
     * After this method, there's no need to keep the data parameter consistent
     * @param data An array of bytes for the texture, must be width * height * 3 in length
     * @param width Width of the input texture
     * @param height Height of the input texture
     * @throws java.security.InvalidParameterException If the data parameter isn't equal to width * height * 3
     */
    public void uploadTexture(ByteBuffer data, int width, int height) throws InvalidParameterException {
        if (data.limit() < width * height * 3){
            System.out.println("Data length is too short, continuing would result in a segFault");
            throw new InvalidParameterException("Provided Texture data is less than width * height * 3");
        }

        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        // Bind the texture to the 2D unit
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, handle);

        GL33.glPixelStorei(GL33.GL_TEXTURE_2D, 1);

        // Upload the texture
        GL33.glTexImage2D(GL33.GL_TEXTURE_2D,
                0, GL33.GL_RGB, width, height, 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, data);

        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR_MIPMAP_LINEAR);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);


        GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
    }

    /**
     * Bind the texture to a specified texture unit
     * This will likely override from what's already in the unit
     * @param unit The texture unit to bind to, must be > 0
     * @throws InvalidParameterException If the unit is less than zero
     */
    public void bindToUnit(int unit){
        if (unit < 0){
            throw new InvalidParameterException("Provided Texture unit less than 0");
        }
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + unit);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, handle);
    }

    /**
     * Destroy the texture object
     */
    @Override
    public void destroy() {
        GL33.glDeleteTextures(handle);
    }

    /**
     * This method binds a texture to a texture unit then sets the sampler for the currently bound shader
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws InvalidParameterException If there is no shader bound right now
     */
    @Override
    public void use(GraphicsContext context) {
        // Bind our texture to unit 0
        bindToUnit(0);

        if (!context.hasShader()){
            throw new InvalidParameterException("Texture use() provided with a context without shader!");
        }

        // Get the location to bind to, we assume the shader has an iTex uniform
        int location = context.getShader().getUniformLocation("iTex");

        // Bind our unit to the shader
        context.getShader().setUniformInt(location, 0);

        // Set ourselves to the context
        context.setTexture(this);
    }
}
