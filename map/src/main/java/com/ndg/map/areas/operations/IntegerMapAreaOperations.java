package com.ndg.map.areas.operations;

import com.ndg.map.areas.storage.MapArea;

/**
 * Adds methods to operations interface that are appropraite for integers  
 *
 * @param <C> Coordinates storage object
 */
public interface IntegerMapAreaOperations<C> extends MapAreaOperations<Integer, C>
{
	/**
	 * @param area Map area to write to
	 * @param value Value to ensure every map cell is at least this
	 */
	public void setAllToMinimum (final MapArea<Integer, C> area, final int value);
	
	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to select
	 * @param y Y coordinate of centre of area to select
	 * @param radius Radius to select (negative = do nothing, 0 = enforce minimum on this cell only, 1 = 1 ring around this cell, and so on)
	 * @param value Value to ensure every map cell within the designated area is at least this
	 */
	public void setMinimumWithinRadius (final MapArea<Integer, C> area, final int x, final int y, final int radius, final int value);
}