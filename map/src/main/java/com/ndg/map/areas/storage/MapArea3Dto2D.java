package com.ndg.map.areas.storage;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Treats a 3D volume like a 2D area by selecting one plane at a time.
 * This is so that complex 2D methods like .enlarge () don't have to be duplicated in their 3D equivalents. 
 *
 * @param <E> Type of object held at each cell
 */
public final class MapArea3Dto2D<E> implements MapArea2D<E>
{
	/** Underlying 3D storage */
	private MapArea<E, MapCoordinates3DEx> storage;
	
	/** Currently seleted plane */
	private int z;

	/**
	 * @return Coordinate system of the map area
	 */
	@Override
	public final CoordinateSystem getCoordinateSystem ()
	{
		return getStorage ().getCoordinateSystem ();
	}

	/**
	 * The effect of altering the coordinate system is implementation specific, but typically results in losing all existing values
	 * @param sys Coordinate system of the map area
	 */
	@Override
	public final void setCoordinateSystem (final CoordinateSystem sys)
	{
		getStorage ().setCoordinateSystem (sys);
	}
	
	/**
	 * @param x X coordinate of cell to read
	 * @param y Y coordinate of cell to read
	 * @return Value held at this cell
	 */
	@Override
	public final E get (final int x, final int y)
	{
		return getStorage ().get (new MapCoordinates3DEx (x, y, getZ ()));
	}

	/**
	 * @param x X coordinate of cell to set
	 * @param y Y coordinate of cell to set
	 * @param value Value to write to this cell
	 */
	@Override
	public final void set (final int x, final int y, final E value)
	{
		getStorage ().set (new MapCoordinates3DEx (x, y, getZ ()), value);
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
	 * @return Underlying 3D storage
	 */
	public final MapArea<E, MapCoordinates3DEx> getStorage ()
	{
		return storage;
	}

	/**
	 * @param stor Underlying 3D storage
	 */
	public final void setStorage (final MapArea<E, MapCoordinates3DEx> stor)
	{
		storage = stor;
	}
	
	/**
	 * @return Currently seleted plane
	 */
	public final int getZ ()
	{
		return z;
	}

	/**
	 * @param newZ Currently seleted plane
	 */
	public final void setZ (final int newZ)
	{
		z = newZ;
	}
}
