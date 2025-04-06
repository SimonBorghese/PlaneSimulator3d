package Math;

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
     * Construct this transform with provided transformation values
     * @param pos The 3d position of this transform
     * @param rotation The eulars rotation
     */
    public Transform(Vector pos, Vector rotation) {
        this.pos = pos;
        this.rotation = rotation;
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
     * Get the transform eulars rotation
     * @return The current eulars of this transform, as a pointer
     */
    public Vector getRotation() {
        return rotation;
    }
}
