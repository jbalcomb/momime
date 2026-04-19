package com.ndg.map.generator;

import java.util.List;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.utils.random.RandomUtils;

/**
 * Generates plasma fractal height maps, that are used as the basis of both the overland and combat map generators
 */
public interface HeightMapGenerator
{
	/**
	 * Generates the height map; convenience method for calling the normal 3 methods in sequence
	 */
	public void generateHeightMap ();

	/**
	 * Generates a landscape height map via a plasma fractal
	 * This was originally based on the routine "mapgenerator5" in mapgen.c from FreeCiv.
	 * The method in FreeCiv has since been renamed to "make_pseudofractal1_hmap" and moved to height_map.c.
	 * Both of these are in the "Master of Magic\Reference code" folder.
	 */
	public void generateFractalLandscape ();
	
	/**
	 * Raises the height map so that the lowest point of land is guaranteed to be at height 0
	 */
	public void generateZeroBasedHeightMap ();
	
	/**
	 * Counts how many tiles there are of each particular height
	 */
	public void countTilesAtEachHeight ();
	
	/**
	 * @param threshold Height to test
	 * @return Number of tiles whose height is at least threshold
	 */
	public int countTilesAboveThreshold (final int threshold);

	/**
	 * @param threshold Height to test
	 * @return Number of tiles whose height is at most threshold
	 */
	public int countTilesBelowThreshold (final int threshold);
	
	/**
	 * @param desiredTileCount Number of tiles to aim for
	 * @return Height where there are approximately the desired number of tiles at equal to or above this height
	 */
	public int findHeightWithSpecifiedTileCountAbove (final int desiredTileCount);
	
	/**
	 * @param desiredTileCount Number of tiles to aim for
	 * @return Height where there are approximately the desired number of tiles at equal to or below this height
	 */
	public int findHeightWithSpecifiedTileCountBelow (final int desiredTileCount);
	
	/**
	 * Finds the height in the heightmap for which as close as possible to DesiredTileCount tiles are above that height and the remainder are below it
	 * Then sets all tiles above this height to have scenery combatTileTypeID
	 *
	 * @param desiredTileCount How many tiles to set to this tile type
	 * @param callback Method to trigger on each of the tiles to update
	 */
	public void setHighestTiles (final int desiredTileCount, final ProcessTileCallback callback);
	
	/**
	 * Finds the height in the heightmap for which as close as possible to DesiredTileCount tiles are below that height and the remainder are above it
	 * Then sets all tiles above this height to have scenery combatTileTypeID
	 *
	 * @param desiredTileCount How many tiles to set to this tile type
	 * @param callback Method to trigger on each of the tiles to update
	 */
	public void setLowestTiles (final int desiredTileCount, final ProcessTileCallback callback);
	
	/**
	 * @return Number of rows from non-wrapping edges were we may place tundra
	 */
	public List<Integer> getNumberOfRowsFromMapEdgeToAvoid ();
	
	/**
	 * @return Coordinate system we are generating a height map for
	 */
	public CoordinateSystem getCoordinateSystem ();

	/**
	 * @param c Coordinate system we are generating a height map for
	 */
	public void setCoordinateSystem (CoordinateSystem c);
	
	/**
	 * @return Generated height map
	 */
	public MapArea2D<Integer> getHeightMap ();

	/**
	 * @return Height map with minimum value on each plane adjusted to zero
	 */
	public MapArea2D<Integer> getZeroBasedHeightMap ();

	/**
	 * @param m Height map with minimum value on each plane adjusted to zero
	 */
	public void setZeroBasedHeightMap (final MapArea2D<Integer> m);
	
	/**
	 * @return Number of occurrences of each height on each plane - outer list contains the two planes; inner list is the list of heights
	 */
	public List<Integer> getHeightCounts ();

	/**
	 * @return Width of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	public int getZoneWidth ();

	/**
	 * @param w Width of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	public void setZoneWidth (final int w);

	/**
	 * @return Height of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	public int getZoneHeight ();

	/**
	 * @param h Height of each "zone" - small values produce more random jagged landscape, larger zones produce more gradual height changes
	 */
	public void setZoneHeight (final int h);
	
	/**
	 * @return Random number generator
	 */
	public RandomUtils getRandomUtils ();

	/**
	 * @param utils Random number generator
	 */
	public void setRandomUtils (final RandomUtils utils);
}