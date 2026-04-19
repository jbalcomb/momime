package com.ndg.map.areas.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.CoordinateSystemUtilsImpl;
import com.ndg.map.areas.storage.MapArea3D;
import com.ndg.map.areas.storage.MapArea3DArrayListImpl;
import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.utils.random.RandomUtils;

/**
 * Tests the MapAreaOperations3DImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapAreaOperations3DImpl
{
	// Methods from MapAreaOperationsImpl
	
	/**
	 * Tests the processAllCells method
	 */
	@Test
	public final void testProcessAllCells ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (3);
		sys.setHeight (2);
		sys.setDepth (2);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, 0, "A");
		area.set (1, 0, 0, "B");
		area.set (2, 0, 0, "C");
		area.set (0, 1, 0, "D");
		area.set (1, 1, 0, "E");
		area.set (2, 1, 0, "F");
		area.set (0, 0, 1, "G");
		area.set (1, 0, 1, "H");
		area.set (2, 0, 1, "I");
		area.set (0, 1, 1, "J");
		area.set (1, 1, 1, "K");
		area.set (2, 1, 1, "L");
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		final List<String> values = new ArrayList<String> ();
		op.processAllCells (area, (v) ->
		{
			values.add (v);
			return !v.equals ("H");
		});
		
		// Check results
		assertEquals (8, values.size ());
		assertEquals ("A", values.get (0));
		assertEquals ("B", values.get (1));
		assertEquals ("C", values.get (2));
		assertEquals ("D", values.get (3));
		assertEquals ("E", values.get (4));
		assertEquals ("F", values.get (5));
		assertEquals ("G", values.get (6));
		assertEquals ("H", values.get (7));
	}
	
	/**
	 * Tests the setAll method
	 * This assumes that countCellsEqualTo works
	 */
	@Test
	public final void testSetAll ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setAll (area, "A");
		assertEquals (400, op.countCellsEqualTo (area, "A"));
		assertEquals (0, op.countCellsEqualTo (area, "B"));
		assertEquals (0, op.countCellsEqualTo (area, null));
		
		op.setAll (area, "B");
		assertEquals (0, op.countCellsEqualTo (area, "A"));
		assertEquals (400, op.countCellsEqualTo (area, "B"));
		assertEquals (0, op.countCellsEqualTo (area, null));
		
		op.setAll (area, null);
		assertEquals (0, op.countCellsEqualTo (area, "A"));
		assertEquals (0, op.countCellsEqualTo (area, "B"));
		assertEquals (400, op.countCellsEqualTo (area, null));
	}

	/**
	 * Tests the copy method
	 */
	@Test
	public final void testCopy ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (3);
		sys.setHeight (2);
		sys.setDepth (2);
		
		// Set up test areas
		final MapArea3D<String> from = new MapArea3DArrayListImpl<String> ();
		from.setCoordinateSystem (sys);

		final MapArea3D<String> to = new MapArea3DArrayListImpl<String> ();
		to.setCoordinateSystem (sys);
		
		from.set (0, 0, 0, "A");
		from.set (1, 0, 0, "B");
		from.set (2, 0, 0, "C");
		from.set (0, 1, 0, "D");
		from.set (1, 1, 0, "E");
		from.set (2, 1, 0, "F");
		from.set (0, 0, 1, "G");
		from.set (1, 0, 1, "H");
		from.set (2, 0, 1, "I");
		from.set (0, 1, 1, "J");
		from.set (1, 1, 1, "K");
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.copy (from, to);
		
		// Check results
		assertEquals ("A", to.get (0, 0, 0));
		assertEquals ("B", to.get (1, 0, 0));
		assertEquals ("C", to.get (2, 0, 0));
		assertEquals ("D", to.get (0, 1, 0));
		assertEquals ("E", to.get (1, 1, 0));
		assertEquals ("F", to.get (2, 1, 0));
		assertEquals ("G", to.get (0, 0, 1));
		assertEquals ("H", to.get (1, 0, 1));
		assertEquals ("I", to.get (2, 0, 1));
		assertEquals ("J", to.get (0, 1, 1));
		assertEquals ("K", to.get (1, 1, 1));
		assertNull (to.get (2, 1, 1));
	}
	
	/**
	 * Tests the countCellsEqualTo method
	 */
	@Test
	public final void testCountCellsEqualTo ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (4, 5, 0, "A");
		area.set (5, 6, 1, "A");
		area.set (6, 7, 1, "B");
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		assertEquals (2, op.countCellsEqualTo (area, "A"));
		assertEquals (1, op.countCellsEqualTo (area, "B"));
		assertEquals (397, op.countCellsEqualTo (area, null));
	}
	
	/**
	 * Tests the findRandomCellEqualTo method
	 */
	@Test
	public final void testFindRandomCellEqualTo ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (4, 5, 0, "A");
		area.set (5, 6, 1, "A");
		area.set (6, 7, 1, "A");
		area.set (7, 8, 0, "B");
		
		// Mock random number generator
		final RandomUtils randomUtils = mock (RandomUtils.class);
		when (randomUtils.nextInt (3)).thenReturn (1);
		when (randomUtils.nextInt (396)).thenReturn (395);
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		op.setRandomUtils (randomUtils);
		
		// Run method
		final MapCoordinates3DEx coords1 = op.findRandomCellEqualTo (area, "A");
		assertEquals (5, coords1.getX ());
		assertEquals (6, coords1.getY ());
		assertEquals (1, coords1.getZ ());

		final MapCoordinates3DEx coords2 = op.findRandomCellEqualTo (area, null);
		assertEquals (19, coords2.getX ());
		assertEquals (9, coords2.getY ());
		assertEquals (1, coords2.getZ ());
	}

	/**
	 * Tests the anyCellEqualTo method
	 */
	@Test
	public final void testAnyCellEqualTo ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, 0, "A");
		area.set (5, 8, 1, "B");

		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		assertFalse (op.anyCellEqualTo (area, "C"));
		area.set (17, 2, 1, "C");
		assertTrue (op.anyCellEqualTo (area, "C"));
	}
	
	/**
	 * Tests the setWithinRadius method on a square map
	 */
	@Test
	public final void testSetWithinRadius_Square ()
	{
		// Use a real coordinate system, otherwise we have to mock 25 calls to .areCoordinatesWithinRange () and .moveCoords ()
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setWithinRadius (area, 3, 5, 2, "A");

		// Don't want to list 50 assertEquals out, just count the number of trues and then verify they're in the right spot
		assertEquals (50, op.countCellsEqualTo (area, "A"));

		for (int z = 0; z <= 1; z++)
			for (int y = 3; y <= 7; y++)
				for (int x = 1; x <= 5; x++)
					assertEquals ("A", area.get (x, y, z));
	}

	/**
	 * Tests the setWithinRadius method on a hex map
	 */
	@Test
	public final void testSetWithinRadius_Hex ()
	{
		// Use a real coordinate system, otherwise we have to mock 19 calls to .areCoordinatesWithinRange () and .moveCoords ()
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);

		// Set up a test area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations3DImpl<String> op = new MapAreaOperations3DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setWithinRadius (area, 3, 5, 2, "A");

		// Don't want to list 38 assertEquals out, just count the number of trues
		assertEquals (38, op.countCellsEqualTo (area, "A"));
	}
}