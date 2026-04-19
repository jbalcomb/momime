package com.ndg.multiplayer.session;

import java.io.IOException;

/**
 * Type of exception thrown when we look for a particular player that we expect to be present in the session and we can't find them
 */
public final class PlayerNotFoundException extends IOException
{
	/**
	 * @param playerID ID of the player we're looking for
	 * @param caller The routine that was looking for the value
	 */
	public PlayerNotFoundException (final int playerID, final String caller)
	{
		super ("Player with ID " + playerID + " not found by \"" + caller + "\"");
	}
}