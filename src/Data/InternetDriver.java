/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import javax.naming.LimitExceededException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * This class is used for retrieving data from online sources, notably by storing relevant API keys and making requests
 */
public class InternetDriver {
    /**
     * Our Google API key, should only be accessed with this class, hense the private modifier
     */
    private String google_api_key;

    /**
     * A special embedded constant, for debugging, to stop the program should the number of queries exceed a reasonable amount
     */
    private final static int MAX_ELEVATION_QUERIES = 0;
    /**
     * A special debugging value to inform a programmer how many queries they used in elevation
     */
    private static int num_queries_elevation = 0;

    /**
     * Construct this class using a provided Google API key from somewhere else in the program
     * @param google_key Our Google Cloud API Key
     */
    public InternetDriver(String google_key){
        google_api_key = google_key;
    }

    /**
     * Construct this class by reading our APIs keys from specific files. This is a more secure way of doing things
     * since the APY key only touches this class within a private context.
     * This class should read from:
     * .google_api_key = google cloud API key
     */
    public InternetDriver() throws FileNotFoundException {
        // Read the Google API key
        FileReader google_reader = new FileReader(".google_api_key");
        Scanner google_scanner = new Scanner(google_reader);

        String google_key = google_scanner.next();

        // Throw an error if the key is not a reasonable length
        if (google_key.length() < 8){
            throw new FileNotFoundException("Found key was not a reasonable length");
        }

        google_api_key = google_key;
    }

    /**
     * Read the raw JSON output from the Google Elevation API
     * It's important to query as many cords as possible at once as there is some latency in the response
     * @param cords A Hashmap of latitude and longitude coordinates (Must have less than 512 values)
     * @return The raw JSON from Google's server
     * @throws java.security.InvalidParameterException Thrown if cords has more than 512 key value pairs (A limit from Google's API)
     */
    public String getElevation(HashMap<String, String> cords){
        if (cords.size() > 512){
            throw new InvalidParameterException("Too many coordinates provided!");
        }

        if (num_queries_elevation + cords.size() > MAX_ELEVATION_QUERIES){
            System.out.printf("[WARNING!!!!!!!!] There has been %d elevation queries from a limit of %d which, " +
                    "after this call will become %d queries!!!!!\n",
                    num_queries_elevation, MAX_ELEVATION_QUERIES, num_queries_elevation + cords.size());

            System.out.println("To continue, enter \"continue\" into the prompt below or \"quit\" to quit!");

            String input = "";
            Scanner scn = new Scanner(System.in);
            while (!input.equalsIgnoreCase("continue") && !input.equalsIgnoreCase("quit")){
                input = scn.next();
                // Skip to next line
                scn.nextLine();
            }

            if (input.equalsIgnoreCase("quit")){
                System.out.println("Exiting due to too many elevation queries!");
                System.exit(0);
            }
        }
        // Google defines a list of cords as "[Lat1],[Long1]|[Lat2],[Long2]|...[LatN],[LongN}"
        StringJoiner parameter_generation = new StringJoiner("|");
        for (Map.Entry<String, String> value: cords.entrySet()){
            parameter_generation.add(String.format("%s,%s", value.getKey(), value.getValue()));
        }

        // Construct the RESTful API request
        String built_url = String.format("https://maps.googleapis.com/maps/api/elevation/json?locations=%s&key=%s",
                parameter_generation.toString(), google_api_key);

        return ReadFromUrl(built_url);
    }

    /**
     * Read JSON from a URL
     * @param targetURL URL to read from
     * @return The request body
     */
    private static String ReadFromUrl(String targetURL){
        // Create URL
        URI target_url = null;
        try {
            target_url = URI.create(targetURL);
        } catch (IllegalArgumentException e) {
            System.out.println("Malformed URL. This is most likely caused by the API key being malformed!");
            throw new RuntimeException("URL error, perhaps due to malformed API key!");
        }

        // Construct HTTP Request
        HttpRequest req = HttpRequest.newBuilder()
                .uri(target_url)
                .GET()
                .build();

        // Send the request
        HttpResponse<String> response = null;
        try {
            HttpClient client = HttpClient.newHttpClient();

            response = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (UncheckedIOException | InterruptedException | IOException e) {
            System.out.println("Failed to read HTTP response");
            throw new RuntimeException(e);
        }

        return response.body();
    }
}
