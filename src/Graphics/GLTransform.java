package Graphics;

import Math.Transform;
import Math.Matrix;

import java.security.InvalidParameterException;

/**
 * Represents a 3d transform to be pushed to the "model" uniform before rendering.
 */
public class GLTransform extends GLObject {
    /**
     * The transform to copy to the model uniform
     */
    private Transform transform;

    /**
     * Construct this object with a provided transform
     */
    public GLTransform(Transform transform) {
        this.transform = transform;
    }


    /**
     * This object doesn't create any OpenGL objects and therefore does nothing in
     * destroy()
     */
    @Override
    public void destroy() {
        // Empty
    }

    /**
     * Bind this transform to the currently bound shader's model uniform
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws java.security.InvalidParameterException If there is no shader in the context
     */
    @Override
    public void use(GraphicsContext context) {
        if (!context.hasShader()){
            throw new InvalidParameterException("No shader provided to GLTransform on use!");
        }

        Matrix model = new Matrix();

        model.rotate(transform.getRotation().toGLM().getArray());
        model.translate(transform.getPos().toGLM().getArray());

        int model_loc = context.getShader().getUniformLocation("model");
        context.getShader().setMatrixUniform(model_loc, model.getRawMatrix());
    }
}
