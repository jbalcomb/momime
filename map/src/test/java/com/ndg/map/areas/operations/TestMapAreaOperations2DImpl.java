package com.ndg.map.areas.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.CoordinateSystemType;
import com.ndg.map.CoordinateSystemUtilsImpl;
import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.areas.storage.MapArea2DArrayListImpl;
import com.ndg.map.areas.storage.MapArea2DBufferedImageImpl;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.utils.random.RandomUtils;

/**
 * Tests the MapAreaOperations2DImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapAreaOperations2DImpl
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

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, "A");
		area.set (1, 0, "B");
		area.set (2, 0, "C");
		area.set (0, 1, "D");
		area.set (1, 1, "E");
		area.set (2, 1, "F");
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		final List<String> values = new ArrayList<String> ();
		op.processAllCells (area, (v) ->
		{
			values.add (v);
			return !v.equals ("E");
		});
		
		// Check results
		assertEquals (5, values.size ());
		assertEquals ("A", values.get (0));
		assertEquals ("B", values.get (1));
		assertEquals ("C", values.get (2));
		assertEquals ("D", values.get (3));
		assertEquals ("E", values.get (4));
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

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setAll (area, "A");
		assertEquals (200, op.countCellsEqualTo (area, "A"));
		assertEquals (0, op.countCellsEqualTo (area, "B"));
		assertEquals (0, op.countCellsEqualTo (area, null));
		
		op.setAll (area, "B");
		assertEquals (0, op.countCellsEqualTo (area, "A"));
		assertEquals (200, op.countCellsEqualTo (area, "B"));
		assertEquals (0, op.countCellsEqualTo (area, null));
		
		op.setAll (area, null);
		assertEquals (0, op.countCellsEqualTo (area, "A"));
		assertEquals (0, op.countCellsEqualTo (area, "B"));
		assertEquals (200, op.countCellsEqualTo (area, null));
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
		
		// Set up test areas
		final MapArea2D<String> from = new MapArea2DArrayListImpl<String> ();
		from.setCoordinateSystem (sys);

		final MapArea2D<String> to = new MapArea2DArrayListImpl<String> ();
		to.setCoordinateSystem (sys);
		
		from.set (0, 0, "A");
		from.set (1, 0, "B");
		from.set (2, 0, "C");
		from.set (0, 1, "D");
		from.set (1, 1, "E");
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.copy (from, to);
		
		// Check results
		assertEquals ("A", to.get (0, 0));
		assertEquals ("B", to.get (1, 0));
		assertEquals ("C", to.get (2, 0));
		assertEquals ("D", to.get (0, 1));
		assertEquals ("E", to.get (1, 1));
		assertNull (to.get (2, 1));
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

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (4, 5, "A");
		area.set (5, 6, "A");
		area.set (6, 7, "B");
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		assertEquals (2, op.countCellsEqualTo (area, "A"));
		assertEquals (1, op.countCellsEqualTo (area, "B"));
		assertEquals (197, op.countCellsEqualTo (area, null));
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

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (4, 5, "A");
		area.set (5, 6, "A");
		area.set (6, 7, "A");
		area.set (7, 8, "B");
		
		// Mock random number generator
		final RandomUtils randomUtils = mock (RandomUtils.class);
		when (randomUtils.nextInt (3)).thenReturn (1);
		when (randomUtils.nextInt (196)).thenReturn (195);
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		op.setRandomUtils (randomUtils);
		
		// Run method
		final MapCoordinates2DEx coords1 = op.findRandomCellEqualTo (area, "A");
		assertEquals (5, coords1.getX ());
		assertEquals (6, coords1.getY ());

		final MapCoordinates2DEx coords2 = op.findRandomCellEqualTo (area, null);
		assertEquals (19, coords2.getX ());
		assertEquals (9, coords2.getY ());
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

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (0, 0, "A");
		area.set (5, 8, "B");

		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		assertFalse (op.anyCellEqualTo (area, "C"));
		area.set (17, 2, "C");
		assertTrue (op.anyCellEqualTo (area, "C"));
	}

	/**
	 * Tests the setWithinRadius method on a square map
	 */
	@Test
	public final void testSetWithinRadius_Square ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setWithinRadius (area, 3, 5, 2, "A");

		// Don't want to list 25 assertEquals out, just count the number of trues and then verify they're in the right spot
		assertEquals (25, op.countCellsEqualTo (area, "A"));

		for (int y = 3; y <= 7; y++)
			for (int x = 1; x <= 5; x++)
				assertEquals ("A", area.get (x, y));
	}

	/**
	 * Tests the setWithinRadius method on a hex map
	 */
	@Test
	public final void testSetWithinRadius_Hex ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.HEX);

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		op.setWithinRadius (area, 3, 5, 2, "A");

		// Don't want to list 19 assertEquals out, just count the number of trues
		assertEquals (19, op.countCellsEqualTo (area, "A"));
	}

	// Methods from MapAreaOperations2DImpl
	
	/**
	 * Tests the setPolygon method.  The folder in src/test/resources contains a number of bitmaps, with dots marked
	 * for the coordinate locations and the expected results filled in.
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSetPolygon () throws Exception
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (60);
		sys.setHeight (40);
		sys.setWrapsLeftToRight (true);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up object to test
		final MapAreaOperations2DImpl<Integer> op = new MapAreaOperations2DImpl<Integer> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Test each file
		final File folder = new File (getClass ().getResource ("/com.ndg.map.areas.operations.setpolygon/Simple.gif").toURI ()).getParentFile ();
		for (final File file : folder.listFiles ())
		{
			// Read bitmap
			final BufferedImage image = ImageIO.read (file);
			assertEquals (sys.getWidth (), image.getWidth ());
			assertEquals (sys.getHeight (), image.getHeight ());
			
			// Find the white dots in it that mark the vertices
			final List<MapCoordinates2DEx> coords = new ArrayList<MapCoordinates2DEx> ();
			for (int y = 0; y < sys.getHeight (); y++)
				for (int x = 0; x < sys.getWidth (); x++)
					if (image.getRGB (x, y) == Color.WHITE.getRGB ())
						coords.add (new MapCoordinates2DEx (x, y));
			
			// Set up a area to output to
			final MapArea2DBufferedImageImpl area = new MapArea2DBufferedImageImpl ();
			area.setCoordinateSystem (sys);
			
			// Run method
			op.setPolygon (area, coords, Color.BLUE.getRGB ());
			
			// Check results
			for (int y = 0; y < sys.getHeight (); y++)
				for (int x = 0; x < sys.getWidth (); x++)
					if (image.getRGB (x, y) == Color.BLUE.getRGB ())
						assertEquals (Color.BLUE.getRGB (), area.get (x, y).intValue ());
		}
	}

	/**
	 * Tests the setLine method.  The folder in src/test/resources contains a number of bitmaps, with dots marked
	 * for the coordinate locations and the expected results filled in.
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSetLine () throws Exception
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (60);
		sys.setHeight (40);
		sys.setWrapsLeftToRight (true);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up object to test
		final MapAreaOperations2DImpl<Integer> op = new MapAreaOperations2DImpl<Integer> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Test each file
		final File folder = new File (getClass ().getResource ("/com.ndg.map.areas.operations.setline/xDirection.gif").toURI ()).getParentFile ();
		for (final File file : folder.listFiles ())
		{
			// Read bitmap
			final BufferedImage image = ImageIO.read (file);
			assertEquals (sys.getWidth (), image.getWidth ());
			assertEquals (sys.getHeight (), image.getHeight ());
			
			// Find the white dots in it that mark the vertices
			final List<MapCoordinates2DEx> coords = new ArrayList<MapCoordinates2DEx> ();
			for (int y = 0; y < sys.getHeight (); y++)
				for (int x = 0; x < sys.getWidth (); x++)
					if (image.getRGB (x, y) == Color.WHITE.getRGB ())
						coords.add (new MapCoordinates2DEx (x, y));
			
			assertEquals (2, coords.size ());
			
			// Set up a area to output to
			final MapArea2DBufferedImageImpl area = new MapArea2DBufferedImageImpl ();
			area.setCoordinateSystem (sys);
			
			// Run method
			op.setLine (area, coords.get (0), coords.get (1), 1, Color.BLUE.getRGB ());
			op.setLine (area, coords.get (0), coords.get (1), 0, Color.RED.getRGB ());
			
			// Check results
			for (int y = 0; y < sys.getHeight (); y++)
				for (int x = 0; x < sys.getWidth (); x++)
					if (image.getRGB (x, y) == Color.BLUE.getRGB ())
						assertEquals (Color.BLUE.getRGB (), area.get (x, y).intValue ());
					else if (image.getRGB (x, y) == Color.RED.getRGB ())
						assertEquals (Color.RED.getRGB (), area.get (x, y).intValue ());
		}
	}
	
	/**
	 * Tests the findClosestMatchingCell method on a square map
	 */
	@Test
	public final void testFindClosestMatchingCell_Square ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setCoordinateSystemType (CoordinateSystemType.SQUARE);

		// Set up a test area
		final MapArea2D<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		area.set (5, 3, "A");
		area.set (4, 3, "A");
		area.set (4, 2, "B");
		area.set (5, 2, "B");
		area.set (3, 1, "C");
		
		// Set up object to test
		final MapAreaOperations2DImpl<String> op = new MapAreaOperations2DImpl<String> ();
		op.setCoordinateSystemUtils (new CoordinateSystemUtilsImpl ());
		
		// Run method
		assertNull (op.findClosestMatchingCell (area, new MapCoordinates2DEx (5, 3), "D"));
		
		final MapCoordinates2DEx coords = op.findClosestMatchingCell (area, new MapCoordinates2DEx (5, 3), "C");
		assertEquals (3, coords.getX ());
		assertEquals (1, coords.getY ());
	}
}