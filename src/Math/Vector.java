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

    /**
     * Return the sum of this vector and another vector
     * @param vector The other vector to add to
     * @return The sum of this vector and the other vector
     * @throws NullPointerException If the provided vector is null
     */
    public Vector plus(Vector vector){
        if (vector == null){
            throw new NullPointerException("The provided vector for addition is null!");
        }

        return new Vector(x + vector.getX(), y + vector.getY(), z + vector.getZ());
    }

    /**
     * Return the multiplication of this vector and a scalar
     * @param scalar The scalar to multiply by
     * @return The sum of this vector and the other vector
     */
    public Vector mul(float scalar){
        return new Vector(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Return the cross product of this vector and another vector
     * @param vector The other vector to add to
     * @return The sum of this vector and the other vector
     * @throws NullPointerException If the provided vector is null
     */
    public Vector cross(Vector vector){
        if (vector == null){
            throw new NullPointerException("The provided vector for cross product is null!");
        }

        // Yes I am offloading the work to the glm library
        glm_.vec3.Vec3 cross = toGLM().cross(vector.toGLM());

        return new Vector(cross.getX(), cross.getY(), cross.getZ());
    }

    /**
     * Get the length of this vector
     * @return The length of this vector
     */
    public double getLength(){
        return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
    }
}
