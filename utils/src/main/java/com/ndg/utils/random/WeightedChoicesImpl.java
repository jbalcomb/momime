package com.ndg.utils.random;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of values so one can be chosen at random, but each value can have a different weighting value
 *
 * @param <E> Type of object stored in the list
 */
public final class WeightedChoicesImpl<E> implements WeightedChoices<E>
{
	/** Underlying list for storage */
	private final List<WeightedChoice<E>> list = new ArrayList<WeightedChoice<E>> ();

	/** Random utils */
	private RandomUtils randomUtils; 
	
	/**
	 * Clears the list
	 */
	@Override
	public final void clear ()
	{
		list.clear ();
	}
	
	/**
	 * @param weighting Weighting score for this value
	 * @param value This value
	 */
	@Override
	public final void add (final int weighting, final E value)
	{
		if (weighting < 1)
			throw new IllegalArgumentException ("Weighting value " + weighting + " should be at least 1 or value can never be chosen");
		
		if (value == null)
			throw new IllegalArgumentException ("Cannot give a weighting value to a null");
		
		if (list.stream ().anyMatch (e -> value.equals (e.getValue ())))
			throw new IllegalArgumentException ("Value " + value + " already present in weighting list");
		
		list.add (new WeightedChoice<E> (weighting, value));
	}
	
	/**
	 * @return Value chosen at random according to the weightings; the only way this will return null is if no values were ever added to the weighting list
	 */
	@Override
	public final E nextWeightedValue ()
	{
		final E choice;
		if (list.size () == 0)
			choice = null;
		else if (list.size () == 1)
			choice = list.get (0).getValue ();
		else
		{
			// Make proper roll
			final int totalWeightings = list.stream ().mapToInt (e -> e.getWeighting ()).sum ();
			int roll = getRandomUtils ().nextInt (totalWeightings);
			int index = 0;
			
			// Advance however far through the list we need to
			while (roll >= list.get (index).getWeighting ())
			{
				roll = roll - list.get (index).getWeighting ();
				index++;
			}
			
			choice = list.get (index).getValue ();
		}
		
		return choice;
	}

	/**
	 * @return Random utils
	 */
	public final RandomUtils getRandomUtils ()
	{
		return randomUtils;
	}

	/**
	 * @param utils Random utils
	 */
	public final void setRandomUtils (final RandomUtils utils)
	{
		randomUtils = utils;
	}
}