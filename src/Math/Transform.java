package Math;

import java.security.InvalidParameterException;

/**
 * A transform in 3d space. This represents a position in 3d space and eular rotations.
 */
public class Transform {
    /**
     * The position in 3d space
     */
    private Vector pos;

    /**
     * The eular rotations, X: Pitch, Y: Yaw, Z: Roll
     */
    private Vector rotation;

    /**
     * The amount to scale this transform
     */
    private Vector scale;

    /**
     * Construct this transform with provided transformation values
     * @param pos The 3d position of this transform
     * @param rotation The eulars rotation
     * @param scale The scale of this transform
     */
    public Transform(Vector pos, Vector rotation, Vector scale) {
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Construct this transform with provided transformation values
     * @param pos The 3d position of this transform
     * @param rotation The eulars rotation
     */
    public Transform(Vector pos, Vector rotation) {
        this(pos, rotation, new Vector(1,1,1));
    }

    /**
     * Construct an empty transform
     */
    public Transform(){
        this(new Vector(), new Vector());
    }

    /**
     * Get the transform position
     * @return The currently set transform position, as a pointer
     */
    public Vector getPos() {
        return pos;
    }

    /**
     * Set this transform's position from a vector
     * @param pos The new position for this transform
     * @throws java.security.InvalidParameterException If the provided position is null
     */
    public void setPos(Vector pos) {
        if (pos == null){
            throw new InvalidParameterException("Transform provided with new position as null");
        }
        this.pos = pos;
    }

    /**
     * Get the transform eulars rotation
     * @return The current eulars of this transform, as a pointer
     */
    public Vector getRotation() {
        return rotation;
    }

    /**
     * Get a pointer to the transform's scale
     * @return A pointer to the current scale of this transform
     */
    public Vector getScale(){
        return scale;
    }
}
