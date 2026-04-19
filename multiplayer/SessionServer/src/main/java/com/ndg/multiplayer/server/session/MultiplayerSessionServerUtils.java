package com.ndg.multiplayer.server.session;

import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ServerToClientMessage;
import com.ndg.multiplayer.session.PlayerNotFoundException;

/**
 * Server only helper methods for dealing with players in a session
 */
public interface MultiplayerSessionServerUtils
{
	/**
	 * This is just a special server version of the method from MultiplayerSessionUtils to save typecasting the result to a PlayerServerDetails
	 *
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @return Info for the requested player
	 */
	public PlayerServerDetails findPlayerWithID (final List<PlayerServerDetails> players, final int playerID);

	/**
	 * This is just a special server version of the method from MultiplayerSessionUtils to save typecasting the result to a PlayerServerDetails
	 *
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Info for the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	public PlayerServerDetails findPlayerWithID (final List<PlayerServerDetails> players, final int playerID, final String caller) throws PlayerNotFoundException;

	/**
	 * @param players List of players to search
	 * @param playerID player ID to search for
	 * @param caller Name of method calling this, for inclusion in debug message if there is a problem
	 * @return Index into the players list of the requested player
	 * @throws PlayerNotFoundException If the player isn't in the list
	 */
	public int indexOfPlayerWithID (final List<? extends PlayerServerDetails> players, final int playerID, final String caller) throws PlayerNotFoundException;
	
	/**
	 * Any places that need to send a message to multiple players should do so via this method, that way they won't
	 * all need to be rewired should we ever make this more intelligent around trying to send messages to disconnected clients
	 *
	 * @param players List of players to send message to
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem sending the message
	 * @throws XMLStreamException If there is a problem sending the message
	 */
	public void sendMessageToAllClients (final List<PlayerServerDetails> players, final ServerToClientMessage msg)
		throws JAXBException, XMLStreamException;
}