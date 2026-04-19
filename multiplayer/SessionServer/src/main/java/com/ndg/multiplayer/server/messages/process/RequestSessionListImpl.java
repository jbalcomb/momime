package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.messages.RequestSessionListSuccessfulCollectPlayerData;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.RequestSessionList;
import com.ndg.multiplayer.sessionbase.RequestSessionListFailed;
import com.ndg.multiplayer.sessionbase.RequestSessionListFailedReason;
import com.ndg.multiplayer.sessionbase.RequestSessionListSuccessful;

/**
 * Requests that server send us a list of sessions that we are either disconnected from, reloading, or free to accept a new player
 */
public final class RequestSessionListImpl extends RequestSessionList implements PreSessionClientToServerMessage
{
	/** Server-side session utils */
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
	public final void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, @SuppressWarnings ("unused") final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final RequestSessionListFailed failed = new RequestSessionListFailed ();
			failed.setReason (RequestSessionListFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// Success
			final RequestSessionListSuccessful success = new RequestSessionListSuccessful ();
			synchronized (server.getSessions ())
			{
				// If there are no sessions, just send message from here, otherwise the final session to collect its data will send it
				if (server.getSessions ().size () == 0)
					sender.sendMessageToClient (success);
				else
				{
					// Now this gets complicated - for each session, we need to collect the list of players in that session to include in the message
					// but the list of players is part of the session's private data which we don't synchronize access to - so the only thread that
					// we can safely read the list of players from is that session's thread.
					
					// So this literally posts a "give me your data" message onto the session thread of every currently active session.
					// Each thread then, when it is free, collects its data, adds it to the message, and the final thread to add to the
					// message goes ahead and sends it back to the client.					
					final RequestSessionListSuccessfulCollectPlayerData collectData = new RequestSessionListSuccessfulCollectPlayerData ();
					collectData.setMessage (success);
					collectData.setPlayerID (sender.getPlayerID ());
					collectData.setSessionThreadsRemaining (server.getSessions ().size ());
					collectData.setMultiplayerSessionServerUtils (getMultiplayerSessionServerUtils ());
					
					for (final MultiplayerSessionThread thisSession : server.getSessions ())
						thisSession.addMessageToQueue (collectData, sender);
				}
			}
		}
	}

	/**
	 * @return Server-side session utils
	 */
	public final MultiplayerSessionServerUtils getMultiplayerSessionServerUtils ()
	{
		return multiplayerSessionServerUtils;
	}

	/**
	 * @param utils Server-side session utils
	 */
	public final void setMultiplayerSessionServerUtils (final MultiplayerSessionServerUtils utils)
	{
		multiplayerSessionServerUtils = utils;
	}
}