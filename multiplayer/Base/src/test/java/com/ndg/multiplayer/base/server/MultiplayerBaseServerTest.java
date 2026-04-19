package com.ndg.multiplayer.base.server;

import java.net.Socket;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.ServerToClientMessage;
import com.ndg.multiplayer.base.client.MultiplayerBaseClientImpl;
import com.ndg.multiplayer.base.client.MultiplayerBaseClientThread;
import com.ndg.multiplayer.baseunittest.Request;
import com.ndg.multiplayer.baseunittest.Response;
import com.ndg.multiplayer.baseunittest.extension.ObjectFactoryEx;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

/**
 * Tests the example server, with the client sending messages to the server on one thread and listening for replies on another
 */
@ExtendWith(MockitoExtension.class)
public final class MultiplayerBaseServerTest
{
	/** TCP/IP port the test server should listen on */
	public final static int SERVER_PORT = 9000;

	/** TCP/IP port the test client should connect to (can make it different to go through a proxy so can see the packets being sent) */
	public final static int CLIENT_PORT = SERVER_PORT;

	/**
	 * Tests the example server, with the client sending messages to the server on one thread and listening for replies on another
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void multiplayerBaseServerTest () throws Exception
	{
		// Start up server
		System.out.println ("Starting up server");
		final Object readyForConnectionsMonitor = new Object ();

		final MultiplayerBaseServer server = new MultiplayerBaseServer ("ServerThread");
		server.setPort (SERVER_PORT);
		server.setClientToServerContext (JAXBContext.newInstance (ClientToServerMessage.class, Request.class));
		server.setServerToClientContext (JAXBContext.newInstance (ServerToClientMessage.class, Response.class));
		server.setClientToServerContextFactoryArray (new Object [] {new ObjectFactoryEx ()});
		server.setCloseAfterFirstConnection (true);
		server.setReadyForConnectionsMonitor (readyForConnectionsMonitor);
		server.start ();
		System.out.println ("Waiting for server to be ready");
		synchronized (readyForConnectionsMonitor)
		{
			readyForConnectionsMonitor.wait ();
		}

		// Start up client - the separate thread does all the monitoring and processing of messages the server sends
		System.out.println ("Starting up client");
		final TestClient client = new TestClient ();
		client.setServerAddress ("localhost");
		client.setServerPort (CLIENT_PORT);
		client.setClientToServerContext (JAXBContext.newInstance (ClientToServerMessage.class, Request.class));
		client.setServerToClientContext (JAXBContext.newInstance (ServerToClientMessage.class, Response.class));
		client.setServerToClientContextFactoryArray (new Object [] {new ObjectFactoryEx ()});
		client.connect ();
		
		System.out.println ("Waiting for client to be ready");
	}
	
	/**
	 * Client thread which runs remainder of the test after the connection has started up
	 */
	public final class TestClientThread extends MultiplayerBaseClientThread
	{
		/**
		 * Creates a new thread for handling requests from the server
		 * @param threadName Name for this thread
		 */
		public TestClientThread (final String threadName)
		{
			super (threadName);
		}

		/**
		 * Sends some test packets
		 * 
		 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
		 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
		 * @throws InterruptedException If there is a thread synchronization issue
		 */
		@Override
		protected final void streamsInitialized ()
			throws JAXBException, XMLStreamException, InterruptedException
		{
			// Send a few client > server messages
			for (int n = 1; n <= 5; n++)
			{
				Thread.sleep (2000);
				System.out.println ("Sending request " + n);

				final Request req = new Request ();
				req.setCount (n);
				getClient ().getServerConnection ().sendMessageToServer (req);
			}

			// Disconnect client
			System.out.println ("Disconnecting client");
			final Object closeMonitor = new Object ();
			getClient ().getServerConnection ().close (closeMonitor);

			System.out.println ("Waiting for client to close");
			synchronized (closeMonitor)
			{
				closeMonitor.wait ();
			}
			System.out.println ("Client closed, ending");
		}
	}
	
	/**
	 * Client which creates test threads like above
	 */
	public final class TestClient extends MultiplayerBaseClientImpl
	{
		/**
		 * Descendant server classes may want to override this to create different thread classes
		 * @param socket Socket on which connected to the server
		 * @return Thread object to handle requests from the server
		 * @throws InterruptedException If there is a problem waiting for the thread to start up
		 * @throws JAXBException If there is a problem sending something to connecting client
		 * @throws XMLStreamException If there is a problem sending something to connecting client
		 */
		@Override
		protected MultiplayerBaseClientThread createAndStartServerThread (final Socket socket) throws InterruptedException, JAXBException, XMLStreamException
		{
			final TestClientThread thread = new TestClientThread ("TestClientThread-" + socket);
			thread.setSocket (socket);
			thread.setClient (this);
			thread.setSendContext (getClientToServerContext ());
			thread.setReceiveContext (getServerToClientContext ());
			thread.setReceiveObjectFactoryArray (getServerToClientContextFactoryArray ());
			thread.setConversationTag (getConversationTag ());
			thread.setMarshallLogWriter (getMarshallLogWriter ());
			thread.setUnmarshallLogWriter (getUnmarshallLogWriter ());
			thread.setCompress (isCompress ());
			thread.setDecompress (isDecompress ());
			thread.start ();

			return thread;
		}
	}
}