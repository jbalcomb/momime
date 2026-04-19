package com.ndg.multiplayer.basedemo.server;

import java.net.Socket;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.MultiplayerBaseServer;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;

/**
 * Demonstrates subclassing the server so that we can create subclasses of client connection threads
 */
public final class MultiplayerDemoServer extends MultiplayerBaseServer
{
	/**
	 * Gives a name to the server thread
	 */
	public MultiplayerDemoServer ()
	{
		super ("AcceptConnections");
	}
	
	/**
	 * Descendant server classes may want to override this to create different thread classes
	 * @param socket Socket on which a new client has connected
	 * @return Thread object to handle requests from this client
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	@Override
	protected final MultiplayerBaseServerThread createAndStartClientThread (final Socket socket) throws InterruptedException, JAXBException, XMLStreamException
	{
		final MultiplayerDemoServerThread thread = new MultiplayerDemoServerThread ("ClientSocket-" + socket);
		thread.setServer (this);
		thread.setSocket (socket);
		thread.setSendContext (getServerToClientContext ());
		thread.setReceiveContext (getClientToServerContext ());
		thread.setReceiveObjectFactoryArray (getClientToServerContextFactoryArray ());
		thread.setConversationTag (getConversationTag ());
		thread.setCompress (isCompress ());
		thread.setDecompress (isDecompress ());
		thread.start ();
		return thread;
	}
}