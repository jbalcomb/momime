package com.ndg.multiplayer.session;

import com.ndg.multiplayer.sessionbase.PersistentPlayerPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.TransientPlayerPublicKnowledge;

/**
 * Stores all the publicly known information about one player
 * This is basically the same as PlayerCombinedPublicInfo, except that by hand writing the class we can make the values immutable as they should be
 *
 * This is the class clients would use to store the list of players in their session, i.e. List<PlayerPublicDetails>, and then store their own private details separately outside the list
 */
public class PlayerPublicDetails
{
	/** Publicly available fixed details about this player */
	private final PlayerDescription playerDescription;

	/** Publicly available varying details about this player that are persisted to save game files */
	private final PersistentPlayerPublicKnowledge persistentPlayerPublicKnowledge;

	/** Publicly available varying details about this player that are not persisted to save game files */
	private final TransientPlayerPublicKnowledge transientPlayerPublicKnowledge;

	/**
	 * @param aPlayerDescription Publicly available details about this player
	 * @param aPersistentPlayerPublicKnowledge Publicly available varying details about this player that are persisted to save game files
	 * @param aTransientPlayerPublicKnowledge Publicly available varying details about this player that are not persisted to save game files
	 */
	public PlayerPublicDetails (final PlayerDescription aPlayerDescription,
		final PersistentPlayerPublicKnowledge aPersistentPlayerPublicKnowledge, final TransientPlayerPublicKnowledge aTransientPlayerPublicKnowledge)
	{
		super ();

		playerDescription = aPlayerDescription;
		persistentPlayerPublicKnowledge = aPersistentPlayerPublicKnowledge;
		transientPlayerPublicKnowledge = aTransientPlayerPublicKnowledge;
	}

	/**
	 * @return Publicly available details about this player
	 */
	public final PlayerDescription getPlayerDescription ()
	{
		return playerDescription;
	}

	/**
	 * @return Publicly available varying details about this player that are persisted to save game files
	 */
	public final PersistentPlayerPublicKnowledge getPersistentPlayerPublicKnowledge ()
	{
		return persistentPlayerPublicKnowledge;
	}

	/**
	 * @return Publicly available varying details about this player that are not persisted to save game files
	 */
	public final TransientPlayerPublicKnowledge getTransientPlayerPublicKnowledge ()
	{
		return transientPlayerPublicKnowledge;
	}
}