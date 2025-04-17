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
        latlng = new Vector(lat,lng, 0);
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
        latlng = new Vector(lat,lng, 0);
        coordinates = convertWorldToGenericPoint(lat,lng);
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
     * Convert the tile coordinate, interpreted as a generic point when only latitude and longitude are provided,
     * to a proper 2d tile
     */
    public void makeTileFromGeneric(int tile_size, int zoom){
        coordinates = convertGenericPointToTile(coordinates, tile_size, zoom);
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
                360 * (-0.25 + ((1/Math.PI) * (Math.atan(Math.pow(Math.E, -(Math.PI * (((tile.getX()*2)/s)-1)))))));

        double longitude =
                360 * (-0.5 + (tile.getY()/s));

        if (latitude < -90.0 || latitude > 90.0){
            throw new InvalidParameterException("Generated Tile doesn't exist in the X axis!");
        }
        if (longitude < -180.0 || longitude > 180.0){
            throw new InvalidParameterException("Generated Tile doesn't exist in the Y axis!");
        }

        return new Vector(latitude, longitude, 0);
    }

    /**
     * Find the bounds of this tile, this will throw nonsense values if this coordinate doesn't have a tile
     * configured with a zoom.
     * @return The extent of this tile, x = distance, in latitude, from the center of the tile to the edge, y =
     * distance, in longitude, from the center of the tile to the edge. Z = 0
     */
    public Vector findBounds(){
        Vector tile = getTile();
        Vector world_coordinate = getWorldCoordinate();
        // The difference between the center latitudes of this and the right adjacent tile, divided by 2 to get radius
        double lat = (convertTileToWorld(new Vector(tile.getX() + 1, tile.getY(), tile.getZ())).getX()
                - world_coordinate.getX()) / 2.0;

        double lng = (convertTileToWorld(new Vector(tile.getX(), tile.getY()+1, tile.getZ())).getY()
                - world_coordinate.getY()) / 2.0;

        return new Vector(lat, lng, 0);
    }

    /**
     * This recursive method determines the offset from the center of a grid of tiles for a zoom level.
     * This is necessary because latitude and longitude are not linear, a tile of zoom and a larger tile of zoom-1 will
     * NOT align if the tile of zoom is placed at the center. It must be offset. When three or more tiles are used,
     * offsets must also be applied to lower levels of tiles. This method takes the current world coordinate and finds
     * where a tile of the same position but of zoom-1 would be offset to align. The method is recursive, starting
     * at the provided max zoom which will be the outermost tile and move until the desired zoom is reached. If the max
     * and target zoom are the same, the offset is zero because no offset is needed for those tiles to align.
     * @param current_offset The current offset of the zoom level. If you're starting from the max zoom, this should be
     *                       a zero vector. This is used recursively to add the zoom offsets from the previously
     *                       applied offsets.
     * @param max_zoom This integer is the maximum zoom out. This is static and does not change in recursive calls
     *                 and also determines the base case. This can be any value and the result of this method will
     *                 have a physical meaning but not necessarily a valid input to google's API. If max_zoom >=
     *                 current_zoom then this method will return a zero offset.
     * @param current_zoom The current zoom level to calculate the offset for. If this zoom level is equal to max_zoom
     *                     +1 then this method will perform the calculation and return without performing recursion.
     *                     Otherwise, the result will become equal to:
     *                     determineZoomOffset(determineZoomOffset(current_offset, current_zoom - 1, current_zoom),
     *                     max_zoom, current_zoom - 1)
     *                     Such that the result will return, recursively, the zoom offset with current zoom - 1 as
     *                     the current zoom and the current offset being the zoom offset from current_zoom - 1 and
     *                     current zoom.
     */
    public Vector determineZoomOffset(Vector current_offset, int max_zoom, int current_zoom){
        Vector offset = new Vector();
        System.out.printf("Zooms: %d %d\n", max_zoom, current_zoom);
        if (max_zoom < current_zoom){
            System.out.printf("Zoom diff: %d\n", current_zoom - max_zoom);
            if (Math.abs(current_zoom - max_zoom) == 1){
                WorldCoordinate base_zoom = new WorldCoordinate(latlng.getX(), latlng.getY(), 256, max_zoom);
                WorldCoordinate inner_zoom = new WorldCoordinate(latlng.getX(), latlng.getY(), 256, current_zoom);

                Vector inner_bounds_extents = inner_zoom.findBounds();

                // The latitude and longitude at the right and top of the tile
                Vector inner_bounds = new Vector(
                        latlng.getX() + inner_bounds_extents.getX(),
                        latlng.getY() + inner_bounds_extents.getY(),
                        0.0
                );

                Vector outer_bounds_extents = base_zoom.findBounds();

                // The latitude and longitude at the right and top of the tile
                Vector outer_bounds = new Vector(
                        latlng.getX() + outer_bounds_extents.getX(),
                        latlng.getY() + outer_bounds_extents.getY(),
                        0.0
                );



                offset.setX(
                        (outer_bounds_extents.getX() - inner_bounds_extents.getX()) + current_offset.getX()
                );

                offset.setY(
                        (outer_bounds_extents.getY() - inner_bounds_extents.getY()) + current_offset.getY()
                );
            } else{
                offset = determineZoomOffset(
                        determineZoomOffset(current_offset, current_zoom - 1, current_zoom),
                        max_zoom, current_zoom - 1
                );
            }
        }
        return offset;
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
