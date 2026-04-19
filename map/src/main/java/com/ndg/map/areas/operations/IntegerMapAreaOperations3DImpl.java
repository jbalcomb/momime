package com.ndg.map.areas.operations;

import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Provides 3D implementations of all the methods declared on IntegerMapAreaOperations
 */
public final class IntegerMapAreaOperations3DImpl extends MapAreaOperations3DImpl<Integer> implements IntegerMapAreaOperations3D
{
	/**
	 * @param area Map area to write to
	 * @param value Value to ensure every map cell is at least this
	 */
	@Override
	public final void setAllToMinimum (final MapArea<Integer, MapCoordinates3DEx> area, final int value)
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
	 * @param radius Radius to select (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 * @param value Value to ensure every map cell within the designated area is at least this
	 */
	@Override
	public final void setMinimumWithinRadius (final MapArea<Integer, MapCoordinates3DEx> area, final int x, final int y, final int radius, final int value)
	{
		processCoordinatesWithinRadius (area.getCoordinateSystem (), x, y, radius, (c) ->
		{
			final Integer current = area.get (c);
			if ((current == null) || (current < value))
				area.set (c, value);

			return true;
		});
	}
}