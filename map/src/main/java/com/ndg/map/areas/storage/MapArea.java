package com.ndg.map.areas.storage;

import com.ndg.map.CoordinateSystem;

/**
 * Interface that covers reading and writing to map areas, without containing any logic or operations.  Previous implementation was an abstract
 * class which failed in the situation for wanting to e.g. call .enlarge () on a 2D boolean area backed by a structure generated from XSDs - in that
 * it was impossible to extend from both the XSD generated class and the processing abstract class.  So have now split storage and operations
 * into separate interfaces and classes.  
 *
 * @param <E> Type of object held at each cell
 * @param <C> Coordinates storage object
 */
public interface MapArea<E, C>
{
	/**
	 * @return Coordinate system of the map area
	 */
	public CoordinateSystem getCoordinateSystem ();

	/**
	 * The effect of altering the coordinate system is implementation specific, but typically results in losing all existing values
	 * @param sys Coordinate system of the map area
	 */
	public void setCoordinateSystem (final CoordinateSystem sys);
	
	/**
	 * @param coords Coordinates of cell to read
	 * @return Value held at this cell
	 */
	public E get (final C coords);

	/**
	 * @param coords Coordinates of cell to set
	 * @param value Value to write to this cell
	 */
	public void set (final C coords, final E value);
}
