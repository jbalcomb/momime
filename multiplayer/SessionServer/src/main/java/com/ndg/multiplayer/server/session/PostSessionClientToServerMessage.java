package com.ndg.multiplayer.server.session;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * Server must extend all client-to-server messages with a version that supports either this interface or PreSessionClientToServerMessage,
 * defining the operations to take perform when the message is processed.
 * 
 * Note the session thread is included in the parameter list - so by definition, messages implementing this interface can
 * only be processed after the player has joined a session - sending one of these messages outside of a session will give the
 * "MultiplayerClientConnection doesn't know how to process message of type X" error.  However that's what we want anyway - all
 * pre-session messages are handled by the multiplayer module, so it'd be unusual for an application to want to implement its own pre-session messages.
 * 
 * This also means by definition that any messages implementing this interface will always be executed in the session's thread,
 * and therefore providing they only access variables scoped under the session, are inherently thread safe.
 */
public interface PostSessionClientToServerMessage
{
	/**
	 * @param thread Thread for the session this message is for; from the thread, the processor can obtain the list of players, sd, gsk, gpl, etc
	 * @param sender Player who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void process (final MultiplayerSessionThread thread, final PlayerServerDetails sender)
		throws JAXBException, XMLStreamException, IOException;
}
