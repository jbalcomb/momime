package com.ndg.map.areas.operations;

import java.util.ArrayList;
import java.util.List;

import com.ndg.map.CoordinateSystemType;
import com.ndg.map.SquareMapDirection;
import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.areas.storage.MapArea2DArrayListImpl;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Provides 2D implementations of all the methods declared on BooleanMapAreaOperations
 */
public final class BooleanMapAreaOperations2DImpl extends MapAreaOperations2DImpl<Boolean> implements BooleanMapAreaOperations2D
{
	/** North-east-south-west direction */
	private final static SquareMapDirection [] NESW = new SquareMapDirection []
		{SquareMapDirection.NORTH, SquareMapDirection.EAST, SquareMapDirection.SOUTH, SquareMapDirection.WEST};
	
	/**
	 * Selects every map cell
	 * @param area Map area to write to
	 */
	@Override
	public final void selectAll (final MapArea<Boolean, MapCoordinates2DEx> area)
	{
		setAll (area, true);
	}

	/**
	 * Deselects every map cell
	 * @param area Map area to write to
	 */
	@Override
	public final void deselectAll (final MapArea<Boolean, MapCoordinates2DEx> area)
	{
		setAll (area, false);
	}

    /**
	 * @param area Map area to read from
     * @return Number of selected cells in the map area
     */
	@Override
    public final int countSelected (final MapArea<Boolean, MapCoordinates2DEx> area)
    {
		return countCellsEqualTo (area, true);
    }
	
    /**
	 * @param area Map area to read from
     * @return Whether there is any cell set to true over the whole area
     */
	@Override
    public final boolean anySelected (final MapArea<Boolean, MapCoordinates2DEx> area)
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
	public final void selectRadius (final MapArea<Boolean, MapCoordinates2DEx> area, final int x, final int y, final int radius)
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
	public final void deselectRadius (final MapArea<Boolean, MapCoordinates2DEx> area, final int x, final int y, final int radius)
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
	public final boolean anyCellWithinRadiusSelected (final MapArea<Boolean, MapCoordinates2DEx> area, final int x, final int y, final int radius)
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
	public final void enlarge (final MapArea<Boolean, MapCoordinates2DEx> area, final int [] directions, final boolean selectEdgeOfMap)
	{
		// Build directions list
		final int [] useDirections;
		if (directions != null)
			useDirections = directions;
		else
		{
			useDirections = new int [getCoordinateSystemUtils ().getMaxDirection (area.getCoordinateSystem ().getCoordinateSystemType ())];
			for (int d = 0; d < useDirections.length; d++)
				useDirections [d] = d + 1;
		}

		// Take a temporary copy of the area
		final MapArea2D<Boolean> copy = new MapArea2DArrayListImpl<Boolean> ();
		copy.setCoordinateSystem (area.getCoordinateSystem ());
		
		for (int y = 0; y < area.getCoordinateSystem ().getHeight (); y++)
			for (int x = 0; x < area.getCoordinateSystem ().getWidth (); x++)
			{
				final MapCoordinates2DEx coords = new MapCoordinates2DEx (x, y);			
				copy.set (coords, area.get (coords));
			}

		// Now scan the copy, expanding in each direction and mark it back in the original area
		for (int y = 0; y < area.getCoordinateSystem ().getHeight (); y++)
			for (int x = 0; x < area.getCoordinateSystem ().getWidth (); x++)
			{
				final Boolean source = copy.get (x, y);
				if ((source != null) && (source.equals (Boolean.TRUE)))
				{
					// Mark off every cell adjacent to this one
					for (final int d : useDirections)
					{
						final MapCoordinates2DEx coords = new MapCoordinates2DEx (x, y);
						if (getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, d))
							area.set (coords, true);
					}
				}
			}
		
		// If we want to select the edge of the map then do so
		if (selectEdgeOfMap)
		{
			for (int x = 0; x < area.getCoordinateSystem ().getWidth (); x++)
			{
				area.set (new MapCoordinates2DEx (x, 0), true);
				area.set (new MapCoordinates2DEx (x, area.getCoordinateSystem ().getHeight () - 1), true);
			}

			for (int y = 0; y < area.getCoordinateSystem ().getHeight (); y++)
			{
				area.set (new MapCoordinates2DEx (0, y), true);
				area.set (new MapCoordinates2DEx (area.getCoordinateSystem ().getWidth () - 1, y), true);
			}
		}
	}

	/**
	 * @param area Boolean map area to trace borders of
	 * @return Area indicating the orders of the true cells, e.g. if 0, 1 was true but 0, 0 was false, then the list output for 0, 1 will include NORTH
	 */
	@Override
	public final MapArea<List<SquareMapDirection>, MapCoordinates2DEx> traceBorders (final MapArea<Boolean, MapCoordinates2DEx> area)
	{
		if (area.getCoordinateSystem ().getCoordinateSystemType () != CoordinateSystemType.SQUARE)
			throw new IllegalArgumentException ("traceBorders: Does not support " + area.getCoordinateSystem ().getCoordinateSystemType () + " coordinate systems");
		
		final MapArea2D<List<SquareMapDirection>> borders = new MapArea2DArrayListImpl<List<SquareMapDirection>> ();
		borders.setCoordinateSystem (area.getCoordinateSystem ());

		for (int y = 0; y < area.getCoordinateSystem ().getHeight (); y++)
			for (int x = 0; x < area.getCoordinateSystem ().getWidth (); x++)
			{
				final Boolean b = area.get (new MapCoordinates2DEx (x, y));
				if ((b != null) && (b))
				{
					final List<SquareMapDirection> directions = new ArrayList<SquareMapDirection> ();
					
					// Test each direction to see if we can find a deselected one
					for (final SquareMapDirection d : NESW)
					{
						final MapCoordinates2DEx coords = new MapCoordinates2DEx (x, y);
						if (getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, d.getDirectionID ()))
						{
							final Boolean b2 = area.get (coords);
							if ((b2 == null) || (!b2))
								directions.add (d);
						}
						else
							// At edge of map, so want to draw borders there too
							directions.add (d);
					}
					
					if (directions.size () > 0)
						borders.set (x, y, directions);
				}
			}
		
		return borders;
	}

	/**
	 * @param area Area to search over
	 * @param coords Coords of centre to scan out from
	 * @return Coordinates of selected cell closest to specified location, or null if no cell in the entire area is selected
	 */
	@Override
	public final MapCoordinates2DEx findClosestSelectedCell (final MapArea2D<Boolean> area, final MapCoordinates2DEx coords)
	{
		final MapCoordinates2DEx match = findClosestMatchingCell (area, coords, true);
		return match;
	}
}