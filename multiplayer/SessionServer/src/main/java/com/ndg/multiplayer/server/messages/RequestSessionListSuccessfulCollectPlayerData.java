package com.ndg.multiplayer.server.messages;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.multiplayer.sessionbase.RequestSessionListSuccessful;
import com.ndg.multiplayer.sessionbase.SessionAndPlayerDescriptions;

/**
 * Internal message that, within the session's thread, checks to see if the specified player is a member
 * of the session or is able to join it and if so, adds this session to the RequestSessionListSuccessful message
 *
 * If this is the last session to process this message, then the completed LoginSuccessful message is sent to the client
 *
 * This relies on it being the *same message* posted onto each session's message queue, so they all
 * update the same sessionThreadsRemaining counter - creating a different message for each session thread won't work
 */
public final class RequestSessionListSuccessfulCollectPlayerData extends ClientToServerMessage implements PreSessionClientToServerMessage
{
	/** Server only helper methods for dealing with players in a session */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;
	
	/** Message being built up; also used as the lock */
	private RequestSessionListSuccessful msg;

	/** Player who is looking for sessions to join */
	private int playerID;

	/** Number of session threads left to respond */
	private int sessionThreadsRemaining;

	/**
	 * @param server The server handling player connections
	 * @param sender Player who sent the message
	 * @param thread Thread for the session this message is for; if the message is being executed in the client connection's thread instead, this will be null
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (@SuppressWarnings ("unused") final MultiplayerSessionServer server, final MultiplayerClientConnection sender, final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		// This message gets posted simultaneously to every session thread - but only allow one to execute at time, to protect the list and counter
		synchronized (getMessage ())
		{
			// Add session to list, if requesting player belongs to it or is able to join it
			final PlayerServerDetails rejoin = getMultiplayerSessionServerUtils ().findPlayerWithID (thread.getPlayers (), getPlayerID ());
			
			if (((rejoin != null) && (rejoin.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)) || (thread.canJoinSession (getPlayerID ()) == null))
			{
				final SessionAndPlayerDescriptions spd = new SessionAndPlayerDescriptions ();
				spd.setSessionDescription (thread.getSessionDescription ());
				for (final PlayerServerDetails thisPlayer : thread.getPlayers ())
					spd.getPlayer ().add (thisPlayer.getPlayerDescription ());

				getMessage ().getSession ().add (spd);
			}

			// Send the completed message if this was the last thread that needed to be checked
			final int counter = getSessionThreadsRemaining () - 1;
			setSessionThreadsRemaining (counter);
			if (counter == 0)
				sender.sendMessageToClient (getMessage ());
		}
	}
	
	/**
	 * @return Message being built up, note the player specified in the message is the one we are searching for; also used as the lock
	 */
	public final RequestSessionListSuccessful getMessage ()
	{
		return msg;
	}

	/**
	 * @param message Message being built up, note the player specified in the message is the one we are searching for; also used as the lock
	 */
	public final void setMessage (final RequestSessionListSuccessful message)
	{
		msg = message;
	}

	/**
	 * @return Player who is looking for sessions to join
	 */
	public final int getPlayerID ()
	{
		return playerID;
	}

	/**
	 * @param id Player who is looking for sessions to join
	 */
	public final void setPlayerID (final int id)
	{
		playerID = id;
	}

	/**
	 * @return Number of session threads left to respond
	 */
	public final int getSessionThreadsRemaining ()
	{
		return sessionThreadsRemaining;
	}

	/**
	 * @param sessionCount Number of session threads left to respond
	 */
	public final void setSessionThreadsRemaining (final int sessionCount)
	{
		sessionThreadsRemaining = sessionCount;
	}

	/**
	 * @return Server only helper methods for dealing with players in a session
	 */
	public final MultiplayerSessionServerUtils getMultiplayerSessionServerUtils ()
	{
		return multiplayerSessionServerUtils;
	}

	/**
	 * @param obj Server only helper methods for dealing with players in a session
	 */
	public final void setMultiplayerSessionServerUtils (final MultiplayerSessionServerUtils obj)
	{
		multiplayerSessionServerUtils = obj;
	}
}