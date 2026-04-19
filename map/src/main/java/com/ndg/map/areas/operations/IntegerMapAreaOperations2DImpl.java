package com.ndg.map.areas.operations;

import com.ndg.map.CoordinateSystemType;
import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Provides 2D implementations of all the methods declared on IntegerMapAreaOperations
 */
public final class IntegerMapAreaOperations2DImpl extends MapAreaOperations2DImpl<Integer> implements IntegerMapAreaOperations2D
{
	/**
	 * @param area Map area to write to
	 * @param value Value to ensure every map cell is at least this
	 */
	@Override
	public final void setAllToMinimum (final MapArea<Integer, MapCoordinates2DEx> area, final int value)
	{
		processAllCoordinates (area.getCoordinateSystem (), (c) ->
		{
			final Integer current = area.get (c);
			if ((current == null) || (current < value))
				area.set (c, value);
			
			return true;
		});
	}

	/**
	 * @param area Map area to write to
	 * @param x X coordinate of centre of area to select
	 * @param y Y coordinate of centre of area to select
	 * @param radius Radius to select (negative = do nothing, 0 = enforce minimum on this cell only, 1 = 1 ring around this cell, and so on)
	 * @param value Value to ensure every map cell within the designated area is at least this
	 */
	@Override
	public final void setMinimumWithinRadius (final MapArea<Integer, MapCoordinates2DEx> area, final int x, final int y, final int radius, final int value)
	{
		processCoordinatesWithinRadius (area.getCoordinateSystem (), x, y, radius, (c) ->
		{
			final Integer current = area.get (c);
			if ((current == null) || (current < value))
				area.set (c, value);

			return true;
		});
	}

	/**
	 * Adds a value to each map cell which is the distance of that map cell from the specified coordinates
	 * 
	 * @param area Map area to write to
	 * @param start Coordinates to measure distance from
	 */
	@Override
	public final void addDistancesFrom (final MapArea<Integer, MapCoordinates2DEx> area, final MapCoordinates2DEx start)
	{
		if (area.getCoordinateSystem ().getCoordinateSystemType () != CoordinateSystemType.HEX)
			throw new IllegalArgumentException ("addDistancesFrom: Does not support " + area.getCoordinateSystem ().getCoordinateSystemType () +
				" coordinate systems");
		
		getCoordinateSystemUtils ().processCoordinatesWithinRadius (area.getCoordinateSystem (), start.getX (), start.getY (), Integer.MAX_VALUE, (x, y, r, d, n) ->
		{
			final MapCoordinates2DEx c = new MapCoordinates2DEx (x, y);
			area.set (c, r + ((area.get (c) == null) ? 0 : area.get (c)));
			return true;
		});
	}
}