package com.ndg.multiplayer.demo.server;

import com.ndg.multiplayer.demo.CtosChat;
import com.ndg.multiplayer.demo.ObjectFactory;
import com.ndg.multiplayer.demo.RequestUpdateHumanPlayerToAI;

/**
 * Create versions of messages that include the processing interface
 */
public final class MultiplayerSessionDemoClientToServerObjectFactory extends ObjectFactory
{
	/**
	 * Versions of message that include the processing interface
	 */
	@Override
	public final CtosChat createCtosChat ()
	{
		return new CtosChatEx ();
	}

	/**
	 * Versions of message that include the processing interface
	 */
	@Override
	public final RequestUpdateHumanPlayerToAI createRequestUpdateHumanPlayerToAI ()
	{
		return new RequestUpdateHumanPlayerToAiImpl ();
	}
}