package com.ndg.utils.random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the WeightedChoicesImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestWeightedChoicesImpl
{
	/**
	 * Tests the nextWeightedValue method
	 */
	@Test
	public final void testNextWeightedValue ()
	{
		// Mock random number generator
		final RandomUtils random = mock (RandomUtils.class);
		when (random.nextInt (9)).thenReturn (0, 1, 2, 3, 4, 5, 6, 7, 8);
		
		// Set up object to test
		final WeightedChoicesImpl<String> list = new WeightedChoicesImpl<String> ();
		list.setRandomUtils (random);
		list.add (1, "A");
		list.add (2, "B");
		list.add (3, "C");
		list.add (2, "D");
		list.add (1, "E");
		
		// Try with every possible random number
		assertEquals ("A", list.nextWeightedValue ());
		assertEquals ("B", list.nextWeightedValue ());
		assertEquals ("B", list.nextWeightedValue ());
		assertEquals ("C", list.nextWeightedValue ());
		assertEquals ("C", list.nextWeightedValue ());
		assertEquals ("C", list.nextWeightedValue ());
		assertEquals ("D", list.nextWeightedValue ());
		assertEquals ("D", list.nextWeightedValue ());
		assertEquals ("E", list.nextWeightedValue ());
	}
}