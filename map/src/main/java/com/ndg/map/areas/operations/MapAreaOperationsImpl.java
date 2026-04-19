package com.ndg.map.areas.operations;

import java.util.ArrayList;
import java.util.List;

import com.ndg.map.CoordinateSystemUtils;
import com.ndg.map.areas.storage.MapArea;
import com.ndg.utils.Holder;
import com.ndg.utils.random.RandomUtils;

/**
 * Common storage for the random number generator and coordinate system utils, since both 2D and 3D implementations need it
 * 
 * @param <E> Type of object held at each cell
 * @param <C> Coordinates storage object
 */
public abstract class MapAreaOperationsImpl<E, C> implements MapAreaOperations<E, C>
{
	/** Coordinate system utils */
	private CoordinateSystemUtils coordinateSystemUtils;

	/** Random number generator */
	private RandomUtils randomUtils;

	/**
	 * @param area Map area to process
	 * @param callback Operation to call against every map cell in the map area
	 */
	@Override
	public final void processAllCells (final MapArea<E, C> area, final ProcessMapCell<E> callback)
	{
		processAllCoordinates (area.getCoordinateSystem (), (coords) ->
		{
			return callback.process (area.get (coords));
		});
	}
	
	/**
	 * @param area Map area to write to
	 * @param value Value to set all cells equal to
	 */
	@Override
	public final void setAll (final MapArea<E, C> area, final E value)
	{
		processAllCoordinates (area.getCoordinateSystem (), (coords) ->
		{
			area.set (coords, value);
			return true;
		});
	}

	/**
	 * Note, this directly copies object references so is only really safe on immutable types such as Boolean, Integer or String
	 * @param from Map area to copy cell values from
	 * @param to Map area to copy cell values to
	 */
	@Override
	public final void copy (final MapArea<E, C> from, final MapArea<E, C> to)
	{
		if (from.getCoordinateSystem () != to.getCoordinateSystem ())
			throw new IllegalArgumentException ("To copy values from one 2D map area to another, they must be based on the same coordinate system");

		processAllCoordinates (from.getCoordinateSystem (), (coords) ->
		{
			to.set (coords, from.get (coords));
			return true;
		});
	}
	
    /**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Number of cells in the map area that have the requested value
     */
    @Override
    public final int countCellsEqualTo (final MapArea<E, C> area, final E value)
    {
		final Holder<Integer> count = new Holder<Integer> (0);
    	processAllCells (area, (thisValue) ->
    	{
			if (((value == null) && (thisValue == null)) || ((value != null) && (value.equals (thisValue))))
				count.setValue (count.getValue () + 1);
			
			return true;
		});
		
		return count.getValue ();
    }
	
	/**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Randomly chosen cell with this value (compared using the .equals method), or null if there are none
     */
    @Override
    public final C findRandomCellEqualTo (final MapArea<E, C> area, final E value)
    {
    	// List all matching locations
		final List<C> possibleLocations = new ArrayList<C> ();
		processAllCoordinates (area.getCoordinateSystem (), (coords) ->
		{
			final E thisValue = area.get (coords);
			if (((value == null) && (thisValue == null)) || ((value != null) && (value.equals (thisValue))))
				possibleLocations.add (coords);
			
			return true;
		});
		
		// Pick a random location
		C location = null;
		if (possibleLocations.size () > 0)
			location = possibleLocations.get (getRandomUtils ().nextInt (possibleLocations.size ()));

		return location;
    }
    
    /**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Whether any cell in the map area has the requested  value
     */
    @Override
    public final boolean anyCellEqualTo (final MapArea<E, C> area, final E value)
    {
		final Holder<Boolean> found = new Holder<Boolean> (false);
    	processAllCells (area, (thisValue) ->
    	{
			if (((value == null) && (thisValue == null)) || ((value != null) && (value.equals (thisValue))))
				found.setValue (true);
			
			return !found.getValue ();
		});
		
		return found.getValue ();
    }
    
	/**
	 * Triggers a callback on all map cells within a certain radius
	 * 
	 * @param area Map area to process
	 * @param x X coordinate of centre of area to process
	 * @param y Y coordinate of centre of area to process
	 * @param radius Radius to process (negative = process nothing, 0 = process this cell only, 1 = 1 ring around this cell, and so on)
	 * @param callback Operation to call against every cell within the specified radius
	 */
    @Override
	public final void processCellsWithinRadius (final MapArea<E, C> area, final int x, final int y, final int radius, final ProcessMapCell<E> callback)
	{
		processCoordinatesWithinRadius (area.getCoordinateSystem (), x, y, radius, (coords) ->
		{
			return callback.process (area.get (coords));
		});
	}
	
	/**
	 * Sets all objects within a certain radius to a particular value
	 * 
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = set nothing, 0 = set this cell only, 1 = 1 ring around this cell, and so on)
	 * @param value The object to replace the contents of all the cells within the radius
	 */
    @Override
	public final void setWithinRadius (final MapArea<E, C> area, final int x, final int y, final int radius, final E value)
	{
		processCoordinatesWithinRadius (area.getCoordinateSystem (), x, y, radius, (coords) ->
		{
			area.set (coords, value);
			return true;
		});
	}
    
	/**
	 * Checks if any cell within a certain radius has a certain value
	 * 
	 * @param area Map area to read from
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to check (negative = check nothing, 0 = check this cell only, 1 = 1 ring around this cell, and so on)
     * @param value Value to search for
     * @return Whether any cell within the specified radius has the requested value
	 */
    @Override
	public final boolean anyCellWithinRadiusEqualTo (final MapArea<E, C> area, final int x, final int y, final int radius, final E value)
	{
		final Holder<Boolean> found = new Holder<Boolean> (false);
		processCellsWithinRadius (area, x, y, radius, (thisValue) ->
		{
			if (((value == null) && (thisValue == null)) || ((value != null) && (value.equals (thisValue))))
				found.setValue (true);
			
			return !found.getValue ();
		});

		return found.getValue ();
	}
    
	/**
	 * @return Coordinate system utils
	 */
	public final CoordinateSystemUtils getCoordinateSystemUtils ()
	{
		return coordinateSystemUtils;
	}

	/**
	 * @param utils Coordinate system utils
	 */
	public final void setCoordinateSystemUtils (final CoordinateSystemUtils utils)
	{
		coordinateSystemUtils = utils;
	}
	
	/**
	 * @return Random number generator
	 */
	public final RandomUtils getRandomUtils ()
	{
		return randomUtils;
	}

	/**
	 * @param gen Random number generator
	 */
	public final void setRandomUtils (final RandomUtils gen)
	{
		randomUtils = gen;
	}
}
