package com.ndg.utils.random;

/**
 * Stores a list of values so one can be chosen at random, but each value can have a different weighting value
 *
 * @param <E> Type of object stored in the list
 */
public interface WeightedChoices<E>
{
	/**
	 * Clears the list
	 */
	public void clear ();
	
	/**
	 * @param weighting Weighting score for this value
	 * @param value This value
	 */
	public void add (final int weighting, final E value);
	
	/**
	 * @return Value chosen at random according to the weightings; the only way this will return null is if no values were ever added to the weighting list
	 */
	public E nextWeightedValue ();
}