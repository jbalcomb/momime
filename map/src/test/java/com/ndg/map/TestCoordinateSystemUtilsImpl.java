package com.ndg.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Tests the CoordinateSystemUtilsImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestCoordinateSystemUtilsImpl
{
	/**
	 * @return Demo MoM overland map-like coordinate system with a 60x40 square map wrapping left-to-right but not top-to-bottom
	 */
	private final CoordinateSystem createSquareCoordinateSystem ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		sys.setWidth (60);
		sys.setHeight (40);
		sys.setDepth (2);
		sys.setWrapsLeftToRight (true);
		return sys;
	}

	/**
	 * @return Demo coordinate system with a small hex map
	 */
	private final CoordinateSystem createHexCoordinateSystem ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);
		sys.setWidth (20);
		sys.setHeight (20);
		sys.setDepth (2);
		return sys;
	}

	/**
	 * @return Demo coordinate system with a small vertical hex map
	 */
	private final CoordinateSystem createVerticalHexCoordinateSystem ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.VERTICAL_HEX);
		sys.setWidth (20);
		sys.setHeight (20);
		sys.setDepth (2);
		return sys;
	}

	/**
	 * @return Demo MoM combat map-like coordinate system with a 12x25 diamond map
	 */
	private final CoordinateSystem createDiamondCoordinateSystem ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.DIAMOND);
		sys.setWidth (12);
		sys.setHeight (25);
		sys.setDepth (2);
		return sys;
	}

	/**
	 * @return Example wrapped diamond coordinate system
	 */
	private final CoordinateSystem createWrappedDiamondCoordinateSystem ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.WRAPPED_DIAMOND);
		sys.setWidth (12);
		sys.setHeight (24);
		sys.setDepth (2);
		sys.setWrapsLeftToRight (true);
		return sys;
	}

	/**
	 * Lists of test path movements for a square map that will never go outside the map boundary
	 * First element in each row is a direction to move in; 2nd and 3rd elements are the coordinates this move should take us to
	 * This is based on the sample pic of the coordinate layout in Map.xsd
	 */
	private final static int [] [] SQUARE_PATH_WITHIN_BOUNDARY = {
		{0, 1, 1},
		{1, 1, 0},
		{3, 2, 0},
		{5, 2, 1},
		{7, 1, 1},
		{2, 2, 0},
		{4, 3, 1},
		{6, 2, 2},
		{8, 1, 1}};

	/**
	 * Lists of test path movements for a square map that goes immediately outside the map boundary
	 */
	private final static int [] [] SQUARE_PATH_OUTSIDE_BOUNDARY = {
		{0, 0, 0},
		{1, 0, -1},
		{2, 1, -2},
		{3, 2, -2},
		{4, 3, -1}};

	/**
	 * Lists of test path movements for a square map that wraps along the left-right edge
	 */
	private final static int [] [] SQUARE_PATH_WRAPS = {
		{0, 0, 0},
		{6, 59, 1},
		{7, 58, 1},
		{2, 59, 0},
		{3, 0, 0}};

	/**
	 * Lists of test path movements for a hex map that will never go outside the map boundary
	 */
	private final static int [] [] HEX_PATH_WITHIN_BOUNDARY = {
		{0, 0, 1},
		{2, 1, 0},
		{2, 2, 0},
		{3, 3, 0},
		{3, 4, 1},
		{5, 3, 1},
		{5, 2, 2},
		{6, 1, 1},
		{6, 0, 1},
		{4, 0, 2},
		{1, 0, 1}};

	/**
	 * Lists of test path movements for a hex map that goes immediately outside the map boundary
	 */
	private final static int [] [] HEX_PATH_OUTSIDE_BOUNDARY = {
		{0, 0, 0},
		{5, -1, 0},
		{5, -2, 1},
		{6, -3, 0},
		{6, -4, 0},
		{4, -4, 1},
		{1, -4, 0},
		{3, -3, 0},
		{3, -2, 1},
		{2, -1, 0}};

	/**
	 * Lists of test path movements for a vertical hex map that will never go outside the map boundary
	 */
	private final static int [] [] VERTICAL_HEX_PATH_WITHIN_BOUNDARY = {
		{0, 0, 2},
		{1, 0, 1},
		{1, 1, 0},
		{2, 2, 0},
		{3, 2, 1},
		{3, 3, 2},
		{4, 2, 3},
		{4, 2, 4},
		{5, 1, 4},
		{6, 0, 3},
		{6, 0, 2}};

	/**
	 * Lists of test path movements for a vertical hex map that goes immediately outside the map boundary
	 */
	private final static int [] [] VERTICAL_HEX_PATH_OUTSIDE_BOUNDARY = {
		{0, 0, 0},
		{4, -1, 1},
		{4, -1, 2},
		{6, -2, 1},
		{6, -2, 0},
		{2, -1, 0},
		{5, -2, 0},
		{1, -2, -1},
		{1, -1, -2},
		{3, -1, -1}};
	
	/**
	 * Lists of test path movements for a diamond map that will never go outside the map boundary
	 */
	private final static int [] [] DIAMOND_PATH_WITHIN_BOUNDARY = {
		{0, 0, 2},
		{2, 0, 1},
		{2, 1, 0},
		{4, 1, 1},
		{4, 2, 2},
		{6, 1, 3},
		{6, 1, 4},
		{8, 0, 3},
		{8, 0, 2},
		{3, 1, 2},
		{5, 1, 4},
		{7, 0, 4},
		{1, 0, 2}};

	/**
	 * Lists of test path movements for a diamond map that goes immediately outside the map boundary
	 */
	private final static int [] [] DIAMOND_PATH_OUTSIDE_BOUNDARY_NO_WRAPPING = {
		{0, 0, 2},
		{6, -1, 3},
		{6, -1, 4},
		{8, -2, 3},
		{8, -2, 2},
		{2, -2, 1},
		{2, -1, 0},
		{4, -1, 1}};

	/**
	 * Lists of test path movements for a diamond map that wraps around the left-right boundary
	 */
	private final static int [] [] DIAMOND_PATH_OUTSIDE_BOUNDARY_WITH_WRAPPING = {
		{0, 0, 2},
		{6, 11, 3},
		{6, 11, 4},
		{8, 10, 3},
		{8, 10, 2},
		{2, 10, 1},
		{2, 11, 0},
		{4, 11, 1}};
	
	/**
	 * Lists of test path movements for a wrapped diamond map that will never go outside the map boundary
	 */
	private final static int [] [] WRAPPED_DIAMOND_PATH_WITHIN_BOUNDARY = {
		{0, 1, 1},
		{3, 2, 1},
		{5, 1, 3},
		{7, 0, 3},
		{1, 1, 1},
		{2, 2, 0},
		{4, 2, 1},
		{6, 1, 2},
		{8, 1, 1}};		

	/**
	 * Lists of test path movements for a wrapped diamond map that goes above the map boundary and also crosses the left-right wrapping border
	 */
	private final static int [] [] WRAPPED_DIAMOND_PATH_OUTSIDE_BOUNDARY = {
		{0, 0, 0},
		{5, 11, 2},
		{6, 10, 3},
		{7, 9, 3},
		{8, 9, 2},
		{1, 10, 0},
		{2, 11, -1},
		{3, 0, -1},
		{4, 0, 0}};
	
	/**
	 * Tests the are2DCoordinatesWithinRange method
	 */
	@Test
	public final void testAre2DCoordinatesWithinRange ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		assertTrue (utils.are2DCoordinatesWithinRange (sys, 0, 0));
		assertTrue (utils.are2DCoordinatesWithinRange (sys, 59, 39));

		assertFalse (utils.are2DCoordinatesWithinRange (sys, -1, 0));
		assertFalse (utils.are2DCoordinatesWithinRange (sys, 0, -1));
		assertFalse (utils.are2DCoordinatesWithinRange (sys, 60, 39));
		
		// Test one with a coordinates object
		final MapCoordinates2DEx coords = new MapCoordinates2DEx (59, 40);
		assertFalse (utils.are2DCoordinatesWithinRange (sys, coords));
	}

	/**
	 * Tests the are3DCoordinatesWithinRange method
	 */
	@Test
	public final void testAre3DCoordinatesWithinRange ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		assertTrue (utils.are3DCoordinatesWithinRange (sys, 0, 0, 0));
		assertTrue (utils.are3DCoordinatesWithinRange (sys, 59, 39, 1));

		assertFalse (utils.are3DCoordinatesWithinRange (sys, -1, 0, 0));
		assertFalse (utils.are3DCoordinatesWithinRange (sys, 0, -1, 1));
		assertFalse (utils.are3DCoordinatesWithinRange (sys, 60, 39, 0));
		assertFalse (utils.are3DCoordinatesWithinRange (sys, 10, 20, 2));
		assertFalse (utils.are3DCoordinatesWithinRange (sys, 59, 39, -1));
		
		// Test one with a coordinates object
		final MapCoordinates3DEx coords = new MapCoordinates3DEx (59, 39, 2);
		assertFalse (utils.are3DCoordinatesWithinRange (sys, coords));
	}

	/**
	 * Tests the getMaxDirection method
	 */
	@Test
	public final void testGetMaxDirection ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		assertEquals (8, utils.getMaxDirection (CoordinateSystemType.SQUARE));
		assertEquals (6, utils.getMaxDirection (CoordinateSystemType.HEX));
		assertEquals (6, utils.getMaxDirection (CoordinateSystemType.VERTICAL_HEX));
		assertEquals (8, utils.getMaxDirection (CoordinateSystemType.DIAMOND));
		assertEquals (8, utils.getMaxDirection (CoordinateSystemType.WRAPPED_DIAMOND));
	}

	/**
	 * Tests the normalizeDirection method
	 */
	@Test
	public final void testNormalizeDirection ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.SQUARE, -9));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.SQUARE, -8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.SQUARE, -7));
		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.SQUARE, -1));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.SQUARE, 0));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.SQUARE, 1));

		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.SQUARE, 8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.SQUARE, 9));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.SQUARE, 10));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.SQUARE, 16));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.SQUARE, 17));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.SQUARE, 18));

		assertEquals (5, utils.normalizeDirection (CoordinateSystemType.HEX, -7));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.HEX, -6));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.HEX, -5));
		assertEquals (5, utils.normalizeDirection (CoordinateSystemType.HEX, -1));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.HEX, 0));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.HEX, 1));

		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.HEX, 6));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.HEX, 7));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.HEX, 8));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.HEX, 12));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.HEX, 13));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.HEX, 14));

		assertEquals (5, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, -7));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, -6));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, -5));
		assertEquals (5, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, -1));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 0));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 1));

		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 6));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 7));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 8));
		assertEquals (6, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 12));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 13));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.VERTICAL_HEX, 14));

		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.DIAMOND, -9));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.DIAMOND, -8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.DIAMOND, -7));
		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.DIAMOND, -1));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 0));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 1));

		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 9));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 10));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 16));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 17));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.DIAMOND, 18));

		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, -9));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, -8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, -7));
		assertEquals (7, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, -1));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 0));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 1));

		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 8));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 9));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 10));
		assertEquals (8, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 16));
		assertEquals (1, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 17));
		assertEquals (2, utils.normalizeDirection (CoordinateSystemType.WRAPPED_DIAMOND, 18));
	}
	
	/**
	 * Tests the normalizeCoordinate method when the coordinate is already in range
	 */
	@Test
	public final void testNormalizeCoordinate ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		assertEquals (5, utils.normalizeCoordinate (5, 10));
	}

	/**
	 * Tests the normalizeCoordinate method when the coordinate needs to be bumped right
	 */
	@Test
	public final void testNormalizeCoordinate_negative ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		assertEquals (5, utils.normalizeCoordinate (-5, 10));
	}

	/**
	 * Tests the normalizeCoordinate method when the coordinate needs to be bumped right multiple times
	 */
	@Test
	public final void testNormalizeCoordinate_multipleNegative ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		assertEquals (5, utils.normalizeCoordinate (-5005, 10));
	}

	/**
	 * Tests the normalizeCoordinate method when the coordinate needs to be bumped left
	 */
	@Test
	public final void testNormalizeCoordinate_positive ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		assertEquals (5, utils.normalizeCoordinate (15, 10));
	}

	/**
	 * Tests the normalizeCoordinate method when the coordinate needs to be bumped left multiple times
	 */
	@Test
	public final void testNormalizeCoordinate_multiplePositive ()
	{
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		assertEquals (5, utils.normalizeCoordinate (5005, 10));
	}
	
	/**
	 * Tests a movement path on a square map which never takes us outside the map boundary
	 */
	@Test
	public final void testMove2DCoordinates_Square_WithinBoundary ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : SQUARE_PATH_WITHIN_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a square map going outside the map boundary
	 * Besides the false return value, basically what we are testing here is that movement still carries on as normal despite being out of range
	 */
	@Test
	public final void testMove2DCoordinates_Square_OutsideBoundary ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : SQUARE_PATH_OUTSIDE_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertFalse (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a square map which wraps around the left-right map edge in both directions
	 */
	@Test
	public final void testMove2DCoordinates_Square_Wraps ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : SQUARE_PATH_WRAPS)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a hex map which never takes us outside the map boundary
	 */
	@Test
	public final void testMove2DCoordinates_Hex_WithinBoundary ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : HEX_PATH_WITHIN_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a hex map going outside the map boundary
	 * Besides the false return value, basically what we are testing here is that movement still carries on as normal despite being out of range
	 */
	@Test
	public final void testMove2DCoordinates_Hex_OutsideBoundary ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : HEX_PATH_OUTSIDE_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertFalse (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a vertical hex map which never takes us outside the map boundary
	 */
	@Test
	public final void testMove2DCoordinates_VerticalHex_WithinBoundary ()
	{
		final CoordinateSystem sys = createVerticalHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : VERTICAL_HEX_PATH_WITHIN_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a vertical hex map going outside the map boundary
	 * Besides the false return value, basically what we are testing here is that movement still carries on as normal despite being out of range
	 */
	@Test
	public final void testMove2DCoordinates_VerticalHex_OutsideBoundary ()
	{
		final CoordinateSystem sys = createVerticalHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : VERTICAL_HEX_PATH_OUTSIDE_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertFalse (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a diamond map which never takes us outside the map boundary
	 */
	@Test
	public final void testMove2DCoordinates_Diamond_WithinBoundary ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : DIAMOND_PATH_WITHIN_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests a movement path on a diamond map going outside the map boundary
	 * Besides the false return value, basically what we are testing here is that movement still carries on as normal despite being out of range
	 */
	@Test
	public final void testMove2DCoordinates_Diamond_OutsideBoundary_NoWrapping ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : DIAMOND_PATH_OUTSIDE_BOUNDARY_NO_WRAPPING)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertFalse (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}

	/**
	 * Tests the same movement path as above on a diamond map, but now with wrapping enabeld so we wrap around the map boundary instead of instead of going outside of it
	 */
	@Test
	public final void testMove2DCoordinates_Diamond_OutsideBoundary_WithWrapping ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		sys.setWrapsLeftToRight (true);
		
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : DIAMOND_PATH_OUTSIDE_BOUNDARY_WITH_WRAPPING)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}
	
	/**
	 * Tests a movement path on a wrapped diamond map which never takes us outside the map boundary
	 */
	@Test
	public final void testMove2DCoordinates_WrappedDiamond_WithinBoundary ()
	{
		final CoordinateSystem sys = createWrappedDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : WRAPPED_DIAMOND_PATH_WITHIN_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				assertTrue (utils.move2DCoordinates (sys, coords, thisMove [0]));
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}
	
	/**
	 * Tests a movement path on a wrapped diamond map going outside the map boundary and over the left to right wrapping border
	 * Besides the false return value, basically what we are testing here is that movement still carries on as normal despite being out of range
	 */
	@Test
	public final void testMove2DCoordinates_WrappedDiamond_OutsideBoundary ()
	{
		final CoordinateSystem sys = createWrappedDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		MapCoordinates2DEx coords = null;
		for (final int [] thisMove : WRAPPED_DIAMOND_PATH_OUTSIDE_BOUNDARY)
		{
			// If first one, set coords
			if (coords == null)
				coords = new MapCoordinates2DEx (thisMove [1], thisMove [2]);
			else
			{
				// Move, and check resulting coordinates
				final boolean insideMap = utils.move2DCoordinates (sys, coords, thisMove [0]);
				final boolean expectedInsideMap = (thisMove [2] >= 0);
				assertEquals (expectedInsideMap, insideMap);
				
				assertEquals (thisMove [1], coords.getX ());
				assertEquals (thisMove [2], coords.getY ());
			}
		}
	}
	
	/**
	 * Tests the move3DCoordinates method on a square map
	 */
	@Test
	public final void testMove3DCoordinates_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		final MapCoordinates3DEx coords = new MapCoordinates3DEx (59, 1, 0);
		
		// Wrap around the X boundary
		assertTrue (utils.move3DCoordinates (sys, coords, 2));
		assertEquals (0, coords.getX ());
		assertEquals (0, coords.getY ());
		assertEquals (0, coords.getZ ());
		
		// Go off the top of the Y boundary
		assertFalse (utils.move3DCoordinates (sys, coords, 2));
		assertEquals (1, coords.getX ());
		assertEquals (-1, coords.getY ());
		assertEquals (0, coords.getZ ());
	}
	
	/**
	 * Tests the processAll2DCoordinates method
	 */
	@Test
	public final void testProcessAll2DCoordinates ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		sys.setWidth (3);
		sys.setHeight (2);
		sys.setDepth (2);
		
		// Call method
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		final List<MapCoordinates2DEx> list = new ArrayList<MapCoordinates2DEx> ();
		utils.processAll2DCoordinates (sys, (c) ->
		{
			list.add (c);
			return (c.getX () != 1) || (c.getY () != 1);
		});
		
		// Should have processed cells in order, except the last one
		assertEquals (5, list.size ());
		assertEquals (0, list.get (0).getX ());		assertEquals (0, list.get (0).getY ());
		assertEquals (1, list.get (1).getX ());		assertEquals (0, list.get (1).getY ());
		assertEquals (2, list.get (2).getX ());		assertEquals (0, list.get (2).getY ());
		assertEquals (0, list.get (3).getX ());		assertEquals (1, list.get (3).getY ());
		assertEquals (1, list.get (4).getX ());		assertEquals (1, list.get (4).getY ());
	}
	
	/**
	 * Tests the processAll3DCoordinates method
	 */
	@Test
	public final void testProcessAll3DCoordinates ()
	{
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		sys.setWidth (3);
		sys.setHeight (2);
		sys.setDepth (2);
		
		// Call method
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		final List<MapCoordinates3DEx> list = new ArrayList<MapCoordinates3DEx> ();
		utils.processAll3DCoordinates (sys, (c) ->
		{
			list.add (c);
			return (c.getX () != 1) || (c.getY () != 0) || (c.getZ () != 1);
		});
		
		// Should have processed cells in order, except the last four
		assertEquals (8, list.size ());
		assertEquals (0, list.get (0).getX ());		assertEquals (0, list.get (0).getY ());		assertEquals (0, list.get (0).getZ ());
		assertEquals (1, list.get (1).getX ());		assertEquals (0, list.get (1).getY ());		assertEquals (0, list.get (1).getZ ());
		assertEquals (2, list.get (2).getX ());		assertEquals (0, list.get (2).getY ());		assertEquals (0, list.get (2).getZ ());
		assertEquals (0, list.get (3).getX ());		assertEquals (1, list.get (3).getY ());		assertEquals (0, list.get (3).getZ ());
		assertEquals (1, list.get (4).getX ());		assertEquals (1, list.get (4).getY ());		assertEquals (0, list.get (4).getZ ());
		assertEquals (2, list.get (5).getX ());		assertEquals (1, list.get (5).getY ());		assertEquals (0, list.get (5).getZ ());

		assertEquals (0, list.get (6).getX ());		assertEquals (0, list.get (6).getY ());		assertEquals (1, list.get (6).getZ ());
		assertEquals (1, list.get (7).getX ());		assertEquals (0, list.get (7).getY ());		assertEquals (1, list.get (7).getZ ());
	}
	
	/**
	 * Tests the processCoordinatesWithinRadius method on a square map
	 */
	@Test
	public final void testProcessCoordinatesWithinRadius_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		// Call method
		final List<RadialMapCoordinates> list = new ArrayList<RadialMapCoordinates> ();
		utils.processCoordinatesWithinRadius (sys, 5, 3, 2, (x, y, r, d, n) ->
		{
			final RadialMapCoordinates coords = new RadialMapCoordinates ();
			coords.setX (x);
			coords.setY (y);
			coords.setR (r);
			coords.setD (d);
			coords.setN (n);
			list.add (coords);
			return (coords.getX () != 3) || (coords.getY () != 1);
		});

		// Should have processed cells in order, until top left corner
		assertEquals (13, list.size ());
		assertEquals (5, list.get (0).getX ());		assertEquals (3, list.get (0).getY ());		assertEquals (0, list.get (0).getR ());		assertEquals (0, list.get (0).getD ());		assertEquals (0, list.get (0).getN ());
		assertEquals (4, list.get (1).getX ());		assertEquals (3, list.get (1).getY ());		assertEquals (1, list.get (1).getR ());		assertEquals (1, list.get (1).getD ());		assertEquals (1, list.get (1).getN ());
		assertEquals (4, list.get (2).getX ());		assertEquals (2, list.get (2).getY ());		assertEquals (1, list.get (2).getR ());		assertEquals (1, list.get (2).getD ());		assertEquals (2, list.get (2).getN ());
		assertEquals (5, list.get (3).getX ());		assertEquals (2, list.get (3).getY ());		assertEquals (1, list.get (3).getR ());		assertEquals (3, list.get (3).getD ());		assertEquals (1, list.get (3).getN ());
		assertEquals (6, list.get (4).getX ());		assertEquals (2, list.get (4).getY ());		assertEquals (1, list.get (4).getR ());		assertEquals (3, list.get (4).getD ());		assertEquals (2, list.get (4).getN ());
		assertEquals (6, list.get (5).getX ());		assertEquals (3, list.get (5).getY ());		assertEquals (1, list.get (5).getR ());		assertEquals (5, list.get (5).getD ());		assertEquals (1, list.get (5).getN ());
		assertEquals (6, list.get (6).getX ());		assertEquals (4, list.get (6).getY ());		assertEquals (1, list.get (6).getR ());		assertEquals (5, list.get (6).getD ());		assertEquals (2, list.get (6).getN ());
		assertEquals (5, list.get (7).getX ());		assertEquals (4, list.get (7).getY ());		assertEquals (1, list.get (7).getR ());		assertEquals (7, list.get (7).getD ());		assertEquals (1, list.get (7).getN ());
		assertEquals (4, list.get (8).getX ());		assertEquals (4, list.get (8).getY ());		assertEquals (1, list.get (8).getR ());		assertEquals (7, list.get (8).getD ());		assertEquals (2, list.get (8).getN ());
		assertEquals (3, list.get (9).getX ());		assertEquals (4, list.get (9).getY ());		assertEquals (2, list.get (9).getR ());		assertEquals (1, list.get (9).getD ());		assertEquals (1, list.get (9).getN ());
		assertEquals (3, list.get (10).getX ());		assertEquals (3, list.get (10).getY ());		assertEquals (2, list.get (10).getR ());		assertEquals (1, list.get (10).getD ());		assertEquals (2, list.get (10).getN ());
		assertEquals (3, list.get (11).getX ());		assertEquals (2, list.get (11).getY ());		assertEquals (2, list.get (11).getR ());		assertEquals (1, list.get (11).getD ());		assertEquals (3, list.get (11).getN ());
		assertEquals (3, list.get (12).getX ());		assertEquals (1, list.get (12).getY ());		assertEquals (2, list.get (12).getR ());		assertEquals (1, list.get (12).getD ());		assertEquals (4, list.get (12).getN ());
	}
	
	/**
	 * Tests the processCoordinatesWithinRadius method on a hex map
	 */
	@Test
	public final void testProcessCoordinatesWithinRadius_Hex ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		// Call method
		final List<RadialMapCoordinates> list = new ArrayList<RadialMapCoordinates> ();
		utils.processCoordinatesWithinRadius (sys, 3, 2, 2, (x, y, r, d, n) ->
		{
			final RadialMapCoordinates coords = new RadialMapCoordinates ();
			coords.setX (x);
			coords.setY (y);
			coords.setR (r);
			coords.setD (d);
			coords.setN (n);
			list.add (coords);
			return (coords.getX () != 1) || (coords.getY () != 1);
		});

		// Should have processed cells in order, until top left corner
		assertEquals (9, list.size ());
		assertEquals (3, list.get (0).getX ());		assertEquals (2, list.get (0).getY ());		assertEquals (0, list.get (0).getR ());		assertEquals (0, list.get (0).getD ());		assertEquals (0, list.get (0).getN ());
		assertEquals (2, list.get (1).getX ());		assertEquals (2, list.get (1).getY ());		assertEquals (1, list.get (1).getR ());		assertEquals (1, list.get (1).getD ());		assertEquals (1, list.get (1).getN ());
		assertEquals (3, list.get (2).getX ());		assertEquals (1, list.get (2).getY ());		assertEquals (1, list.get (2).getR ());		assertEquals (2, list.get (2).getD ());		assertEquals (1, list.get (2).getN ());
		assertEquals (4, list.get (3).getX ());		assertEquals (2, list.get (3).getY ());		assertEquals (1, list.get (3).getR ());		assertEquals (3, list.get (3).getD ());		assertEquals (1, list.get (3).getN ());
		assertEquals (4, list.get (4).getX ());		assertEquals (3, list.get (4).getY ());		assertEquals (1, list.get (4).getR ());		assertEquals (4, list.get (4).getD ());		assertEquals (1, list.get (4).getN ());
		assertEquals (3, list.get (5).getX ());		assertEquals (3, list.get (5).getY ());		assertEquals (1, list.get (5).getR ());		assertEquals (5, list.get (5).getD ());		assertEquals (1, list.get (5).getN ());
		assertEquals (2, list.get (6).getX ());		assertEquals (3, list.get (6).getY ());		assertEquals (1, list.get (6).getR ());		assertEquals (6, list.get (6).getD ());		assertEquals (1, list.get (6).getN ());
		assertEquals (1, list.get (7).getX ());		assertEquals (2, list.get (7).getY ());		assertEquals (2, list.get (7).getR ());		assertEquals (1, list.get (7).getD ());		assertEquals (1, list.get (7).getN ());
		assertEquals (1, list.get (8).getX ());		assertEquals (1, list.get (8).getY ());		assertEquals (2, list.get (8).getR ());		assertEquals (1, list.get (8).getD ());		assertEquals (2, list.get (8).getN ());
	}

	/**
	 * Tests the processCoordinatesWithinRadius method on a diamond map
	 */
	@Test
	public final void testProcessCoordinatesWithinRadius_Diamond ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		// Call method
		final List<RadialMapCoordinates> list = new ArrayList<RadialMapCoordinates> ();
		utils.processCoordinatesWithinRadius (sys, 3, 4, 2, (x, y, r, d, n) ->
		{
			final RadialMapCoordinates coords = new RadialMapCoordinates ();
			coords.setX (x);
			coords.setY (y);
			coords.setR (r);
			coords.setD (d);
			coords.setN (n);
			list.add (coords);
			return (coords.getX () != 3) || (coords.getY () != 0);
		});

		// Should have processed cells in order, until top left corner
		assertEquals (13, list.size ());
		assertEquals (3, list.get (0).getX ());		assertEquals (4, list.get (0).getY ());		assertEquals (0, list.get (0).getR ());		assertEquals (0, list.get (0).getD ());		assertEquals (0, list.get (0).getN ());
		assertEquals (2, list.get (1).getX ());		assertEquals (3, list.get (1).getY ());		assertEquals (1, list.get (1).getR ());		assertEquals (2, list.get (1).getD ());		assertEquals (1, list.get (1).getN ());
		assertEquals (3, list.get (2).getX ());		assertEquals (2, list.get (2).getY ());		assertEquals (1, list.get (2).getR ());		assertEquals (2, list.get (2).getD ());		assertEquals (2, list.get (2).getN ());
		assertEquals (3, list.get (3).getX ());		assertEquals (3, list.get (3).getY ());		assertEquals (1, list.get (3).getR ());		assertEquals (4, list.get (3).getD ());		assertEquals (1, list.get (3).getN ());
		assertEquals (4, list.get (4).getX ());		assertEquals (4, list.get (4).getY ());		assertEquals (1, list.get (4).getR ());		assertEquals (4, list.get (4).getD ());		assertEquals (2, list.get (4).getN ());
		assertEquals (3, list.get (5).getX ());		assertEquals (5, list.get (5).getY ());		assertEquals (1, list.get (5).getR ());		assertEquals (6, list.get (5).getD ());		assertEquals (1, list.get (5).getN ());
		assertEquals (3, list.get (6).getX ());		assertEquals (6, list.get (6).getY ());		assertEquals (1, list.get (6).getR ());		assertEquals (6, list.get (6).getD ());		assertEquals (2, list.get (6).getN ());
		assertEquals (2, list.get (7).getX ());		assertEquals (5, list.get (7).getY ());		assertEquals (1, list.get (7).getR ());		assertEquals (8, list.get (7).getD ());		assertEquals (1, list.get (7).getN ());
		assertEquals (2, list.get (8).getX ());		assertEquals (4, list.get (8).getY ());		assertEquals (1, list.get (8).getR ());		assertEquals (8, list.get (8).getD ());		assertEquals (2, list.get (8).getN ());
		assertEquals (1, list.get (9).getX ());		assertEquals (3, list.get (9).getY ());		assertEquals (2, list.get (9).getR ());		assertEquals (2, list.get (9).getD ());		assertEquals (1, list.get (9).getN ());
		assertEquals (2, list.get (10).getX ());		assertEquals (2, list.get (10).getY ());		assertEquals (2, list.get (10).getR ());		assertEquals (2, list.get (10).getD ());		assertEquals (2, list.get (10).getN ());
		assertEquals (2, list.get (11).getX ());		assertEquals (1, list.get (11).getY ());		assertEquals (2, list.get (11).getR ());		assertEquals (2, list.get (11).getD ());		assertEquals (3, list.get (11).getN ());
		assertEquals (3, list.get (12).getX ());		assertEquals (0, list.get (12).getY ());		assertEquals (2, list.get (12).getR ());		assertEquals (2, list.get (12).getD ());		assertEquals (4, list.get (12).getN ());
	}
	
	/**
	 * Tests the determineDirectionTo method on a wrapping square map
	 */
	@Test
	public final void testDetermineDirectionTo_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		// Midde of the map - also test this one with coordinates objects
		final MapCoordinates2DEx from = new MapCoordinates2DEx (10, 10);
		final MapCoordinates2DEx to = new MapCoordinates2DEx (20, 10);
		
		assertEquals (3, utils.determineDirectionTo (sys, from, to));

		// Really this is wrong, these are much closer to direction 3 than 2/4, but this is how the method is currently written
		assertEquals (2, utils.determineDirectionTo (sys, 10, 10, 20, 9));
		assertEquals (4, utils.determineDirectionTo (sys, 10, 10, 20, 11));

		// Top of map all the way to the bottom - because doesn't wrap top to bottom
		assertEquals (4, utils.determineDirectionTo (sys, 10, 1, 11, 35));
		assertEquals (5, utils.determineDirectionTo (sys, 10, 1, 10, 35));
		assertEquals (6, utils.determineDirectionTo (sys, 10, 1, 9, 35));

		// Left of the map wrapping around to the right
		assertEquals (6, utils.determineDirectionTo (sys, 1, 10, 55, 11));
		assertEquals (7, utils.determineDirectionTo (sys, 1, 10, 55, 10));
		assertEquals (8, utils.determineDirectionTo (sys, 1, 10, 55, 9));

		// Try to reproduce bug where units animate in the wrong direction across the wrapping boundary
		assertEquals (6, utils.determineDirectionTo (sys, 0, 10, 59, 11));
		assertEquals (7, utils.determineDirectionTo (sys, 0, 10, 59, 10));
		assertEquals (8, utils.determineDirectionTo (sys, 0, 10, 59, 9));

		assertEquals (4, utils.determineDirectionTo (sys, 59, 10, 0, 11));
		assertEquals (3, utils.determineDirectionTo (sys, 59, 10, 0, 10));
		assertEquals (2, utils.determineDirectionTo (sys, 59, 10, 0, 9));
	}

	/**
	 * Tests the determineDirectionTo method on a hex map
	 */
	@Test
	public final void testDetermineDirectionTo_Hex ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		// This tests every hex on a ring of length 3 on each side
		assertEquals (1, utils.determineDirectionTo (sys, 3, 3, 3, 0));	// N
		assertEquals (1, utils.determineDirectionTo (sys, 3, 3, 4, 1));
		assertEquals (2, utils.determineDirectionTo (sys, 3, 3, 5, 1));
		assertEquals (2, utils.determineDirectionTo (sys, 3, 3, 6, 2));	// NE
		assertEquals (2, utils.determineDirectionTo (sys, 3, 3, 6, 3));
		assertEquals (3, utils.determineDirectionTo (sys, 3, 3, 6, 4));
		assertEquals (3, utils.determineDirectionTo (sys, 3, 3, 6, 5));	// SE
		assertEquals (3, utils.determineDirectionTo (sys, 3, 3, 5, 5));
		assertEquals (4, utils.determineDirectionTo (sys, 3, 3, 4, 6));
		assertEquals (4, utils.determineDirectionTo (sys, 3, 3, 3, 6));	// S
		assertEquals (4, utils.determineDirectionTo (sys, 3, 3, 2, 6));
		assertEquals (5, utils.determineDirectionTo (sys, 3, 3, 1, 5));
		assertEquals (5, utils.determineDirectionTo (sys, 3, 3, 0, 5));	// SW
		assertEquals (5, utils.determineDirectionTo (sys, 3, 3, 0, 4));
		assertEquals (6, utils.determineDirectionTo (sys, 3, 3, 0, 3));
		assertEquals (6, utils.determineDirectionTo (sys, 3, 3, 0, 2));	// NW
		assertEquals (6, utils.determineDirectionTo (sys, 3, 3, 1, 1));
		assertEquals (1, utils.determineDirectionTo (sys, 3, 3, 2, 1));
	}

	/**
	 * Tests the determineDirectionTo method on a diamond map
	 */
	@Test
	public final void testDetermineDirectionTo_Diamond ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		// This tests every hex on a diamond of length 7 on each side
		assertEquals (1, utils.determineDirectionTo (sys, 3, 6, 3, 0));	// N
		assertEquals (1, utils.determineDirectionTo (sys, 3, 6, 3, 1));
		assertEquals (2, utils.determineDirectionTo (sys, 3, 6, 4, 2));
		assertEquals (2, utils.determineDirectionTo (sys, 3, 6, 4, 3));
		assertEquals (2, utils.determineDirectionTo (sys, 3, 6, 5, 4));
		assertEquals (3, utils.determineDirectionTo (sys, 3, 6, 5, 5));
		assertEquals (3, utils.determineDirectionTo (sys, 3, 6, 6, 6));	// E
		assertEquals (3, utils.determineDirectionTo (sys, 3, 6, 5, 7));
		assertEquals (4, utils.determineDirectionTo (sys, 3, 6, 5, 8));
		assertEquals (4, utils.determineDirectionTo (sys, 3, 6, 4, 9));
		assertEquals (4, utils.determineDirectionTo (sys, 3, 6, 4, 10));
		assertEquals (5, utils.determineDirectionTo (sys, 3, 6, 3, 11));
		assertEquals (5, utils.determineDirectionTo (sys, 3, 6, 3, 12));	// S
		assertEquals (5, utils.determineDirectionTo (sys, 3, 6, 2, 11));
		assertEquals (6, utils.determineDirectionTo (sys, 3, 6, 2, 10));
		assertEquals (6, utils.determineDirectionTo (sys, 3, 6, 1, 9));
		assertEquals (6, utils.determineDirectionTo (sys, 3, 6, 1, 8));
		assertEquals (7, utils.determineDirectionTo (sys, 3, 6, 0, 7));
		assertEquals (7, utils.determineDirectionTo (sys, 3, 6, 0, 6));	// W
		assertEquals (7, utils.determineDirectionTo (sys, 3, 6, 0, 5));
		assertEquals (8, utils.determineDirectionTo (sys, 3, 6, 1, 4));
		assertEquals (8, utils.determineDirectionTo (sys, 3, 6, 1, 3));
		assertEquals (8, utils.determineDirectionTo (sys, 3, 6, 2, 2));
		assertEquals (1, utils.determineDirectionTo (sys, 3, 6, 2, 1));
	}

	/**
	 * Tests the findDistanceBetweenXCoordinates method on a square map
	 */
	@Test
	public final void testFindDistanceBetweenXCoordinates_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (10, utils.findDistanceBetweenXCoordinates (sys, 10, 20));
		assertEquals (10, utils.findDistanceBetweenXCoordinates (sys, 20, 10));
		assertEquals (15, utils.findDistanceBetweenXCoordinates (sys, 10, 55));
		assertEquals (15, utils.findDistanceBetweenXCoordinates (sys, 55, 10));

		sys.setWrapsLeftToRight (false);
		assertEquals (45, utils.findDistanceBetweenXCoordinates (sys, 10, 55));
		assertEquals (45, utils.findDistanceBetweenXCoordinates (sys, 55, 10));
	}

	/**
	 * Tests the findDistanceBetweenYCoordinates method on a square map
	 */
	@Test
	public final void testFindDistanceBetweenYCoordinates_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (10, utils.findDistanceBetweenYCoordinates (sys, 10, 20));
		assertEquals (10, utils.findDistanceBetweenYCoordinates (sys, 20, 10));
		assertEquals (25, utils.findDistanceBetweenYCoordinates (sys, 10, 35));
		assertEquals (25, utils.findDistanceBetweenYCoordinates (sys, 35, 10));

		sys.setWrapsTopToBottom (true);
		assertEquals (15, utils.findDistanceBetweenYCoordinates (sys, 10, 35));
		assertEquals (15, utils.findDistanceBetweenYCoordinates (sys, 35, 10));
	}

	/**
	 * Tests the determineStep2DDistanceBetween method on a square map
	 */
	@Test
	public final void testDetermineStep2DDistanceBetween_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (1, utils.determineStep2DDistanceBetween (sys, 1, 1, 2, 2));
		assertEquals (2, utils.determineStep2DDistanceBetween (sys, 1, 1, 59, 3));
		assertEquals (38, utils.determineStep2DDistanceBetween (sys, 1, 1, 59, 39));

		// Test one using 2D coordinates objects
		sys.setWrapsLeftToRight (false);

		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (1, 1);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (59, 39);
		
		assertEquals (58, utils.determineStep2DDistanceBetween (sys, from2D, to2D));

		// Test one using 3D coordinates objects
		sys.setWrapsLeftToRight (true);
		sys.setWrapsTopToBottom (true);

		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (1, 1, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (59, 39, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (2, utils.determineStep2DDistanceBetween (sys, from3D, to3D));
	}
	
	/**
	 * Tests the determineStep2DDistanceBetween method on a diamond map
	 */
	@Test
	public final void testDetermineStep2DDistanceBetween_Diamond ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (5, utils.determineStep2DDistanceBetween (sys, 3, 6, 5, 1));
		assertEquals (2, utils.determineStep2DDistanceBetween (sys, 4, 5, 6, 5));
		assertEquals (5, utils.determineStep2DDistanceBetween (sys, 3, 7, 7, 5));

		// This tests every hex on a diamond of length 7 on each side
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 3, 0));	// N
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 3, 1));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 4, 2));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 4, 3));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 5, 4));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 5, 5));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 6, 6));	// E
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 5, 7));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 5, 8));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 4, 9));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 4, 10));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 3, 11));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 3, 12));	// S
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 2, 11));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 2, 10));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 1, 9));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 1, 8));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 0, 7));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 0, 6));	// W
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 0, 5));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 1, 4));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 1, 3));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 2, 2));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 6, 2, 1));
		
		// Test one using 2D coordinates objects
		sys.setWrapsLeftToRight (false);

		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (3, 7);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (2, 4);
		
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, from2D, to2D));

		// Test one using 3D coordinates objects
		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (1, 1, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (1, 5, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (2, utils.determineStep2DDistanceBetween (sys, from3D, to3D));
	}

	/**
	 * Tests the determineStep2DDistanceBetween method on a Hex map
	 */
	@Test
	public final void testDetermineStep2DDistanceBetween_Hex ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (5, utils.determineStep2DDistanceBetween (sys, 1, 3, 6, 1));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 0, 0, 2));
		assertEquals (4, utils.determineStep2DDistanceBetween (sys, 3, 0, 3, 4));

		// This tests every hex on a ring of length 3 on each side
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 3, 0));	// N
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 4, 1));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 5, 1));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 6, 2));	// NE
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 6, 3));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 6, 4));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 6, 5));	// SE
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 5, 5));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 4, 6));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 3, 6));	// S
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 2, 6));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 1, 5));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 0, 5));	// SW
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 0, 4));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 0, 3));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 0, 2));	// NW
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 1, 1));
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, 3, 3, 2, 1));
		
		// Test one using 2D coordinates objects
		sys.setWrapsLeftToRight (false);

		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (1, 3);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (6, 1);
		
		assertEquals (5, utils.determineStep2DDistanceBetween (sys, from2D, to2D));

		// Test one using 3D coordinates objects
		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (3, 0, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (0, 2, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (3, utils.determineStep2DDistanceBetween (sys, from3D, to3D));
	}

	/**
	 * Tests the determineReal2DDistanceBetween method on a square map
	 */
	@Test
	public final void testDetermineReal2DDistanceBetween_Square ()
	{
		final CoordinateSystem sys = createSquareCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (1.414, utils.determineReal2DDistanceBetween (sys, 1, 1, 2, 2), 0.001);
		assertEquals (2.828, utils.determineReal2DDistanceBetween (sys, 1, 1, 59, 3), 0.001);
		assertEquals (38.053, utils.determineReal2DDistanceBetween (sys, 1, 1, 59, 39), 0.001);

		// Test one using 2D coordinates objects
		sys.setWrapsLeftToRight (false);

		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (1, 1);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (59, 39);
		
		assertEquals (69.340, utils.determineReal2DDistanceBetween (sys, from2D, to2D), 0.001);

		// Test one using 3D coordinates objects
		sys.setWrapsLeftToRight (true);
		sys.setWrapsTopToBottom (true);

		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (1, 1, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (59, 39, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (2.828, utils.determineReal2DDistanceBetween (sys, from3D, to3D), 0.001);
	}
	
	/**
	 * Tests the determineReal2DDistanceBetween method on a diamond map
	 */
	@Test
	public final void testDetermineReal2DDistanceBetween_Diamond ()
	{
		final CoordinateSystem sys = createDiamondCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (5.000, utils.determineReal2DDistanceBetween (sys, 3, 6, 5, 1), 0.001);
		assertEquals (2.828, utils.determineReal2DDistanceBetween (sys, 4, 5, 6, 5), 0.001);
		assertEquals (5.831, utils.determineReal2DDistanceBetween (sys, 3, 7, 7, 5), 0.001);

		// Test one using 2D coordinates objects
		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (3, 7);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (2, 4);
		
		assertEquals (3.000, utils.determineReal2DDistanceBetween (sys, from2D, to2D), 0.001);

		// Test one using 3D coordinates objects
		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (1, 1, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (1, 5, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (2.828, utils.determineReal2DDistanceBetween (sys, from3D, to3D), 0.001);
	}

	/**
	 * Tests the determineReal2DDistanceBetween method on a Hex map
	 */
	@Test
	public final void testDetermineReal2DDistanceBetween_Hex ()
	{
		final CoordinateSystem sys = createHexCoordinateSystem ();
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();

		assertEquals (5.000, utils.determineReal2DDistanceBetween (sys, 1, 3, 6, 1), 0.001);
		assertEquals (3.000, utils.determineReal2DDistanceBetween (sys, 3, 0, 0, 2), 0.001);
		assertEquals (4.000, utils.determineReal2DDistanceBetween (sys, 3, 0, 3, 4), 0.001);

		// Test one using 2D coordinates objects
		final MapCoordinates2DEx from2D = new MapCoordinates2DEx (1, 3);
		final MapCoordinates2DEx to2D = new MapCoordinates2DEx (6, 1);
		
		assertEquals (5.000, utils.determineReal2DDistanceBetween (sys, from2D, to2D), 0.001);

		// Test one using 3D coordinates objects
		final MapCoordinates3DEx from3D = new MapCoordinates3DEx (3, 0, 0);
		final MapCoordinates3DEx to3D = new MapCoordinates3DEx (0, 2, 100);		// Set Z wildly different to prove it isn't being used
		
		assertEquals (3.000, utils.determineReal2DDistanceBetween (sys, from3D, to3D), 0.001);
	}
}