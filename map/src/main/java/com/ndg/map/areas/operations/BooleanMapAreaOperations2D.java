package com.ndg.map.areas.operations;

import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Covers all operations over boolean 2D areas
 */
public interface BooleanMapAreaOperations2D extends BooleanMapAreaOperations<MapCoordinates2DEx>, MapAreaOperations2D<Boolean>
{
	/**
	 * @param area Area to search over
	 * @param coords Coords of centre to scan out from
	 * @return Coordinates of selected cell closest to specified location, or null if no cell in the entire area is selected
	 */
	public MapCoordinates2DEx findClosestSelectedCell (final MapArea2D<Boolean> area, final MapCoordinates2DEx coords);
}