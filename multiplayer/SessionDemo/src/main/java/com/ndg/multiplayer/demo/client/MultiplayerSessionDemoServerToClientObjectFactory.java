package com.ndg.multiplayer.demo.client;

import com.ndg.multiplayer.demo.ObjectFactory;
import com.ndg.multiplayer.demo.StocChat;

/**
 * Create versions of messages that include the processing interface
 */
public final class MultiplayerSessionDemoServerToClientObjectFactory extends ObjectFactory
{
	/** Message factory */
	private MultiplayerSessionDemoServerToClientMessageFactory messageFactory;
	
	/**
	 * Versions of message that include the processing interface
	 */
	@Override
	public final StocChat createStocChat ()
	{
		return getMessageFactory ().createChatMessage ();
	}

	/**
	 * @return Message factory
	 */
	public final MultiplayerSessionDemoServerToClientMessageFactory getMessageFactory ()
	{
		return messageFactory;
	}

	/**
	 * @param fac Message factory
	 */
	public final void setMessageFactory (final MultiplayerSessionDemoServerToClientMessageFactory fac)
	{
		messageFactory = fac;
	}
}