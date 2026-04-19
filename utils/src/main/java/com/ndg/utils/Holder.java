package com.ndg.utils;

/**
 * Holds a value of type T.  Replacement for javax.xml.ws.Holder as this was literally the only class I was using from there and no real JAX-WS functionality.
 * 
 * @param <T> Type of value to store
 */
public final class Holder<T>
{
	/** The value contained in the holder */
	private T value;

	/**
	 * Creates a new holder with a null value
	 */
	public Holder ()
	{
	}

	/**
	 * Create a new holder with the specified value
	 *
	 * @param v The value to be stored in the holder
	 */
	public Holder (final T v)
	{
		value = v;
	}

	/**
	 * @return The value contained in the holder
	 */
	public final T getValue ()
	{
		return value;
	}

	/**
	 * @param v New value to store
	 */
	public final void setValue (final T v)
	{
		value = v;
	}
}