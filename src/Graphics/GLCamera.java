package Graphics;

/**
 * This object isn't a traditional OpenGL object but still should be stored in the graphics stack.
 * This class holds the projection and view of the camera and then binds for the use method
 * However, the handle isn't actually used
 */
public class GLCamera extends GLObject{

    /**
     * Do nothing as we don't use the handle
     */
    @Override
    public void destroy() {}

    @Override
    public void use(GraphicsContext context) {

    }
}
