package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.CreateAccount;
import com.ndg.multiplayer.sessionbase.CreateAccountFailed;
import com.ndg.multiplayer.sessionbase.CreateAccountFailedReason;
import com.ndg.multiplayer.sessionbase.CreateAccountSuccessful;

/**
 * Request creation of a user account on the server; the playerName is then reserved for that person
 */
public final class CreateAccountImpl extends CreateAccount implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (CreateAccountImpl.class);
	
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
		// See if player name is already taken
		final int playerID = server.getUserRegistry ().createAccount (getPlayerName (), getPlayerPassword ());
		if (playerID <= 0)
		{
			// Player already exists
			final CreateAccountFailed failed = new CreateAccountFailed ();
			failed.setReason (CreateAccountFailedReason.PLAYER_ALREADY_EXISTS);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// Success
			server.getUserRegistry ().saveUserRegistry ();
			
			final CreateAccountSuccessful success = new CreateAccountSuccessful ();
			success.setPlayerID (playerID);
			sender.sendMessageToClient (success);

			log.info (sender.getRemoteSocketAddress () + " created account for player ID " + playerID + " for name \"" + getPlayerName () + "\"");
		}
	}
}