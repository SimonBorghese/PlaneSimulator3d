/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Graphics;


import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.*;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

/**
 * This class should expose some useful OpenGL functions for rendering. Most of the OpenGL functions can be abstracted
 * into methods that are much more friendly for the rest of the app.
 */
public class GLDriver {
    /**
     * Empty Constructor
     */
    public GLDriver() {

    }

    /**
     * Uploads a texture to the GPU and returns a handle to use it.
     * The texture MUST be in RGB
     * @param target The raw bytes for the image, in an OpenGL compatible format. Size MUST be width * height * 3
     * @param width The target image's width
     * @param height The target image's height
     * @return An OpenGL texture handle
     * @throws java.security.InvalidParameterException If the texture isn't large enough
     */
    public int uploadTexture(byte[] target, int width, int height) {
        if (target.length < width * height * 3){
            System.out.println("Provided texture is too small!");
            throw new InvalidParameterException("Provided texture is less than width * height * 3");
        }
        GL33.glActiveTexture(GL33.GL_TEXTURE0);
        int texture = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture);

        // Copy the target to a real buffer
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 3);

        for (int b = 0; b < width * height * 3; b++){
            buffer.put(b, target[b]);
        }

        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, width, height, 0, GL33.GL_RGB,
                GL33.GL_UNSIGNED_BYTE, buffer);

        GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
        GL33.glActiveTexture(0);
        return texture;
    }
}
