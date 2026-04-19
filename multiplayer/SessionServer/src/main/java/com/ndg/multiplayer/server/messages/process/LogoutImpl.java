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
import com.ndg.multiplayer.sessionbase.Logout;
import com.ndg.multiplayer.sessionbase.LogoutFailed;
import com.ndg.multiplayer.sessionbase.LogoutFailedReason;
import com.ndg.multiplayer.sessionbase.LogoutSuccessful;

/**
 * Logs off the current player (without dropping the connection)
 */
public final class LogoutImpl extends Logout implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (LogoutImpl.class);
	
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
		if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final LogoutFailed failed = new LogoutFailed ();
			failed.setReason (LogoutFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// Success
			log.info (sender.getRemoteSocketAddress () + " logged out from using player ID " + sender.getPlayerID ());

			sender.setPlayerID (0);
			sender.sendMessageToClient (new LogoutSuccessful ());
		}
	}
}