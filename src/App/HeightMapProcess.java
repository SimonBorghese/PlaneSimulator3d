package App;

import Data.WorldCoordinate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Graphics.GLHeightMapConfig;
import Graphics.GLPointArray;
import Graphics.GLShader;
import Math.Vector;

/**
 * This class provides a processes for management and updating of height maps for each grid of tiles, asynchronously
 * of the world process
 */
public class HeightMapProcess implements AppProcess{
    private GLHeightMapConfig config;

    public HeightMapProcess(){
        config = new GLHeightMapConfig();
    }

    @Override
    public void init(AppContext context) throws InvalidParameterException {
        WorldCoordinate base_tile = new WorldCoordinate(38.95939, -79.34584, 256, 9);
        Vector base_loc = base_tile.getTile();
        WorldCoordinate up = new WorldCoordinate(base_loc.getX(), base_loc.getY() + 1, 9);
        WorldCoordinate down = new WorldCoordinate(base_loc.getX(), base_loc.getY() - 1, 9);
        WorldCoordinate left = new WorldCoordinate(base_loc.getX() - 1, base_loc.getY(), 9);
        WorldCoordinate right = new WorldCoordinate(base_loc.getX() + 1, base_loc.getY(), 9);

        double max_long = up.getWorldCoordinate().getY();
        double min_long = down.getWorldCoordinate().getY();
        double max_lat = right.getWorldCoordinate().getX();
        double min_lat = left.getWorldCoordinate().getX();

        double base_lat = base_tile.getWorldCoordinate().getX();
        double base_long = base_tile.getWorldCoordinate().getY();

        double delta_lat = (max_lat - min_lat)  / 2.0;
        double delta_lon = (max_long - min_long) / 2.0;

        // Calculate our needed coordinates
        ArrayList<WorldCoordinate> cords = new ArrayList<>();

        int elevation_res = 2;

        for (int x = -elevation_res; x < elevation_res; x++){
            for (int y = -elevation_res; y < elevation_res; y++){
                double lat_p = ((double) x * delta_lat / (double) elevation_res) + base_lat;
                double lng_p = ((double) y * delta_lon / (double) elevation_res) + base_long;
                cords.add(
                        new WorldCoordinate(lat_p, lng_p)
                );
            }
        }

        HashMap<WorldCoordinate, Float> result_elevation = context.getDataDriver().getElevationData(cords);

        try {
            GLShader map_shader = new GLShader();
            map_shader.createProgram(
                    Files.readString(Paths.get("shaders/vertex_map.glsl")),
                    Files.readString(Paths.get("shaders/geometry_map.glsl")),
                    Files.readString(Paths.get("shaders/fragment_map.glsl"))
            );

            map_shader.setDepthTest(false);

            context.getGraphicsDriver().pushObject(map_shader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        context.getGraphicsDriver().pushObject(config);

        GLPointArray mesh = new GLPointArray();

        float[] vertices = new float[3 * result_elevation.size()];

        int offset = 0;

        double min_lat2 = result_elevation.entrySet().stream().findAny().get().getKey().getWorldCoordinate().getX();
        double max_lat2 = result_elevation.entrySet().stream().findAny().get().getKey().getWorldCoordinate().getX();

        double min_lng2 = result_elevation.entrySet().stream().findAny().get().getKey().getWorldCoordinate().getY();
        double max_lng2 = result_elevation.entrySet().stream().findAny().get().getKey().getWorldCoordinate().getY();
        for (Map.Entry<WorldCoordinate, Float> cord : result_elevation.entrySet()){
            Vector latlng = cord.getKey().getWorldCoordinate();

            min_lat2 = Math.min(min_lat2, latlng.getX());
            max_lat2 = Math.max(max_lat2, latlng.getX());

            min_lng2 = Math.min(min_lng2, latlng.getY());
            max_lng2 = Math.max(max_lng2, latlng.getY());

            System.out.printf("LAt: %f Lng: %f\n", latlng.getX(), latlng.getY());
            vertices[offset * 3] = (float)latlng.getX();
            vertices[(offset * 3) + 1] = (float)latlng.getY();
            vertices[(offset * 3) + 2] = (float)cord.getValue();
            offset++;
        }

        mesh.bindElementsForUse();

        mesh.uploadVertices(vertices);

        mesh.configureVertexArray();

        context.getGraphicsDriver().pushObject(mesh);

        config.lat_bounds = new Vector(min_lat2, max_lat2, 0);
        config.lng_bounds = new Vector(min_lng2, max_lng2, 0);
    }

    @Override
    public void frame(double dt, AppContext context) throws InvalidParameterException {

    }

    @Override
    public void destroy() {

    }
}
