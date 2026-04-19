package com.ndg.multiplayer.basedemo.server;

import com.ndg.multiplayer.basedemo.AddToTotal;
import com.ndg.multiplayer.basedemo.CounterRequest;
import com.ndg.multiplayer.basedemo.DateTimeRequest;
import com.ndg.multiplayer.basedemo.ObjectFactory;
import com.ndg.multiplayer.basedemo.PauseRequest;

/**
 * Creates server side messages with process implementations
 */
public final class ObjectFactoryEx extends ObjectFactory
{
	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final DateTimeRequest createDateTimeRequest ()
	{
		return new DateTimeRequestEx ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final AddToTotal createAddToTotal ()
	{
		return new AddToTotalEx ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public PauseRequest createPauseRequest ()
	{
		return new PauseRequestEx ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public CounterRequest createCounterRequest ()
	{
		return new CounterRequestEx ();
	}
}