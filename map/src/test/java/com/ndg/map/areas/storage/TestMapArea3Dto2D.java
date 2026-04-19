package com.ndg.map.areas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Tests the MapArea3Dto2D class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapArea3Dto2D
{
	/**
	 * Tests the MapArea3Dto2D class
	 * Its only a bit of storage, so hard to test the methods in isolation
	 */
	@Test
	public final void testMapArea3Dto2D ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);
		
		// Set up a 3D area
		final MapArea3D<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Insert some values
		final MapCoordinates3DEx coords1 = new MapCoordinates3DEx (10, 9, 1);
		area.set (2, 5, 0, "A");
		area.set (coords1, "B");
		
		// Map onto a 2D area
		final MapArea3Dto2D<String> map = new MapArea3Dto2D<String> ();
		map.setStorage (area);
		
		map.setZ (0);
		assertEquals ("A", map.get (2, 5));
		map.setZ (1);
		assertNull (map.get (2, 5));

		final MapCoordinates2DEx coords2 = new MapCoordinates2DEx (10, 9);
		assertEquals ("B", map.get (coords2));
		map.setZ (0);
		assertNull (map.get (coords2));
		
		// Write some values into the 2D area, prove they reflect back in the 3D area
		map.set (15, 4, "C");
		map.setZ (1);
		map.set (16, 4, "D");
		
		assertEquals ("C", area.get (15, 4, 0));
		assertNull (area.get (15, 4, 1));
		assertNull (area.get (16, 4, 0));
		assertEquals ("D", area.get (16, 4, 1));
	}
}