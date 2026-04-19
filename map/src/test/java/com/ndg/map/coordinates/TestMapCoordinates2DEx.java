package com.ndg.map.coordinates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.MapCoordinates2D;

/**
 * Tests the MapCoordinates2DEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapCoordinates2DEx
{
	/**
	 * Tests the toString method with a normal param
	 */
	@Test
	public final void testToString ()
	{
		final MapCoordinates2DEx coords = new MapCoordinates2DEx (56, 0);
		assertEquals ("(56, 0)", coords.toString ());
	}

	/**
	 * Tests the equals method against a null
	 */
	@Test
	public final void testEquals_Null ()
	{
		final MapCoordinates2DEx coords = new MapCoordinates2DEx (56, 0);
		assertFalse (coords.equals (null));
	}

	/**
	 * Tests the equals method with both being same object
	 */
	@Test
	public final void testEquals_SameObject ()
	{
		final MapCoordinates2DEx coords = new MapCoordinates2DEx (56, 0);
		assertTrue (coords.equals (coords));
	}

	/**
	 * Tests the equals method with two equal objects
	 */
	@Test
	public final void testEquals_Equal ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 0);
		final MapCoordinates2DEx secondCoords = new MapCoordinates2DEx (56, 0);
		assertTrue (firstCoords.equals (secondCoords)); 
	}

	/**
	 * Tests the equals method with two unequal objects
	 */
	@Test
	public final void testEquals_Unequal ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 0);
		final MapCoordinates2DEx secondCoords = new MapCoordinates2DEx (56, 10);
		assertFalse (firstCoords.equals (secondCoords));
	}

	/**
	 * Tests the equals method when we forgot to use the Ex class
	 */
	@Test
	public final void testEquals_DidntUseEx ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 10);
		
		final MapCoordinates2D secondCoords = new MapCoordinates2D ();
		secondCoords.setX (56);
		secondCoords.setY (10);

		assertFalse (firstCoords.equals (secondCoords));
		assertFalse (secondCoords.equals (firstCoords));
	}

	/**
	 * Tests the hashCode method with two equal objects
	 */
	@Test
	public final void testHashCode_Equal ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 0);
		final MapCoordinates2DEx secondCoords = new MapCoordinates2DEx (56, 0);
		assertEquals (firstCoords.hashCode (), secondCoords.hashCode ()); 
	}

	/**
	 * Tests the hashCode method with two unequal objects
	 */
	@Test
	public final void testHashCode_Unequal ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 0);
		final MapCoordinates2DEx secondCoords = new MapCoordinates2DEx (56, 10);
		assertNotEquals (firstCoords.hashCode (), secondCoords.hashCode ());
	}

	/**
	 * Tests the hashCode method when we forgot to use the Ex class
	 */
	@Test
	public final void testHashCode_DidntUseEx ()
	{
		final MapCoordinates2DEx firstCoords = new MapCoordinates2DEx (56, 10);
		
		final MapCoordinates2D secondCoords = new MapCoordinates2D ();
		secondCoords.setX (56);
		secondCoords.setY (10);

		assertNotEquals (firstCoords.hashCode (), secondCoords.hashCode ());
	}
}