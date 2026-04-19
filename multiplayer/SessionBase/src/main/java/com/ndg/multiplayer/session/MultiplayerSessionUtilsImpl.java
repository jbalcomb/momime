package com.ndg.multiplayer.session;

import java.util.Iterator;
import java.util.List;

/**
 * Helper methods for dealing with players in a session
 */
public final class MultiplayerSessionUtilsImpl implements MultiplayerSessionUtils
{
	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @return Info for the requested player, or null if they aren't in the list
	 */
	@Override
	public final PlayerPublicDetails findPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID)
	{
		PlayerPublicDetails result = null;
		final Iterator<? extends PlayerPublicDetails> iter = players.iterator ();

		while ((result == null) && (iter.hasNext ()))
		{
			final PlayerPublicDetails thisPlayer = iter.next ();
			if ((thisPlayer.getPlayerDescription ().getPlayerID () != null) && (thisPlayer.getPlayerDescription ().getPlayerID () == playerID))
				result = thisPlayer;
		}

		return result;
	}
	
	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Info for the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Override
	public final PlayerPublicDetails findPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID, final String caller) throws PlayerNotFoundException
	{
		final PlayerPublicDetails result = findPlayerWithID (players, playerID);
		if (result == null)
			throw new PlayerNotFoundException (playerID, caller);

		return result;
	}

	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Index into the players list of the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Override
	public final int indexOfPlayerWithID (final List<? extends PlayerPublicDetails> players, final int playerID, final String caller) throws PlayerNotFoundException
	{
		int result = -1;
		int index = 0;

		while ((result < 0) && (index < players.size ()))
		{
			final PlayerPublicDetails thisPlayer = players.get (index);
			if ((thisPlayer.getPlayerDescription ().getPlayerID () != null) && (thisPlayer.getPlayerDescription ().getPlayerID () == playerID))
				result = index;
			else
				index++;
		}

		if (result < 0)
			throw new PlayerNotFoundException (playerID, caller);

		return result;
	}
}