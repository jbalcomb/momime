package com.ndg.map.areas.operations;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.areas.storage.MapArea;

/**
 * Interface describing map operations applicable to all types of maps and all coordinate systems 
 *
 * @param <E> Type of object held at each cell
 * @param <C> Coordinates storage object
 */
public interface MapAreaOperations<E, C>
{
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	public void processAllCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<C> callback);

	/**
	 * @param area Map area to process
	 * @param callback Operation to call against every map cell in the map area
	 */
	public void processAllCells (final MapArea<E, C> area, final ProcessMapCell<E> callback);

	/**
	 * @param area Map area to write to
	 * @param value Value to set all cells equal to
	 */
	public void setAll (final MapArea<E, C> area, final E value);
	
	/**
	 * Note, this directly copies object references so is only really safe on immutable types such as Boolean, Integer or String
	 * @param from Map area to copy cell values from
	 * @param to Map area to copy cell values to
	 */
	public void copy (final MapArea<E, C> from, final MapArea<E, C> to);
	
    /**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Number of cells in the map area that have the requested value
     */
    public int countCellsEqualTo (final MapArea<E, C> area, final E value);
    
	/**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Randomly chosen cell with this value (compared using the .equals method), or null if there are none
     */
    public C findRandomCellEqualTo (final MapArea<E, C> area, final E value);

    /**
	 * @param area Map area to read from
     * @param value Value to search for
     * @return Whether any cell in the map area has the requested  value
     */
    public boolean anyCellEqualTo (final MapArea<E, C> area, final E value);
    
	/**
	 * Triggers a callback on every set of coordinates within a certain radius
	 * 
	 * @param sys Coordinate system to trace around
	 * @param x X coordinate of centre of area to process
	 * @param y Y coordinate of centre of area to process
	 * @param radius Radius to process (negative = process nothing, 0 = process this cell only, 1 = 1 ring around this cell, and so on)
	 * @param callback Operation to call against every set of coordinates within the specified radius
	 */
	public void processCoordinatesWithinRadius (final CoordinateSystem sys, final int x, final int y, final int radius, final ProcessMapCoordinates<C> callback);

	/**
	 * Triggers a callback on all map cells within a certain radius
	 * 
	 * @param area Map area to process
	 * @param x X coordinate of centre of area to process
	 * @param y Y coordinate of centre of area to process
	 * @param radius Radius to process (negative = process nothing, 0 = process this cell only, 1 = 1 ring around this cell, and so on)
	 * @param callback Operation to call against every cell within the specified radius
	 */
	public void processCellsWithinRadius (final MapArea<E, C> area, final int x, final int y, final int radius, final ProcessMapCell<E> callback);
	
	/**
	 * Sets all objects within a certain radius to a particular value
	 * 
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = set nothing, 0 = set this cell only, 1 = 1 ring around this cell, and so on)
	 * @param value The object to replace the contents of all the cells within the radius
	 */
	public void setWithinRadius (final MapArea<E, C> area, final int x, final int y, final int radius, final E value);

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
	public boolean anyCellWithinRadiusEqualTo (final MapArea<E, C> area, final int x, final int y, final int radius, final E value);
}