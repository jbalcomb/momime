package com.ndg.multiplayer.base.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

/**
 * Interface for messages that take some custom duration to complete.  Any other messages
 * arriving will be blocked and paused until this completes.  Since there is no polling
 * mechanism, when the application decides that the message has completed, it must call
 * 
 * client.finishCustomDurationMessage (msg);
 * 
 * at which time the finish () method will be called, the message removed from the queue and
 * any messages queued up behind it can be processed.  The finish method will be called
 * in whichever thread calls finishCustomDurationMessage.
 */
public interface CustomDurationServerToClientMessage extends BaseServerToClientMessage
{
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void finish () throws JAXBException, XMLStreamException, IOException;
}