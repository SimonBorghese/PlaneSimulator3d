import Graphics.GLCamera;
import Graphics.GraphicsDriver;

import Math.Image;

import Data.*;
import glm_.glm;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.primitives.Point;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import javax.naming.ConfigurationException;
import java.util.*;

/**
 * PlaneSimulator3d
 * This is a flight tracker/simulator created by Simon Borghese for CS 220
 * All files in src were authored entirely by Simon Borghese
 * This code will be uploaded at a later date to Github under an Open Source License.
 * @author Simon Borghese
 */

public class Driver {
    public static void main(String[] args){

        GraphicsDriver gDriver = new GraphicsDriver(800,600,4,1);
        gDriver.init();

        GLCamera newCamera = new GLCamera(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);

        // Add our camera to the driver
        gDriver.addCamera(newCamera);

        // Add our texture to the stack

        Data.DataDriver dataDriver = null;
        try {
            dataDriver = new DataDriver();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        double[][] heightmap = new double[10][10];
        double lat = 37.837383;
        double lng = -79.068722;
        ArrayList<WorldCoordinate> cords = new ArrayList<>();

        for (int x = -2; x < 2; x++){
            for (int y = -2; y < 2; y++){
                double lat_p = (double) x * (0.001) + lat;
                double lng_p = (double) y * (0.001) + lng;
                cords.add(
                        new WorldCoordinate(lat_p, lng_p)
                );
            }
        }

        HashMap<WorldCoordinate, Float> result_cords = dataDriver.getElevationData(cords);


        double lat_min = result_cords.keySet().stream().toList().getFirst().getLatitude();
        double lat_max = result_cords.keySet().stream().toList().getFirst().getLatitude();

        double lng_min = result_cords.keySet().stream().toList().getFirst().getLongitude();
        double lng_max = result_cords.keySet().stream().toList().getFirst().getLongitude();

        for (Map.Entry<WorldCoordinate, Float> cord : result_cords.entrySet()){
            lat_min = Math.min(lat_min,cord.getKey().getLatitude());
            lat_max = Math.max(lat_max,cord.getKey().getLatitude());

            lng_min = Math.min(lng_min, cord.getKey().getLongitude());
            lng_max = Math.max(lng_max, cord.getKey().getLongitude());
        }

        // Create a tessilated square
        int resolution = 10;
        double constant_factor = 1.0 / 1000.0;

        float[] vertices = new float[resolution*resolution*5];
        int[] elements = new int[(resolution-1)*resolution * 2];

        for (int i =0;  i < resolution - 1; i++){
            for (int j = 0; j < resolution; j++){
                for (int k =0; k < 2; k++){
                    elements[(i*resolution*2) + (j*2) + k] = (j + resolution * (i + k));
                }
            }
        }

        for (int i = 0; i < resolution; i++){
            for (int j  = 0; j < resolution; j++){
                vertices[(i * resolution * 5) + (j * 5)]
                        = (float) ( -resolution / 2.0 + i);
                double result_y = 0.0;
                for (Map.Entry<WorldCoordinate, Float> cord: result_cords.entrySet()){
                    double pos_x = (lat_max - cord.getKey().getLatitude()) / (lat_max - lat_min);
                    double pos_y = (lng_max - cord.getKey().getLongitude()) / (lng_max - lng_min);

                    double radius_x = ((double) j / (double) resolution) - pos_x;
                    double radius_y = ((double) i / (double) resolution) - pos_y;

                    double radius = Math.sqrt(Math.pow(radius_x, 2) + Math.pow(radius_y, 2));
                    double r_sqr = Math.pow(radius, 2);

                    double dist = (constant_factor) * (cord.getValue()) * (1 / Math.max(r_sqr, 1.0));
                    result_y += dist;
                    System.out.printf("Result cord: %f, Result: %f\n", cord.getValue(), dist);
                }
                vertices[(i * resolution * 5) + (j * 5) + 1]
                        = (float) (result_y);
                vertices[(i * resolution * 5) + (j * 5) + 2]
                        = (float) (-resolution / 2.0 + j);

                vertices[(i * resolution * 5) + (j * 5) + 3] = (float) (vertices[(i * resolution * 5) + (j * 5)]
                + (resolution / 2.0 + (double) i)) / (float) (-resolution / 2.0 + resolution);
                vertices[(i * resolution * 5) + (j * 5) + 4] = (float) (vertices[(i * resolution * 5) + (j * 5) + 2]
                        + (resolution / 2.0 + (double) j)) / (float) (-resolution / 2.0 + resolution);
            }
        }

        try {
            Image raw_image = dataDriver.getSatalliteImage(new WorldCoordinate(lat,
                    lng), 15);

            gDriver.pushTexture(raw_image);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        Graphics.GLVertexArray test_mesh = new Graphics.GLVertexArray();

        test_mesh.bindElementsForUse();

        test_mesh.uploadVertices(vertices);

        test_mesh.uploadElements(elements);

        test_mesh.configureVertexArray();

        gDriver.pushObject(test_mesh);

        long time = System.currentTimeMillis();
        while (gDriver.loop()){

            long current_time = System.currentTimeMillis();

            double dt = (double) (current_time - time) / 1000.0;

            newCamera.getTransform().getPos().setZ(-15.5);

            newCamera.getTransform().getPos().setX(-2.5);

            newCamera.getTransform().getPos().setY(45.0);

            newCamera.getTransform().getRotation().setX(-55.0);

            time = System.currentTimeMillis();
        }

        gDriver.destroy();
        /**
        Graphics.Window win = new Graphics.Window(800, 600, 4,1);

        Data.DataDriver dataDriver = null;
        try {
            dataDriver = new DataDriver();
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        win.init();

        Graphics.GLShader test_shader = new Graphics.GLShader();

        try {
            test_shader.createProgram(Files.readString(Paths.get("shaders/vertex.glsl")),
                    Files.readString(Paths.get("shaders/fragment.glsl")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Graphics.GLVertexArray test_mesh = new Graphics.GLVertexArray();

        test_mesh.bindElementsForUse();

        test_mesh.uploadVertices(new float[]{
                -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        });

        test_mesh.uploadElements(new int[]{0,1,3,3,2,0});

        test_mesh.configureVertexArray();

        Graphics.GLTexture tex = new Graphics.GLTexture();

        Matrix proj = new Matrix(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);

        Matrix model = new Matrix();

        try {
            Image raw_image = dataDriver.getSatalliteImage(new WorldCoordinate(39.7391536,
                    -104.9847034), 15);

            byte[] internal_result = new byte[raw_image.getWidth() * raw_image.getHeight() * 3];

            for (int i = 0; i < raw_image.getWidth() * raw_image.getHeight() * 3; i++) {
                internal_result[i] = raw_image.getData().get(i);
            }

            tex.uploadTexture(internal_result, raw_image.getWidth(), raw_image.getHeight());

        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        long start_time = System.currentTimeMillis();

        model.translate(new float[]{0.0f, 0.0f, -3.0f});

        while (win.loop()){
            GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
            test_shader.useProgram();
            tex.bindToUnit(0);
            int loc = test_shader.getUniformLocation("iTex");
            int proj_loc = test_shader.getUniformLocation("projection");
            int model_loc = test_shader.getUniformLocation("model");

            long dt = System.currentTimeMillis() - start_time;

            model.rotate(new float[]{4.0f * (float) ((double) dt / 3000.0), 0.0f, 0.0f});

            // Zero is our current texture unit
            test_shader.setUniformInt(loc, 0);
            test_shader.setMatrixUniform(proj_loc, proj.getRawMatrix());
            test_shader.setMatrixUniform(model_loc, model.getRawMatrix());
            test_mesh.useMesh();
            GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);

            start_time += dt;
        }

        win.destroy();

        try{
            ArrayList<WorldCoordinate> requested_locations = new ArrayList<>();

            requested_locations.add(new WorldCoordinate(39.7391536,
                    -104.9847034));

            HashMap<WorldCoordinate, Float> elevations = dataDriver.getElevationData(requested_locations);

            for (Map.Entry<WorldCoordinate, Float> coordinate : elevations.entrySet()) {
                System.out.printf("At lat: %f and Long: %f, the elevation is: %f\n", coordinate.getKey().getLatitude(),
                        coordinate.getKey().getLongitude(), coordinate.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
         */

    }
}
