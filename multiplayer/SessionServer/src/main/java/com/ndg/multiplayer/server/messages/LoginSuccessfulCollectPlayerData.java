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
import com.ndg.multiplayer.sessionbase.LoginSuccessful;
import com.ndg.multiplayer.sessionbase.SessionAndPlayerDescriptions;

/**
 * Internal message that, within the session's thread, checks to see if the specified player is a member
 * of the session and if so, adds this session to the LoginSuccessful message
 *
 * If this is the last session to process this message, then the completed LoginSuccessful message is sent to the client
 *
 * This relies on it being the *same message* posted onto each session's message queue, so they all
 * update the same sessionThreadsRemaining counter - creating a different message for each session thread won't work
 */
public final class LoginSuccessfulCollectPlayerData extends ClientToServerMessage implements PreSessionClientToServerMessage
{
	/** Server only helper methods for dealing with players in a session */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;
	
	/** Message being built up, note the player specified in the message is the one we are searching for; also used as the lock */
	private LoginSuccessful msg;

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
	public final void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		// This message gets posted simultaneously to every session thread - but only allow one to execute at time, to protect the list and counter
		final int counter;
		synchronized (getMessage ())
		{
			// Add session to list, if requesting player belongs to it
			if (getMultiplayerSessionServerUtils ().findPlayerWithID (thread.getPlayers (), getMessage ().getPlayerID ()) != null)
			{
				final SessionAndPlayerDescriptions spd = new SessionAndPlayerDescriptions ();
				spd.setSessionDescription (thread.getSessionDescription ());
				for (final PlayerServerDetails thisPlayer : thread.getPlayers ())
					spd.getPlayer ().add (thisPlayer.getPlayerDescription ());

				getMessage ().getSession ().add (spd);
			}

			// Send the completed message if this was the last thread that needed to be checked
			counter = getSessionThreadsRemaining () - 1;
			setSessionThreadsRemaining (counter);
			if (counter == 0)
			{
				sender.sendMessageToClient (getMessage ());
				server.playerLoggedIn (getMessage ().getPlayerID (), sender);
			}
		}
	}

	/**
	 * @return Message being built up, note the player specified in the message is the one we are searching for; also used as the lock
	 */
	public final LoginSuccessful getMessage ()
	{
		return msg;
	}

	/**
	 * @param message Message being built up, note the player specified in the message is the one we are searching for; also used as the lock
	 */
	public final void setMessage (final LoginSuccessful message)
	{
		msg = message;
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