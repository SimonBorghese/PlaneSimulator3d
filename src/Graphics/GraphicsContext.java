package Graphics;

/**
 * This class will represent the state of the currents graphics system. It stores which objects are being "used"
 * at any given moment. Which can be either all objects or none. But only type of object can be bound at once.
 */
public class GraphicsContext {
    /**
     * The currently bound shader, if there is one
     */
    private GLShader shader;

    /**
     * The currently bound texture, if there is one
     */
    private GLTexture texture;

    /**
     * The currently bound mesh, if there is one
     */
    private GLVertexArray mesh;

    /**
     * An empty constructor for the graphics context. There isn't a need to set anything here as it should
     * be created, once, by the graphics driver then set everything later
     */
    public GraphicsContext(){
        shader = null;
        texture = null;
        mesh = null;
    }

    /**
     * Check whether this graphics context has a shader bound
     * @return True if a shader is bound, false otherwise
     */
    public boolean hasShader(){
        return shader != null;
    }

    /**
     * Check whether this graphics context has a texture bound
     * @return True if a texture is bound, false otherwise
     */
    public boolean hasTexture(){
        return texture != null;
    }

    /**
     * Check whether this graphics context has a mesh bound
     * @return True if a mesh is bound, false otherwise
     */
    public boolean hasMesh(){
        return mesh != null;
    }

    /**
     * Get the currently bound shader
     * @return The currently bound shader, will be null if we have no shader bound
     */
    public GLShader getShader(){
        return shader;
    }

    /**
     * Get the currently bound texture
     * @return The currently bound texture, will be null if we have no texture bound
     */
    public GLTexture getTexture(){
        return texture;
    }

    /**
     * Get the currently bound mesh
     * @return The currently bound mesh, will be null if we have no mesh bound
     */
    public GLVertexArray getMesh(){
        return mesh;
    }

    /**
     * Set a shader to be bound to this context
     * @param shader The new shader, may be null
     */
    public void setShader(GLShader shader){
        this.shader = shader;
    }

    /**
     * Set a texture to be bound to this context
     * @param texture The new texture, may be null
     */
    public void setTexture(GLTexture texture){
        this.texture = texture;
    }

    /**
     * Set a mesh to be bound to this context
     * @param mesh The new mesh, may be null
     */
    public void setMesh(GLVertexArray mesh){
        this.mesh = mesh;
    }


}
