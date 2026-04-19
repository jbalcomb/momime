package com.ndg.map;

import com.ndg.map.areas.operations.ProcessMapCoordinates;
import com.ndg.map.areas.operations.ProcessRadialMapCoordinates;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Helper methods for working with coordinate systems.
 */
public final class CoordinateSystemUtilsImpl implements CoordinateSystemUtils
{
	/**
	 * @param sys Coordinate system
	 * @param x X coordinate to test
	 * @param y Y coordinate to test
	 * @return True if these 2D coordinates are within the coordinate system
	 */
	@Override
	public final boolean are2DCoordinatesWithinRange (final CoordinateSystem sys, final int x, final int y)
	{
		return ((x >= 0) && (y >= 0) && (x < sys.getWidth ()) && (y < sys.getHeight ()));
	}

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to test
	 * @return True if these 2D coordinates are within the coordinate system
	 */
	@Override
	public final boolean are2DCoordinatesWithinRange (final CoordinateSystem sys, final MapCoordinates2DEx coords)
	{
		return are2DCoordinatesWithinRange (sys, coords.getX (), coords.getY ());
	}

	/**
	 * @param sys Coordinate system
	 * @param x X coordinate to test
	 * @param y Y coordinate to test
	 * @param z Z coordinate to test
	 * @return True if these 3D coordinates are within the coordinate system
	 */
	@Override
	public final boolean are3DCoordinatesWithinRange (final CoordinateSystem sys, final int x, final int y, final int z)
	{
		return ((x >= 0) && (y >= 0) && (z >= 0) && (x < sys.getWidth ()) && (y < sys.getHeight ()) && (z < sys.getDepth ()));
	}

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to test
	 * @return True if these 3D coordinates are within the coordinate system
	 */
	@Override
	public boolean are3DCoordinatesWithinRange (final CoordinateSystem sys, final MapCoordinates3DEx coords)
	{
		return are3DCoordinatesWithinRange (sys, coords.getX (), coords.getY (), coords.getZ ());
	}
	
	/**
	 * @param st Coordinate system type
	 * @return Maximum valid direction for this coordinate system
	 */
	@Override
	public final int getMaxDirection (final CoordinateSystemType st)
	{
		final int maxDirection;
		switch (st)
		{
			case SQUARE:
			case DIAMOND:
			case WRAPPED_DIAMOND:
				maxDirection = 8;
				break;

			case HEX:
			case VERTICAL_HEX:
				maxDirection = 6;
				break;

				// Invalid coordinate system type
			default:
				throw new IllegalArgumentException ("getMaxDirection: Invalid coordinate system type: " + st);
		}

		return maxDirection;
	}

	/**
	 * @param st Coordinate system type
	 * @param direction Direction in this coordinate system
	 * @return Direction brought into the range 1..maxDirection
	 */
	@Override
	public final int normalizeDirection (final CoordinateSystemType st, final int direction)
	{
		final int maxDirection = getMaxDirection (st);
		int result = direction;

		while (result < 1)					result = result + maxDirection;
		while (result > maxDirection)	result = result - maxDirection;

		return result;
	}

	/**
	 * @param value Base value
	 * @param max Width/Height/Depth as appropriate
	 * @return Value shifted to the range 0 < value <= max
	 */
	@Override
	public final int normalizeCoordinate (final int value, final int max)
	{
		int v = value;
		
		while (v < 0)
			v = v + max;
		
		while (v >= max)
			v = v - max;
		
		return v;
	}
	
	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to move from - the updated coordinates will be written to here
	 * @param d Direction to move in
	 * @return whether the resulting coordinates are still within the range of the coordinate system or not
	 */
	@Override
	public final boolean move2DCoordinates (final CoordinateSystem sys, final MapCoordinates2DEx coords, final int d)
	{
		switch (sys.getCoordinateSystemType ())
		{
			// Square map - easy
			case SQUARE:
				if ((d < 1) || (d > getMaxDirection (sys.getCoordinateSystemType ())))
					throw new IllegalArgumentException ("move2DCoordinates: Requested direction " + d + " of a " + sys.getCoordinateSystemType () + " map");

				// Move X
				if ((d == 2) || (d == 3) || (d == 4))
					coords.setX (coords.getX () + 1);
				else if ((d == 6) || (d == 7) || (d == 8))
					coords.setX (coords.getX () - 1);

				// Move Y
				if ((d == 8) || (d == 1) || (d == 2))
					coords.setY (coords.getY () - 1);
				else if ((d == 4) || (d == 5) || (d == 6))
					coords.setY (coords.getY () + 1);
				break;

			// Hex map - little more complex
			case HEX:
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("move2DCoordinates: Wrapping not supported on hex maps");

				switch (d)
				{
					// Up
					case 1:
						coords.setY (coords.getY () - 1);
					break;

					// Up-right
					case 2:
						if ((coords.getX () & 1) == 0)
							coords.setY (coords.getY () - 1);

						coords.setX (coords.getX () + 1);
						break;

					// Down-right
					case 3:
						if ((coords.getX () & 1) != 0)
							coords.setY (coords.getY () + 1);

						coords.setX (coords.getX () + 1);
						break;

					// Down
					case 4:
						coords.setY (coords.getY () + 1);
						break;

					// Down-left
					case 5:
						if ((coords.getX () & 1) != 0)
							coords.setY (coords.getY () + 1);

						coords.setX (coords.getX () - 1);
						break;

					// Up-left
					case 6:
						if ((coords.getX () & 1) == 0)
							coords.setY (coords.getY () - 1);

						coords.setX (coords.getX () - 1);
						break;

					// Invalid direction
					default:
						throw new IllegalArgumentException ("move2DCoordinates: Requested direction " + d + " of a " + sys.getCoordinateSystemType () + " map");
				}
				break;

			// Vertical hex map
			case VERTICAL_HEX:
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("move2DCoordinates: Wrapping not supported on vertical hex maps");
				
				switch (d)
				{
					// Up-right
					case 1:
						if ((coords.getY () & 1) != 0)
							coords.setX (coords.getX () + 1);
						
						coords.setY (coords.getY () - 1);
						break;
					
					// Right
					case 2:
						coords.setX (coords.getX () + 1);
						break;
					
					// Down-right
					case 3:
						if ((coords.getY () & 1) != 0)
							coords.setX (coords.getX () + 1);

						coords.setY (coords.getY () + 1);
						break;
					
					// Down-left
					case 4:
						if ((coords.getY () & 1) == 0)
							coords.setX (coords.getX () - 1);

						coords.setY (coords.getY () + 1);
						break;

					// Left
					case 5:
						coords.setX (coords.getX () - 1);
						break;
					
					// Up-left
					case 6:
						if ((coords.getY () & 1) == 0)
							coords.setX (coords.getX () - 1);

						coords.setY (coords.getY () - 1);
						break;
					
					// Invalid direction
					default:
						throw new IllegalArgumentException ("move2DCoordinates: Requested direction " + d + " of a " + sys.getCoordinateSystemType () + " map");
				}
				break;				
				
			// Diamond map - similar to hexes in that coordinate movement does not always result in the same shift in x, y values
			case DIAMOND:
				if ((sys.isWrapsTopToBottom ()) && (sys.getHeight () % 2 != 0))
					throw new UnsupportedOperationException ("determineDirectionTo: Wrapping in vertical direction only supported on diamond maps when the height is an even number, but it is " + sys.getHeight ());

				switch (d)
				{
					// Up
					case 1:
						coords.setY (coords.getY () - 2);
						break;

					// Up-right
					case 2:
						if ((coords.getY () & 1) != 0)
							coords.setX (coords.getX () + 1);

						coords.setY (coords.getY () - 1);
						break;

					// Right
					case 3:
						coords.setX (coords.getX () + 1);
						break;

					// Down-right
					case 4:
						if ((coords.getY () & 1) != 0)
							coords.setX (coords.getX () + 1);

						coords.setY (coords.getY () + 1);
						break;

					// Down
					case 5:
						coords.setY (coords.getY () + 2);
						break;

					// Down-left
					case 6:
						if ((coords.getY () & 1) == 0)
							coords.setX (coords.getX () - 1);

						coords.setY (coords.getY () + 1);
						break;

					// Left
					case 7:
						coords.setX (coords.getX () - 1);
						break;

					// Up-left
					case 8:
						if ((coords.getY () & 1) == 0)
							coords.setX (coords.getX () - 1);

						coords.setY (coords.getY () - 1);
						break;

					// Invalid direction
					default:
						throw new IllegalArgumentException ("move2DCoordinates: Requested direction " + d + " of a " + sys.getCoordinateSystemType () + " map");
				}
				break;

				// Wrapped diamond is more like square in that we don't need special cases for odd/even rows
				case WRAPPED_DIAMOND:
					if ((sys.isWrapsTopToBottom ()) && (sys.getHeight () % 2 != 0))
						throw new UnsupportedOperationException ("determineDirectionTo: Wrapping in vertical direction only supported on wrapped diamond maps when the height is an even number, but it is " + sys.getHeight ());

					if ((d < 1) || (d > getMaxDirection (sys.getCoordinateSystemType ())))
						throw new IllegalArgumentException ("move2DCoordinates: Requested direction " + d + " of a " + sys.getCoordinateSystemType () + " map");

					// Move X
					if ((d == 1) || (d == 2) || (d == 3))
						coords.setX (coords.getX () + 1);
					else if ((d == 5) || (d == 6) || (d == 7))
						coords.setX (coords.getX () - 1);

					// Move Y
					if ((d == 2) || (d == 8))
						coords.setY (coords.getY () - 1);
					else if ((d == 4) || (d == 6))
						coords.setY (coords.getY () + 1);
					else if (d == 1)
						coords.setY (coords.getY () - 2);
					else if (d == 5)
						coords.setY (coords.getY () + 2);
					break;
				
			// Invalid coordinate system type
			default:
				throw new IllegalArgumentException ("move2DCoordinates: Invalid coordinate system type: " + sys.getCoordinateSystemType ());
		}

		// Check if new coordinates are off map, and if so whether to wrap them to the other side
		if ((coords.getX () < 0) && (sys.isWrapsLeftToRight ()))
			coords.setX (coords.getX () + sys.getWidth ());

		if ((coords.getX () >= sys.getWidth ()) && (sys.isWrapsLeftToRight ()))
			coords.setX (coords.getX () - sys.getWidth ());

		if ((coords.getY () < 0) && (sys.isWrapsTopToBottom ()))
			coords.setY (coords.getY () + sys.getHeight ());

		if ((coords.getY () >= sys.getHeight ()) && (sys.isWrapsTopToBottom ()))
			coords.setY (coords.getY () - sys.getHeight ());

		return are2DCoordinatesWithinRange (sys, coords);
	}

	/**
	 * @param sys Coordinate system
	 * @param coords Coordinates to move from - the updated coordinates will be written to here; the Z component will be left untouched
	 * @param d Direction to move in
	 * @return whether the resulting coordinates are still within the range of the coordinate system or not
	 */
	@Override
	public final boolean move3DCoordinates (final CoordinateSystem sys, final MapCoordinates3DEx coords, final int d)
	{
		// Just implement by calling 2D version; if ever there was a need for it, I would see this becoming a real
		// 3D move routine which crosses planes as well, with d = 1..26 (or even better, an enum) for square maps
		final MapCoordinates2DEx c = new MapCoordinates2DEx (coords.getX (), coords.getY ());
		
		move2DCoordinates (sys, c, d);
		coords.setX (c.getX ());
		coords.setY (c.getY ());
		
		return are3DCoordinatesWithinRange (sys, coords);
	}
	
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	@Override
	public final void processAll2DCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates2DEx> callback)
	{
		boolean keepGoing = true;
		int y = 0;
		while ((keepGoing) && (y < sys.getHeight ()))
		{
			int x = 0;
			while ((keepGoing) && (x < sys.getWidth ()))
			{
				keepGoing = callback.process (new MapCoordinates2DEx (x, y));
				x++;
			}
			y++;
		}
	}
	
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	@Override
	public final void processAll3DCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates3DEx> callback)
	{
		boolean keepGoing = true;
		int z = 0;
		while ((keepGoing) && (z < sys.getDepth ()))
		{
			int y = 0;
			while ((keepGoing) && (y < sys.getHeight ()))
			{
				int x = 0;
				while ((keepGoing) && (x < sys.getWidth ()))
				{
					keepGoing = callback.process (new MapCoordinates3DEx (x, y, z));
					x++;
				}
				y++;
			}
			z++;
		}
	}
	
	/**
	 * Triggers a callback on every set of coordinates within a certain radius
	 * 
	 * @param sys Coordinate system to trace around
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on); Integer.MAX_VALUE will traverse the entire map 
	 * @param callback Operation to call against every set of coordinates within the specified radius
	 */
	@Override
	public final void processCoordinatesWithinRadius (final CoordinateSystem sys, final int x, final int y, final int radius, final ProcessRadialMapCoordinates callback)
	{
		// How many cells do we need to cover the entire map
		int cellsRemaining = sys.getWidth () * sys.getHeight ();
		
		// Mark centre square, but check first if it is on the map
		final MapCoordinates2DEx coords = new MapCoordinates2DEx (x, y);
		boolean keepGoing = true;
		if (are2DCoordinatesWithinRange (sys, coords))
		{
			keepGoing = callback.process (x, y, 0, 0, 0);
			cellsRemaining--;
			if (cellsRemaining <= 0)
				keepGoing = false;
		}
		
		switch (sys.getCoordinateSystemType ())
		{
			// Square map - start at centre hex, then loop outwards in rings
			// We still only get a 4-sided area (which we could just do by spanning the area x-radius...x+radius and y-radius...y+radius),
			// but this way we correctly cope with maps which wrap top/bottom or left/right#
			case SQUARE:
			{
				// Mark each ring
				int ringNumber = 1;
				while ((keepGoing) && (ringNumber <= radius))
				{
					// Move down-left
					move2DCoordinates (sys, coords, 6);

					int directionChk = 0;
					while ((keepGoing) && (directionChk < 4))
					{
						final int d = (directionChk * 2) + 1;
						int traverseSide = 1;
						while ((keepGoing) && (traverseSide <= ringNumber * 2))
						{
							// Move in direction d
							if (move2DCoordinates (sys, coords, d))
							{
								keepGoing = callback.process (coords.getX (), coords.getY (), ringNumber, d, traverseSide);
								cellsRemaining--;
								if (cellsRemaining <= 0)
									keepGoing = false;
							}
							
							traverseSide++;
						}
						
						directionChk++;
					}
					
					ringNumber++;
				}
				break;
			}

			// Diamond map - start at centre diamond, then loop outwards in rings
			// We still only get a 4-sided area (which we could just do by spanning the area x-radius...x+radius and y-radius...y+radius),
			// but this way we correctly cope with maps which wrap top/bottom or left/right#
			case DIAMOND:
			{
				// Mark each ring
				int ringNumber = 1;
				while ((keepGoing) && (ringNumber <= radius))
				{
					// Move left
					move2DCoordinates (sys, coords, 7);

					int directionChk = 1;
					while ((keepGoing) && (directionChk <= 4))
					{
						final int d = directionChk * 2;
						int traverseSide = 1;
						while ((keepGoing) && (traverseSide <= ringNumber * 2))
						{
							// Move in direction d
							if (move2DCoordinates (sys, coords, d))
							{
								keepGoing = callback.process (coords.getX (), coords.getY (), ringNumber, d, traverseSide);
								cellsRemaining--;
								if (cellsRemaining <= 0)
									keepGoing = false;
							}
							
							traverseSide++;
						}
						
						directionChk++;
					}
					
					ringNumber++;
				}
				break;
			}

			// Hex map - start at centre hex, then loop outwards in rings so we end up with a 6-sided area
			case HEX:
			{
				// Mark each ring
				int ringNumber = 1;
				while ((keepGoing) && (ringNumber <= radius))
				{
					// Move down-left
					move2DCoordinates (sys, coords, 5);

					int d = 1;
					while ((keepGoing) && (d <= 6))
					{
						int traverseSide = 1;
						while ((keepGoing) && (traverseSide <= ringNumber))
						{
							// Move in direction d
							if (move2DCoordinates (sys, coords, d))
							{
								keepGoing = callback.process (coords.getX (), coords.getY (), ringNumber, d, traverseSide);
								cellsRemaining--;
								if (cellsRemaining <= 0)
									keepGoing = false;
							}
							
							traverseSide++;
						}
						
						d++;
					}

					ringNumber++;
				}
				break;
			}

			// Invalid coordinate system type
			default:
				throw new IllegalArgumentException ("processCoordinatesWithinRadius: Invalid coordinate system type: " + sys.getCoordinateSystemType ());
		}
	}
	
	/**
	 * @param sys Coordinate system
	 * @param from Coordinates of starting point
	 * @param to Coordinates of destination point
	 * @return Best approximation of direction between two locations
	 */
	@Override
	public final int determineDirectionTo (final CoordinateSystem sys, final MapCoordinates2DEx from, final MapCoordinates2DEx to)
	{
		final int result;

		// Check the daft scenario first
		if (from.equals (to))
			result = 1;
		else
			switch (sys.getCoordinateSystemType ())
			{
				// Square map
				case SQUARE:
				{
					// Work out X and Y differences, taking into account edges where the map wraps
					final int dx;
					final int dy;

					if (sys.isWrapsLeftToRight ())
					{
						if ((Math.abs (to.getX () - from.getX ()) <= Math.abs (to.getX () - from.getX () - sys.getWidth ())) &&
							(Math.abs (to.getX () - from.getX ()) <= Math.abs (to.getX () - from.getX () + sys.getWidth ())))
							dx = to.getX () - from.getX ();
						
						else if (Math.abs (to.getX () - from.getX () - sys.getWidth ()) <= Math.abs (to.getX () - from.getX () + sys.getWidth ()))
							dx = to.getX () - from.getX () - sys.getWidth ();
						
						else
							dx = to.getX () - from.getX () + sys.getWidth ();
					}
					else
						dx = to.getX () - from.getX ();

					if (sys.isWrapsTopToBottom ())
					{
						if ((Math.abs (to.getY () - from.getY ()) <= Math.abs (to.getY () - from.getY () - sys.getHeight ())) &&
							(Math.abs (to.getY () - from.getY ()) <= Math.abs (to.getY () - from.getY () + sys.getHeight ())))
							dy = to.getY () - from.getY ();
						
						else if (Math.abs (to.getY () - from.getY () - sys.getHeight ()) <= Math.abs (to.getY () - from.getY () + sys.getHeight ()))
							dy = to.getY () - from.getY () - sys.getHeight ();
						
						else
							dy = to.getY () - from.getY () + sys.getHeight ();
					}
					else
						dy = to.getY () - from.getY ();

					// Now check the amounts changed by
					// Currently this will only return level directions if going exactly in this direction, otherwise will return a diagonal
					// It should instead return the closest direction
					if (dy == 0)
					{
						// Staying level in y direction
						if (dx > 0) result = 3;
						else result = 7;
					}
					else if (dy > 0)
					{
						// Moving down
						if (dx == 0) result = 5;
						else if (dx > 0) result = 4;
						else result = 6;
					}
					else
					{
						// Moving up
						if (dx == 0) result = 1;
						else if (dx > 0) result = 2;
						else result = 8;
					}
					break;
				}

				// Hex map
				case HEX:
				{
					if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
						throw new UnsupportedOperationException ("determineDirectionTo: Wrapping not supported on hex maps");

					// Deal with simple vertical case first
					if (from.getX () == to.getX ())
						result = (to.getY () > from.getY ()) ? 4 : 1;
					else
					{
						// Convert into literal positions, see longer explanation in determineReal2DDistanceBetween
						final int ty1 = (from.getY () * 2) + (((from.getX () % 2) != 0) ? 1 : 0);
						final int ty2 = (to.getY () * 2) + (((to.getX () % 2) != 0) ? 1 : 0);
						
						final int dy = Math.abs (ty1 - ty2);
						final int dx = Math.abs (from.getX () - to.getX ());
					
						// Moving diagonally one cell in any direction now requires altering both x and y by 1.  This means the scales aren't
						// the same, but its perfect for what we need to test, whether we lie on the 2/5 or 3/6 axes.
						// Also if dx > dy this is also easy as the possible cells are split exactly along a horizontal line.
						if (dx >= dy)
						{
							if (to.getX () > from.getX ())
								result = (to.getY () > from.getY ()) ? 3 : 2;
							else
								result = (to.getY () > from.getY ()) ? 5 : 6;
						}
						else
						{
							final int r2 = dx + dy;
							if (to.getX () > from.getX ())
							{
								if (to.getY () > from.getY ())
									result = ((dx * 4) < r2) ? 4 : 3;
								else
									result = ((dx * 4) < r2) ? 1 : 2;
							}
							else
							{
								if (to.getY () > from.getY ())
									result = ((dx * 4) < r2) ? 4 : 5;
								else
									result = ((dx * 4) < r2) ? 1 : 6;
							}
						}
					}
					break;
				}

				// Diamond map
				case DIAMOND:
				{
					if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
						throw new UnsupportedOperationException ("determineDirectionTo: Wrapping not supported on diamond maps");

					// Convert into literal positions, with distance from the centre of a diamond to the pointy edges as '1'.  Leave y as is.
					// (See longer explanation in determineReal2DDistanceBetween).
					int tx1 = from.getX () * 2;
					if (from.getY () % 2 != 0)
						tx1++;

					int tx2 = to.getX () * 2;
					if (to.getY () % 2 != 0)
						tx2++;

					// Deal with simple horizontal / vertical cases first
					if (tx1 == tx2)
						result = (to.getY () > from.getY ()) ? 5 : 1;
					else if (to.getY () == from.getY ())
						result = (tx2 > tx1) ? 3 : 7;
					else
					{
						final int dx = Math.abs (tx1 - tx2);
						final int dy = Math.abs (from.getY () - to.getY ());

						// This is the direction to the middle of the side of the diamond; so the actual answer may be +/- 1 of this
						final int d;
						if (to.getY () > from.getY ())
							d = (tx2 > tx1) ? 4 : 6;
						else
							d = (tx2 > tx1) ? 2 : 8;
						
						// Split the side, which is length r2, into 4 pieces.  If we're in either of the end pieces, move the direction to the horizontal/vertical tip.
						// If we're in the middle 50% then leave pointing to the side of the diamond.  So we really want to do  if (dx < r2/4), but can't do that
						// with the way Java handles integers, so move the divison to the other side.
						final int r2 = dx + dy;
						if (dx * 4 < r2)
							result = ((d == 2) || (d == 8)) ? 1 : 5;
						else if (dy * 4 < r2)
							result = ((d == 2) || (d == 4)) ? 3 : 7;
						else
							result = d;
					}
							
					break;
				}

				// Invalid coordinate system type
				default:
					throw new IllegalArgumentException ("determineDirectionTo: Invalid coordinate system type: " + sys.getCoordinateSystemType ());
			}

		return result;
	}

	/**
	 * @param sys Coordinate system
	 * @param fromX X coordinate of starting point
	 * @param fromY Y coordinate of starting point
	 * @param toX X coordinate of destination point
	 * @param toY Y coordinate of destination point
	 * @return Best approximation of direction between two locations
	 */
	@Override
	public final int determineDirectionTo (final CoordinateSystem sys, final int fromX, final int fromY, final int toX, final int toY)
	{
		final MapCoordinates2DEx from = new MapCoordinates2DEx (fromX, fromY);
		final MapCoordinates2DEx to = new MapCoordinates2DEx (toX, toY);
		
		return determineDirectionTo (sys, from, to);
	}

	/**
	 * Deals correctly with if the shorter distance is to go via the wrapping edge
	 * @param sys Coordinate system
	 * @param x1 First x coordinate
	 * @param x2 Second x coordinate
	 * @return Absolute distance between two X coordinates
	 */
	@Override
	public final int findDistanceBetweenXCoordinates (final CoordinateSystem sys, final int x1, final int x2)
	{
		if (sys.getCoordinateSystemType () != CoordinateSystemType.SQUARE)
			throw new IllegalArgumentException ("findDistanceBetweenXCoordinates: Does not support " + sys.getCoordinateSystemType () + " coordinate systems");

		final int result;

		if (sys.isWrapsLeftToRight ())
			result = Math.min (Math.min
				(Math.abs (x1 - x2),								// Regular situation
				 Math.abs (x1 - x2 - sys.getWidth ())),		// e.g. 95 -  5 - 100 = -10 -> 10
				 Math.abs (x1 - x2 + sys.getWidth ()));		// e.g.  5 - 95 + 100 =  10 -> 10
		else
			result = Math.abs (x1 - x2);

		return result;
	}

	/**
	 * Deals correctly with if the shorter distance is to go via the wrapping edge
	 * @param sys Coordinate system
	 * @param y1 First y coordinate
	 * @param y2 Second y coordinate
	 * @return Absolute distance between two Y coordinates
	 */
	@Override
	public final int findDistanceBetweenYCoordinates (final CoordinateSystem sys, final int y1, final int y2)
	{
		if (sys.getCoordinateSystemType () != CoordinateSystemType.SQUARE)
			throw new IllegalArgumentException ("findDistanceBetweenYCoordinates: Does not support " + sys.getCoordinateSystemType () + " coordinate systems");

		final int result;

		if (sys.isWrapsTopToBottom ())
			result = Math.min (Math.min
				(Math.abs (y1 - y2),								// Regular situation
				 Math.abs (y1 - y2 - sys.getHeight ())),		// e.g. 95 -  5 - 100 = -10 -> 10
				 Math.abs (y1 - y2 + sys.getHeight ()));	// e.g.  5 - 95 + 100 =  10 -> 10
		else
			result = Math.abs (y1 - y2);

		return result;
	}

	/**
	 * @param sys Coordinate system
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @return Number of steps required to move between two map cells
	 */
	@Override
	public final int determineStep2DDistanceBetween (final CoordinateSystem sys, final int x1, final int y1, final int x2, final int y2)
	{
		final int distance;
		switch (sys.getCoordinateSystemType ())
		{
			// Square map
			case SQUARE:
			{
				// Will move Min (dx, dy) squares diagonally, then the remaining squares either horizontally or vertically,
				// either way we just end up taking the number of steps which is the greatest value so this very easy
				final int dx = findDistanceBetweenXCoordinates (sys, x1, x2);
				final int dy = findDistanceBetweenYCoordinates (sys, y1, y2);

				distance = Math.max (dx, dy);
				break;
			}

			// Diamond map
			case DIAMOND:
			{
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("determineStep2DDistanceBetween: Wrapping not supported on diamond maps");

				// Convert into literal positions, with distance from the centre of a diamond to the pointy edges as '1'.  Leave y as is.
				// (See longer explanation in determineReal2DDistanceBetween).
				int tx1 = x1 * 2;
				if (y1 % 2 != 0)
					tx1++;

				int tx2 = x2 * 2;
				if (y2 % 2 != 0)
					tx2++;
				
				// Rotate the entire map 45° anti-clockwise around the origin, such that it becomes a square grid system.
				// Because trying to keep working in integers, this is also multiplying up the lengths by sqrt(2).
				// Effectively this just means we're redefining '1' as the distance from the centre of a diamond to the middle of an edge, instead of to the point.
				final int rx1 = tx1 + y1;
				final int ry1 = y1 - tx1;

				final int rx2 = tx2 + y2;
				final int ry2 = y2 - tx2;
				
				// Now its a square grid system, this is easy (copied from how findDistanceBetweenXCoordinates works, and how step calc works on square grids above)
				final int dx = Math.abs (rx1 - rx2);
				final int dy = Math.abs (ry1 - ry2);
				final int d = Math.max (dx, dy);
				
				if ((d % 2) != 0)
					throw new RuntimeException ("determineStep2DDistanceBetween on a diamond map should always give d as a multiple of 2: from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") got " + d);
				
				distance = d / 2;
				break;
			}
			
			// Hex map
			case HEX:
			{
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("determineStep2DDistanceBetween: Wrapping not supported on hex maps");

				// Convert into literal positions, see longer explanation in determineReal2DDistanceBetween
				final int ty1 = (y1 * 2) + (((x1 % 2) != 0) ? 1 : 0);
				final int ty2 = (y2 * 2) + (((x2 % 2) != 0) ? 1 : 0);
				
				final int dy = Math.abs (ty1 - ty2);
				final int dx = Math.abs (x1 - x2);
				
				// Moving diagonally one cell in any direction now requires altering both x and y by 1.  This means the scales aren't
				// the same, but its perfect for what we need to test, whether we lie above/below this x=y / x=-y line.
				if (dx >= dy)
				{
					// All movement will be in direction 2/3 (right) or 5/6 (left), there won't be any 1/4 (up/down) component.
					// As we move left or right we may go up and down, and will be some combination of 2/3/5/6 to achieve this
					// but we don't need to know the exact directions or path so we can ignore all that and the answer is simple.
					distance = dx;
				}
				else
				{
					// Some height difference will be reduced by horizontal movement, equal to how far we are moving horizontally.
					// The rest will be in direction 1/4 (up/down).
					final int reducedDy = dy - dx;
					if ((reducedDy % 2) != 0)
						throw new RuntimeException ("determineStep2DDistanceBetween on a hex map should always give reducedDy as a multiple of 2: from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ") got " +
							dx + ", " + dy + " > " + reducedDy);

					distance = dx + (reducedDy / 2);
				}
				
				break;
			}
			
			default:
				throw new IllegalArgumentException ("determineStep2DDistanceBetween: Does not support " + sys.getCoordinateSystemType () + " coordinate systems");
		}

		return distance;
	}

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Number of steps required to move between two map cells
	 */
	@Override
	public final int determineStep2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates2DEx first, final MapCoordinates2DEx second)
	{
		return determineStep2DDistanceBetween (sys, first.getX (), first.getY (), second.getX (), second.getY ());
	}

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Number of steps required to move between two map cells; NB. Although this passes in 3D coords, the Z-component is ignored - the distance is calc'd in the 2D plane
	 */
	@Override
	public final int determineStep2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates3DEx first, final MapCoordinates3DEx second)
	{
		return determineStep2DDistanceBetween (sys, first.getX (), first.getY (), second.getX (), second.getY ());
	}

	/**
	 * @param sys Coordinate system
	 * @param x1 First x coordinate
	 * @param y1 First y coordinate
	 * @param x2 Second x coordinate
	 * @param y2 Second y coordinate
	 * @return Distance ("as the crow flies") between two sets of coordinates
	 */
	@Override
	public final double determineReal2DDistanceBetween (final CoordinateSystem sys, final int x1, final int y1, final int x2, final int y2)
	{
		final double distance;
		switch (sys.getCoordinateSystemType ())
		{
			// Square map
			case SQUARE:
			{
				final int dx = findDistanceBetweenXCoordinates (sys, x1, x2);
				final int dy = findDistanceBetweenYCoordinates (sys, y1, y2);

				distance = Math.sqrt ((dx * dx) + (dy * dy));
				break;
			}

			// Diamond map
			case DIAMOND:
			{
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("determineReal2DDistanceBetween: Wrapping not supported on diamond maps");
				
				// This works by converting the coordinates into literal positions, just as if we were drawing the map.
				// This treats the distance from the centre of a diamond to the pointy edges as '1', which means we can
				// do the first bit just using integers.  This also means we can leave the y coords as-is.
				int tx1 = x1 * 2;
				if (y1 % 2 != 0)
					tx1++;

				int tx2 = x2 * 2;
				if (y2 % 2 != 0)
					tx2++;
				
				final int dx = tx1 - tx2;
				final int dy = y1 - y2;
				
				final double d = (dx * dx) + (dy * dy);
				
				// This /sqrt(2) takes care of the fact that we've been measuring in units that are larger than the result we want, i.e. the difference between the
				// distance from the centre of a diamond to one of its points (further), vs the distance to the midpoint of one of its edges (closer).
				distance = Math.sqrt (d / 2d);
				break;
			}
			
			// Hex map
			case HEX:
			{
				if ((sys.isWrapsLeftToRight ()) || (sys.isWrapsTopToBottom ()))
					throw new UnsupportedOperationException ("determineReal2DDistanceBetween: Wrapping not supported on hex maps");

				// If you slice a hex up into twelve 30° triangles then can treat each triangle as being
				// Length 1 horizontally, which is half of a hex edge
				// Length 2 along the diagonal, which is the distance from the centre of the hex to one of the 6 points
				// Length sqrt(3) vertically, which is the distance from the centre of the hex to the middle of one of the edges
				
				// So going by this, we get the following
				final int ty1 = (y1 * 2) + (((x1 % 2) != 0) ? 1 : 0);
				final int ty2 = (y2 * 2) + (((x2 % 2) != 0) ? 1 : 0);
				final int dy = ty1 - ty2;	// Multiplied by sqrt(3)
				
				final int dx = x1 - x2;		// Multiplied by 3
				
				final double d = (dx * dx * 9) + (dy * dy * 3);
				
				// Now multiply this up so we're measuring the distance from the centre of one hex to another as '1'
				distance = Math.sqrt(d / 3) / 2d;				
				break;
			}
			
			default:
				throw new IllegalArgumentException ("determineReal2DDistanceBetween: Does not support " + sys.getCoordinateSystemType () + " coordinate systems");
		}

		return distance;
	}

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Distance ("as the crow flies") between two sets of coordinates
	 */
	@Override
	public final double determineReal2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates2DEx first, final MapCoordinates2DEx second)
	{
		return determineReal2DDistanceBetween (sys, first.getX (), first.getY (), second.getX (), second.getY ());
	}

	/**
	 * @param sys Coordinate system
	 * @param first First set of coordinates
	 * @param second Second set of coordinates
	 * @return Distance ("as the crow flies") between two sets of coordinates; NB. Although this passes in 3D coords, the Z-component is ignored - the distance is calc'd in the 2D plane
	 */
	@Override
	public final double determineReal2DDistanceBetween (final CoordinateSystem sys, final MapCoordinates3DEx first, final MapCoordinates3DEx second)
	{
		return determineReal2DDistanceBetween (sys, first.getX (), first.getY (), second.getX (), second.getY ());
	}
}