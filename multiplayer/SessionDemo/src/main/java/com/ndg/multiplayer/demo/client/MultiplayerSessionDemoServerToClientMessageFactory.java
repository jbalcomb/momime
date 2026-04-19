package com.ndg.multiplayer.demo.client;

/**
 * Creates demo messages with Spring injections
 */
public interface MultiplayerSessionDemoServerToClientMessageFactory
{
	/**
	 * @return Message with Spring injections
	 */
	public StocChatEx createChatMessage ();
}