package com.ndg.multiplayer.base.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.MultiplayerReceiveMessagesThread;
import com.ndg.multiplayer.base.ServerToClientMessage;

/**
 * Thread to handle requests from one client
 */
public class MultiplayerBaseServerThread extends MultiplayerReceiveMessagesThread implements ServerToClientConnection
{
	/** Server this connection belongs to */
	private MultiplayerBaseServer server;

	/**
	 * Creates a new thread for handling requests from this client
	 * @param threadName Name for this thread
	 */
	public MultiplayerBaseServerThread (final String threadName)
	{
		super (threadName);
	}

	/**
	 * Processes received XML message
	 * @param msg Message received from client that needs to be processed
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws JAXBException If there is a problem converting the object into XML
	 */
	@Override
	protected final void processMessage (final Object msg)
		throws IOException, JAXBException, XMLStreamException
	{
		if (msg instanceof ClientToServerMessage)
			processMessageFromClient ((ClientToServerMessage) msg);
		else
			throw new IOException ("MultiplayerBaseServerThread received message of type " + msg.getClass ().getName () + " which is not a descendant of ClientToServerMessage");
	}

	/**
	 * Processes XML message received from client - descendant server types must implement this to provide useful message processing
	 * @param msg Message received from client that needs to be processed
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws JAXBException If there is a problem converting the object into XML
	 */
	protected void processMessageFromClient (final ClientToServerMessage msg)
		throws IOException, JAXBException, XMLStreamException
	{
		if (msg instanceof BaseClientToServerMessage)
			((BaseClientToServerMessage) msg).process (this);
			
		else
			throw new IOException ("MultiplayerBaseServerThread doesn't know how to process message of type " + msg.getClass ().getName ());
	}

	/**
	 * Sends XML message to the client
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	@Override
	public final void sendMessageToClient (final ServerToClientMessage msg) throws JAXBException, XMLStreamException
	{
		sendMessage (msg);
	}

	/**
	 * Remove this connection from server's list of connections
	 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
	 */
	@Override
	protected void disconnected (final Exception e)
	{
		super.disconnected (e);

		synchronized (getServer ().getClientConnections ())
		{
			getServer ().getClientConnections ().remove (this);
		}
	}

	/**
	 * @return Server this connection belongs to
	 */
	public final MultiplayerBaseServer getServer ()
	{
		return server;
	}

	/**
	 * @param svr Server this connection belongs to
	 */
	public final void setServer (final MultiplayerBaseServer svr)
	{
		server = svr;
	}
}