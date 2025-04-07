package Graphics;

import Math.Transform;
import Math.Matrix;
import Math.Vector;
import glm_.glm;

import java.security.InvalidParameterException;

/**
 * This object isn't a traditional OpenGL object but still should be stored in the graphics stack.
 * This class holds the projection and view of the camera and then binds for the use method
 * However, the handle isn't actually used
 */
public class GLCamera extends GLObject{
    /**
     * The transform for this camera
     */
    private Transform transform;

    /**
     * The projection matrix for this camera
     */
    private Matrix projection;

    /**
     * Construct this camera with provided projection parameters and transform vector
     * @param transform The transform to assign to this camera
     * @param fov The fov of this camera, in degrees
     * @param aspect The aspect ratio of this window
     * @param zNear The near parameter, i.e., the closest points
     * @param zFar The far parameter, i.e., the cut off point
     */
    public GLCamera(Transform transform, float fov, float aspect, float zNear, float zFar) {
        this.transform = transform;
        this.projection = new Matrix(fov, aspect, zNear, zFar);
    }

    /**
     * Construct this camera with provided projection parameters and empty transform
     * @param fov The fov of this camera, in degrees
     * @param aspect The aspect ratio of this window
     * @param zNear The near parameter, i.e., the closest points
     * @param zFar The far parameter, i.e., the cut off point
     */
    public GLCamera(float fov, float aspect, float zNear, float zFar) {
        this(new Transform(), fov, aspect, zNear, zFar);
    }

    /**
     * Get this transform, as a pointer
     * @return The current transform, as a pointer
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Set the transform
     * @param transform The new transform for this camera
     * @throws InvalidParameterException If transform is null
     */
    public void setTransform(Transform transform){
        if (transform == null){
            throw new InvalidParameterException("Provided Transform to camera is null");
        }
        this.transform = transform;
    }


    /**
     * Do nothing as we don't use the handle
     */
    @Override
    public void destroy() {
        // It's not like we're destroying anything on our own, but this marks this camera as destroyed
        projection = null;
    }

    /**
     * Bind the camera projection and view to the shader.
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws java.security.InvalidParameterException If there is no shader bound
     * @throws RuntimeException If the camera doesn't have a projection matrix (probably due to being destroyed)
     */
    @Override
    public void use(GraphicsContext context) {
        if (!context.hasShader()){
            throw new InvalidParameterException("Camera provided with no shader in context!");
        }
        if (projection == null){
            throw new RuntimeException("Camera provided with no projection! (Likely destroyed)");
        }
        // Retrieve the location of projection and view
        int proj_loc = context.getShader().getUniformLocation("projection");
        int view_loc = context.getShader().getUniformLocation("view");

        // Generate our transformation matrix
        // We need a position, look at, and then up, we assume up is always +y but look at must be derived

        Vector look_at = getForward();

        // Our view matrix assumes up is always +y
        Matrix view = new Matrix(transform.getPos(), transform.getPos().plus(look_at),
                new Vector(0.0f, 1.0f, 0.0f));

        // Set the view and projection matrix
        context.getShader().setMatrixUniform(proj_loc, projection.getRawMatrix());
        context.getShader().setMatrixUniform(view_loc, view.getRawMatrix());
    }

    /**
     * Calculate the forward vector for this camera
     * @return A normalized vector for the forward of this camera
     */
    public Vector getForward(){
        // BASED ON : https://learnopengl.com/Getting-started/Camera
        Vector rotation = transform.getRotation();

        // Extract the pitch
        double pitch = rotation.getX();

        // Offset yaw by 90 degrees to offset for a math issue
        double yaw = rotation.getY() + 90.0;
        Vector look_at = new Vector();
        look_at.setX(
                glm.INSTANCE.cos(glm.INSTANCE.radians(yaw)) *
                        glm.INSTANCE.cos(glm.INSTANCE.radians(pitch))
        );
        look_at.setY(
                glm.INSTANCE.sin(glm.INSTANCE.radians(pitch))
        );
        look_at.setZ(
                glm.INSTANCE.sin(glm.INSTANCE.radians(yaw)) *
                        glm.INSTANCE.cos(glm.INSTANCE.radians(pitch))
        );

        // Normalize
        look_at.normalize();
        return look_at;
    }
}
