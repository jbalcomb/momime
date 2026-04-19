package com.ndg.map.coordinates;

import com.ndg.map.MapCoordinates2D;

/**
 * Provides .equals () and .toString () for CombatMapCoordinates
 */
public final class MapCoordinates2DEx extends MapCoordinates2D
{
	/**
	 * This needs to be here so Spring and JAXB can create coordinates and then set their values via the set methods,
	 * but shouldn't be used by hand written code since you'll always want to set the coordinates to some initial value,
	 * so set as deprecated so that any use of it throws up a warning
	 */
	@Deprecated
	public MapCoordinates2DEx ()
	{
		super ();
	}

	/**
	 * Duplicates a set of existing coordinates, so one can be modified without affecting the other
	 * @param source Coordinates to copy
	 */
	public MapCoordinates2DEx (final MapCoordinates2DEx source)
	{
		super ();
		setX (source.getX ());
		setY (source.getY ());
	}

	/**
	 * @param ax X coordinate value
	 * @param ay Y coordinate value
	 */
	public MapCoordinates2DEx (final int ax, final int ay)
	{
		super ();
		setX (ax);
		setY (ay);
	}
	
	/**
	 * @return String representation of coordinates 
	 */
	@Override
	public final String toString ()
	{
		return "(" + getX () + ", " + getY () + ")";
	}

	/**
	 * @return Whether the two sets of coordinates are the same
	 */
	@Override
	public final boolean equals (final Object obj)
	{
		if (obj instanceof MapCoordinates2DEx)
		{
			final MapCoordinates2DEx coords = (MapCoordinates2DEx) obj;
			return ((getX () == coords.getX ()) && (getY () == coords.getY ()));
		}
		else
			return super.equals (obj);
	}

	/**
	 * @return Hash computed from field values
	 */
	@Override
	public final int hashCode ()
	{
		// This hash assumes coordinate values will typically be < 65,536
		return (getY () << 16) + getX ();
	}
}