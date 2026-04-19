package com.ndg.multiplayer.base.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * Client must extend all server-to-client messages with a version that supports this interface,
 * defining the operations to take perform when the message is processed.
 * 
 * Whether this is executed on the server connection thread or the Swing event dispatcher thread depends on the
 * processMessagesOnSwingEventDispatcherThread property set on the client.  In non-Swing apps, or Swing apps
 * where this is set to false, it is up to the message implementations to take care of any necessary synchronization.
 * Swing apps where this is set to true are safe to update Swing components directly in the message implementations.  
 * 
 * Messages implementing this interface trigger a single execution and then complete - they take no time duration.
 * If there are animation messages currently being played and delaying the queue, then both base and
 * further animated messages will be queued up behind the animation and executed once it completes.
 */
public interface BaseServerToClientMessage
{
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void start () throws JAXBException, XMLStreamException, IOException;
}