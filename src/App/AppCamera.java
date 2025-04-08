package App;

import Graphics.GLCamera;
import Graphics.GLObject;
import org.lwjgl.glfw.GLFW;

import Math.Vector;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * This process manages the camera of this app.
 */
public class AppCamera implements AppProcess{
    /**
     * The GLCamera for this app process
     */
    private GLCamera camera;

    /**
     * The speed of movement for the camera
     */
    private float speed;

    /**
     * The speed of the rotation of the camera
     */
    private float rotation_speed;

    /**
     * An empty constructor, nothing can be initialized yet without context
     * @param speed The speed the camera moves
     * @param rotation_speed The speed in which the camera rotates
     */
    public AppCamera(float speed, float rotation_speed){
        this.speed = speed;
        this.rotation_speed = rotation_speed;
    }

    /**
     * Initialize the camera by constructing the object then adding it to the graphics stack
     * @param context The current app context, should be initialized before this
     * @throws InvalidParameterException If context is null
     */
    @Override
    public void init(AppContext context) throws InvalidParameterException {
        if (context == null){
            throw new InvalidParameterException("Null Context provided to GLCamera");
        }
        float apsect = (float) context.getGraphicsDriver().getWindowWidth()
                / (float) context.getGraphicsDriver().getWindowHeight();

        camera = new GLCamera(45.0f, apsect, 0.1f, 1000.0f);

        context.getGraphicsDriver().addCamera(camera);
    }

    /**
     * Iterate this frame for the camera. This method handles movement of the camera via the keyboard
     * @param dt The time, in seconds, since the last call (Delta Time)
     * @param context The current app context this process is running in
     * @throws InvalidParameterException
     */
    @Override
    public void frame(double dt, AppContext context) throws InvalidParameterException {
        // This method is for calculating movement of the camera
        // Take the current forward vector of the camera
        Vector forward = camera.getForward();

        // The direction to move the camera, added to the position vector. Should be normalized.
        Vector movement_delta = new Vector();

        ArrayList<Integer> keylist = context.getGraphicsDriver().getWindow().getKeyList();
        for (Integer key : keylist){
            switch (key){
                case GLFW.GLFW_KEY_W: {
                    movement_delta = movement_delta.plus(forward.mul(1.0f));
                    break;
                }
                case GLFW.GLFW_KEY_S: {
                    movement_delta = movement_delta.plus(forward.mul(-1.0f));
                    break;
                }
                case GLFW.GLFW_KEY_A: {
                    Vector cam_right = new Vector(0.0, 1.0, 0.0).cross(forward);
                    cam_right.normalize();
                    movement_delta = movement_delta.plus(cam_right);
                    break;
                }
                case GLFW.GLFW_KEY_D: {
                    Vector cam_right = new Vector(0.0, 1.0, 0.0).cross(forward);
                    cam_right.normalize();
                    movement_delta = movement_delta.plus(cam_right.mul(-1.0f));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if (movement_delta.getLength() > 0.01) {
            movement_delta.normalize();

            movement_delta = movement_delta.mul(speed * (float) dt);

            Vector old_pos = camera.getTransform().getPos();
            Vector new_pos = old_pos.plus(movement_delta);

            camera.getTransform().setPos(new_pos);
        }
    }

    /**
     * Call this method to the destroy the camera. The GLCamera isn't actually unsafe, but it'll mark itself as
     * destroyed. So after this call, this process cannot be used for *RENDERING* anymore.
     */
    @Override
    public void destroy() {
        camera.destroy();
    }

    /**
     * Get the GLCamera from this object
     * @return A GLCamera from this object
     */
    public GLCamera getGLCamera(){
        return camera;
    }
}
