package com.ndg.multiplayer.server.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.server.DummyServerToClientSessionConnection;
import com.ndg.multiplayer.sessionbase.AdditionalPlayerJoined;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSuccessful;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.multiplayer.sessionbase.SessionDescription;

/**
 * Tests the MultiplayerSessionThread class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMultiplayerSessionThread
{
	/**
	 * Tests the canJoinSession method when there's no limit on the number who can join
	 */
	@Test
	public final void testCanJoinSession_NullMaximum ()
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// Add some players
		for (int n = 0; n < 10; n++)
			session.getPlayers ().add (null);
		
		// Run method
		assertNull (session.canJoinSession (1));
	}

	/**
	 * Tests the canJoinSession method when there's no limit on the number who can join
	 */
	@Test
	public final void testCanJoinSession_ZeroMaximum ()
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();
		sd.setMaxPlayers (0);
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// Add some players
		for (int n = 0; n < 10; n++)
			session.getPlayers ().add (null);
		
		// Run method
		assertNull (session.canJoinSession (1));
	}

	/**
	 * Tests the canJoinSession method when there's limited space, but still spots available
	 */
	@Test
	public final void testCanJoinSession_Space ()
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();
		sd.setMaxPlayers (10);
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// Add some players
		for (int n = 0; n < 9; n++)
			session.getPlayers ().add (null);
		
		// Run method
		assertNull (session.canJoinSession (1));
	}

	/**
	 * Tests the canJoinSession method when there's limited space and its already full
	 */
	@Test
	public final void testCanJoinSession_NoSpace ()
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();
		sd.setMaxPlayers (10);
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// Add some players
		for (int n = 0; n < 10; n++)
			session.getPlayers ().add (null);
		
		// Run method
		assertEquals (JoinFailedReason.SESSION_FULL, session.canJoinSession (1));
	}
	
	/**
	 * Tests the addPlayer method through several scenarios from setting up a new game, players joining, and players disconnecting and rejoining
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testAddPlayer_NewGame () throws Exception
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();

		// Player descriptions
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerType (PlayerType.HUMAN);
		pd1.setPlayerID (1);
		
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerType (PlayerType.AI);

		final PlayerDescription pd3 = new PlayerDescription ();
		pd3.setPlayerType (PlayerType.HUMAN);
		pd3.setPlayerID (2);
		
		// Connections
		final DummyServerToClientSessionConnection conn1 = new DummyServerToClientSessionConnection ();
		final DummyServerToClientSessionConnection conn3 = new DummyServerToClientSessionConnection ();
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// 1st human player creates the game
		final PlayerServerDetails player1 = session.addPlayer (pd1, conn1, JoinSuccessfulReason.NEW_SESSION, true, null);
		
		// Verify the player was set up correctly
		assertSame (pd1, player1.getPlayerDescription ());
		assertNull (player1.getPersistentPlayerPublicKnowledge ());
		assertNull (player1.getPersistentPlayerPrivateKnowledge ());
		assertNull (player1.getTransientPlayerPublicKnowledge ());
		assertNull (player1.getTransientPlayerPrivateKnowledge ());
		
		assertSame (conn1, player1.getConnection ());
		assertSame (player1, conn1.getPlayer ());
		
		// Verify the player was added on the server
		assertEquals (1, session.getPlayers ().size ());
		assertSame (player1, session.getPlayers ().get (0));
		
		// Verify the player was sent the success response and their own details
		assertEquals (1, conn1.getMessages ().size ());
		final JoinSuccessful msg1 = (JoinSuccessful) conn1.getMessages ().get (0);
		
		assertEquals (JoinSuccessfulReason.NEW_SESSION, msg1.getReason ());
		assertSame (sd, msg1.getSessionDescription ());
		assertNull (msg1.getGeneralPublicKnowledge ());
		assertNull (msg1.getPersistentPlayerPrivateKnowledge ());
		assertNull (msg1.getTransientPlayerPrivateKnowledge ());
		assertEquals (1, msg1.getPlayer ().size ());
		
		assertSame (pd1, msg1.getPlayer ().get (0).getPlayerDescription ());
		assertNull (msg1.getPlayer ().get (0).getPersistentPlayerPublicKnowledge ());
		assertNull (msg1.getPlayer ().get (0).getTransientPlayerPublicKnowledge ());
		
		// Now add an AI player
		final PlayerServerDetails player2 = session.addPlayer (pd2, null, null, true, null);
		
		// Check a player ID got allocated for the AI player
		assertEquals (-1, pd2.getPlayerID ().intValue ());

		// Verify the player was set up correctly
		assertSame (pd2, player2.getPlayerDescription ());
		assertNull (player2.getPersistentPlayerPublicKnowledge ());
		assertNull (player2.getPersistentPlayerPrivateKnowledge ());
		assertNull (player2.getTransientPlayerPublicKnowledge ());
		assertNull (player2.getTransientPlayerPrivateKnowledge ());
		assertNull (player2.getConnection ());

		// Verify the player was added on the server
		assertEquals (2, session.getPlayers ().size ());
		assertSame (player2, session.getPlayers ().get (1));
		
		// Verify the 1st human player was sent details about the AI player joining
		assertEquals (2, conn1.getMessages ().size ());
		final AdditionalPlayerJoined msg2 = (AdditionalPlayerJoined) conn1.getMessages ().get (1);

		assertSame (pd2, msg2.getPlayer ().getPlayerDescription ());
		assertNull (msg2.getPlayer ().getPersistentPlayerPublicKnowledge ());
		assertNull (msg2.getPlayer ().getTransientPlayerPublicKnowledge ());
		
		// Now add a 2nd human player
		final PlayerServerDetails player3 = session.addPlayer (pd3, conn3, JoinSuccessfulReason.JOINED_SESSION, true, null);
		
		// Verify the player was set up correctly
		assertSame (pd3, player3.getPlayerDescription ());
		assertNull (player3.getPersistentPlayerPublicKnowledge ());
		assertNull (player3.getPersistentPlayerPrivateKnowledge ());
		assertNull (player3.getTransientPlayerPublicKnowledge ());
		assertNull (player3.getTransientPlayerPrivateKnowledge ());
		
		assertSame (conn3, player3.getConnection ());
		assertSame (player3, conn3.getPlayer ());
		
		// Verify the player was added on the server
		assertEquals (3, session.getPlayers ().size ());
		assertSame (player3, session.getPlayers ().get (2));
		
		// Verify the player was sent the success response and all 3 sets of details
		assertEquals (1, conn3.getMessages ().size ());
		final JoinSuccessful msg3 = (JoinSuccessful) conn3.getMessages ().get (0);
		
		assertEquals (JoinSuccessfulReason.JOINED_SESSION, msg3.getReason ());
		assertSame (sd, msg3.getSessionDescription ());
		assertNull (msg3.getGeneralPublicKnowledge ());
		assertNull (msg3.getPersistentPlayerPrivateKnowledge ());
		assertNull (msg3.getTransientPlayerPrivateKnowledge ());
		assertEquals (3, msg3.getPlayer ().size ());

		assertSame (pd1, msg3.getPlayer ().get (0).getPlayerDescription ());
		assertNull (msg3.getPlayer ().get (0).getPersistentPlayerPublicKnowledge ());
		assertNull (msg3.getPlayer ().get (0).getTransientPlayerPublicKnowledge ());

		assertSame (pd2, msg3.getPlayer ().get (1).getPlayerDescription ());
		assertNull (msg3.getPlayer ().get (1).getPersistentPlayerPublicKnowledge ());
		assertNull (msg3.getPlayer ().get (1).getTransientPlayerPublicKnowledge ());
		
		assertSame (pd3, msg3.getPlayer ().get (2).getPlayerDescription ());
		assertNull (msg3.getPlayer ().get (2).getPersistentPlayerPublicKnowledge ());
		assertNull (msg3.getPlayer ().get (2).getTransientPlayerPublicKnowledge ());
		
		// Verify the 1st human player was sent info about the 2nd joining
		assertEquals (3, conn1.getMessages ().size ());
		final AdditionalPlayerJoined msg4 = (AdditionalPlayerJoined) conn1.getMessages ().get (2);

		assertSame (pd3, msg4.getPlayer ().getPlayerDescription ());
		assertNull (msg4.getPlayer ().getPersistentPlayerPublicKnowledge ());
		assertNull (msg4.getPlayer ().getTransientPlayerPublicKnowledge ());
		
		// The 2nd human player now disconnects, and rejoins on a new connection
		player3.setConnection (null);
		conn3.setPlayer (null);
		
		final DummyServerToClientSessionConnection conn4 = new DummyServerToClientSessionConnection ();
		assertSame (player3, session.addPlayer (pd3, conn4, JoinSuccessfulReason.REJOINED_SESSION, true, player3));
		
		// Verify the connection was set up correctly
		assertSame (conn4, player3.getConnection ());
		assertSame (player3, conn4.getPlayer ());

		// Verify nothing changed on the server
		assertEquals (3, session.getPlayers ().size ());
		
		// Verify the player was sent the success response and all 3 sets of details
		assertEquals (1, conn4.getMessages ().size ());
		final JoinSuccessful msg5 = (JoinSuccessful) conn4.getMessages ().get (0);
		
		assertEquals (JoinSuccessfulReason.REJOINED_SESSION, msg5.getReason ());
		assertSame (sd, msg5.getSessionDescription ());
		assertNull (msg5.getGeneralPublicKnowledge ());
		assertNull (msg5.getPersistentPlayerPrivateKnowledge ());
		assertNull (msg5.getTransientPlayerPrivateKnowledge ());
		assertEquals (3, msg5.getPlayer ().size ());

		assertSame (pd1, msg5.getPlayer ().get (0).getPlayerDescription ());
		assertNull (msg5.getPlayer ().get (0).getPersistentPlayerPublicKnowledge ());
		assertNull (msg5.getPlayer ().get (0).getTransientPlayerPublicKnowledge ());

		assertSame (pd2, msg5.getPlayer ().get (1).getPlayerDescription ());
		assertNull (msg5.getPlayer ().get (1).getPersistentPlayerPublicKnowledge ());
		assertNull (msg5.getPlayer ().get (1).getTransientPlayerPublicKnowledge ());
		
		assertSame (pd3, msg5.getPlayer ().get (2).getPlayerDescription ());
		assertNull (msg5.getPlayer ().get (2).getPersistentPlayerPublicKnowledge ());
		assertNull (msg5.getPlayer ().get (2).getTransientPlayerPublicKnowledge ());

		// Verify the 1st human player wasn't resent info just because of the dropped connection
		assertEquals (3, conn1.getMessages ().size ());
	}

	/**
	 * Tests the addPlayer method reloading a saved game
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testAddPlayer_LoadGame () throws Exception
	{
		// Session description
		final SessionDescription sd = new SessionDescription ();

		// Player descriptions
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerType (PlayerType.HUMAN);
		pd1.setPlayerID (1);
		
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerType (PlayerType.AI);
		pd2.setPlayerID (-1);

		final PlayerDescription pd3 = new PlayerDescription ();
		pd3.setPlayerType (PlayerType.HUMAN);
		pd3.setPlayerID (2);
		
		// Reloading game, so all the full player objects already exist
		final PlayerServerDetails player1 = new PlayerServerDetails (pd1, null, null, null, null);
		final PlayerServerDetails player2 = new PlayerServerDetails (pd2, null, null, null, null);
		final PlayerServerDetails player3 = new PlayerServerDetails (pd3, null, null, null, null);
		
		// Connection
		final DummyServerToClientSessionConnection conn1 = new DummyServerToClientSessionConnection ();
		
		// Set up object to test
		final MultiplayerSessionThread session = new MultiplayerSessionThread ();
		session.setSessionDescription (sd);
		
		// Add players in sequence, first player loading the game has a connection, nobody else has joined yet
		assertSame (player1, session.addPlayer (pd1, conn1, null, false, player1));
		assertSame (player2, session.addPlayer (pd2, null, null, false, player2));
		assertSame (player3, session.addPlayer (pd3, null, null, false, player3));

		// Verify the connection was set up correctly
		assertSame (conn1, player1.getConnection ());
		assertSame (player1, conn1.getPlayer ());
		
		// Verify no messages were sent
		assertEquals (0, conn1.getMessages ().size ());
		
		// Verify all the players are now in the list
		assertEquals (3, session.getPlayers ().size ());
		assertSame (player1, session.getPlayers ().get (0));
		assertSame (player2, session.getPlayers ().get (1));
		assertSame (player3, session.getPlayers ().get (2));
	}
}