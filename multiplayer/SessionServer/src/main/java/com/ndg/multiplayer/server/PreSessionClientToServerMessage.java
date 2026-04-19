package com.ndg.multiplayer.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.session.MultiplayerSessionThread;

/**
 * Server must extend all client-to-server messages with a version that supports either this interface or PostSessionClientToServerMessage,
 * defining the operations to take perform when the message is processed.
 *
 * Messages implementing this interface will be executed whether the player has joined a session already or not.  This also means that messages
 * implementing this interface may be executed either on the client connection thread (if the player is not yet in a session) or in the
 * session's thread (if the player is in a session).  So implementations must be wary of threading and synchronization issues. 
 */
public interface PreSessionClientToServerMessage
{
	/**
	 * @param server The server handling player connections
	 * @param sender Player who sent the message
	 * @param thread Thread for the session this message is for; if the message is being executed in the client connection's thread instead, this will be null
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException;
}
