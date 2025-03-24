/*
 * Copyright (c) 2025.
 * Created by Simon Borghese for CS 220
 * File created on 3/23/2025
 */

package Data;

import java.security.InvalidParameterException;
import Math.Vector;

/**
 * This class provides a representation of coordinates within the World
 * This can be created either from Latitude and Longitude or from Google's XYZ system to then be converted between them
 * Internally these are stored as latitude and longitude as that's a more usable format to the user
 */
public class WorldCoordinate {
    // The lat and longitude values stores internally
    private double latitude;
    private double longitude;

    /**
     * Construct a world coordinate based on a provided, real, latitude and longitude value
     * @param lat Provided, valid Latitude
     * @param lng Provided, valid Longitude
     * @throws java.security.InvalidParameterException If the latitude and longitude values are not valid for Earth
     */
    public WorldCoordinate(double lat, double lng){
        if (lat < -90.0 || lat > 90.0){
            throw new InvalidParameterException("Range check for latitude failed!");
        }
        if (lng < -180.0 || lng > 180.0){
            throw new InvalidParameterException("Range check for longitude failed!");
        }
        latitude = lat;
        longitude = lng;
    }

    /**
     * Construct a default world coordinate from a reasonable, recognizable world coordinate
     */
    public WorldCoordinate(){
        // Denver Colorado as provided by the Google API example
        this(39.7391536, -104.9847034);
    }

    /**
     * Convert this coordinate to a point used for the Tile API
     * FROM GOOGLE'S DOCS: https://developers.google.com/maps/documentation/tile/2d-tiles-overview
     * @return A Vector representing the point
     */
    public Vector toPoint(double tileSize){
        double mercator = -Math.log(Math.tan((0.25 + latitude / 360.0) * Math.PI));
        double x = tileSize * (longitude / 360.0 + 0.5);
        double y = tileSize / 2 * (1 + mercator / Math.PI);
        return new Vector(x, y);
    }

    /**
     * Convert this coordinate to a tile cord used for the Tile API
     * FROM GOOGLE'S DOCS: https://developers.google.com/maps/documentation/tile/2d-tiles-overview
     * @return A Vector representing the tile cord where Z is zoom
     */
    public Vector toPoint(double tileSize, double zoom){
        Vector point = toPoint(tileSize);
        double scale = Math.pow(2, zoom);

        double x = Math.floor(point.getX() * scale / tileSize);
        double y = Math.floor(point.getY() * scale / tileSize);

        return new Vector(x, y, zoom);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double newLat){
        latitude = newLat;
    }

    public void setLongitude(double newLng){
        longitude = newLng;
    }
}
