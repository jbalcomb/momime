package com.ndg.multiplayer.base.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.MultiplayerReceiveMessagesThread;
import com.ndg.multiplayer.base.ServerToClientMessage;

/**
 * Thread to handle requests from the server
 */
public class MultiplayerBaseClientThread extends MultiplayerReceiveMessagesThread implements ClientToServerConnection
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerBaseClientThread.class);
	
	/** Client this connection belongs to */
	private MultiplayerBaseClient client;
	
	/** Messages held up waiting to be processed - must synchronize all access to this */
	private List<BaseServerToClientMessage> queuedMessages = new ArrayList<BaseServerToClientMessage> ();
	
	/** Tick number of the animation at the head of the queue */
	private int tickNumber;
	
	/** Swing timer used for animation at the head of the queue */
	private Timer swingTimer;
	
	/**
	 * Creates a new thread for handling requests from the server
	 * @param threadName Name for this thread
	 */
	public MultiplayerBaseClientThread (final String threadName)
	{
		super (threadName);
	}

	/**
	 * Processes received XML message
	 * @param msg Message received from serverthat needs to be processed
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws JAXBException If there is a problem converting the object into XML
	 */
	@Override
	protected final void processMessage (final Object msg)
		throws IOException, JAXBException, XMLStreamException
	{
		if (!(msg instanceof ServerToClientMessage))
			throw new IOException ("MultiplayerBaseClientThread received message of type " + msg + " which is not a descendant of ServerToClientMessage");
		
		if (!(msg instanceof BaseServerToClientMessage))
			throw new IOException ("MultiplayerBaseClientThread received message of type " + msg + " which does not implement the BaseServerToClientMessage interface");

		// Queue the message
		synchronized (queuedMessages)
		{
			queuedMessages.add ((BaseServerToClientMessage) msg);
		
			// If this is the only message on the queue then process it
			if (queuedMessages.size () == 1)
			{			
				if (getClient ().isProcessMessagesOnSwingEventDispatcherThread ())
				{
					// Process in Swing thread
					SwingUtilities.invokeLater (new Runnable ()
					{
						@Override
						public final void run ()
						{
							try
							{
								synchronized (queuedMessages)
								{
									startFirstMessageOnQueue ();
								}
							}
							catch (final XMLStreamException e)
							{
								// 	XML stream was corrupt or unreadable
								log.warn ("Invalid data sent, in Swing thread", e);
								disconnected (e);
							}
							catch (final JAXBException e)
							{
								// Encountered an object that we didn't know how to deal with
								log.warn ("Unkown message sent, in Swing thread", e);
								disconnected (e);
							}
							catch (final IOException e)
							{
								// Uncontrolled disconnection
								log.warn ("Disconnected abnormally, in Swing thread", e);
								disconnected (e);
							}
						}
					});
				}
				else
					// Process on this thread
					startFirstMessageOnQueue ();
			}
		}			
	}

	/**
	 * Retrieves the first message off the queue and starts it.  The caller must take care of
	 * a) Running this method on the correct thread, according to the processMessagesOnSwingEventDispatcherThread setting; and
	 * b) synchronizing access to queuedMessages
	 * 
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 */
	private final void startFirstMessageOnQueue () throws IOException, JAXBException, XMLStreamException
	{
		if (queuedMessages.size () > 0)
		{
			final BaseServerToClientMessage msg = queuedMessages.get (0);
			log.debug ("startFirstMessageOnQueue starting " + msg);
			msg.start ();
			
			// Is it an animation?
			final AnimatedServerToClientMessage anim = (msg instanceof AnimatedServerToClientMessage) ? (AnimatedServerToClientMessage) msg : null;

			// Did it finish instantly?  Custom duration messages cannot finish instantly, but base messages do by definition
			// and animations can finish instantly if their duration is set to 0 and isFinishAfterDuration is true.
			final boolean finished;
			if (anim != null)
				finished = (anim.getDuration () == 0) && (anim.isFinishAfterDuration ());
			else
				finished = !(msg instanceof CustomDurationServerToClientMessage);
			
			if (finished)
			{
				if (anim != null)
					anim.finish ();
				
				// Remove it from the queue
				queuedMessages.remove (0);
				
				// Process the next one
				startFirstMessageOnQueue ();
			}

			// Do we need to start a timer?
			else if ((anim != null) && (anim.getTickCount () > 0) && (anim.getDuration () > 0))
			{
				tickNumber = 0;
				
				// I think should create a javax.swing.Timer if processMessagesOnSwingEventDispatcherThread is true and
				// a java.util.Timer if processMessagesOnSwingEventDispatcherThread is false, but since every app I'm
				// using this for at the moment is a Swing app, am not bothering to implement that yet
				swingTimer = new Timer ((int) (1000.0 * anim.getDuration () / anim.getTickCount ()), (ev) ->
				{
					tickNumber++;
					anim.tick (tickNumber);
					if (tickNumber >= anim.getTickCount ())
					{
						swingTimer.stop ();
						swingTimer = null;
						
						// The timer is finished - whether the anim now finishes depends on its settings
						if (anim.isFinishAfterDuration ())
							try
							{
								anim.finish ();
								queuedMessages.remove (0);

								synchronized (queuedMessages)
								{
									startFirstMessageOnQueue ();
								}
							}
							catch (final Exception e)
							{
								log.error (e, e);
							}
					}
				});
				swingTimer.start ();
			}
		}
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
		if ((getClient ().isProcessMessagesOnSwingEventDispatcherThread ()) && (!SwingUtilities.isEventDispatchThread ()))
			throw new IOException ("finishCustomDurationMessage tried to finish message " + msg +
				" but processMessagesOnSwingEventDispatcherThread=true and it was not called from the Swing event dispatcher thread");
		
		synchronized (queuedMessages)
		{
			if (queuedMessages.size () == 0)
				throw new IOException ("finishCustomDurationMessage tried to finish message " + msg + " but there are no messages on the queue");
			
			if (queuedMessages.get (0) != msg)
				throw new IOException ("finishCustomDurationMessage tried to finish message " + msg + " but it isn't the message at the head of the queue: " + queuedMessages.get (0));
			
			// Is it an animation?
			final AnimatedServerToClientMessage anim = (msg instanceof AnimatedServerToClientMessage) ? (AnimatedServerToClientMessage) msg : null;
			
			// If so, were we allowed to finish it?
			if ((anim != null) && (anim.isFinishAfterDuration ()))
				throw new IOException ("finishCustomDurationMessage tried to finish message " + msg + " but it is an animation with isFinishAfterDuration = true");
			
			if ((anim != null) && (anim.getTickCount () > 0) && (anim.getDuration () > 0) && (tickNumber < anim.getTickCount ()))
				throw new IOException ("finishCustomDurationMessage tried to finish message " + msg + " but it is an animation and its only on frame " + tickNumber + " / " + anim.getTickCount ());
			
			// Ok, all checks passed
			msg.finish ();
			queuedMessages.remove (0);
			startFirstMessageOnQueue ();
		}
	}

	/**
	 * Sends XML message to the server
	 * @param msg Message to send
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	@Override
	public final void sendMessageToServer (final ClientToServerMessage msg) throws JAXBException, XMLStreamException
	{
		sendMessage (msg);
	}

	/**
	 * @return Client this connection belongs to
	 */
	public MultiplayerBaseClient getClient ()
	{
		return client;
	}

	/**
	 * @param obj Client this connection belongs to
	 */
	public void setClient (final MultiplayerBaseClient obj)
	{
		client = obj;
	}
}