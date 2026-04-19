package com.ndg.utils.random;

/**
 * Internal storage class used by WeightedChoicesImpl
 *
 * @param <E> Type of object stored in the list
 */
final class WeightedChoice<E>
{
	/** Weighting score for this value */
	private final int weighting;
	
	/** This value */
	private final E value;
	
	/**
	 * @param aWeighting Weighting score for this value
	 * @param aValue This value
	 */
	WeightedChoice (final int aWeighting, final E aValue)
	{
		weighting = aWeighting;
		value = aValue;
	}

	/**
	 * @return Weighting score for this value
	 */
	final int getWeighting ()
	{
		return weighting;
	}
	
	/**
	 * @return This value
	 */
	final E getValue ()
	{
		return value;
	}
}