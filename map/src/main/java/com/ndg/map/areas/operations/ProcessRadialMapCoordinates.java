package com.ndg.map.areas.operations;

/**
 * Callback that performs some operation on a particular map cell as we are traversing the map in circles of increasing radius starting from a point,
 * so also provides information about the distance and direction from the starting point
 */
@FunctionalInterface
public interface ProcessRadialMapCoordinates
{
	/**
	 * @param x X coordinate of map cell being traversed
	 * @param y Y coordinate of map cell being traversed
	 * @param r Radius or step distance from the start point of this map cell
	 * @param d Direction being traversed to arrive at this cell (exact sequence of directions and so the meaning of this value depends on the CoordinateSystemType)
	 * @param n Number of cells travelled in direction d; for hex maps this ranges from 1..r, for square and diamond maps this ranges from 1..2r
	 * @return Whether to keep going processing further cells
	 */
	public boolean process (final int x, final int y, final int r, final int d, final int n);
}