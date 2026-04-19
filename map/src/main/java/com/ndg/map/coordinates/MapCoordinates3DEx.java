package com.ndg.map.coordinates;

import com.ndg.map.MapCoordinates3D;

/**
 * Provides .equals () and .toString () for OverlandMapCoordinates
 */
public final class MapCoordinates3DEx extends MapCoordinates3D
{
	/**
	 * This needs to be here so Spring and JAXB can create coordinates and then set their values via the set methods,
	 * but shouldn't be used by hand written code since you'll always want to set the coordinates to some initial value,
	 * so set as deprecated so that any use of it throws up a warning
	 */
	@Deprecated
	public MapCoordinates3DEx ()
	{
		super ();
	}

	/**
	 * Duplicates a set of existing coordinates, so one can be modified without affecting the other
	 * @param source Coordinates to copy
	 */
	public MapCoordinates3DEx (final MapCoordinates3DEx source)
	{
		super ();
		setX (source.getX ());
		setY (source.getY ());
		setZ (source.getZ ());
	}

	/**
	 * @param ax X coordinate value
	 * @param ay Y coordinate value
	 * @param az Z coordinate value
	 */
	public MapCoordinates3DEx (final int ax, final int ay, final int az)
	{
		super ();
		setX (ax);
		setY (ay);
		setZ (az);
	}
	
	/**
	 * @return String representation of coordinates 
	 */
	@Override
	public final String toString ()
	{
		return "(" + getX () + ", " + getY () + ", " + getZ () + ")";
	}

	/**
	 * @return Whether the two sets of coordinates are the same
	 */
	@Override
	public final boolean equals (final Object obj)
	{
		if (obj instanceof MapCoordinates3DEx)
		{
			final MapCoordinates3DEx coords = (MapCoordinates3DEx) obj;
			return ((getX () == coords.getX ()) && (getY () == coords.getY ()) && (getZ () == coords.getZ ()));
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
		// This hash assumes coordinate values will typically be < 1,024
		return (getZ () << 20) + (getY () << 10) + getX ();
	}
}