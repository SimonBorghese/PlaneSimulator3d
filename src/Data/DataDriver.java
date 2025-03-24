/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.stb.STBImage;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

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
     * Using a list of coordinates, grab elevation data using Google's API.
     * @param coordinates A list, of any type, of coordinates to query.
     * @return A map where a desired coordinate maps to the relevant elevation returned by the API.
     */
    public HashMap<WorldCoordinate, Float> getElevationData(List<WorldCoordinate> coordinates){
        // Our output, to be built throughout this method
        HashMap<WorldCoordinate, Float> data = new HashMap<>();
        // Begin converting our coordinates to strings usable by the Google API
        // The Google API only accepts lists of 512 or less, split up the list into groups of 512 for the most efficiency
        for (int i = 0; i < (coordinates.size() % 512); i++){
            // Establish our raw coordinates
            HashMap<String, String> rawCoordinates = new HashMap<>();

            // Generate out raw coordinates
            for (int j = 0; j < (coordinates.size() - 512 * i); j++){
                WorldCoordinate coordinate = coordinates.get(j + (512 * i));

                rawCoordinates.put(String.format("%.7f", coordinate.getLatitude()),
                        String.format("%.7f", coordinate.getLongitude()));
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
    public byte[] getSatalliteImage(WorldCoordinate coordinate, double zoom) throws ConfigurationException{
        try {
            byte[] jpeg_bytes = inetDriver.getSatalliteImage(coordinate.toPoint(256, zoom));

            ByteBuffer jpg_buffer = MemoryUtil.memAlloc(jpeg_bytes.length);

            for (int i = 0; i < jpeg_bytes.length; i++){
                jpg_buffer.put(jpeg_bytes[i]);
            }

            return decodeImage(jpg_buffer);
        } catch (ConfigurationException e) {
            System.out.println("Failed to get satallite image, probably an API key issue!");
            throw new ConfigurationException("API or Session Key misconfiguration!");
        }
    }

    /**
     * Given a provided, arbitrary image compatible with stb, return a decoded image
     * @param jpeg_image The bytes for a jpeg (which is preferred) image
     * @return The decoded bytes
     */
    private byte[] decodeImage(ByteBuffer jpeg_image){
        // STB writes some data to these values, it can only be provided as a sort of pointer from an array
        int[] x_output = new int[1];
        int[] y_output = new int[1];
        int[] channel_output = new int[1];

        // Load the image
        ByteBuffer image = STBImage.stbi_load_from_memory(jpeg_image,
                x_output, y_output, channel_output, 3);

        if (image == null){
            // STB actually fails in a safe way, no need for exception
            System.out.println("Image Load Failed: " + STBImage.stbi_failure_reason());
        }

        // Then we must make another array and copy the buffer into that because image.array() becomes null
        byte[] result_bytes = new byte[x_output[0] * y_output[0] * 3];
        for (int i = 0; i < x_output[0] * y_output[0] * 3; i++){
            result_bytes[i] = image.get(i);
        }

        // Return the decoded image
        return result_bytes;
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
