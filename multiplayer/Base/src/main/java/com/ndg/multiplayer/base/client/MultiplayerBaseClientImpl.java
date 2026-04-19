package com.ndg.multiplayer.base.client;

import java.io.IOException;
import java.io.Writer;
import java.net.Socket;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.ServerToClientMessage;

/**
 * Main client class to initiate a connection to a server.
 * A separate thread is kicked off to listen for messages from the server when we make a connection, but state should be
 * held here so that if the connection thread dies, we don't lose the info.
 */
public class MultiplayerBaseClientImpl implements MultiplayerBaseClient
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerBaseClient.class); 

	/** Connection to server */
	private ClientToServerConnection serverConnection;
	
	/** IP or DNS name of the server */
	private String serverAddress;
	
	/** Port TCP/IP port number that the server is listening on */
	private int serverPort;

	/** JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this client must understand how to marshall */
	private JAXBContext clientToServerContext;

	/** Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required */
	private Object [] serverToClientContextFactoryArray;

	/** JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this client must understand how to unmarshall */
	private JAXBContext serverToClientContext;

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
	 * Which thread messages arriving from the server will be processed on:
	 * False = messages are processed on the server connection thread;
	 * True = messages are processed on the Swing event dispatcher thread.
	 * See more comments in {@link BaseServerToClientMessage} regarding this.
	 */
	private boolean processMessagesOnSwingEventDispatcherThread;

	/**
	 * Initiate connection to the server
	 * @throws IOException If there is a problem making the connection
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	@Override
	public final void connect () throws IOException, InterruptedException, JAXBException, XMLStreamException
	{
		log.info ("Connecting to server \"" + getServerAddress () + "\" port " + getServerPort () + "...");
		
		@SuppressWarnings ("resource")
		final Socket socket = new Socket (getServerAddress (), getServerPort ());

		log.info ("Socket connected, starting up thread to listen for messages from server");
		serverConnection = createAndStartServerThread (socket);
	}
	
	/**
	 * Descendant server classes may want to override this to create different thread classes
	 * @param socket Socket on which connected to the server
	 * @return Thread object to handle requests from the server
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	protected MultiplayerBaseClientThread createAndStartServerThread (final Socket socket) throws InterruptedException, JAXBException, XMLStreamException
	{
		final MultiplayerBaseClientThread thread = new MultiplayerBaseClientThread ("MultiplayerBaseClientThread-" + socket);
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
	
	/**
	 * Finishes a custom duration message, or an animated message with isFinishAfterDuration = false
	 * 
	 * @param msg Message to finish
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 */
	@Override
	public final void finishCustomDurationMessage (final CustomDurationServerToClientMessage msg) throws IOException, JAXBException, XMLStreamException
	{
		// Delegate to method on the connection thread
		if (getServerConnection () == null)
			throw new IOException ("finishCustomDurationMessage tried to finish message " + msg + " but there is no serverConnection");
		
		getServerConnection ().finishCustomDurationMessage (msg);
	}

	/**
	 * @return Connection to server
	 */
	@Override
	public final ClientToServerConnection getServerConnection ()
	{
		return serverConnection;
	}
	
	/**
	 * @return IP or DNS name of the server
	 */
	@Override
	public final String getServerAddress ()
	{
		return serverAddress;
	}
	
	/**
	 * @param addr IP or DNS name of the server
	 */
	@Override
	public final void setServerAddress (final String addr)
	{
		serverAddress = addr;
	}
	
	/**
	 * @return Port TCP/IP port number that the server is listening on
	 */
	@Override
	public final int getServerPort ()
	{
		return serverPort;
	}

	/**
	 * @param port Port TCP/IP port number that the server is listening on
	 */
	@Override
	public final void setServerPort (final int port)
	{
		serverPort = port;
	}

	/**
	 * @return JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this client must understand how to marshall
	 */
	public final JAXBContext getClientToServerContext ()
	{
		return clientToServerContext;
	}

	/**
	 * @param ctx JAXB Context that knows about all descendants of {@link ClientToServerMessage} that this client must understand how to marshall
	 */
	public final void setClientToServerContext (final JAXBContext ctx)
	{
		clientToServerContext = ctx;
	}

	/**
	 * @return Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final Object [] getServerToClientContextFactoryArray ()
	{
		return serverToClientContextFactoryArray;
	}

	/**
	 * @param facs Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final void setServerToClientContextFactoryArray (final Object [] facs)
	{
		serverToClientContextFactoryArray = facs;
	}

	/**
	 * @return JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this client must understand how to unmarshall
	 */
	public final JAXBContext getServerToClientContext ()
	{
		return serverToClientContext;
	}

	/**
	 * @param ctx JAXB Context that knows about all descendants of {@link ServerToClientMessage} that this client must understand how to unmarshall
	 */
	public final void setServerToClientContext (final JAXBContext ctx)
	{
		serverToClientContext = ctx;
	}

	/**
	 * @return Which thread messages arriving from the server will be processed on
	 */
	@Override
	public final boolean isProcessMessagesOnSwingEventDispatcherThread ()
	{
		return processMessagesOnSwingEventDispatcherThread;
	}

	/**
	 * @param swingEDT Which thread messages arriving from the server will be processed on
	 */
	@Override
	public final void setProcessMessagesOnSwingEventDispatcherThread (final boolean swingEDT)
	{
		processMessagesOnSwingEventDispatcherThread = swingEDT;
	}

	/**
	 * @return Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null
	 */
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