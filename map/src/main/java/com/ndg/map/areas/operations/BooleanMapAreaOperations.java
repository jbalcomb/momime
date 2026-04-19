package com.ndg.map.areas.operations;

import java.util.List;

import com.ndg.map.SquareMapDirection;
import com.ndg.map.areas.storage.MapArea;

/**
 * Adds methods to operations interface that are appropraite for booleans  
 *
 * @param <C> Coordinates storage object
 */
public interface BooleanMapAreaOperations<C> extends MapAreaOperations<Boolean, C>
{
	/**
	 * Selects every map cell
	 * @param area Map area to write to
	 */
	public void selectAll (final MapArea<Boolean, C> area);
	
	/**
	 * Deselects every map cell
	 * @param area Map area to write to
	 */
	public void deselectAll (final MapArea<Boolean, C> area);

    /**
	 * @param area Map area to read from
     * @return Number of selected cells in the map area
     */
    public int countSelected (final MapArea<Boolean, C> area);
    
    /**
	 * @param area Map area to read from
     * @return Whether there is any cell set to true over the whole area
     */
    public boolean anySelected (final MapArea<Boolean, C> area);

	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to select
	 * @param y Y coordinate of centre of area to select
	 * @param radius Radius to select (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 */
	public void selectRadius (final MapArea<Boolean, C> area, final int x, final int y, final int radius);

	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to deselect
	 * @param y Y coordinate of centre of area to deselect
	 * @param radius Radius to deselect (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 */
	public void deselectRadius (final MapArea<Boolean, C> area, final int x, final int y, final int radius);

	/**
	 * @param area Map area to check
	 * @param x X coordinate of centre of area to check
	 * @param y Y coordinate of centre of area to check
	 * @param radius Radius to check (negative = check nothing, 0 = check this cell only, 1 = 1 ring around this cell, and so on)
     * @return Whether there is any cell set to true within the specified radius
	 */
	public boolean anyCellWithinRadiusSelected (final MapArea<Boolean, C> area, final int x, final int y, final int radius);
	
	/**
	 * Enlarges the selected area by 1 in each direction
	 * 
	 * @param area Map area to write update
	 * @param directions Directions to expand out in - can use this for example to stop expanding along diagonals - can leave as null to just use all
	 * @param selectEdgeOfMap Whether to also select the outermost edge fo the map
	 */
	public void enlarge (final MapArea<Boolean, C> area, final int [] directions, final boolean selectEdgeOfMap);
	
	/**
	 * @param area Boolean map area to trace borders of
	 * @return Area indicating the orders of the true cells, e.g. if 0, 1 was true but 0, 0 was false, then the list output for 0, 1 will include NORTH
	 */
	public MapArea<List<SquareMapDirection>, C> traceBorders (final MapArea<Boolean, C> area);
}