package com.ndg.map.areas.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Tests the MapArea2DArrayListImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapArea2DArrayListImpl
{
	/**
	 * Tests the MapArea2DArrayListImpl class
	 * Its only a bit of storage, so hard to test the methods in isolation
	 */
	@Test
	public final void testMapArea2DArrayListImpl ()
	{
		// Set up a test coordinate system
		final CoordinateSystem sys = new CoordinateSystem ();
		sys.setWidth (20);
		sys.setHeight (10);
		
		// Set up object to test
		final MapArea2DArrayListImpl<String> area = new MapArea2DArrayListImpl<String> ();
		area.setCoordinateSystem (sys);
		
		// Insert some values
		final MapCoordinates2DEx coords1 = new MapCoordinates2DEx (10, 9);
		area.set (2, 5, "A");
		area.set (coords1, "B");

		// Retrieve them
		final MapCoordinates2DEx coords2 = new MapCoordinates2DEx (10, 9);
		assertEquals ("A", area.get (2, 5));
		assertEquals ("B", area.get (coords2));
	}	
}
