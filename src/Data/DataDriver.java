/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

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
        } catch (FileNotFoundException e) {
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
            for (int j = 0; j < 512 - (coordinates.size() - 512 * i); j++){
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
