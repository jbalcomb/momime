package com.ndg.multiplayer.base.client;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import jakarta.xml.bind.JAXBException;

/**
 * Main client class to initiate a connection to a server.
 * A separate thread is kicked off to listen for messages from the server when we make a connection, but state should be
 * held here so that if the connection thread dies, we don't lose the info.
 */
public interface MultiplayerBaseClient
{
	/**
	 * Initiate connection to the server
	 * @throws IOException If there is a problem making the connection
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	public void connect () throws IOException, InterruptedException, JAXBException, XMLStreamException;
	
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
	 * @return Connection to server
	 */
	public ClientToServerConnection getServerConnection ();
	
	/**
	 * @return IP or DNS name of the server
	 */
	public String getServerAddress ();
	
	/**
	 * @param addr IP or DNS name of the server
	 */
	public void setServerAddress (final String addr);
	
	/**
	 * @return Port TCP/IP port number that the server is listening on
	 */
	public int getServerPort ();

	/**
	 * @param port Port TCP/IP port number that the server is listening on
	 */
	public void setServerPort (final int port);
	
	/**
	 * @return Which thread messages arriving from the server will be processed on
	 */
	public boolean isProcessMessagesOnSwingEventDispatcherThread ();

	/**
	 * @param swingEDT Which thread messages arriving from the server will be processed on
	 */
	public void setProcessMessagesOnSwingEventDispatcherThread (final boolean swingEDT);
}