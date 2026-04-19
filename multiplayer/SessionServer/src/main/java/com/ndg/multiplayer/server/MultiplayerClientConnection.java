package com.ndg.multiplayer.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.server.session.PostSessionClientToServerMessage;

/**
 * Extends base client connection thread to be able to record the player/session we are currently joined into
 */
public class MultiplayerClientConnection extends MultiplayerBaseServerThread implements ServerToClientSessionConnection
{
	/** Indicates player ID this client has logged on with, 0 if not yet logged in */
	private int playerID;

	/** Indicates session this client is joined into, null if not joined into a session yet */
	private MultiplayerSessionThread session;

	/** Indicates player details this client is using in this session, null if not joined into a session yet */
	private PlayerServerDetails playerServerDetails;

	/**
	 * Creates a new thread for handling requests from this client
	 * @param threadName Name for this thread
	 */
	public MultiplayerClientConnection (final String threadName)
	{
		super (threadName);
	}

	/**
	 * Processes XML message received from client - descendant server types must implement this to provide useful message processing
	 * @param msg Message received from client that needs to be processed
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws JAXBException If there is a problem converting the object into XML
	 */
	@Override
	protected final void processMessageFromClient (final ClientToServerMessage msg)
		throws IOException, JAXBException, XMLStreamException
	{
		// If we're already in a session, or this is a request to join a session, then we have to
		// pass the message to the session to process in its own thread
		// Other types of messages we can process immediately here
		if (session != null)
			session.addMessageToQueue (msg, this);

		// Multiplayer module messages
		else if (msg instanceof PreSessionClientToServerMessage)
			((PreSessionClientToServerMessage) msg).process (getSessionServer (), this, null);
		
		else if (msg instanceof PostSessionClientToServerMessage)
			throw new IOException ("MultiplayerClientConnection received a message " + msg + " which can only be processed after the player joins a session, but they haven't");
		
		else
			throw new IOException ("MultiplayerClientConnection doesn't know how to process message " + msg);
	}

	/**
	 * If player is in a session, remove them from it when they disconnect
	 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
	 */
	@Override
	protected void disconnected (final Exception e)
	{
		super.disconnected (e);

		if (playerServerDetails != null)
			playerServerDetails.setConnection (null);
	}
	
	/**
	 * @return Server this connection belongs to
	 */
	protected final MultiplayerSessionServer getSessionServer ()
	{
		return (MultiplayerSessionServer) super.getServer ();
	}

	/**
	 * @return Indicates session this client is joined into, null if not joined into a session yet
	 */
	public final MultiplayerSessionThread getSession ()
	{
		return session;
	}

	/**
	 * @param aSession Session this player is now joined into, null if they are leaving a session
	 */
	public final void setSession (final MultiplayerSessionThread aSession)
	{
		session = aSession;
	}

	/**
	 * @return Indicates player details this client is using in this session, null if not joined into a session yet
	 */
	@Override
	public final PlayerServerDetails getPlayer ()
	{
		return playerServerDetails;
	}

	/**
	 * @param psd Indicates player details this client is using in this session, null if not joined into a session yet
	 */
	@Override
	public final void setPlayer (final PlayerServerDetails psd)
	{
		playerServerDetails = psd;
	}

	/**
	 * @return Indicates player ID this client has logged on with, 0 if not yet logged in
	 */
	public final int getPlayerID ()
	{
		return playerID;
	}

	/**
	 * @param id Indicates player ID this client has logged on with, 0 if not yet logged in
	 */
	public final void setPlayerID (final int id)
	{
		playerID = id;
	}
}