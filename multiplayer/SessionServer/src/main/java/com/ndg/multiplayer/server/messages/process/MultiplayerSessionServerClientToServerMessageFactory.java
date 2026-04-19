package com.ndg.multiplayer.server.messages.process;

/**
 * Creates message implementations with Spring injections
 */
public interface MultiplayerSessionServerClientToServerMessageFactory
{
	/**
	 * @return Message with processing implementation and Spring injections
	 */
	public LoginImpl createLoginMessage ();
	
	/**
	 * @return Message with processing implementation and Spring injections
	 */
	public RequestSessionListImpl createRequestSessionListMessage ();
	
	/**
	 * @return Message with processing implementation and Spring injections
	 */
	public JoinSessionImpl createJoinSessionMessage ();
}