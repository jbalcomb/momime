package com.ndg.map.areas.storage;

import java.util.ArrayList;
import java.util.List;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Implementation of 2D map area storage that is backed by an ArrayList (since you can create lists of generics, but not arrays of generics)
 *
 * @param <E> Type of object held at each cell
 */
public class MapArea2DArrayListImpl<E> implements MapArea2D<E>
{
	/** Coordinate system of the map area */
	private CoordinateSystem coordinateSystem;
	
	/** Storage array */
	private List<List<E>> area;

	/**
	 * @return Coordinate system of the map area
	 */
	@Override
	public final CoordinateSystem getCoordinateSystem ()
	{
		return coordinateSystem;
	}

	/**
	 * Note altering the coordinate system loses an existing values
	 * @param sys Coordinate system of the map area
	 */
	@Override
	public final void setCoordinateSystem (final CoordinateSystem sys)
	{
		coordinateSystem = sys;
		
		// Create empty area of nulls
		area = new ArrayList<List<E>> (sys.getHeight ());
		for (int y = 0; y < sys.getHeight (); y++)
		{
			final List<E> row = new ArrayList<E> (sys.getWidth ());
			for (int x = 0; x < sys.getWidth (); x++)
				row.add (null);
			
			area.add (row);
		}
	}

	/**
	 * @param coords Coordinates of cell to read
	 * @return Value held at this cell
	 */
	@Override
	public final E get (final MapCoordinates2DEx coords)
	{
		return get (coords.getX (), coords.getY ());
	}

	/**
	 * @param coords Coordinates of cell to set
	 * @param value Value to write to this cell
	 */
	@Override
	public final void set (final MapCoordinates2DEx coords, final E value)
	{
		set (coords.getX (), coords.getY (), value);
	}

	/**
	 * @param x X coordinate of cell to read
	 * @param y Y coordinate of cell to read
	 * @return Value held at this cell
	 */
	@Override
	public final E get (final int x, final int y)
	{
		return area.get (y).get (x);
	}

	/**
	 * @param x X coordinate of cell to set
	 * @param y Y coordinate of cell to set
	 * @param value Value to write to this cell
	 */
	@Override
	public final void set (final int x, final int y, final E value)
	{
		area.get (y).set (x, value);
	}
}
