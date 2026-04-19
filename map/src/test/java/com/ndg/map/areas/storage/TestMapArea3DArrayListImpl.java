package com.ndg.map.areas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Tests the MapArea3DArrayListImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapArea3DArrayListImpl
{
	/**
	 * Tests the MapArea3DArrayListImpl class
	 * Its only a bit of storage, so hard to test the methods in isolation
	 */
	@Test
	public final void testMapArea3DArrayListImpl ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		sys.setDepth (2);
		
		// Set up object to test
		final MapArea3DArrayListImpl<String> area = new MapArea3DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Insert some values
		final MapCoordinates3DEx coords1 = new MapCoordinates3DEx (10, 9, 1);
		area.set (2, 5, 0, "A");
		area.set (coords1, "B");

		// Retrieve them
		final MapCoordinates3DEx coords2 = new MapCoordinates3DEx (10, 9, 1);
		assertEquals ("A", area.get (2, 5, 0));
		assertEquals ("B", area.get (coords2));
	}	
}
