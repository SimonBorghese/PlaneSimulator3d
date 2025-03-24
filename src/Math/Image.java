/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Math;


import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This class provides an object for holding image data such as width, height, and number of channels. The image may
 * or may not contain the image data itself.
 */
public class Image {
    /**
     * The Image's width and height, in pixels
     */
    private int width;
    private int height;

    /**
     * The number of channels in this image.
     * Within this app, this should usually be 3, but it may differ and can work as a sanity check.
     */
    private int channels;

    /**
     * The number of bytes per pixel
     * If an image is represented as unsigned bytes, as most should be, this should be equal to the number of channels
     */
    private int bpp;

    /**
     * An optional container for the image data itself, is stored as bytes.
     * This is stored on the heap as otherwise it can't be used by external C libraries
     */
    private ArrayList<Byte> pixels;

    /**
     * Construct this image with some provided parameters
     * They will not be checked for sanity.
     * @param width Image width, in pixels
     * @param height Image height, in pixels
     * @param channels Image channels
     * @param bpp Number of bytes per pixel
     */
    public Image(int width, int height, int channels, int bpp) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.bpp = bpp;
    }

    /**
     * Construct this image with all parameters provided
     * They will not be checked for sanity
     * @param width Image width, in pixels
     * @param height Image height, in pixels
     * @param channels Image channels
     * @param bpp Number of bytes per pixel
     * @param data The raw image data
     */
    public Image(int width, int height, int channels, int bpp, byte[] data) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.bpp = bpp;
        if (data != null) {
            this.pixels = new ArrayList<>(data.length);
            for (byte b : data) {
                this.pixels.add(b);
            }
        } else{
            this.pixels = null;
        }
    }

    /**
     * Construct this image with all parameters provided
     * They will not be checked for sanity
     * @param width Image width, in pixels
     * @param height Image height, in pixels
     * @param channels Image channels
     * @param bpp Number of bytes per pixel
     * @param data The raw image data
     */
    public Image(int width, int height, int channels, int bpp, ArrayList<Byte> data) {
        /**
         * TODO!
         */
    }

    /**
     * Construct an empty Image with some invalid but identifiable as invalid parameters
     */
    public Image(){
        this(0,0,0,0);
    }


    /**
     * Get the width of the image, in pixels
     * @return Width in pixels, may be insane
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the image, in pixels
     * @return Height in pixels, may be insane
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the number of color channels in this image
     * @return Number of color channels, may be insane
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Get the number of bytes per pixel of this image
     * @return The number of bytes per pixel of this image, may be insane
     */
    public int getBpp() {
        return bpp;
    }

    /**
     * Get image data size.
     * @return The number of bytes needed to occupy this image, calculated as width * height * bpp * channels
     */
    public int getDataSize(){
        return width*height*bpp*channels;
    }

    /**
     * Get the resolution of the image
     * @return The number of pixels in this image
     */
    public int getResolution(){
        return width * height;
    }


}
