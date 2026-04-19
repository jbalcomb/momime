package com.ndg.utils.random;

/**
 * Random number interface, so random numbers can be mocked in unit tests to give predicatable results
 */
public interface RandomUtils
{
	/**
	 * @param n Upper limit
	 * @return A random number between between 0 (inclusive) and n (exclusive)
	 */
	public int nextInt (final int n);
	
	/**
	 * @return True or false, at random
	 */
	public boolean nextBoolean ();
	
	/**
	 * @return Random double between 0..1
	 */
	public double nextDouble ();
	
	/**
	 * @return Random double following normal distribution with mean 0 and standard deviation 1 
	 */
	public double nextGaussian ();
}