import Data.DataDriver;
import Data.WorldCoordinate;

import javax.imageio.ImageIO;
import javax.naming.ConfigurationException;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Graphics.GLDriver;
import Math.Vector;
import Math.Image;
import Math.Matrix;
import org.lwjgl.opengl.GL33;

/**
 * PlaneSimulator3d
 * This is a flight tracker/simulator created by Simon Borghese for CS 220
 * All files in src were authored entirely by Simon Borghese
 * This code will be uploaded at a later date to Github under an Open Source License.
 * @author Simon Borghese
 */

public class Driver {
    public static void main(String[] args){
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
                -1.0f, -1.0f, -3.0f, 0.0f, 0.0f,
                1.0f, -1.0f, -3.0f, 1.0f, 0.0f,
                -1.0f, 1.0f, -3.0f, 0.0f, 1.0f,
                1.0f, 1.0f, -3.0f, 1.0f, 1.0f,
        });

        test_mesh.uploadElements(new int[]{0,1,3,3,2,0});

        test_mesh.configureVertexArray();

        Graphics.GLDriver driver = new GLDriver();

        Graphics.GLTexture tex = new Graphics.GLTexture();

        Matrix proj = new Matrix(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);

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

        while (win.loop()){
            GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
            test_shader.useProgram();
            tex.bindToUnit(0);
            int loc = test_shader.getUniformLocation("iTex");
            int proj_loc = test_shader.getUniformLocation("projection");

            // Zero is our current texture unit
            test_shader.setUniformInt(loc, 0);
            test_shader.setMatrixUniform(proj_loc, proj.getRawMatrix());
            test_mesh.useMesh();
            GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
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

    }
}
