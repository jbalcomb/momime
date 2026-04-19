package com.ndg.multiplayer.base.server;

import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.ServerToClientMessage;

/**
 * Main server class to listen for client connection requests.  For each connection request a new {@link MultiplayerBaseServerThread} is kicked off to deal
 * with communications to that client.  It receives XML messages on the connection and calls a method to process the message, and/or sends XML
 * messages on the connection to the client.
 *
 * At the base level, the clients are independant from each other and unaware of each other.  Each client runs in its own thread, so messages from
 * multiple clients can be being received and processed concurrently.
 */
@ManagedResource
public class MultiplayerBaseServer extends Thread
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerBaseServer.class); 

	/** List of client connections */
	private final List<MultiplayerBaseServerThread> clientConnections;
	
	/** Port TCP/IP port number that the server should listen on */
	private int port;

	/** JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this server must understand how to unmarshall */
	private JAXBContext clientToServerContext;

	/** Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required */
	private Object [] clientToServerContextFactoryArray;

	/** JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this server must understand how to marshall */
	private JAXBContext serverToClientContext;

	/** Thread lock object to notify once server has started successfully; null if no start notification is requested */
	private Object readyForConnectionsMonitor;

	/** If a unit test, we probably want to close the server down as soon as the test has executed, so set this to true */
	private boolean closeAfterFirstConnection;

	/** Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null */
	private String conversationTag;
	
	/** Writer to write debug info out to about the output stream */
	private Writer marshallLogWriter;

	/** Writer to write debug info out to about the input stream */
	private Writer unmarshallLogWriter;
	
	/** Whether to compress outgoing data */
	private boolean compress;
	
	/** Whether to decompress incoming data */
	private boolean decompress;
	
	/**
	 * @param threadName Name to give to the server thread
	 */
	public MultiplayerBaseServer (final String threadName)
	{
		super (threadName);
		
		clientConnections = new ArrayList<MultiplayerBaseServerThread> ();
	}

	/**
	 * Kicks off running the server
	 */
	@Override
	public final void run ()
	{
		// Create socket
		log.info ("Opening port " + port);
		try
		{
			try (final ServerSocket serverSocket = new ServerSocket (port))
			{
				log.info ("Listening for client connection requests on port " + port);

				if (readyForConnectionsMonitor != null)
				{
					log.debug ("Notifying readyForConnectionsMonitor of successful startup");
					synchronized (readyForConnectionsMonitor)
					{
						readyForConnectionsMonitor.notifyAll ();
					}
				}

				boolean listenForFurtherConnections = true;
				while (listenForFurtherConnections)
				{
					// Accept blocks until the next client attempts to connect
					log.debug ("Waiting for next client connection");
					try
					{
						final MultiplayerBaseServerThread newClientConnection = createAndStartClientThread (serverSocket.accept ());

						synchronized (getClientConnections ())
						{
							getClientConnections ().add (newClientConnection);
						}

						// If a unit test, we probably want to close the server down as soon as the test has executed
						if (closeAfterFirstConnection)
							listenForFurtherConnections = false;
					}
					catch (final InterruptedException e)
					{
						log.warn ("InterruptedException while trying to accept new client connection: " + e);
					}
					catch (final JAXBException e)
					{
						log.warn ("JAXBException while trying to accept new client connection: " + e);
					}
					catch (final XMLStreamException e)
					{
						log.warn ("XMLStreamException while trying to accept new client connection: " + e);
					}
				}

				serverSocket.close ();
			}
		}
		catch (final IOException e)
		{
			// An IO Exception out here will totally kill the server
			log.fatal ("Server ending for an IOException while listening for new client connection requests: " + e);
		}
	}

	/**
	 * Descendant server classes may want to override this to create different thread classes
	 * @param socket Socket on which a new client has connected
	 * @return Thread object to handle requests from this client
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	protected MultiplayerBaseServerThread createAndStartClientThread (final Socket socket) throws InterruptedException, JAXBException, XMLStreamException
	{
		final MultiplayerBaseServerThread thread = new MultiplayerBaseServerThread ("ClientSocket-" + socket);
		thread.setServer (this);
		thread.setSocket (socket);
		thread.setSendContext (getServerToClientContext ());
		thread.setReceiveContext (getClientToServerContext ());
		thread.setReceiveObjectFactoryArray (getClientToServerContextFactoryArray ());
		thread.setConversationTag (getConversationTag ());
		thread.setMarshallLogWriter (getMarshallLogWriter ());
		thread.setUnmarshallLogWriter (getUnmarshallLogWriter ());
		thread.setCompress (isCompress ());
		thread.setDecompress (isDecompress ());
		thread.start ();
		return thread;
	}

	/**
	 * @return List of client connections
	 */
	public final List<MultiplayerBaseServerThread> getClientConnections ()
	{
		return clientConnections;
	}
	
	/**
	 * @return Number of client connections
	 */
	@ManagedAttribute
	public final int getClientConnectionCount ()
	{
		synchronized (getClientConnections ())
		{
			return getClientConnections ().size ();
		}
	}
	
	/**
	 * @return Port TCP/IP port number that the server should listen on
	 */
	@ManagedAttribute
	public final int getPort ()
	{
		return port;
	}

	/**
	 * @param value Port TCP/IP port number that the server should listen on
	 */
	public final void setPort (final int value)
	{
		port = value;
	}
	
	/**
	 * @return JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this server must understand how to unmarshall
	 */
	public final JAXBContext getClientToServerContext ()
	{
		return clientToServerContext;
	}

	/**
	 * @param ctx JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this server must understand how to unmarshall
	 */
	public final void setClientToServerContext (final JAXBContext ctx)
	{
		clientToServerContext = ctx;
	}
	
	/**
	 * @return Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final Object [] getClientToServerContextFactoryArray ()
	{
		return clientToServerContextFactoryArray;
	}

	/**
	 * @param facs Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final void setClientToServerContextFactoryArray (final Object [] facs)
	{
		clientToServerContextFactoryArray = facs;
	}
	
	/**
	 * @return JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this server must understand how to marshall
	 */
	public final JAXBContext getServerToClientContext ()
	{
		return serverToClientContext;
	}

	/**
	 * @param ctx JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this server must understand how to marshall
	 */
	public final void setServerToClientContext (final JAXBContext ctx)
	{
		serverToClientContext = ctx;
	}
	
	/**
	 * @return Thread lock object to notify once server has started successfully; null if no start notification is requested
	 */
	public final Object getReadyForConnectionsMonitor ()
	{
		return readyForConnectionsMonitor;
	}

	/**
	 * @param monitor Thread lock object to notify once server has started successfully; null if no start notification is requested
	 */
	public final void setReadyForConnectionsMonitor (final Object monitor)
	{
		readyForConnectionsMonitor = monitor;
	}

	/**
	 * @return If a unit test, we probably want to close the server down as soon as the test has executed, so set this to true
	 */
	public final boolean isCloseAfterFirstConnection ()
	{
		return closeAfterFirstConnection;
	}
	
	/**
	 * @param closeAfterFirst If a unit test, we probably want to close the server down as soon as the test has executed, so set this to true
	 */
	public final void setCloseAfterFirstConnection (final boolean closeAfterFirst)
	{
		closeAfterFirstConnection = closeAfterFirst;
	}

	/**
	 * @return Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null
	 */
	@ManagedAttribute
	public final String getConversationTag ()
	{
		return conversationTag;
	}

	/**
	 * @param tag Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null
	 */
	public final void setConversationTag (final String tag)
	{
		conversationTag = tag;
	}

	/**
	 * @return Writer to write debug info out to about the output stream
	 */
	public final Writer getMarshallLogWriter ()
	{
		return marshallLogWriter;
	}

	/**
	 * @param w Writer to write debug info out to about the output stream
	 */
	public final void setMarshallLogWriter (final Writer w)
	{
		marshallLogWriter = w;
	}

	/**
	 * @return Writer to write debug info out to about the input stream
	 */
	public final Writer getUnmarshallLogWriter ()
	{
		return unmarshallLogWriter;
	}
	
	/**
	 * @param w Writer to write debug info out to about the input stream
	 */
	public final void setUnmarshallLogWriter (final Writer w)
	{
		unmarshallLogWriter = w;
	}

	/**
	 * @return Whether to compress outgoing data
	 */
	@ManagedAttribute
	public final boolean isCompress ()
	{
		return compress;
	}

	/**
	 * @param c Whether to compress outgoing data
	 */
	public final void setCompress (final boolean c)
	{
		compress = c;
	}
	
	/**
	 * @return Whether to decompress incoming data
	 */
	@ManagedAttribute
	public final boolean isDecompress ()
	{
		return decompress;
	}
	
	/**
	 * @param d Whether to decompress incoming data
	 */
	public final void setDecompress (final boolean d)
	{
		decompress = d;
	}
}