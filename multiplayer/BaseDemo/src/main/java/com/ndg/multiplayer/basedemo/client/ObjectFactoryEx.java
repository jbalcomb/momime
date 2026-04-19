package com.ndg.multiplayer.basedemo.client;

import com.ndg.multiplayer.basedemo.CounterResponse;
import com.ndg.multiplayer.basedemo.DateTimeResponse;
import com.ndg.multiplayer.basedemo.ObjectFactory;
import com.ndg.multiplayer.basedemo.PauseResponse;
import com.ndg.multiplayer.basedemo.TotalSoFar;

/**
 * Creates client side messages with process implementations
 */
public final class ObjectFactoryEx extends ObjectFactory
{
	/** Message factory */
	private MessageFactory messageFactory;
	
	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final TotalSoFar createTotalSoFar ()
	{
		return getMessageFactory ().createTotalSoFar ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final DateTimeResponse createDateTimeResponse ()
	{
		return getMessageFactory ().createDateTimeResponse ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final PauseResponse createPauseResponse ()
	{
		return getMessageFactory ().createPauseResponse ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final CounterResponse createCounterResponse ()
	{
		return getMessageFactory ().createCounterResponse ();
	}

	/**
	 * @return Message factory
	 */
	public final MessageFactory getMessageFactory ()
	{
		return messageFactory;
	}

	/**
	 * @param fac Message factory
	 */
	public final void setMessageFactory (final MessageFactory fac)
	{
		messageFactory = fac;
	}
}