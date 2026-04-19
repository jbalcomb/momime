package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.JoinFailed;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.NewSession;
import com.ndg.multiplayer.userregistry.Player;

/**
 * Requests that server creates a new session and joins the player as the first player in it
 */
public final class NewSessionImpl extends NewSession implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (NewSessionImpl.class);
	
	/**
	 * @param server The server handling player connections
	 * @param sender Player who sent the message
	 * @param thread Thread for the session this message is for; if the message is being executed in the client connection's thread instead, this will be null
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, @SuppressWarnings ("unused") final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final JoinFailed failed = new JoinFailed ();
			failed.setReason (JoinFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't start another one
			final JoinFailed failed = new JoinFailed ();
			failed.setReason (JoinFailedReason.ALREADY_IN_SESSION);
			sender.sendMessageToClient (failed);
		}
		else
		{
			synchronized (server.getSessions ())
			{
				// Fill in remaining fields into player and session description
				log.info ("Player ID " + sender.getPlayerID () + " started new session with session ID " + server.getNextAvailableSessionID () + " and name \"" + getSessionDescription ().getSessionName () + "\"");

				// Complete the partial session description sent by the client by filling in the auto populated parts
				final GregorianCalendar now = new GregorianCalendar ();
				now.setTime (new Date ());

				getSessionDescription ().setSessionID (server.getNextAvailableSessionID () );
				getSessionDescription ().setStartedAt (server.getDatatypeFactory ().newXMLGregorianCalendar (now));
				server.setNextAvailableSessionID (server.getNextAvailableSessionID () + 1);

				// Allocate a saved game ID
				if (server.isSavedGamesEnabled ())
				{
					final Player player = server.getUserRegistry ().findPlayerByID (sender.getPlayerID ());
					if (player != null)
					{
						getSessionDescription ().setSavedGameID (player.getNextAvailableSavedGameID ());
						player.setNextAvailableSavedGameID (player.getNextAvailableSavedGameID () + 1);
						server.getUserRegistry ().saveUserRegistry ();
					}
				}
				
				// Create the sesson
				final MultiplayerSessionThread sessionThread = server.createSessionThread (getSessionDescription ());
				sessionThread.setMultiplayerSessionServer (server);
				server.getSessions ().add (sessionThread);
				sessionThread.initializeNewGame ();

				// Add the player into the session, and sends the success reply to them
				sender.setSession (sessionThread);
				sessionThread.addPlayer (getPlayerDescription (), sender, JoinSuccessfulReason.NEW_SESSION, true, null);
				sessionThread.sessionAdded ();
				sessionThread.start ();
			}
		}
	}
}