package com.ndg.multiplayer.basedemo.server;

import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;

/**
 * Demonstrates declaring some local storage on the connection thread for stateful operations
 */
public final class MultiplayerDemoServerThread extends MultiplayerBaseServerThread
{
	/** Package private so the message can access it */
	int totalSoFar;
	
	/**
	 * Creates a new thread for handling requests from this client
	 * @param threadName Name for this thread
	 */
	public MultiplayerDemoServerThread (final String threadName)
	{
		super (threadName);
	}
}