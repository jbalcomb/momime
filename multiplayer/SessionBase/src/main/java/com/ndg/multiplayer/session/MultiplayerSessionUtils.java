package com.ndg.multiplayer.session;

import java.util.List;

/**
 * Helper methods for dealing with players in a session
 */
public interface MultiplayerSessionUtils
{
	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @return Info for the requested player, or null if they aren't in the list
	 */
	public PlayerPublicDetails findPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID);
	
	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Info for the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	public PlayerPublicDetails findPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID, final String caller) throws PlayerNotFoundException;

	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Index into the players list of the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	public int indexOfPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID, final String caller) throws PlayerNotFoundException;
}