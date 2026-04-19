package com.ndg.multiplayer.server.users;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;

import com.ndg.multiplayer.userregistry.Player;

/**
 * Interface that allows user accounts to be created and authenticated
 */
public interface MultiplayerSessionUserRegistry
{
	/**
	 * Sets up a new, empty user registry
	 */
	public void newUserRegistry ();
	
	/**
	 * Loads the user registry from disk
	 * @throws IOException If there is any misconfiguration
	 * @throws JAXBException If there is a problem parsing the XML to load the user registry
	 */
	public void loadUserRegistry () throws IOException, JAXBException;

	/**
	 * Saves the user registry XML to disk
	 * @throws IOException If there is any misconfiguration
	 * @throws JAXBException If there is a problem generating the XML to save the user registry
	 */
	public void saveUserRegistry () throws IOException, JAXBException;
	
	/**
	 * @param playerID Player ID to search for
	 * @return User registry entry if player found, null if not found
	 */
	public Player findPlayerByID (final int playerID);
	
	/**
	 * @param playerName Player name to search for
	 * @return User registry entry if player found, null if not found
	 */
	public Player findPlayerByName (final String playerName);
	
	/**
	 * @param playerName Requested player name
	 * @param playerPassword Password that player supplied
	 * @return playerID allocated to this new player account, or <=0 if player name already exists
	 */
	public int createAccount (final String playerName, final String playerPassword);

	/**
	 * @param playerName Requested player name
	 * @param playerPassword Password that player supplied
	 * @return playerID for this player, or <=0 if name or password is incorrect
	 */
	public int authenticateUser (final String playerName, final String playerPassword);
}