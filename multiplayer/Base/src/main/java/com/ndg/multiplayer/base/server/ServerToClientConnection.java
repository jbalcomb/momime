package com.ndg.multiplayer.base.server;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ServerToClientMessage;

/**
 * Interface describing methods that the server-side connection to each client must support
 * 
 * This is declared like so so that server-side unit tests that send messages to clients can create a
 * dummy implementation which captures and checks the messages rather than actually sending them
 */
public interface ServerToClientConnection
{
	/**
	 * Sends XML message to the client
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public void sendMessageToClient (final ServerToClientMessage msg) throws JAXBException, XMLStreamException;
}
