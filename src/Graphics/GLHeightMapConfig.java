package Graphics;

import Math.Vector;

/**
 * TODO: REMOVE AND CREATE A MORE ELEGANT WAY FOR UNIFORMS
 */
public class GLHeightMapConfig extends GLObject{

    public Vector lat_bounds;
    public Vector lng_bounds;

    public GLHeightMapConfig(){
        lat_bounds = new Vector();
        lng_bounds = new Vector();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void use(GraphicsContext context) {
        int lat_loc = context.getShader().getUniformLocation("lat_bounds");
        int lng_loc = context.getShader().getUniformLocation("lng_bounds");

        context.getShader().setVec2Uniform(lat_loc, lat_bounds.getRawArray());
        context.getShader().setVec2Uniform(lng_loc, lng_bounds.getRawArray());
    }
}
