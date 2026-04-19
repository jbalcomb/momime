package com.ndg.multiplayer.server.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.base.ServerToClientMessage;
import com.ndg.multiplayer.server.ServerToClientSessionConnection;
import com.ndg.multiplayer.session.MultiplayerSessionUtilsImpl;
import com.ndg.multiplayer.session.PlayerNotFoundException;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerType;

/**
 * Tests the MultiplayerSessionServerUtilsImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMultiplayerSessionServerUtilsImpl
{
	/**
	 * Tests the findPlayerWithID method where the player is in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_Exists () throws PlayerNotFoundException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerServerDetails (pd, null, null, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerServerDetails (new PlayerDescription (), null, null, null, null));

		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		assertEquals (12, utils.findPlayerWithID (players, 12).getPlayerDescription ().getPlayerID ().intValue ());
		assertEquals (12, utils.findPlayerWithID (players, 12, "testFindPlayerWithID_Exists").getPlayerDescription ().getPlayerID ().intValue ());
	}

	/**
	 * Tests the findPlayerWithID method where the player isn't in the list and we get a null back
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_NotExists_Null () throws PlayerNotFoundException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerServerDetails (pd, null, null, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerServerDetails (new PlayerDescription (), null, null, null, null));

		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		assertNull (utils.findPlayerWithID (players, 14));
	}

	/**
	 * Tests the findPlayerWithID method where the player isn't in the list and we get an exception back
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_NotExists_Fatal () throws PlayerNotFoundException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerServerDetails (pd, null, null, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerServerDetails (new PlayerDescription (), null, null, null, null));

		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		assertThrows (PlayerNotFoundException.class, () ->
		{
			utils.findPlayerWithID (players, 14, "testFindPlayerWithID_NotExists");
		});
	}

	/**
	 * Tests the indexOfPlayerWithID method where the player is in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testIndexOfPlayerWithID_Exists () throws PlayerNotFoundException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerServerDetails (pd, null, null, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerServerDetails (new PlayerDescription (), null, null, null, null));

		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		assertEquals (1, utils.indexOfPlayerWithID (players, 12, "testIndexOfPlayerWithID_Exists"));
	}

	/**
	 * Tests the indexOfPlayerWithID method where the player isn't in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testIndexOfPlayerWithID_NotExists () throws PlayerNotFoundException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerServerDetails (pd, null, null, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerServerDetails (new PlayerDescription (), null, null, null, null));

		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		assertThrows (PlayerNotFoundException.class, () ->
		{
			utils.indexOfPlayerWithID (players, 4, "testIndexOfPlayerWithID_NotExists");
		});
	}
	
	/**
	 * Tests the sendMessageToAllClients method
	 * @throws JAXBException If there is a problem sending the message
	 * @throws XMLStreamException If there is a problem sending the message
	 */
	@Test
	public final void testSendMessageToAllClients () throws JAXBException, XMLStreamException
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			pd.setPlayerType ((n == 2) ? PlayerType.AI : PlayerType.HUMAN);
			
			final PlayerServerDetails player = new PlayerServerDetails (pd, null, null, null, null);
			player.setConnection (mock (ServerToClientSessionConnection.class));
			
			players.add (player);
		}

		// Message to send
		final ServerToClientMessage msg = new ServerToClientMessage ();
		
		// Set up object to test
		final MultiplayerSessionServerUtilsImpl utils = new MultiplayerSessionServerUtilsImpl ();
		utils.setMultiplayerSessionUtils (new MultiplayerSessionUtilsImpl ());
		
		// Run method
		utils.sendMessageToAllClients (players, msg);
		
		// Check results
		verify (players.get (0).getConnection (), times (1)).sendMessageToClient (msg);
		verify (players.get (1).getConnection (), times (0)).sendMessageToClient (msg);		// AI player
		verify (players.get (2).getConnection (), times (1)).sendMessageToClient (msg);
	}
}