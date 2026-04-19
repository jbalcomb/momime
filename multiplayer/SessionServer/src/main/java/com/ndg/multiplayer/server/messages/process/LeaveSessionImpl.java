package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.server.session.PostSessionClientToServerMessage;
import com.ndg.multiplayer.sessionbase.LeaveSession;
import com.ndg.multiplayer.sessionbase.LeaveSessionFailed;
import com.ndg.multiplayer.sessionbase.LeaveSessionFailedReason;
import com.ndg.multiplayer.sessionbase.PlayerLeft;
import com.ndg.multiplayer.sessionbase.PlayerType;

/**
 * Requests that we want to leave the session we are in
 */
public final class LeaveSessionImpl extends LeaveSession implements PreSessionClientToServerMessage, PostSessionClientToServerMessage
{
	/**
	 * @param server The server handling player connections
	 * @param sender Player who sent the message
	 * @param thread Thread for the session this message is for; if the message is being executed in the client connection's thread instead, this will be null
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@SuppressWarnings ("unused")
	@Override
	public final void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		// This version gets called if executing in the client's connection thread - in which case its obviously bogus
		// because they have to be in a session in order to be able to leave it
		if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final LeaveSessionFailed failed = new LeaveSessionFailed ();
			failed.setReason (LeaveSessionFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// Already in session, can't start another one
			final LeaveSessionFailed failed = new LeaveSessionFailed ();
			failed.setReason (LeaveSessionFailedReason.NOT_IN_SESSION);
			sender.sendMessageToClient (failed);
		}
	}

	/**
	 * @param thread Thread for the session this message is for; from the thread, the processor can obtain the list of players, sd, gsk, gpl, etc
	 * @param sender Player who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerSessionThread thread, final PlayerServerDetails sender)
		throws JAXBException, XMLStreamException, IOException
	{
		// This version gets called if executing in the session thread - which is right because
		// we have to be in the session's thread in order to be able to process them leaving so that we can access the player list
		
		// They must already be logged in and correctly in the session to have gotten this far, so no real validation to do
		
		// First tell everybody (including the leaving player) that they're leaving
		final PlayerLeft playerLeft = new PlayerLeft ();
		playerLeft.setPlayerID (sender.getPlayerDescription ().getPlayerID ());
		
		for (final PlayerServerDetails thisPlayer : thread.getPlayers ())
			if (thisPlayer.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
				thisPlayer.getConnection ().sendMessageToClient (playerLeft);
		
		// Remove leaving player from session on server
		thread.getPlayers ().remove (sender);
		
		final MultiplayerClientConnection senderConn = (MultiplayerClientConnection) sender.getConnection ();
		senderConn.setSession (null);
		senderConn.setPlayer (null);

		// End session as well?
		thread.checkEndSessionAfterPlayerLeft ();
	}
}