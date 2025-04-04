/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/24/2025
 */

package Math;

import glm_.glm;

/**
 * A class representing a 3D Vector
 */
public class Vector {
    /**
     * Our X, Y, and Z coordinates
     */
    private double x;
    private double y;
    private double z;

    /**
     * Create a vector from provided 3d coordinates
     * @param x X cord
     * @param y Y cord
     * @param z Z cord
     */
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a vector from provided 2d coordinates
     * Z will be zero
     * @param x X cord
     * @param y Y cord
     */
    public Vector(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Create a vector from provided 1d coordinates
     * Y will be zero
     * Z will be zero
     * @param x X cord
     */
    public Vector(double x) {
        this(x, 0, 0);
    }

    /**
     * Create a vector of zero length
     * All values will be zero
     */
    public Vector(){
        this(0.0, 0.0, 0.0);
    }

    /**
     * Create a vector by copying another vector
     * @param v The other vector to copy
     */
    public Vector(Vector v){
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Get the X cord
     * @return X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Get the Y cord
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Get the Z cord
     * @return Z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Set the X parameter
     * @param x New X coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the Y parameter
     * @param y New Y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Set the Z parameter
     * @param z New Z coordinate
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Normalize this vector
     */
    public void normalize(){
        double length = Math.sqrt(x*x + y*y + z*z);

        x /= length;
        y /= length;
        z /= length;
    }

    /**
     * Convert this vector to a glm vector
     * @return A GLM vector that copied this vector
     */
    public glm_.vec3.Vec3 toGLM(){
        return new glm_.vec3.Vec3(x, y, z);
    }
}
