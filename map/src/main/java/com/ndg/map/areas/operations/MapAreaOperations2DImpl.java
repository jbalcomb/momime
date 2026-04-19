package com.ndg.map.areas.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.utils.Holder;

/**
 * Implements the general methods from MapAreaOperations and MapAreaOperations2D
 *
 * @param <E> Type of object held at each cell
 */
public class MapAreaOperations2DImpl<E> extends MapAreaOperationsImpl<E, MapCoordinates2DEx> implements MapAreaOperations2D<E>
{
	/**
	 * @param sys Coordinate system to trace around
	 * @param callback Operation to call against every set of coordinates in the map area
	 */
	@Override
	public final void processAllCoordinates (final CoordinateSystem sys, final ProcessMapCoordinates<MapCoordinates2DEx> callback)
	{
		getCoordinateSystemUtils ().processAll2DCoordinates (sys, callback);
	}
	
	/**
	 * Triggers a callback on every set of coordinates within a certain radius
	 * 
	 * @param sys Coordinate system to trace around
	 * @param x X coordinate of centre of area to set
	 * @param y Y coordinate of centre of area to set
	 * @param radius Radius to set (negative = deselect nothing, 0 = deselect this cell only, 1 = 1 ring around this cell, and so on)
	 * @param callback Operation to call against every set of coordinates within the specified radius
	 */
	@Override
	public final void processCoordinatesWithinRadius (final CoordinateSystem sys, final int x, final int y, final int radius,
		final ProcessMapCoordinates<MapCoordinates2DEx> callback)
	{
		getCoordinateSystemUtils ().processCoordinatesWithinRadius (sys, x, y, radius, (px, py, r, d, n) ->
		{
			callback.process (new MapCoordinates2DEx (px, py));
			return true;
		});
	}

	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param callback Operation to call against every set of coordinates within the polygon
	 */
	@Override
	public final void processCoordinatesWithinPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final ProcessMapCoordinates<MapCoordinates2DEx> callback)
	{
		if (area.getCoordinateSystem ().getCoordinateSystemType () != CoordinateSystemType.SQUARE)
			throw new IllegalArgumentException ("processCoordinatesWithinPolygon: Does not support " + area.getCoordinateSystem ().getCoordinateSystemType () +
				" coordinate systems");
		
		if (coords.size () > 1)
		{
			// This uses a fabulously straightforward edge-counting algorithm as it scans down each line, got this from here - http://alienryderflex.com/polygon_fill
			// Find the directions of each edge
			final List<MapCoordinates2DEx> edges = new ArrayList<MapCoordinates2DEx> ();
			int j = coords.size () - 1;
			for (int i = 0; i < coords.size (); i++)
			{
				int dx = coords.get (j).getX () - coords.get (i).getX ();
				int dy = coords.get (j).getY () - coords.get (i).getY ();
				
				if (area.getCoordinateSystem ().isWrapsLeftToRight ())
				{
					final int adx1 = Math.abs (dx);
					final int adx2 = Math.abs (coords.get (j).getX () - coords.get (i).getX () - area.getCoordinateSystem ().getWidth ());
					final int adx3 = Math.abs (coords.get (j).getX () - coords.get (i).getX () + area.getCoordinateSystem ().getWidth ());
					final int adx = Math.min (Math.min (adx1, adx2), adx3);
					if (adx != adx1)
					{
						if (adx == adx2)
							dx = dx - area.getCoordinateSystem ().getWidth ();
						else
							dx = dx + area.getCoordinateSystem ().getWidth ();
					}						
				}

				if (area.getCoordinateSystem ().isWrapsTopToBottom ())
				{
					final int ady1 = Math.abs (dy);
					final int ady2 = Math.abs (coords.get (j).getY () - coords.get (i).getY () - area.getCoordinateSystem ().getHeight ());
					final int ady3 = Math.abs (coords.get (j).getY () - coords.get (i).getY () + area.getCoordinateSystem ().getHeight ());
					final int ady = Math.min (Math.min (ady1, ady2), ady3);
					if (ady != ady1)
					{
						if (ady == ady2)
							dy = dy - area.getCoordinateSystem ().getHeight ();
						else
							dy = dy + area.getCoordinateSystem ().getHeight ();
					}						
				}
				
				edges.add (new MapCoordinates2DEx (dx, dy));
				
				j = i;
			}
			
			// If we know the edges, we can now find the top and left of the shape
			int left = coords.get (0).getX ();
			int top = coords.get (0).getY ();
			int x = left;
			int y = top;

			for (int i = 1; i < coords.size (); i++)
			{
				x = x - edges.get (i).getX (); 
				y = y - edges.get (i).getY ();
				
				left = Math.min (left, x);
				top = Math.min (top, y);
			}
			
			// Offset the coords to make them all positive
			x = coords.get (0).getX () - left;
			y = coords.get (0).getY () - top;
			
			final List<MapCoordinates2DEx> newCoords = new ArrayList<MapCoordinates2DEx> ();
			newCoords.add (new MapCoordinates2DEx (x, y));
			
			for (int i = 1; i < coords.size (); i++)
			{
				x = x - edges.get (i).getX (); 
				y = y - edges.get (i).getY ();
				
				newCoords.add (new MapCoordinates2DEx (x, y));
			}
			
			// Now scan each row
			final List<Integer> nodes = new ArrayList<Integer> ();
			boolean keepGoing = true;
			y = 0;
			while ((keepGoing) && (y < area.getCoordinateSystem ().getHeight ()))
			{
				nodes.clear ();
				
				// Find edges that intersect this y-line
				j = newCoords.size () - 1;
				for (int i = 0; i < newCoords.size (); i++)
				{
					if (((y > newCoords.get (i).getY ()) && (y <= newCoords.get (j).getY ())) ||
						((y > newCoords.get (j).getY ()) && (y <= newCoords.get (i).getY ())))
						
						nodes.add (newCoords.get (i).getX () +
							(((y - newCoords.get (i).getY ()) * edges.get (i).getX ()) / edges.get (i).getY ()));
					
					j = i;
				}
				
				// Sort them
				Collections.sort (nodes);
				
				// Now its simple to fill in the gaps
				int n = 0;
				while ((keepGoing) && (n < nodes.size () - 1))
				{
					x = nodes.get (n);
					while ((keepGoing) && (x <= nodes.get (n + 1)))
					{
						int nx = x + left;
						int ny = y + top;
						
						if ((nx < 0) && (area.getCoordinateSystem ().isWrapsLeftToRight ()))
							nx = nx + area.getCoordinateSystem ().getWidth ();

						if ((nx >= area.getCoordinateSystem ().getWidth ()) && (area.getCoordinateSystem ().isWrapsLeftToRight ()))
							nx = nx - area.getCoordinateSystem ().getWidth ();

						if ((ny < 0) && (area.getCoordinateSystem ().isWrapsTopToBottom ()))
							ny = ny + area.getCoordinateSystem ().getHeight ();

						if ((ny >= area.getCoordinateSystem ().getHeight ()) && (area.getCoordinateSystem ().isWrapsTopToBottom ()))
							ny = ny - area.getCoordinateSystem ().getHeight ();
						
						if (getCoordinateSystemUtils ().are2DCoordinatesWithinRange (area.getCoordinateSystem (), nx, ny))
							keepGoing = callback.process (new MapCoordinates2DEx (nx, ny));
						
						x++;
					}
					n++;
				}
				y++;
			}
		}
	}

	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param callback Operation to call against every cell within the polygon
	 */
	@Override
	public final void processCellsWithinPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final ProcessMapCell<E> callback)
	{
		processCoordinatesWithinPolygon (area, coords, (c) ->
		{
			return callback.process (area.get (c));
		});
	}
	
	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param value Value to set all cells inside the polygon equal to
	 */
	@Override
	public final void setPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final E value)
	{
		processCoordinatesWithinPolygon (area, coords, (c) ->
		{
			area.set (c, value);
			return true;
		});
	}
	
	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param callback Operation to call against every set of coordinates along the line
	 */
	@Override
	public final void processCoordinatesAlongLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end,
		final int width, final ProcessMapCoordinates<MapCoordinates2DEx> callback)
	{
		if (area.getCoordinateSystem ().getCoordinateSystemType () != CoordinateSystemType.SQUARE)
			throw new IllegalArgumentException ("processCoordinatesAlongLine: Does not support " + area.getCoordinateSystem ().getCoordinateSystemType () +
				" coordinate systems");
		
		if (!start.equals (end))
		{
			// Figure out the direction of the line, taking into account wrapping edges
			int dx = end.getX () - start.getX ();
			int dy = end.getY () - start.getY ();
			
			if (area.getCoordinateSystem ().isWrapsLeftToRight ())
			{
				final int adx1 = Math.abs (dx);
				final int adx2 = Math.abs (end.getX () - start.getX () - area.getCoordinateSystem ().getWidth ());
				final int adx3 = Math.abs (end.getX () - start.getX () + area.getCoordinateSystem ().getWidth ());
				final int adx = Math.min (Math.min (adx1, adx2), adx3);
				if (adx != adx1)
				{
					if (adx == adx2)
						dx = dx - area.getCoordinateSystem ().getWidth ();
					else
						dx = dx + area.getCoordinateSystem ().getWidth ();
				}						
			}

			if (area.getCoordinateSystem ().isWrapsTopToBottom ())
			{
				final int ady1 = Math.abs (dy);
				final int ady2 = Math.abs (end.getY () - start.getY () - area.getCoordinateSystem ().getHeight ());
				final int ady3 = Math.abs (end.getY () - start.getY () + area.getCoordinateSystem ().getHeight ());
				final int ady = Math.min (Math.min (ady1, ady2), ady3);
				if (ady != ady1)
				{
					if (ady == ady2)
						dy = dy - area.getCoordinateSystem ().getHeight ();
					else
						dy = dy + area.getCoordinateSystem ().getHeight ();
				}						
			}
			
			final int adx = Math.abs (dx);
			final int ady = Math.abs (dy);
			final int xDirection = (dx > 0) ? 3 : 7;
			final int yDirection = (dy > 0) ? 5 : 1;
			
			// Keeps track of when to step down to next row
			int fractionalPart;

			final MapCoordinates2DEx coords = new MapCoordinates2DEx (start);
			if (adx > ady)
			{
				// Step along in x direction and calculate y's
				fractionalPart = adx / 2;
				for (int step = 0; step <= adx; step++)
				{
					callback.process (coords);
					
					// Draw line wide?
					if (width > 0)
						for (final int d : new int [] {5, 1})
						{
							final MapCoordinates2DEx wide = new MapCoordinates2DEx (coords);
							for (int w = 0; w < width; w++)
							{
								if (getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), wide, d))
									callback.process (wide);
							}
						}
					
					// Move to next spot
					getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, xDirection);
					
					// Step down?
					fractionalPart = fractionalPart + ady;
					if (fractionalPart >= adx)
					{
						fractionalPart = fractionalPart - adx;
						getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, yDirection);
					}
				}
			}
			else
			{
				// Step along in y direction and calculate x''s
				fractionalPart = ady / 2;
				for (int step = 0; step <= ady; step++)
				{
					callback.process (coords);

					// Draw line wide?
					if (width > 0)
						for (final int d : new int [] {3, 7})
						{
							final MapCoordinates2DEx wide = new MapCoordinates2DEx (coords);
							for (int w = 0; w < width; w++)
							{
								if (getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), wide, d))
									callback.process (wide);
							}
						}
					
					// Move to next spot
					getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, yDirection);

					// Step across?
					fractionalPart = fractionalPart + adx;
					if (fractionalPart >= ady)
					{
						fractionalPart = fractionalPart - ady;
						getCoordinateSystemUtils ().move2DCoordinates (area.getCoordinateSystem (), coords, xDirection);
					}
				}
			}
		}
	}

	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param callback Operation to call against every cell along the line
	 */
	@Override
	public final void processCellsAlongLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end,
		final int width, final ProcessMapCell<E> callback)
	{
		processCoordinatesAlongLine (area, start, end, width, (coords) ->
		{
			return callback.process (area.get (coords));
		});
	}

	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param value Value to set along the line
	 */
	@Override
	public final void setLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end, final int width, final E value)
	{
		processCoordinatesAlongLine (area, start, end, width, (coords) ->
		{
			area.set (coords, value);
			return true;
		});
	}
	
	/**
	 * @param area Area to search over
	 * @param coords Coords of centre to scan out from
	 * @param value Value to look for
	 * @return Coordinates of cell closest to specified location that matches specified value, or null if no cell in the entire area matches the specified value
	 */
	@Override
	public final MapCoordinates2DEx findClosestMatchingCell (final MapArea2D<E> area, final MapCoordinates2DEx coords, final E value)
	{
		final Holder<MapCoordinates2DEx> match = new Holder<MapCoordinates2DEx> ();
		getCoordinateSystemUtils ().processCoordinatesWithinRadius (area.getCoordinateSystem (), coords.getX (), coords.getY (), Integer.MAX_VALUE, (x, y, r, d, n) ->
		{
			final E thisValue = area.get (x, y);
			if (((value == null) && (thisValue == null)) || ((value != null) && (value.equals (thisValue))))
				match.setValue (new MapCoordinates2DEx (x, y));
			
			return (match.getValue () == null);
		});
		
		return match.getValue ();
	}
}