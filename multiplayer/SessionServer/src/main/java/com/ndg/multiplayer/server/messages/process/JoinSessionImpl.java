package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.JoinFailed;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSession;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.PlayerType;

/**
 * Request to join an existing session, either one we previously disconnected from, reloading, or is accepting new players
 */
public final class JoinSessionImpl extends JoinSession implements PreSessionClientToServerMessage
{
	/** Server only helper methods for dealing with players in a session */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;
	
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
		// We don't know whether this is being ran on the client's connection thead or the session's thread
		// Easy validation is the same
		if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final JoinFailed failed = new JoinFailed ();
			failed.setReason (JoinFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't join another one
			final JoinFailed failed = new JoinFailed ();
			failed.setReason (JoinFailedReason.ALREADY_IN_SESSION);
			sender.sendMessageToClient (failed);
		}
		else if (thread == null)
		{
			// Running in client connection thread - so we need to search for the session that we want to join
			synchronized (server.getSessions ())
			{
				// Search for the session we want to join
				final MultiplayerSessionThread thisSession = server.findSession (getSessionID ());
				if (thisSession == null)
				{
					// Session ID not found
					final JoinFailed failed = new JoinFailed ();
					failed.setReason (JoinFailedReason.SESSION_ID_NOT_FOUND);
					sender.sendMessageToClient (failed);
				}
				else
				{
					// Found session thread, so now queue this message to be processed on the session thread
					// This guarantees that players join in a defined order, as an alternative approach to forcing the player list to be locked
					thisSession.addMessageToQueue (this, sender);
				}
			}
		}
		else
		{
			// Processing message for 2nd time, now in session's thread
			// So now we can check whether we're actually allowed to join this session or not
			final JoinFailedReason failedReason;
			final PlayerServerDetails rejoin = getMultiplayerSessionServerUtils ().findPlayerWithID (thread.getPlayers (), sender.getPlayerID ());
			if (rejoin != null)
				failedReason = (rejoin.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN) ? null : JoinFailedReason.CONVERTED_TO_AI;
			else
				failedReason = thread.canJoinSession (sender.getPlayerID ());

			if (failedReason != null)
			{
				final JoinFailed failed = new JoinFailed ();
				failed.setReason (failedReason);
				sender.sendMessageToClient (failed);
			}
			else
			{
				// Success
				sender.setSession (thread);
				thread.addPlayer (getPlayerDescription (), sender, (rejoin == null) ? JoinSuccessfulReason.JOINED_SESSION : JoinSuccessfulReason.REJOINED_SESSION, true, rejoin);
			}
		}
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