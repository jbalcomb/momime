package com.ndg.map.areas.operations;

import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Covers all operations over integer 2D areas
 */
public interface IntegerMapAreaOperations2D extends IntegerMapAreaOperations<MapCoordinates2DEx>, MapAreaOperations2D<Integer>
{
	/**
	 * Adds a value to each map cell which is the distance of that map cell from the specified coordinates
	 * 
	 * @param area Map area to write to
	 * @param start Coordinates to measure distance from
	 */
	public void addDistancesFrom (final MapArea<Integer, MapCoordinates2DEx> area, final MapCoordinates2DEx start);
}