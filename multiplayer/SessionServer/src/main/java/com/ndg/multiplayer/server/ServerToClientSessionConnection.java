package com.ndg.multiplayer.server;

import com.ndg.multiplayer.base.server.ServerToClientConnection;
import com.ndg.multiplayer.server.session.PlayerServerDetails;

/**
 * Interface describing methods that the server-side connection to each client must support
 * 
 * This is declared like so so that server-side unit tests that send messages to clients can create a
 * dummy implementation which captures and checks the messages rather than actually sending them
 */
public interface ServerToClientSessionConnection extends ServerToClientConnection
{
	/**
	 * @return Indicates player details this client is using in this session, null if not joined into a session yet
	 */
	public PlayerServerDetails getPlayer ();

	/**
	 * @param psd Indicates player details this client is using in this session, null if not joined into a session yet
	 */
	public void setPlayer (final PlayerServerDetails psd);
}