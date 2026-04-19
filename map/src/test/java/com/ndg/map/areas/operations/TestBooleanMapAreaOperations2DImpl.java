package com.ndg.map.areas.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.CoordinateSystemUtilsImpl;
import com.ndg.map.SquareMapDirection;
import com.ndg.map.areas.storage.MapArea;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.areas.storage.MapArea2DArrayListImpl;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Tests the BooleanMapAreaOperations2DImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestBooleanMapAreaOperations2DImpl
{
	/**
	 * Tests the enlarge method on a square map with specified directions
	 */
	@Test
	public final void testEnlarge_Square_LimitDirections ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea2D<Boolean> area = new MapArea2DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, true);
		area.set (4, 0, true);
		
		// Set up object to test
		final BooleanMapAreaOperations2DImpl op = new BooleanMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.enlarge (area, new int [] {1,3,5,7}, false);
		assertEquals (6, op.countCellsEqualTo (area, true));		// 2 are off the top of the map
	}

	/**
	 * Tests the enlarge method on a square map with all directions
	 */
	@Test
	public final void testEnlarge_Square_AllDirections ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea2D<Boolean> area = new MapArea2DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, true);
		area.set (4, 0, true);
		
		// Set up object to test
		final BooleanMapAreaOperations2DImpl op = new BooleanMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.enlarge (area, null, false);
		assertEquals (8, op.countCellsEqualTo (area, true));		// Adds in the 2 diagonals
	}

	/**
	 * Tests the enlarge method on a hex map with all directions
	 */
	@Test
	public final void testEnlarge_Hex_AllDirections ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);
		
		// Set up a test area
		final MapArea2D<Boolean> area = new MapArea2DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, true);
		area.set (4, 0, true);
		
		// Set up object to test
		final BooleanMapAreaOperations2DImpl op = new BooleanMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.enlarge (area, null, false);
		assertEquals (7, op.countCellsEqualTo (area, true));		// Can count them on the pic of the hex layout in Map.xsd
	}
	
	/**
	 * Tests the traceBorders method
	 */
	@Test
	public final void testTraceBorders ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up a test area - this does a T shape, against the top of the map
		final MapArea2DArrayListImpl<Boolean> area = new MapArea2DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		
		area.set (4, 0, true);
		area.set (5, 0, true);
		area.set (6, 0, true);
		area.set (5, 1, true);
		area.set (5, 2, false);

		// Set up object to test
		final BooleanMapAreaOperations2DImpl op = new BooleanMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		final MapArea<List<SquareMapDirection>, MapCoordinates2DEx> borders = op.traceBorders (area);
		
		// Check results
		assertNull (borders.get (new MapCoordinates2DEx (3, 0)));

		assertEquals (3, borders.get (new MapCoordinates2DEx (4, 0)).size ());
		assertEquals (SquareMapDirection.NORTH, borders.get (new MapCoordinates2DEx (4, 0)).get (0));
		assertEquals (SquareMapDirection.SOUTH, borders.get (new MapCoordinates2DEx (4, 0)).get (1));
		assertEquals (SquareMapDirection.WEST, borders.get (new MapCoordinates2DEx (4, 0)).get (2));

		assertEquals (1, borders.get (new MapCoordinates2DEx (5, 0)).size ());
		assertEquals (SquareMapDirection.NORTH, borders.get (new MapCoordinates2DEx (5, 0)).get (0));
		
		assertEquals (3, borders.get (new MapCoordinates2DEx (6, 0)).size ());
		assertEquals (SquareMapDirection.NORTH, borders.get (new MapCoordinates2DEx (6, 0)).get (0));
		assertEquals (SquareMapDirection.EAST, borders.get (new MapCoordinates2DEx (6, 0)).get (1));
		assertEquals (SquareMapDirection.SOUTH, borders.get (new MapCoordinates2DEx (6, 0)).get (2));

		assertEquals (3, borders.get (new MapCoordinates2DEx (5, 1)).size ());
		assertEquals (SquareMapDirection.EAST, borders.get (new MapCoordinates2DEx (5, 1)).get (0));
		assertEquals (SquareMapDirection.SOUTH, borders.get (new MapCoordinates2DEx (5, 1)).get (1));
		assertEquals (SquareMapDirection.WEST, borders.get (new MapCoordinates2DEx (5, 1)).get (2));
	}
}
