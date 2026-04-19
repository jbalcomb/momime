package com.ndg.multiplayer.base.client;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ClientToServerMessage;

/**
 * Interface describing methods that the client-side connection to the server must support
 * 
 * This is declared like so so that client-side unit tests that send messages to the server can create a
 * dummy implementation which captures and checks the messages rather than actually sending them
 */
public interface ClientToServerConnection
{
	/**
	 * Sends XML message to the client
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public void sendMessageToServer (final ClientToServerMessage msg) throws JAXBException, XMLStreamException;
	
	/**
	 * Finishes a custom duration message, or an animated message with isFinishAfterDuration = false
	 * 
	 * @param msg Message to finish
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 */
	public void finishCustomDurationMessage (final CustomDurationServerToClientMessage msg) throws IOException, JAXBException, XMLStreamException;
	
	/**
	 * Closes the XML output stream and the socket
	 * Note the actual closing happens on the listener thread, so anything calling this route cannot assume that the socket
	 * will be closed by the time this method exits
	 * @param aCloseMonitor Thread lock object to notify once socket has been closed; null if no closing notification is requested
	 * @throws XMLStreamException If there is a problem sending the </conversation> tag
	 */
	public void close (final Object aCloseMonitor) throws XMLStreamException;
}