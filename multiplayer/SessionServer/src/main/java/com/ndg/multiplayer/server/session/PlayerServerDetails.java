package com.ndg.multiplayer.server.session;

import com.ndg.multiplayer.server.ServerToClientSessionConnection;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.TransientPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.TransientPlayerPublicKnowledge;

/**
 * Adds private knowledge to PlayerPublicDetails so server has all the details about every player
 */
public final class PlayerServerDetails extends PlayerPublicDetails
{
	/** Private knowledge this player has that is persisted to save game files */
	private final PersistentPlayerPrivateKnowledge persistentPlayerPrivateKnowledge;

	/** Private knowledge this player has that is not persisted to save game files */
	private final TransientPlayerPrivateKnowledge transientPlayerPrivateKnowledge;

	/** Connection to this client - this can be null if the client has disconnected */
	private ServerToClientSessionConnection connection;

	/**
	 * @param aPlayerDescription Publicly available details about this player
	 * @param aPersistentPlayerPublicKnowledge Publicly available varying details about this player that are persisted to save game files
	 * @param aPersistentPlayerPrivateKnowledge Private knowledge this player has that is persisted to save game files
	 * @param aTransientPlayerPublicKnowledge Publicly available varying details about this player that are not persisted to save game files
	 * @param aTransientPlayerPrivateKnowledge Private knowledge this player has that is not persisted to save game files
	 */
	public PlayerServerDetails (final PlayerDescription aPlayerDescription,
		final PersistentPlayerPublicKnowledge aPersistentPlayerPublicKnowledge, final PersistentPlayerPrivateKnowledge aPersistentPlayerPrivateKnowledge,
		final TransientPlayerPublicKnowledge aTransientPlayerPublicKnowledge, final TransientPlayerPrivateKnowledge aTransientPlayerPrivateKnowledge)
	{
		super (aPlayerDescription, aPersistentPlayerPublicKnowledge, aTransientPlayerPublicKnowledge);

		persistentPlayerPrivateKnowledge = aPersistentPlayerPrivateKnowledge;
		transientPlayerPrivateKnowledge = aTransientPlayerPrivateKnowledge;
	}

	/**
	 * @return Private knowledge this player has that is persisted to save game files
	 */
	public final PersistentPlayerPrivateKnowledge getPersistentPlayerPrivateKnowledge ()
	{
		return persistentPlayerPrivateKnowledge;
	}

	/**
	 * @return Private knowledge this player has that is not persisted to save game files
	 */
	public final TransientPlayerPrivateKnowledge getTransientPlayerPrivateKnowledge ()
	{
		return transientPlayerPrivateKnowledge;
	}

	/**
	 * @return Connection to this client - this can be null if the client has disconnected
	 */
	public final ServerToClientSessionConnection getConnection ()
	{
		return connection;
	}

	/**
	 * @param conn Connection to this client - this can be null if the client has disconnected
	 */
	public final void setConnection (final ServerToClientSessionConnection conn)
	{
		connection = conn;
	}
}