package com.ndg.multiplayer.base.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * Server must extend all client-to-server messages with a version that supports this interface,
 * defining the operations to take perform when the message is processed.
 * 
 * This will be executed on the client connection thread.  It is assumed that connections are independant, that is processing of a message
 * from client A won't any affect client B in any way and so there will be no synchronization issues between client connections.
 * If that isn't the case then any synchronization necessary must be taken care of by the message implementations. 
 * 
 * If using session modules, use the interfaces declared there instead, which have different synchronization semantics.
 */
public interface BaseClientToServerMessage
{
	/**
	 * @param sender Client who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void process (final MultiplayerBaseServerThread sender)
		throws JAXBException, XMLStreamException, IOException;
}
