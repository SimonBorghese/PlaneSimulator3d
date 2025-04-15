package Graphics;

import Math.Transform;
import Math.Matrix;

import java.security.InvalidParameterException;

/**
 * A special implementation of GLTransform which includes the ability to link to a parent allowing for a rough
 * scene graph.
 */
public class GLLinkedTransform extends GLTransform{
    /**
     * The parent of this transform
     */
    private GLLinkedTransform parent;

    /**
     * Construct this object with a provided transform AND parent
     * @param transform A transform object to construct this object from
     * @param parent The parent of this transform, may be null as to indicate the top.
     */
    public GLLinkedTransform(Transform transform, GLLinkedTransform parent) {
        super(transform);

        this.parent = parent;
    }

    /**
     * Return the parent of this transform
     * @return The parent of this transform, may be null
     */
    public GLLinkedTransform getParent() {
        return parent;
    }

    /**
     * Return whether this transform has a parent (i.e. is the parent null)
     * @return True if this transform has a parent
     */
    public boolean hasParent(){
        return parent != null;
    }

    /**
     * A recursive method to construct a model matrix from a GLLinkedTransform and continue until the provided
     * GLLinkedTransform
     * @param parent The GLLinkedTransform to apply to the matrix, if null the method exits
     * @param model The model matrix to apply operations to, cannot be null
     */
    public void constructModel(GLLinkedTransform parent, Matrix model){
        if (parent == null) {
            return;
        }

        model.multiply(parent.constructTransform());

        constructModel(parent.getParent(), model);
    }

    /**
     * Modified version of the GLTransform use method to construct the model from the parent
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws java.security.InvalidParameterException If there is no shader in the context
     */
    @Override
    public void use(GraphicsContext context) {
        if (!context.hasShader()){
            throw new InvalidParameterException("No shader provided to GLTransform on use!");
        }

        Matrix model = new Matrix();

        constructModel(this, model);

        int model_loc = context.getShader().getUniformLocation("model");
        context.getShader().setMatrixUniform(model_loc, model.getRawMatrix());
    }
}
