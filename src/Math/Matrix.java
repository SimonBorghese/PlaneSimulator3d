/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/26/2025
 */

package Math;

import java.security.InvalidParameterException;
import java.util.Arrays;

/**
 * This class provides an object for a 4x4 floating point matrix for OpenGL 3d graphics calculations
 * It MUST be stored as a linear structure to remain compatible with OpenGL
 * I'm basically copying C++'s GLM library that i WISH i was using
 * Also I probably am not using SIMD so this is going to be SLOWWWW.....
 */
public class Matrix {
    /**
     * A linear structure to represent the matrix, should be 4*4 in size (but not 4x4 in 2d)
     */
    private float[] matrix;

    /**
     * Construct a matrix with following a matrix identity
     */
    public Matrix(){
        this(1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Construct a matrix with provided parameters
     */
    public Matrix(float a1, float a2, float a3, float a4,
                  float b1, float b2, float b3, float b4,
                  float c1, float c2, float c3, float c4,
                  float d1, float d2, float d3, float d4){
        matrix = new float[]{
                a1, a2, a3, a4,
                b1, b2, b3, b4,
                c1, c2, c3, c4,
                d1, d2, d3, d4
        };
    }

    /**
     * Get a specific value in the matrix
     * @param row Selected Row
     * @param column Selected Column
     * @return The item at the position
     * @throws java.security.InvalidParameterException If the row or column is < 0 or > 3
     */
    public float getItem(int row, int column){
        if (row < 0 || column < 0 || row > 3 || column > 3){
            throw new InvalidParameterException("Either row or column at invalid position");
        }
        return matrix[(row * 4) + column];
    }

    /**
     * Construct a matrix that is a perspective matrix
     * BASED ON THE GLM CODE BASE:
     *      glm/glm/ext/matrix_clip_space.inl (perspectiveRH_ZO)
     * https://github.com/g-truc/glm
     * No exceptions are thrown because for all valid floating point numbers besides NAN, there is some mathematical
     * meaning.
     * @param fov The desired FOV of this projection
     * @param apsect The aspect ratio of the window or view
     * @param near The znear element
     * @param far The cuttof distance for this matrix
     */
    public Matrix(float fov, float apsect, float near, float far){
        // Construct an identity matrix
        this();

        // These calculations come from the GLM function listed above
        // It's all simple linear algebra, I don't understand it at all
        float tanHalfFov = (float) Math.tan(fov / 2.0f);

        matrix[0] = 1.0f / (apsect * tanHalfFov);
        matrix[5] = 1.0f / tanHalfFov;
        matrix[10] = far / (near - far);
        matrix[11] = -1.0f;
        matrix[14] = -(far * near) / (far - near);
    }

    /**
     * Get the row of the matrix
     * @param row Selected row
     * @return The row of the matrix
     * @throws InvalidParameterException If the row isn't in a valid range (0 <= row < 4)
     */
    public float[] getRow(int row){
        if (row < 0 || row > 3){
            throw new InvalidParameterException("Row at invalid position");
        }
        return Arrays.copyOfRange(matrix, (row * 4), (row * 4) + 4);
    }

    /**
     * Get the raw matrix array
     * @return Matrix array AS A COPY
     */
    public float[] getRawMatrix(){
        return matrix.clone();
    }


}
