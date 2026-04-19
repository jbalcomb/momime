package com.ndg.multiplayer.demo.server;

import com.ndg.multiplayer.demo.DemoPersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.demo.DemoPersistentPlayerPublicKnowledge;
import com.ndg.multiplayer.demo.DemoTransientPlayerPrivateKnowledge;
import com.ndg.multiplayer.demo.DemoTransientPlayerPublicKnowledge;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.SessionDescription;
import com.ndg.multiplayer.sessionbase.TransientPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.TransientPlayerPublicKnowledge;

/**
 * Demo showing how to subclass MultiplayerSessionThread
 */
public final class DemoSessionServerThread extends MultiplayerSessionThread
{
	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Application-specific class
	 */
	@Override
	@SuppressWarnings ("unused")
	protected PersistentPlayerPublicKnowledge createPersistentPlayerPublicKnowledge (final SessionDescription sd, final PlayerDescription pd)
	{
		final DemoPersistentPlayerPublicKnowledge pub = new DemoPersistentPlayerPublicKnowledge ();
		pub.setAge (24);

		return pub;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Application-specific class
	 */
	@Override
	@SuppressWarnings ("unused")
	protected PersistentPlayerPrivateKnowledge createPersistentPlayerPrivateKnowledge (final SessionDescription sd, final PlayerDescription pd)
	{
		final DemoPersistentPlayerPrivateKnowledge priv = new DemoPersistentPlayerPrivateKnowledge ();
		priv.setFavouriteColour ("Orange");

		return priv;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Application-specific class
	 */
	@Override
	@SuppressWarnings ("unused")
	public TransientPlayerPublicKnowledge createTransientPlayerPublicKnowledge (final SessionDescription sd, final PlayerDescription pd)
	{
		final DemoTransientPlayerPublicKnowledge pub = new DemoTransientPlayerPublicKnowledge ();
		pub.setFinishedTurn (true);

		return pub;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Application-specific class
	 */
	@Override
	@SuppressWarnings ("unused")
	public TransientPlayerPrivateKnowledge createTransientPlayerPrivateKnowledge (final SessionDescription sd, final PlayerDescription pd)
	{
		final DemoTransientPlayerPrivateKnowledge priv = new DemoTransientPlayerPrivateKnowledge ();
		priv.setMovesRemainingThisTurn (5);

		return priv;
	}
}