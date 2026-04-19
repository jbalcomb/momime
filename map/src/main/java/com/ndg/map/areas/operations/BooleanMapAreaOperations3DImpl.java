package com.ndg.map.areas.operations;

import java.util.List;

import com.ndg.map.SquareMapDirection;
import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.areas.storage.MapArea3D;
import com.ndg.map.areas.storage.MapArea3DArrayListImpl;
import com.ndg.map.areas.storage.MapArea3Dto2D;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Provides 3D implementations of all the methods declared on BooleanMapAreaOperations
 */
public final class BooleanMapAreaOperations3DImpl extends MapAreaOperations3DImpl<Boolean> implements BooleanMapAreaOperations3D
{
	/** Boolean operations for 2D maps */
	private BooleanMapAreaOperations2D booleanMapAreaOperations2D;
	
	/**
	 * Selects every map cell
	 * @param area Map area to write to
	 */
	@Override
	public final void selectAll (final MapArea<Boolean, MapCoordinates3DEx> area)
	{
		setAll (area, true);
	}

	/**
	 * Deselects every map cell
	 * @param area Map area to write to
	 */
	@Override
	public final void deselectAll (final MapArea<Boolean, MapCoordinates3DEx> area)
	{
		setAll (area, false);
	}

    /**
	 * @param area Map area to read from
     * @return Number of selected cells in the map area
     */
	@Override
    public final int countSelected (final MapArea<Boolean, MapCoordinates3DEx> area)
    {
		return countCellsEqualTo (area, true);
    }
	
    /**
	 * @param area Map area to read from
     * @return Whether there is any cell set to true over the whole area
     */
	@Override
    public final boolean anySelected (final MapArea<Boolean, MapCoordinates3DEx> area)
    {
		return anyCellEqualTo (area, true);
    }
	
	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to select
	 * @param y Y coordinate of centre of area to select
	 * @param radius Radius to select (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 */
	@Override
	public final void selectRadius (final MapArea<Boolean, MapCoordinates3DEx> area, final int x, final int y, final int radius)
	{
		setWithinRadius (area, x, y, radius, true);
	}

	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to deselect
	 * @param y Y coordinate of centre of area to deselect
	 * @param radius Radius to deselect (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 */
	@Override
	public final void deselectRadius (final MapArea<Boolean, MapCoordinates3DEx> area, final int x, final int y, final int radius)
	{
		setWithinRadius (area, x, y, radius, false);
	}

	/**
	 * @param area Map area to check
	 * @param x X coordinate of centre of area to check
	 * @param y Y coordinate of centre of area to check
	 * @param radius Radius to check (negative = check nothing, 0 = check this cell only, 1 = 1 ring around this cell, and so on)
     * @return Whether there is any cell set to true within the specified radius
	 */
	@Override
	public final boolean anyCellWithinRadiusSelected (final MapArea<Boolean, MapCoordinates3DEx> area, final int x, final int y, final int radius)
	{
		return anyCellWithinRadiusEqualTo (area, x, y, radius, true);
	}
	
	/**
	 * Enlarges the selected area by 1 in each direction
	 * 
	 * @param area Map area to write update
	 * @param directions Directions to expand out in - can use this for example to stop expanding along diagonals - can leave as null to just use all
	 * @param selectEdgeOfMap Whether to also select the outermost edge fo the map
	 */
	@Override
	public final void enlarge (final MapArea<Boolean, MapCoordinates3DEx> area, final int [] directions, final boolean selectEdgeOfMap)
	{
		// Don't want to duplicate all the code from the 2D method, so loop around the planes and delegate to it
		final MapArea3Dto2D<Boolean> translateArea = new MapArea3Dto2D<Boolean> ();
		translateArea.setStorage (area);
		
		for (int z = 0; z < area.getCoordinateSystem ().getDepth (); z++)
		{
			translateArea.setZ (z);
			getBooleanMapAreaOperations2D ().enlarge (translateArea, directions, selectEdgeOfMap);
		}
	}


	/**
	 * @param area Boolean map area to trace borders of
	 * @return Area indicating the orders of the true cells, e.g. if 0, 1 was true but 0, 0 was false, then the list output for 0, 1 will include NORTH
	 */
	@Override
	public final MapArea<List<SquareMapDirection>, MapCoordinates3DEx> traceBorders (final MapArea<Boolean, MapCoordinates3DEx> area)
	{
		// Don't want to duplicate all the code from the 2D method, so loop around the planes and delegate to it
		final MapArea3Dto2D<Boolean> translateArea = new MapArea3Dto2D<Boolean> ();
		translateArea.setStorage (area);

		final MapArea3D<List<SquareMapDirection>> borders = new MapArea3DArrayListImpl<List<SquareMapDirection>> ();
		borders.setCoordinateSystem (area.getCoordinateSystem ());
		
		final MapArea3Dto2D<List<SquareMapDirection>> translateBorders = new MapArea3Dto2D<List<SquareMapDirection>> ();
		translateBorders.setStorage (borders);
		
		final MapAreaOperations2DImpl<List<SquareMapDirection>> squareMapDirectionsAreaOperations = new MapAreaOperations2DImpl<List<SquareMapDirection>> ();
		squareMapDirectionsAreaOperations.setCoordinateSystemUtils (getCoordinateSystemUtils ());
		
		for (int z = 0; z < area.getCoordinateSystem ().getDepth (); z++)
		{
			translateArea.setZ (z);
			translateBorders.setZ (z);
			
			final MapArea<List<SquareMapDirection>, MapCoordinates2DEx> thisBorders = getBooleanMapAreaOperations2D ().traceBorders (translateArea);
			squareMapDirectionsAreaOperations.copy (thisBorders, translateBorders);
		}

		return borders;
	}
	
	/**
	 * @return Boolean operations for 2D maps
	 */
	public final BooleanMapAreaOperations2D getBooleanMapAreaOperations2D ()
	{
		return booleanMapAreaOperations2D;
	}

	/**
	 * @param op Boolean operations for 2D maps
	 */
	public final void setBooleanMapAreaOperations2D (final BooleanMapAreaOperations2D op)
	{
		booleanMapAreaOperations2D = op;
	}
}