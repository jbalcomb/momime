package com.ndg.map.areas.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.CoordinateSystemUtilsImpl;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.areas.storage.MapArea2DArrayListImpl;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Tests the IntegerMapAreaOperations2DImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestIntegerMapAreaOperations2DImpl
{
	/**
	 * Tests the setAllToMinimum method
	 */
	@Test
	public final void testSetAllToMinimum ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (3);
		sys.setHeight (2);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea2D<Integer> area = new MapArea2DArrayListImpl<Integer> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, 4);
		area.set (1, 0, null);
		area.set (2, 0, 7);
		area.set (0, 1, null);
		area.set (1, 1, 1);
		area.set (2, 1, 5);
		
		// Set up object to test
		final IntegerMapAreaOperations2DImpl op = new IntegerMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setAllToMinimum (area, 5);
		
		// Check results
		assertEquals (5, area.get (0, 0).intValue ());
		assertEquals (5, area.get (1, 0).intValue ());
		assertEquals (7, area.get (2, 0).intValue ());
		assertEquals (5, area.get (0, 1).intValue ());
		assertEquals (5, area.get (1, 1).intValue ());
		assertEquals (5, area.get (2, 1).intValue ());
	}
	
	/**
	 * Tests the setMinimumWithinRadius method
	 */
	@Test
	public final void testSetMinimumWithinRadius ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (4);
		sys.setHeight (4);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);
		
		// Set up a test area
		final MapArea2D<Integer> area = new MapArea2DArrayListImpl<Integer> ();
		area.setCoordinateSystem (sys);
		
		area.set (0, 0, 1);
		area.set (1, 0, 1);
		area.set (2, 0, 5);
		area.set (3, 0, null);

		area.set (0, 1, 5);
		area.set (1, 1, 2);
		area.set (2, 1, 7);
		area.set (3, 1, null);

		area.set (0, 2, 3);
		area.set (1, 2, 1);
		area.set (2, 2, 5);
		area.set (3, 2, null);

		area.set (0, 3, null);
		area.set (1, 3, 1);
		area.set (2, 3, 5);
		area.set (3, 3, 2);
		
		// Set up object to test
		final IntegerMapAreaOperations2DImpl op = new IntegerMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setMinimumWithinRadius (area, 2, 1, 1, 4);
		
		// Check results
		assertEquals (1, area.get (0, 0).intValue ());
		assertEquals (4, area.get (1, 0).intValue ());		// <-- changed
		assertEquals (5, area.get (2, 0).intValue ());
		assertEquals (4, area.get (3, 0).intValue ());		// <-- changed

		assertEquals (5, area.get (0, 1).intValue ());
		assertEquals (4, area.get (1, 1).intValue ());		// <-- changed
		assertEquals (7, area.get (2, 1).intValue ());
		assertEquals (4, area.get (3, 1).intValue ());		// <-- changed

		assertEquals (3, area.get (0, 2).intValue ());
		assertEquals (4, area.get (1, 2).intValue ());		// <-- changed
		assertEquals (5, area.get (2, 2).intValue ());
		assertEquals (4, area.get (3, 2).intValue ());		// <-- changed

		assertNull (area.get (0, 3));
		assertEquals (1, area.get (1, 3).intValue ());
		assertEquals (5, area.get (2, 3).intValue ());
		assertEquals (2, area.get (3, 3).intValue ());
	}
	
	/**
	 * Tests the addDistancesFrom method
	 */
	@Test
	public final void testAddDistancesFrom ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (5);
		sys.setHeight (4);
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);
		
		// Set up a test area
		final MapArea2D<Integer> area = new MapArea2DArrayListImpl<Integer> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, 0);
		area.set (1, 0, 0);
		area.set (2, 0, 0);
		area.set (3, 0, 0);
		area.set (4, 0, 0);

		area.set (0, 1, 0);
		area.set (1, 1, 0);
		area.set (2, 1, 0);
		area.set (3, 1, 0);
		area.set (4, 1, 0);

		area.set (0, 2, 0);
		area.set (1, 2, 0);
		area.set (2, 2, null);
		area.set (3, 2, 10);
		area.set (4, 2, 0);

		area.set (0, 3, 0);
		area.set (1, 3, 0);
		area.set (2, 3, 0);
		area.set (3, 3, 0);
		area.set (4, 3, 0);
	
		// Set up object to test
		final IntegerMapAreaOperations2DImpl op = new IntegerMapAreaOperations2DImpl ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.addDistancesFrom (area, new MapCoordinates2DEx (3, 0));
		
		// Check results
		assertEquals (0, area.get (3, 0).intValue ());

		assertEquals (1, area.get (2, 0).intValue ());
		assertEquals (1, area.get (2, 1).intValue ());
		assertEquals (1, area.get (3, 1).intValue ());
		assertEquals (1, area.get (4, 1).intValue ());
		assertEquals (1, area.get (4, 0).intValue ());

		assertEquals (2, area.get (1, 0).intValue ());
		assertEquals (2, area.get (1, 1).intValue ());
		assertEquals (2, area.get (2, 2).intValue ());
		assertEquals (12, area.get (3, 2).intValue ());
		assertEquals (2, area.get (4, 2).intValue ());

		assertEquals (3, area.get (0, 0).intValue ());
		assertEquals (3, area.get (0, 1).intValue ());
		assertEquals (3, area.get (0, 2).intValue ());
		assertEquals (3, area.get (1, 2).intValue ());
		assertEquals (3, area.get (2, 3).intValue ());
		assertEquals (3, area.get (3, 3).intValue ());
		assertEquals (3, area.get (4, 3).intValue ());

		assertEquals (4, area.get (0, 3).intValue ());
		assertEquals (4, area.get (1, 3).intValue ());
	}
}