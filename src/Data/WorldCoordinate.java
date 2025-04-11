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
    /**
     * A vector representing the provided longitude and latitude coordinates
     */
    private Vector latlng;

    /**
     * A vector representing the potentially provided tile coordinates
     */
    private Vector coordinates;

    /**
     * Construct a world coordinate based on a provided, real, latitude and longitude value
     * @param lat Provided, valid Latitude
     * @param lng Provided, valid Longitude
     * @param tile_size Tile size for derived tile
     * @param zoom The zoom for the derived tile
     * @throws java.security.InvalidParameterException If the latitude and longitude values are not valid for Earth
     */
    public WorldCoordinate(double lat, double lng, int tile_size, int zoom){
        if (lat < -90.0 || lat > 90.0){
            throw new InvalidParameterException("Range check for latitude failed!");
        }
        if (lng < -180.0 || lng > 180.0){
            throw new InvalidParameterException("Range check for longitude failed!");
        }
        latlng = new Vector(lat,lng);
        coordinates = convertGenericPointToTile(convertWorldToGenericPoint(lat,lng), tile_size, zoom);

    }

    /**
     * Construct a world coordinate based on a provided, real, latitude and longitude values with
     * generic tile size and zoom
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
        latlng = new Vector(lat,lng);
        coordinates = convertGenericPointToTile(convertWorldToGenericPoint(lat,lng), 256, 15);
    }

    /**
     * Construct a world coordinate using a tile.
     * @param x The x position of the tile
     * @param y THe y position of the tile
     * @param zoom The zoom provided to generate the tile
     */
    public WorldCoordinate(double x, double y, double zoom){
        coordinates = new Vector(x,y,zoom);
        latlng = convertTileToWorld(coordinates);

    }

    /**
     * Construct a default world coordinate from a reasonable, recognizable world coordinate
     */
    public WorldCoordinate(){
        // Denver Colorado as provided by the Google API example
        // At a tile size of 256 and zoom 15
        this(39.7391536, -104.9847034, 256, 15);
    }

    /**
     * Convert Latitude and Longitude coordinates to a point WITHOUT consideration of tile size.
     * Must be multiplied by tile size to become an actual point
     * @param lat The latitude coordinates, in the range -90, 90
     * @param lng The longitude coordinates, in the range -180, 180
     * @throws InvalidParameterException If lat or lng are not within valid ranges
     */
    private static Vector convertWorldToGenericPoint(double lat, double lng){
        double mercator = -Math.log(Math.tan((0.25 + lat / 360.0) * Math.PI));
        double x = 1 * (lng / 360.0 + 0.5);
        double y = (1.0 / 2.0) * (1 + mercator / Math.PI);
        return new Vector(x, y,0);
    }

    /**
     * Convert a generic point to a specific tile
     * @param point A "generic" point
     * @param tile_size The tile size, usually 256
     * @param zoom The level of zoom
     * @return A vector for the specific tile, Z is zoom
     */
    private static Vector convertGenericPointToTile(Vector point, int tile_size, int zoom){
        Vector out_point = new Vector(point.getX(), point.getY(), point.getZ());
        out_point.setX(point.getX() * tile_size);
        out_point.setY(point.getY() * tile_size);

        double scale = Math.pow(2, zoom);

        double x = ((out_point.getX() * scale) / tile_size);
        double y = ((out_point.getY() * scale) / tile_size);

        return new Vector(x,y,zoom);
    }

    /**
     * Convert a tile to a latitude and longitude
     * @param tile The vector of the tile, z is zoom
     * @throws InvalidParameterException If the tile doesn't result in a latitude or longitude
     * @return The latitude and longitude, z is zero
     */
    private static Vector convertTileToWorld(Vector tile){
        double s = Math.pow(2.0, tile.getZ());

        double latitude =
                360 * (-0.25 + ((1/Math.PI) * (Math.atan(Math.pow(Math.E, -(Math.PI * (((tile.getY()*2)/s)-1)))))));

        double longitude =
                360 * (-0.5 + (tile.getX()/s));

        if (latitude < -90.0 || latitude > 90.0){
            throw new InvalidParameterException("Generated Tile doesn't exist in the X axis!");
        }
        if (longitude < -180.0 || longitude > 180.0){
            throw new InvalidParameterException("Generated Tile doesn't exist in the Y axis!");
        }

        return new Vector(latitude, longitude, 0);
    }

    /**
     * Get either the provided world coordinate or generated one. Guaranteed for at least one to exist
     * @return A vector containing the world coordinates, x = latitude, y = longitude,z = 0
     */
    public Vector getWorldCoordinate(){
        return latlng;
    }

    /**
     * Get either the provided tile coordinates or the generated ones, Guaranteed for at least one to exist.
     * @return A Vector containing the tile coordinates, z = zoom
     */
    public Vector getTile(){
        return coordinates;
    }
}
