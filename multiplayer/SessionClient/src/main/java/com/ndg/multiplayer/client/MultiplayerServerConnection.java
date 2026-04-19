package com.ndg.multiplayer.client;

import com.ndg.multiplayer.base.client.MultiplayerBaseClientThread;

/**
 * Extends base server connection thread
 */
public class MultiplayerServerConnection extends MultiplayerBaseClientThread
{
	/**
	 * Creates a new thread for handling requests from the server
	 * @param threadName Name for this thread
	 */
	public MultiplayerServerConnection (final String threadName)
	{
		super (threadName);
	}

	/**
	 * @return Client this connection belongs to
	 */
	@Override
	public final MultiplayerSessionClient getClient ()
	{
		return (MultiplayerSessionClient) super.getClient ();
	}
	
	/**
	 * Pass disconnections to the event handler
	 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
	 */
	@Override
	protected final void disconnected (final Exception e)
	{
		for (final MultiplayerSessionClientEvent listener : getClient ().getEventListeners ())
			listener.disconnected (e);
	}
}