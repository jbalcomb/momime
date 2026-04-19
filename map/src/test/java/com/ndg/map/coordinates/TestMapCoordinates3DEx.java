package com.ndg.map.coordinates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.MapCoordinates3D;

/**
 * Tests the MapCoordinates3DEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMapCoordinates3DEx
{
	/**
	 * Tests the toString method with a normal param
	 */
	@Test
	public final void testToString ()
	{
		final MapCoordinates3DEx coords = new MapCoordinates3DEx (56, 0, -12);
		assertEquals ("(56, 0, -12)", coords.toString ());
	}

	/**
	 * Tests the equals method against a null
	 */
	@Test
	public final void testEquals_Null ()
	{
		final MapCoordinates3DEx coords = new MapCoordinates3DEx (56, 0, -12);
		assertFalse (coords.equals (null));
	}

	/**
	 * Tests the equals method with both being same object
	 */
	@Test
	public final void testEquals_SameObject ()
	{
		final MapCoordinates3DEx coords = new MapCoordinates3DEx (56, 0, -12);
		assertTrue (coords.equals (coords));
	}

	/**
	 * Tests the equals method with two equal objects
	 */
	@Test
	public final void testEquals_Equal ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 0, -12);
		final MapCoordinates3DEx secondCoords = new MapCoordinates3DEx (56, 0, -12);
		assertTrue (firstCoords.equals (secondCoords)); 
	}

	/**
	 * Tests the equals method with two unequal objects
	 */
	@Test
	public final void testEquals_Unequal ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 0, -12);
		final MapCoordinates3DEx secondCoords = new MapCoordinates3DEx (56, 10, -12);
		assertFalse (firstCoords.equals (secondCoords));
	}

	/**
	 * Tests the equals method when we forgot to use the Ex class
	 */
	@Test
	public final void testEquals_DidntUseEx ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 10, -12);
		
		final MapCoordinates3D secondCoords = new MapCoordinates3D ();
		secondCoords.setX (56);
		secondCoords.setY (10);
		secondCoords.setZ (-12);

		assertFalse (firstCoords.equals (secondCoords));
		assertFalse (secondCoords.equals (firstCoords));
	}

	/**
	 * Tests the hashCode method with two equal objects
	 */
	@Test
	public final void testHashCode_Equal ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 0, -12);
		final MapCoordinates3DEx secondCoords = new MapCoordinates3DEx (56, 0, -12);
		assertEquals (firstCoords.hashCode (), secondCoords.hashCode ()); 
	}

	/**
	 * Tests the hashCode method with two unequal objects
	 */
	@Test
	public final void testHashCode_Unequal ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 0, -12);
		final MapCoordinates3DEx secondCoords = new MapCoordinates3DEx (56, 10, -12);
		assertNotEquals (firstCoords.hashCode (), secondCoords.hashCode ());
	}

	/**
	 * Tests the hashCode method when we forgot to use the Ex class
	 */
	@Test
	public final void testHashCode_DidntUseEx ()
	{
		final MapCoordinates3DEx firstCoords = new MapCoordinates3DEx (56, 10, -12);
		
		final MapCoordinates3D secondCoords = new MapCoordinates3D ();
		secondCoords.setX (56);
		secondCoords.setY (10);
		secondCoords.setZ (-12);

		assertNotEquals (firstCoords.hashCode (), secondCoords.hashCode ());
	}
}