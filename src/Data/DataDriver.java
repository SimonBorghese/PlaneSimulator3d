/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import javax.naming.ConfigurationException;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.rmi.UnexpectedException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Math.Image;
import org.lwjgl.system.MemoryUtil;

/**
 * This class exposes an API for retrieving data from various online sources in a friendly way
 */
public class DataDriver {
    /**
     * An instance of the InternetDriver used for communication
     */
    private InternetDriver inetDriver;

    /**
     * Construct the DataDriver with some usable defaults
     * @throws ConfigurationException If there is no Google API key
     */
    public DataDriver() throws ConfigurationException {
        // Construct the internet driver with the default constructor to use the provided API key
        try {
            inetDriver = new InternetDriver();

            inetDriver.initializeTileSession();
        } catch (ConfigurationException | FileNotFoundException e) {
            throw new ConfigurationException("No API key provided for Google Cloud");
        }
    }

    /**
     * Construct a DataDriver from an existing one with a derived internet driver
     * @param dataDriver The old driver to copy from
     * @throws java.security.InvalidParameterException If dataDriver is null
     */
    public DataDriver(DataDriver dataDriver){
        if (dataDriver == null){
            throw new InvalidParameterException("Provided DataDriver for cloning was null!");
        }
        // Constrict a new internet driver from the old driver
        inetDriver = new InternetDriver(dataDriver.inetDriver);
    }

    /**
     * Using a list of coordinates, grab elevation data using Google's API.
     * @param coordinates A list, of any type, of coordinates to query.
     * @return A map where a desired coordinate maps to the relevant elevation returned by the API.
     */
    public HashMap<WorldCoordinate, Float> getElevationData(List<WorldCoordinate> coordinates){
        // Our output, to be built throughout this method
        HashMap<WorldCoordinate, Float> data = new HashMap<>();
        // Begin converting our coordinates to strings usable by the Google API
        // The Google API only accepts lists of 512 or less, split up the list into groups of 512 for the most efficiency
        for (int i = 0; i <= (coordinates.size() / 512); i++){
            // Establish our raw coordinates
            ArrayList<String> rawCoordinates = new ArrayList<>();

            // Generate out raw coordinates
            for (int j = (512*i); j < (coordinates.size() - (512 * i)); j++){
                WorldCoordinate coordinate = coordinates.get(j + (512 * i));

                rawCoordinates.add(String.format("%.7f", coordinate.getLatitude()));
                rawCoordinates.add(String.format("%.7f", coordinate.getLongitude()));
            }

            // Send and parse the result
            HashMap<WorldCoordinate, Float> parsed_elevation = parseElevation(inetDriver.getElevation(rawCoordinates));

            data.putAll(parsed_elevation);
        }

        return data;
    }

    /**
     * Using a provided coordinate and zoom, return a decoded image of the satallite view
     * @param coordinate The coordinate of the requested location
     * @param zoom The zoom at the coord
     * @return A byte array of a decoded image
     * @throws ConfigurationException Should the API and Session not properly be configured
     */
    public Image getSatalliteImage(WorldCoordinate coordinate, double zoom) throws ConfigurationException{
        try {
            byte[] jpeg_bytes = inetDriver.getSatalliteImage(coordinate.toPoint(256, zoom));

            try {
                return decodeImage(jpeg_bytes);
            } catch (UnexpectedException e) {
                System.out.println("Google failed to provide a valid image!");
                throw new RuntimeException("Google API returned an invalid image!");
            }
        } catch (ConfigurationException e) {
            System.out.println("Failed to get satallite image, probably an API key issue!");
            throw new ConfigurationException("API or Session Key misconfiguration!");
        }
    }

    /**
     * Given a provided, arbitrary image compatible with stb, return a decoded image
     * @param jpeg_image The bytes for a jpeg (which is preferred) image
     * @return The decoded bytes
     * @throws UnexpectedException If the provided image can't be decoded, unexpected because Google should be providing
     * valid images
     */
    private Image decodeImage(byte[] jpeg_image) throws UnexpectedException {
        // STB writes some data to these values, it can only be provided as a sort of pointer from an array
        int[] x_output = new int[1];
        int[] y_output = new int[1];
        int[] channel_output = new int[1];

        try {
            ByteBuffer img_buffer = MemoryUtil.memAlloc(jpeg_image.length);

            img_buffer.put(jpeg_image).flip();

            STBImage.stbi_set_flip_vertically_on_load(true);

            ByteBuffer decoded = STBImage.stbi_load_from_memory(img_buffer, x_output, y_output, channel_output,
                    3);

            if (decoded == null){
                throw new UnexpectedException("Image failed to decode!");
            }

            byte[] result_buffer = new byte[x_output[0] * y_output[0] * 3];

            for (int i = 0; i < result_buffer.length; i++){
                result_buffer[i] = (byte) decoded.get();
            }

            return new Image(x_output[0], y_output[0], 3, 3, result_buffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("FAILED TO DECODE IMAGE!");
            throw new UnexpectedException("Invalid image!");
        }
    }

    /**
     * A method for parsing the elevation specific JSON. Not resistant to changes within the Google API
     * @param rawJson The raw JSON provided by the API
     * @return A list of elevations and their relevant world coordinates
     */
    private HashMap<WorldCoordinate, Float> parseElevation(String rawJson){
        HashMap<WorldCoordinate, Float> data = new HashMap<>();

        // Begin reading the JSON through a hard coded pattern
        JSONObject root_json = new JSONObject(rawJson);

        JSONArray result_array = root_json.getJSONArray("results");

        for (int i = 0; i < result_array.length(); i++){
            JSONObject elevation = result_array.getJSONObject(i);

            float raw_elevation = elevation.getFloat("elevation");

            JSONObject query_loc = elevation.getJSONObject("location");

            float lat = query_loc.getFloat("lat");
            float lng = query_loc.getFloat("lng");

            data.put(new WorldCoordinate(lat, lng), raw_elevation);
        }

        return data;
    }
}
