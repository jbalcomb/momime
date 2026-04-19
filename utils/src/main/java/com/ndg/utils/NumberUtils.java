package com.ndg.utils;

/**
 * Helper methods dealing with numeric classes
 */
public final class NumberUtils
{
	/**
	 * Safe int comparison that works even with either int is null
	 * @param first First value to compare
	 * @param second Second value to compare
	 * @return True if both ints are null, or both are non-null and identical
	 */
	public final static boolean safeIntegerCompare (final Integer first, final Integer second)
	{
		if ((first == null) && (second == null))
			return true;

		else if ((first == null) || (second == null))
			return false;

		else
			return first.equals (second);
	}

	/**
	 * Prevent instatiation of this class
	 */
	private NumberUtils ()
	{
	}
}