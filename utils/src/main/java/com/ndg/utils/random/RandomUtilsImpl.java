package com.ndg.utils.random;

import java.util.Random;

/**
 * Random number generator implementation
 */
public final class RandomUtilsImpl implements RandomUtils
{
	/** The actual random number generator */
	private final static Random generator = new Random (System.currentTimeMillis ());

	/**
	 * @param n Upper limit
	 * @return A random number between between 0 (inclusive) and n (exclusive)
	 */
	@Override
	public final int nextInt (final int n)
	{
		return generator.nextInt (n);
	}
	
	/**
	 * @return True or false, at random
	 */
	@Override
	public final boolean nextBoolean ()
	{
		return generator.nextBoolean ();
	}
	
	/**
	 * @return Random double between 0..1
	 */
	@Override
	public final double nextDouble ()
	{
		return generator.nextDouble ();
	}

	/**
	 * @return Random double following normal distribution with mean 0 and standard deviation 1 
	 */
	@Override
	public final double nextGaussian ()
	{
		return generator.nextGaussian ();
	}
}