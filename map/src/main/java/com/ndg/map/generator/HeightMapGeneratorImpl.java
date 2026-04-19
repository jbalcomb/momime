package com.ndg.map.generator;

import java.util.ArrayList;
import java.util.List;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.areas.storage.MapArea2DArrayListImpl;
import com.ndg.utils.random.RandomUtils;

/**
 * Generates plasma fractal height maps, that are used as the basis of both the overland and combat map generators
 */
public final class HeightMapGeneratorImpl implements HeightMapGenerator
{
	/** Coordinate system we are generating a height map for */
	private CoordinateSystem coordinateSystem;

	/** Width of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes */
	private int zoneWidth;

	/** Height of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes */
	private int zoneHeight;

	/** Number of rows from non-wrapping edges were we may place tundra */
	private final List<Integer> numberOfRowsFromMapEdgeToAvoid = new ArrayList<Integer> ();

	/** Generated height map */
	private final MapArea2D<Integer> heightMap = new MapArea2DArrayListImpl<Integer> ();

	/** Height map with minimum value on each plane adjusted to zero; this can either be set if caller wants to receive the detailed map, or left null and one will be created */
	private MapArea2D<Integer> zeroBasedHeightMap;

	/** Number of occurrences of each height on each plane - outer list contains the two planes; inner list is the list of heights */
	private final List<Integer> heightCounts = new ArrayList<Integer> ();

	/** Random number generator */
	private RandomUtils randomUtils;
	
	/**
	 * Generates the height map; convenience method for calling the normal 3 methods in sequence
	 */
	@Override
	public final void generateHeightMap ()
	{
		generateFractalLandscape ();
		generateZeroBasedHeightMap ();
		countTilesAtEachHeight ();
	}

	/**
	 * Combat map generation shares this, hence it being static, package private and taking all values an inputs rather than from the map generator object
	 *
	 * @param x X coordinate to test
	 * @param y Y coordinate to test
	 * @param dist Distance away from non-wrapping edges to return "true" for
	 * @return True if the requested coordinates near a non-wrapping edge
	 */
	final boolean nearSingularity (final int x, final int y, final int dist)
	{
		return (((!getCoordinateSystem ().isWrapsLeftToRight ()) && ((x < dist) || (x >= getCoordinateSystem ().getWidth () - dist))) ||
					((!getCoordinateSystem ().isWrapsTopToBottom ()) && ((y < dist) || (y >= getCoordinateSystem ().getHeight () - dist))));
	}

	/**
	 * Sets the value only if not previously set (is null)
	 * Combat map generation shares this, hence it being static and taking all values an inputs rather than from the map generator object
	 *
	 * @param x X coordinate to set
	 * @param y Y coordinate to set
	 * @param value Value to set
	 */
	final void setMidPoints (final int x, final int y, final int value)
	{
		if (getHeightMap ().get (x, y) == null)
			getHeightMap ().set (x, y, value);
	}

	/**
	 * Performs one iteraction of the fractal landscape algorithm
	 * Given 4 corner points, chooses values for the 4 midpoints and centrepoint
	 * This was originally based on the routine "gen5rec" in mapgen.c from FreeCiv.
	 * The method in FreeCiv has since been moved to height_map.c.
	 * Both of these are in the "Master of Magic\Reference code" folder.
	 *
	 * Combat map generation shares this, hence it being static and taking all values an inputs rather than from the map generator object
	 *
	 * @param step Used to scale the variation in the height
	 * @param x0 Left edge of the area
	 * @param y0 Top edge of the area
	 * @param x1 Right edge of the area
	 * @param y1 Bottom edge of the area
	 */
	private final void fractalLandscapeIteration (final int step, final int x0, final int y0, final int x1, final int y1)
	{
		// All x and y values are native
		if (((y1 - y0 <= 0) || (x1 - x0 <= 0)) || ((y1 - y0 == 1) && (x1 - x0 == 1)))
		{
			// Do nothing
		}
		else
		{
			//  To wrap correctly
			final int x1wrap;
			if (x1 == getCoordinateSystem ().getWidth ())
				x1wrap = 0;
			else
				x1wrap = x1;

			final int y1wrap;
			if (y1 == getCoordinateSystem ().getHeight ())
				y1wrap = 0;
			else
				y1wrap = y1;

			// Read the current corner values from the height map
			// These should always be reading existing values so should never be null
			final int val00 = getHeightMap ().get (x0, y0);
			final int val01 = getHeightMap ().get (x0, y1wrap);
			final int val10 = getHeightMap ().get (x1wrap, y0);
			final int val11 = getHeightMap ().get (x1wrap, y1wrap);

			// Find mid points
			final int xmid = (x0 + x1) / 2;
			final int ymid = (y0 + y1) / 2;

			// Set midpoints of sides to avg of side's vertices plus a random factor
			// Unset points are null, don't reset if set
			setMidPoints (xmid,		y0,		((val00 + val10) / 2) + getRandomUtils ().nextInt (step) - (step / 2));
			setMidPoints (xmid,		y1wrap,	((val01 + val11) / 2) + getRandomUtils ().nextInt (step) - (step / 2));
			setMidPoints (x0,		ymid,		((val00 + val01) / 2) + getRandomUtils ().nextInt (step) - (step / 2));
			setMidPoints (x1wrap,	ymid,		((val10 + val11) / 2) + getRandomUtils ().nextInt (step) - (step / 2));

			// Set middle to average of midpoints plus a random factor, if not set
			setMidPoints (xmid, ymid, ((val00 + val01 + val10 + val11) / 4) + getRandomUtils ().nextInt (step) - (step / 2));

			// Now call recursively on the four subrectangles
			final int newStep = (2 * step) / 3;
			fractalLandscapeIteration (newStep, x0,		y0,	xmid,	ymid);
			fractalLandscapeIteration (newStep, x0,		ymid,	xmid,	y1);
			fractalLandscapeIteration (newStep, xmid,	y0,	x1,	ymid);
			fractalLandscapeIteration (newStep, xmid,	ymid,	x1,	y1);
		}
	}

	/**
	 * Generates a landscape height map via a plasma fractal
	 * This was originally based on the routine "mapgenerator5" in mapgen.c from FreeCiv.
	 * The method in FreeCiv has since been renamed to "make_pseudofractal1_hmap" and moved to height_map.c.
	 * Both of these are in the "Master of Magic\Reference code" folder.
	 */
	@Override
	public final void generateFractalLandscape ()
	{
		// Session description contains zone width/height - we need the number of zones
		final int xBlocks = (int) Math.round (getCoordinateSystem ().getWidth () / (double) getZoneWidth ());	// a.k.a. zonesHorizontally
		final int yBlocks = (int) Math.round (getCoordinateSystem ().getHeight () / (double) getZoneHeight ());	// a.k.a. zonesVertically

		// If the edge wraps, the last block ends with the first point, so there's the same number of blocks and dividing points
		// If the edge doesn't wrap, we need a point on the right/bottom of it as well to end the last block
		final int xPoints;
		if (getCoordinateSystem ().isWrapsLeftToRight ())
			xPoints = xBlocks;
		else
			xPoints = xBlocks + 1;

		final int yPoints;
		if (getCoordinateSystem ().isWrapsTopToBottom ())
			yPoints = yBlocks;
		else
			yPoints = yBlocks + 1;

		// If the edge wraps, the last pretend block will be the first pixel off the edge of the map.
		// If the edge doesn't wrap, the last block is the actual last pixel
		final int xMax;
		if (getCoordinateSystem ().isWrapsLeftToRight ())
			xMax = getCoordinateSystem ().getWidth ();
		else
			xMax = getCoordinateSystem ().getWidth () - 1;

		final int yMax;
		if (getCoordinateSystem ().isWrapsTopToBottom ())
			yMax = getCoordinateSystem ().getHeight ();
		else
			yMax = getCoordinateSystem ().getHeight () - 1;

		// Just need something > log(max(xsize, ysize)) for the recursion???
		final int step = getCoordinateSystem ().getWidth () + getCoordinateSystem ().getHeight ();

		// Edges are avoided more strongly as this increases
		final int avoidEdge = step / 2;

		// Now generate a plasma fractal to use as the height map
		// Set initial points
		for (int xn = 0; xn < xPoints; xn++)
			for (int yn = 0; yn < yPoints; yn++)
			{
				final int x = (xn * xMax) / xBlocks;
				final int y = (yn * yMax) / yBlocks;

				// Randomize initial point
				int thisHeight = getRandomUtils ().nextInt (2 * step) - step;

				// Avoid edges (topological singularities)
				for (final Integer avoid : getNumberOfRowsFromMapEdgeToAvoid ())
					if (nearSingularity (x, y, avoid))		// cannot find actual value for CITY_MAP_RADIUS
						thisHeight = thisHeight - avoidEdge;

				// Set it
				getHeightMap ().set (x, y, thisHeight);
			}

		// Calculate recursively on each block
		for (int xn = 0; xn < xBlocks; xn++)
			for (int yn = 0; yn < yBlocks; yn++)
				fractalLandscapeIteration (step, (xn * xMax) / xBlocks, (yn * yMax) / yBlocks, ((xn + 1) * xMax) / xBlocks, ((yn + 1) * yMax) / yBlocks);
	}

	/**
	 * Raises the height map so that the lowest point of land is guaranteed to be at height 0
	 */
	@Override
	public final void generateZeroBasedHeightMap ()
	{
		if (getZeroBasedHeightMap () == null)
		{
			setZeroBasedHeightMap (new MapArea2DArrayListImpl<Integer> ());
			getZeroBasedHeightMap ().setCoordinateSystem (getCoordinateSystem ());
		}
		
		// Find the minimum height
		int minimumHeight = getHeightMap ().get (0, 0);
		for (int x = 0; x < getCoordinateSystem ().getWidth (); x++)
			for (int y = 0; y < getCoordinateSystem ().getHeight (); y++)
			{
				final int thisHeight = getHeightMap ().get (x, y);
				if (thisHeight < minimumHeight)
					minimumHeight = thisHeight;
			}

		// Copy + adjust the height map
		for (int x = 0; x < getCoordinateSystem ().getWidth (); x++)
			for (int y = 0; y < getCoordinateSystem ().getHeight (); y++)
				getZeroBasedHeightMap ().set (x, y, getHeightMap ().get (x, y) - minimumHeight);
	}

	/**
	 * Counts how many tiles there are of each particular height
	 */
	@Override
	public final void countTilesAtEachHeight ()
	{
		// This relies on the fact that there are no entries in the height map with negative values
		// so by starting at zero and working up, we've guaranteed to evenually hit every tile on the map
		getHeightCounts ().clear ();
		int tilesDone = 0;
		while (tilesDone < getCoordinateSystem ().getWidth () * getCoordinateSystem ().getHeight ())
		{
			// Count tiles with this height
			int count = 0;
			for (int x = 0; x < getCoordinateSystem ().getWidth (); x++)
				for (int y = 0; y < getCoordinateSystem ().getHeight (); y++)
					if (getZeroBasedHeightMap ().get (x, y) == getHeightCounts ().size ())
						count++;

			// Add to the list
			getHeightCounts ().add (Integer.valueOf (count));
			tilesDone = tilesDone + count;
		}
	}

	/**
	 * @param threshold Height to test
	 * @return Number of tiles whose height is at least threshold
	 */
	@Override
	public final int countTilesAboveThreshold (final int threshold)
	{
		int count = 0;
		for (int index = threshold; index < getHeightCounts ().size (); index++)
			count = count + getHeightCounts ().get (index);

		return count;
	}

	/**
	 * @param threshold Height to test
	 * @return Number of tiles whose height is at most threshold
	 */
	@Override
	public final int countTilesBelowThreshold (final int threshold)
	{
		int count = 0;
		for (int index = 0; index <= threshold; index++)
			count = count + getHeightCounts ().get (index);

		return count;
	}
	
	/**
	 * @param desiredTileCount Number of tiles to aim for
	 * @return Height where there are approximately the desired number of tiles at equal to or above this height
	 */
	@Override
	public final int findHeightWithSpecifiedTileCountAbove (final int desiredTileCount)
	{
		// Check zero first
		int bestThreshold = 0;
		int bestValue = Math.abs (countTilesAboveThreshold (bestThreshold) - desiredTileCount);

		// Then try all others
		for (int thisThreshold = 1; thisThreshold < getHeightCounts ().size (); thisThreshold++)
		{
			final int thisValue = Math.abs (countTilesAboveThreshold (thisThreshold) - desiredTileCount);
			if (thisValue < bestValue)
			{
				bestThreshold = thisThreshold;
				bestValue = thisValue;
			}
		}
		
		return bestThreshold;
	}
	
	/**
	 * @param desiredTileCount Number of tiles to aim for
	 * @return Height where there are approximately the desired number of tiles at equal to or below this height
	 */
	@Override
	public final int findHeightWithSpecifiedTileCountBelow (final int desiredTileCount)
	{
		// Check zero first
		int bestThreshold = 0;
		int bestValue = Math.abs (countTilesBelowThreshold (bestThreshold) - desiredTileCount);

		// Then try all others
		for (int thisThreshold = 1; thisThreshold < getHeightCounts ().size (); thisThreshold++)
		{
			final int thisValue = Math.abs (countTilesBelowThreshold (thisThreshold) - desiredTileCount);
			if (thisValue < bestValue)
			{
				bestThreshold = thisThreshold;
				bestValue = thisValue;
			}
		}
		
		return bestThreshold;
	}

	/**
	 * Finds the height in the heightmap for which as close as possible to DesiredTileCount tiles are above that height and the remainder are below it
	 * Then sets all tiles above this height to have scenery combatTileTypeID
	 *
	 * @param desiredTileCount How many tiles to set to this tile type
	 * @param callback Method to trigger on each of the tiles to update
	 */
	@Override
	public final void setHighestTiles (final int desiredTileCount, final ProcessTileCallback callback)
	{
		final int bestThreshold = findHeightWithSpecifiedTileCountAbove (desiredTileCount);

		// Then set scenery above this threshold
		for (int x = 0; x < getCoordinateSystem ().getWidth (); x++)
			for (int y = 0; y < getCoordinateSystem ().getHeight (); y++)
				if (getZeroBasedHeightMap ().get (x, y) >= bestThreshold)
					callback.process (x, y);
	}

	/**
	 * Finds the height in the heightmap for which as close as possible to DesiredTileCount tiles are below that height and the remainder are above it
	 * Then sets all tiles above this height to have scenery combatTileTypeID
	 *
	 * @param desiredTileCount How many tiles to set to this tile type
	 * @param callback Method to trigger on each of the tiles to update
	 */
	@Override
	public final void setLowestTiles (final int desiredTileCount, final ProcessTileCallback callback)
	{
		final int bestThreshold = findHeightWithSpecifiedTileCountBelow (desiredTileCount);

		// Then set scenery below this threshold
		for (int x = 0; x < getCoordinateSystem ().getWidth (); x++)
			for (int y = 0; y < getCoordinateSystem ().getHeight (); y++)
				if (getZeroBasedHeightMap ().get (x, y) <= bestThreshold)
					callback.process (x, y);
	}
	
	/**
	 * @return Number of rows from non-wrapping edges were we may place tundra
	 */
	@Override
	public final List<Integer> getNumberOfRowsFromMapEdgeToAvoid ()
	{
		return numberOfRowsFromMapEdgeToAvoid;
	}
	
	/**
	 * @return Coordinate system we are generating a height map for
	 */
	@Override
	public final CoordinateSystem getCoordinateSystem ()
	{
		return coordinateSystem;
	}

	/**
	 * @param c Coordinate system we are generating a height map for
	 */
	@Override
	public final void setCoordinateSystem (final CoordinateSystem c)
	{
		coordinateSystem = c;
		getHeightMap ().setCoordinateSystem (c);
		
		if (getZeroBasedHeightMap () != null)
			getZeroBasedHeightMap ().setCoordinateSystem (c);
	}
	
	/**
	 * @return Generated height map
	 */
	@Override
	public final MapArea2D<Integer> getHeightMap ()
	{
		return heightMap;
	}
	
	/**
	 * @return Height map with minimum value on each plane adjusted to zero; this can either be set if caller wants to receive the detailed map, or left null and one will be created
	 */
	@Override
	public final MapArea2D<Integer> getZeroBasedHeightMap ()
	{
		return zeroBasedHeightMap;
	}

	/**
	 * @param m Height map with minimum value on each plane adjusted to zero; this can either be set if caller wants to receive the detailed map, or left null and one will be created
	 */
	@Override
	public final void setZeroBasedHeightMap (final MapArea2D<Integer> m)
	{
		zeroBasedHeightMap = m;
	}
	
	/**
	 * @return Number of occurrences of each height on each plane - outer list contains the two planes; inner list is the list of heights
	 */
	@Override
	public final List<Integer> getHeightCounts ()
	{
		return heightCounts;
	}

	/**
	 * @return Width of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	@Override
	public final int getZoneWidth ()
	{
		return zoneWidth;
	}

	/**
	 * @param w Width of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	@Override
	public final void setZoneWidth (final int w)
	{
		zoneWidth = w;
	}

	/**
	 * @return Height of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	@Override
	public final int getZoneHeight ()
	{
		return zoneHeight;
	}

	/**
	 * @param h Height of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	@Override
	public final void setZoneHeight (final int h)
	{
		zoneHeight = h;
	}
	
	/**
	 * @return Random number generator
	 */
	@Override
	public final RandomUtils getRandomUtils ()
	{
		return randomUtils;
	}

	/**
	 * @param utils Random number generator
	 */
	@Override
	public final void setRandomUtils (final RandomUtils utils)
	{
		randomUtils = utils;
	}
}