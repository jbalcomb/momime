package com.ndg.multiplayer.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.sessionbase.PlayerDescription;

/**
 * Tests the MultiplayerSessionUtilsImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMultiplayerSessionUtilsImpl
{
	/**
	 * Tests the findPlayerWithID method where the player is in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_Exists () throws PlayerNotFoundException
	{
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerPublicDetails (pd, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerPublicDetails (new PlayerDescription (), null, null));

		// Run method
		assertEquals (12, new MultiplayerSessionUtilsImpl ().findPlayerWithID (players, 12).getPlayerDescription ().getPlayerID ().intValue ());
		assertEquals (12, new MultiplayerSessionUtilsImpl ().findPlayerWithID (players, 12, "testFindPlayerWithID_Exists").getPlayerDescription ().getPlayerID ().intValue ());
	}

	/**
	 * Tests the findPlayerWithID method where the player isn't in the list and we get a null back
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_NotExists_Null () throws PlayerNotFoundException
	{
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerPublicDetails (pd, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerPublicDetails (new PlayerDescription (), null, null));

		// Run method
		assertNull (new MultiplayerSessionUtilsImpl ().findPlayerWithID (players, 14));
	}

	/**
	 * Tests the findPlayerWithID method where the player isn't in the list and we get an exception back
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testFindPlayerWithID_NotExists_Fatal () throws PlayerNotFoundException
	{
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerPublicDetails (pd, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerPublicDetails (new PlayerDescription (), null, null));

		// Run method
		assertThrows (PlayerNotFoundException.class, () ->
		{
			new MultiplayerSessionUtilsImpl ().findPlayerWithID (players, 14, "testFindPlayerWithID_NotExists");
		});
	}

	/**
	 * Tests the indexOfPlayerWithID method where the player is in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testIndexOfPlayerWithID_Exists () throws PlayerNotFoundException
	{
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerPublicDetails (pd, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerPublicDetails (new PlayerDescription (), null, null));

		// Run method
		assertEquals (1, new MultiplayerSessionUtilsImpl ().indexOfPlayerWithID (players, 12, "testIndexOfPlayerWithID_Exists"));
	}

	/**
	 * Tests the indexOfPlayerWithID method where the player isn't in the list
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	@Test
	public final void testIndexOfPlayerWithID_NotExists () throws PlayerNotFoundException
	{
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		for (int n = 1; n <= 3; n++)
		{
			final PlayerDescription pd = new PlayerDescription ();
			pd.setPlayerID (n + 10);
			players.add (new PlayerPublicDetails (pd, null, null));
		}

		// Should never get a player without an ID in the list, but to prove that it doesn't crash the method
		players.add (new PlayerPublicDetails (new PlayerDescription (), null, null));

		// Run method
		assertThrows (PlayerNotFoundException.class, () ->
		{
			new MultiplayerSessionUtilsImpl ().indexOfPlayerWithID (players, 4, "testIndexOfPlayerWithID_NotExists");
		});
	}
}