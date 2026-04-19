package com.ndg.multiplayer.basedemo.client;

/**
 * Factory for creating messages with spring injectons
 */
public interface MessageFactory
{
	/**
	 * @return Newly created message
	 */
	public DateTimeResponseEx createDateTimeResponse ();
	
	/**
	 * @return Newly created message
	 */
	public TotalSoFarEx createTotalSoFar ();
	
	/**
	 * @return Newly created message
	 */
	public PauseResponseEx createPauseResponse ();
	
	/**
	 * @return Newly created message
	 */
	public CounterResponseEx createCounterResponse ();
}