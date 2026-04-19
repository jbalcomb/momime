package com.ndg.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the NumberUtils class
 */
@ExtendWith(MockitoExtension.class)
public final class TestNumberUtils
{
	/**
	 * Tests the safeIntegerCompare method
	 */
	@Test
	public final void testSafeIntegerCompare ()
	{
		assertTrue (NumberUtils.safeIntegerCompare (null, null));
		assertTrue (NumberUtils.safeIntegerCompare (2, 2));
		assertFalse (NumberUtils.safeIntegerCompare (null, 2));
		assertFalse (NumberUtils.safeIntegerCompare (2, null));
		assertFalse (NumberUtils.safeIntegerCompare (2, 3));
	}
}