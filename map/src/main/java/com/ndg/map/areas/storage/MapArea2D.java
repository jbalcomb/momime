package com.ndg.map.areas.storage;

import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Map storage interface for a 2D area and coordinate system
 *
 * @param <E> Type of object held at each cell
 */
public interface MapArea2D<E> extends MapArea<E, MapCoordinates2DEx>
{
	/**
	 * @param x X coordinate of cell to read
	 * @param y Y coordinate of cell to read
	 * @return Value held at this cell
	 */
	public E get (final int x, final int y);

	/**
	 * @param x X coordinate of cell to set
	 * @param y Y coordinate of cell to set
	 * @param value Value to write to this cell
	 */
	public void set (final int x, final int y, final E value);
}
