/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;

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
}
