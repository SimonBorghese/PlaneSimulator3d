package Graphics;

import org.lwjgl.opengl.GL33;

import java.security.InvalidParameterException;

/**
 * This is an extension of the GLVertexArray which extends it to render heightmaps by overriding the render
 * function.
 */
public class GLHeightmap extends GLVertexArray{
    /**
     * The resolution of the height map (i.e. resolution ^ 2) = vertices of height map
     */
    private int resolution;

    /**
     * Construct this heightmap with a provided resolution
     */
    public GLHeightmap(int resolution){
        super();
        this.resolution = resolution;
    }

    /**
     * This method overrides the use method to render the heightmap
     * depend on the context however a shader and texture should still be set.
     * @param context A context of the currently bound objects is provided to assist in preparing and execution
     * @throws java.security.InvalidParameterException If the context shows no shader
     */
    @Override
    public void use(GraphicsContext context) {
        if (!context.hasShader()){
            throw new InvalidParameterException("A meshes attempted render without a bound shader!");
        }

        useMesh();

        // For each row of the height map
        for (int s = 0; s < resolution - 1; ++s) {
            /*
             Draw the heightmap line, we render 20 vertices as each 3 form a triangle (but it's also a triangle
             strip so the previous vertex is the first of the next triangle.

             The final parameter is a pointer:
             size of an integer times the 20 vertices rendered times the row of the height map
             */
            GL33.glDrawElements(GL33.GL_TRIANGLE_STRIP, resolution * 2, GL33.GL_UNSIGNED_INT,
                    ((4 * (long) resolution * 2 * s)));
        }

        // Set ourselves to the context
        context.setMesh(this);
    }
}
