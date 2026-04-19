package com.ndg.map;

import com.ndg.map.areas.operations.ProcessMapCoordinates;
import com.ndg.map.areas.operations.ProcessRadialMapCoordinates;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Helper methods for working with coordinate systems.
 */
public interface CoordinateSystemUtils
{
	/**
	 * @param sys Coordinate system
	 * @param x X coordinate to test
	 * @param y Y coordinate to test
	 * @return True if these 2D coordinates are within the coordinate system
	 */
	public boolean are2DCoordinatesWithinRange (final CoordinateSystem sys, final int x, final int y);

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to test
	 * @return True if these 2D coordinates are within the coordinate system
	 */
	public boolean are2DCoordinatesWithinRange (final CoordinateSystem sys, final MapCoordinates2DEx coords);

	/**
	 * @param sys Coordinate system
	 * @param x X coordinate to test
	 * @param y Y coordinate to test
	 * @param z Z coordinate to test
	 * @return True if these 3D coordinates are within the coordinate system
	 */
	public boolean are3DCoordinatesWithinRange (final CoordinateSystem sys, final int x, final int y, final int z);

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to test
	 * @return True if these 3D coordinates are within the coordinate system
	 */
	public boolean are3DCoordinatesWithinRange (final CoordinateSystem sys, final MapCoordinates3DEx coords);

	/**
	 * @param st Coordinate system type
	 * @return Maximum valid direction for this coordinate system
	 */
	public int getMaxDirection (final CoordinateSystemType st);

	/**
	 * @param st Coordinate system type
	 * @param direction Direction in this coordinate system
	 * @return Direction brought into the range 1..maxDirection
	 */
	public int normalizeDirection (final CoordinateSystemType st, final int direction);

	/**
	 * @param value Base value
	 * @param max Width/Height/Depth as appropriate
	 * @return Value shifted to the range 0 < value <= max
	 */
	public int normalizeCoordinate (final int value, final int max);
	
	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to move from - the updated coordinates will be written to here
	 * @param d Direction to move in
	 * @return whether the resulting coordinates are still within the range of the coordinate system or not
	 */
	public boolean move2DCoordinates (final CoordinateSystem sys, final MapCoordinates2DEx coords, final int d);

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to move from - the updated coordinates will be written to here; the Z component will be left untouched
	 * @param d Direction to move in
	 * @return whether the resulting coordinates are still within the range of the coordinate system or not
	 */
	public boolean move3DCoordinates (final CoordinateSystem sys, final MapCoordinates3DEx coords, final int d);

	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	public void processAll2DCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates2DEx> callback);
	
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	public void processAll3DCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates3DEx> callback);
	
	/**
	 * Triggers a callback on every set of coordinates within a certain radius
	 * 
	 * @param sys Coordinate system to trace around
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on); Integer.MAX_VALUE will traverse the entire map 
	 * @param callback Operation to call against every set of coordinates within the specified radius
	 */
	public void processCoordinatesWithinRadius (final CoordinateSystem sys, final int x, final int y, final int radius, final ProcessRadialMapCoordinates callback);
	
	/**
	 * @param sys Coordinate system
	 * @param from Coordinates of starting point
	 * @param to Coordinates of destination point
	 * @return Best approximation of direction between two locations
	 */
	public int determineDirectionTo (final CoordinateSystem sys, final MapCoordinates2DEx from, final MapCoordinates2DEx to);

	/**
	 * @param sys Coordinate system
	 * @param fromX X coordinate of starting point
	 * @param fromY Y coordinate of starting point
	 * @param toX X coordinate of destination point
	 * @param toY Y coordinate of destination point
	 * @return Best approximation of direction between two locations
	 */
	public int determineDirectionTo (final CoordinateSystem sys, final int fromX, final int fromY, final int toX, final int toY);

	/**
	 * @param sys Coordinate system
	 * Deals correctly with if the shorter distance is to go via the wrapping edge
	 * @param x1 First x coordinate
	 * @param x2 Second x coordinate
	 * @return Absolute distance between two X coordinates
	 */
	public int findDistanceBetweenXCoordinates (final CoordinateSystem sys, final int x1, final int x2);

	/**
	 * @param sys Coordinate system
	 * Deals correctly with if the shorter distance is to go via the wrapping edge
	 * @param y1 First y coordinate
	 * @param y2 Second y coordinate
	 * @return Absolute distance between two Y coordinates
	 */
	public int findDistanceBetweenYCoordinates (final CoordinateSystem sys, final int y1, final int y2);

	/**
	 * @param sys Coordinate system
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @return Number of steps required to move between two map cells
	 */
	public int determineStep2DDistanceBetween (final CoordinateSystem sys, final int x1, final int y1, final int x2, final int y2);

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Number of steps required to move between two map cells
	 */
	public int determineStep2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates2DEx first, final MapCoordinates2DEx second);

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Number of steps required to move between two map cells; NB. Although this passes in 3D coords, the Z-component is ignored - the distance is calc'd in the 2D plane
	 */
	public int determineStep2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates3DEx first, final MapCoordinates3DEx second);
	
	/**
	 * @param sys Coordinate system
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @return Distance ("as the crow flies") between two sets of coordinates
	 */
	public double determineReal2DDistanceBetween (final CoordinateSystem sys, final int x1, final int y1, final int x2, final int y2);

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Distance ("as the crow flies") between two sets of coordinates
	 */
	public double determineReal2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates2DEx first, final MapCoordinates2DEx second);

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Distance ("as the crow flies") between two sets of coordinates; NB. Although this passes in 3D coords, the Z-component is ignored - the distance is calc'd in the 2D plane
	 */
	public double determineReal2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates3DEx first, final MapCoordinates3DEx second);
}