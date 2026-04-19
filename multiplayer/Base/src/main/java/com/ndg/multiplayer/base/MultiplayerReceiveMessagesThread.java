package com.ndg.multiplayer.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketAddress;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thread which reads an XML stream down a TCP/IP socket, unmarshalls messages and processes them
 * This is a superclass, there are server and client specific descendants
 */
public abstract class MultiplayerReceiveMessagesThread extends Thread
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerReceiveMessagesThread.class);

	/** Socket used to communicate with the client */
	private Socket socket;

	/** JAXB Context that knows about types of messages that we expect to receive */
	private JAXBContext receiveContext;

	/** Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required */
	private Object [] receiveObjectFactoryArray;

	/** JAXB Context that knows about all types of messages that we expect to send */
	private JAXBContext sendContext;

	/** Message stream used to send messages */
	private MultipleMarshallStream out;

	/** This gets set to true when either the close () method is called from our end, or the other end closes the stream with a </conversation> tag */
	private boolean endOfStream;

	/** Thread lock object to notify once socket has been closed; null if no closing notification is requested */
	private Object closeMonitor;

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
	 * Creates a new thread for listening for messages
	 * @param threadName Name to give to the thread
	 */
	public MultiplayerReceiveMessagesThread (final String threadName)
	{
		super (threadName);
	}

	/**
	 * Handles all requests from one client
	 */
	@Override
	public final void run ()
	{
		try
		{
			try (final InputStream socketIn = getSocket ().getInputStream ())
			{
				try (final OutputStream socketOut = getSocket ().getOutputStream ())
				{
					try
					{
						// Create multiple XML message streams in both directions
						log.debug ("Initializing XML output stream");
						out = new MultipleMarshallStream ();
						out.setJaxbContext (getSendContext ());
						out.setStream (socketOut);
						out.setConversationTag (getConversationTag ());
						out.setLogWriter (getMarshallLogWriter ());
						out.setCompress (isCompress ());
						out.init ();

						log.debug ("Initializing XML input stream");
						final MultipleUnmarshallStream in = new MultipleUnmarshallStream ();
						in.setJaxbContext (getReceiveContext ());
						in.setObjectFactoryArray (getReceiveObjectFactoryArray ());
						in.setStream (socketIn);
						in.setConversationTag (getConversationTag ());
						in.setLogWriter (getUnmarshallLogWriter ());
						in.setDecompress (isDecompress ());
						in.init ();

						streamsInitialized ();

						// Sit in a loop processing message objects from the input stream
						log.debug ("Starting loop to listen for messages");
						while (!endOfStream)
						{
							// Read the next message
							final Object msg = in.readObject ();
							if (msg == null)
							{
								log.info ("Recevied closing conversation tag from " + getSocket ().getRemoteSocketAddress ());
								endOfStream = true;
								disconnected (null);
							}
							else
								processMessage (msg);
						}
					}
					catch (final XMLStreamException e)
					{
						// 	XML stream was corrupt or unreadable
						log.warn ("Invalid data sent from " + getSocket ().getRemoteSocketAddress (), e);
						disconnected (e);
					}
					catch (final JAXBException e)
					{
						// Encountered an object that we didn't know how to deal with
						log.warn ("Unkown message sent from " + getSocket ().getRemoteSocketAddress (), e);
						disconnected (e);
					}
					catch (final IOException e)
					{
						// Uncontrolled disconnection
						log.warn ("Disconnected abnormally (inner) from " + getSocket ().getRemoteSocketAddress (), e);
						disconnected (e);
					}
					catch (final InterruptedException e)
					{
						// Thread synchronization issue
						log.warn ("Thread synchronization issue from " + getSocket ().getRemoteSocketAddress (), e);
						disconnected (e);
					}
				}
			}
		}
		catch (final IOException e)
		{
			// Uncontrolled disconnection
			log.warn ("Disconnected abnormally (outer) from " + getSocket ().getRemoteSocketAddress (), e);
			disconnected (e);
		}

		if (closeMonitor != null)
		{
			log.debug ("Notifying closeMonitor of closed socket");
			synchronized (closeMonitor)
			{
				closeMonitor.notifyAll ();
			}
		}
	}
	
	/**
	 * Descendant classes can call this to do some processing when the connection has started up and the streams successfully initialized
	 * 
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws InterruptedException If there is a thread synchronization issue
	 */
	protected void streamsInitialized ()
		throws IOException, JAXBException, XMLStreamException, InterruptedException
	{
	}

	/**
	 * Processes received XML message, this is protected so that the server and client specific versions can enforce a stricter type for the message
	 * @param msg Message received from client that needs to be processed
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 */
	protected abstract void processMessage (final Object msg)
		throws IOException, JAXBException, XMLStreamException;

	/**
	 * Sends XML message, this is protected so that the server and client specific versions can enforce a stricter type for the message
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	protected final void sendMessage (final Object msg) throws JAXBException, XMLStreamException
	{
		out.writeObject (msg);
	}

	/**
	 * Closes the XML output stream and the socket
	 * Note the actual closing happens on the listener thread, so anything calling this route cannot assume that the socket
	 * will be closed by the time this method exits
	 * @param aCloseMonitor Thread lock object to notify once socket has been closed; null if no closing notification is requested
	 * @throws XMLStreamException If there is a problem sending the </conversation> tag
	 */
	public final void close (final Object aCloseMonitor) throws XMLStreamException
	{
		closeMonitor = aCloseMonitor;
		endOfStream = true;

		// Can't rely on the loop to detect endOfStream and close the socket for us, because it might be blocked on
		// the readObject () call and we might not get another object from the sending side
		out.close ();

		try
		{
			getSocket ().close ();
		}
		catch (final IOException e) {}
	}

	/**
	 * Empty method that descendant classes can override if they want to take some action when the connection drops abnormally
	 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
	 */
	@SuppressWarnings ("unused")
	protected void disconnected (final Exception e)
	{
	}

	/**
	 * @return Remove socket address this thread is connected to
	 */
	public final SocketAddress getRemoteSocketAddress ()
	{
		return getSocket ().getRemoteSocketAddress ();
	}

	/**
	 * @return Socket used to communicate with the client
	 */
	public final Socket getSocket ()
	{
		return socket;
	}

	/**
	 * @param obj Socket used to communicate with the client
	 */
	public final void setSocket (final Socket obj)
	{
		socket = obj;
	}

	/**
	 * @return JAXB Context that knows about types of messages that we expect to receive
	 */
	public final JAXBContext getReceiveContext ()
	{
		return receiveContext;
	}

	/**
	 * @param context JAXB Context that knows about types of messages that we expect to receive
	 */
	public final void setReceiveContext (final JAXBContext context)
	{
		receiveContext = context;
	}

	/**
	 * @return Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final Object [] getReceiveObjectFactoryArray ()
	{
		return receiveObjectFactoryArray;
	}

	/**
	 * @param facs Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave null if not required
	 */
	public final void setReceiveObjectFactoryArray (final Object [] facs)
	{
		receiveObjectFactoryArray = facs;
	}

	/**
	 * @return JAXB Context that knows about all types of messages that we expect to send
	 */
	public final JAXBContext getSendContext ()
	{
		return sendContext;
	}

	/**
	 * @param context JAXB Context that knows about all types of messages that we expect to send
	 */
	public final void setSendContext (final JAXBContext context)
	{
		sendContext = context;
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