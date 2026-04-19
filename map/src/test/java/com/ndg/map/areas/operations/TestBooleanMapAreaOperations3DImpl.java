package com.ndg.map.areas.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.CoordinateSystemUtilsImpl;
import com.ndg.map.areas.storage.MapArea3D;
import com.ndg.map.areas.storage.MapArea3DArrayListImpl;

/**
 * Tests the BooleanMapAreaOperations3DImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestBooleanMapAreaOperations3DImpl
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
		sys.setDepth (2);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea3D<Boolean> area = new MapArea3DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, 0, true);
		area.set (4, 0, 0, true);
		area.set (5, 1, 1, true);
		
		// Set up object to test
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		final BooleanMapAreaOperations2DImpl op2d = new BooleanMapAreaOperations2DImpl ();
		op2d.setCoordinateSystemUtils (utils);

		final BooleanMapAreaOperations3DImpl op3d = new BooleanMapAreaOperations3DImpl ();
		op3d.setCoordinateSystemUtils (utils);
		op3d.setBooleanMapAreaOperations2D (op2d);
		
		// Run method
		op3d.enlarge (area, new int [] {1,3,5,7}, false);
		assertEquals (6+5, op3d.countCellsEqualTo (area, true));		// 2 are off the top of the map
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
		sys.setDepth (2);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea3D<Boolean> area = new MapArea3DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, 0, true);
		area.set (4, 0, 0, true);
		area.set (5, 1, 1, true);
		
		// Set up object to test
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		final BooleanMapAreaOperations2DImpl op2d = new BooleanMapAreaOperations2DImpl ();
		op2d.setCoordinateSystemUtils (utils);

		final BooleanMapAreaOperations3DImpl op3d = new BooleanMapAreaOperations3DImpl ();
		op3d.setCoordinateSystemUtils (utils);
		op3d.setBooleanMapAreaOperations2D (op2d);
		
		// Run method
		op3d.enlarge (area, null, false);
		assertEquals (8+9, op3d.countCellsEqualTo (area, true));		// Adds in the 2 diagonals
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
		sys.setDepth (2);
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);
		
		// Set up a test area
		final MapArea3D<Boolean> area = new MapArea3DArrayListImpl<Boolean> ();
		area.setCoordinateSystem (sys);
		area.set (3, 0, 0, true);
		area.set (4, 0, 0, true);
		area.set (5, 1, 1, true);
		
		// Set up object to test
		final CoordinateSystemUtilsImpl utils = new CoordinateSystemUtilsImpl ();
		
		final BooleanMapAreaOperations2DImpl op2d = new BooleanMapAreaOperations2DImpl ();
		op2d.setCoordinateSystemUtils (utils);

		final BooleanMapAreaOperations3DImpl op3d = new BooleanMapAreaOperations3DImpl ();
		op3d.setCoordinateSystemUtils (utils);
		op3d.setBooleanMapAreaOperations2D (op2d);
		
		// Run method
		op3d.enlarge (area, null, false);
		assertEquals (7+7, op3d.countCellsEqualTo (area, true));		// Can count them on the pic of the hex layout in Map.xsd
	}
}