package com.ndg.map.areas.operations;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Implements the general methods from MapAreaOperations and MapAreaOperations3D
 *
 * @param <E> Type of object held at each cell
 */
public class MapAreaOperations3DImpl<E> extends MapAreaOperationsImpl<E, MapCoordinates3DEx> implements MapAreaOperations3D<E>
{
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	@Override
	public final void processAllCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates3DEx> callback)
	{
		getCoordinateSystemUtils ().processAll3DCoordinates (sys, callback);
	}

	/**
	 * Triggers a callback on every set of coordinates within a certain radius
	 * Note this will trigger the same radius in however many planes there are
	 * 
	 * @param sys Coordinate system to trace around
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 * @param callback Operation to call against every set of coordinates within the specified radius
	 */
	@Override
	public final void processCoordinatesWithinRadius (final CoordinateSystem sys, final int x, final int y, final int radius,
		final ProcessMapCoordinates<MapCoordinates3DEx> callback)
	{
		for (int z = 0; z < sys.getDepth (); z++)
		{
			final int pz = z;
			getCoordinateSystemUtils ().processCoordinatesWithinRadius (sys, x, y, radius, (px, py, r, d, n) ->
			{
				callback.process (new MapCoordinates3DEx (px, py, pz));
				return true;
			});
		}
	}
}