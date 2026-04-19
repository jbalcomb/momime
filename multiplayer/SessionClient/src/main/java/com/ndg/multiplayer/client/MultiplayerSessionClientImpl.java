package com.ndg.multiplayer.client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.MultiplayerBaseClientImpl;
import com.ndg.multiplayer.base.client.MultiplayerBaseClientThread;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.GeneralPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.SessionDescription;
import com.ndg.multiplayer.sessionbase.TransientPlayerPrivateKnowledge;

import jakarta.xml.bind.JAXBException;

/**
 * Main client class to initiate a connection to a server.
 * A separate thread is kicked off to listen for messages from the server when we make a connection, but state should be
 * held here so that if the connection thread dies, we don't lose the info.
 */
public class MultiplayerSessionClientImpl extends MultiplayerBaseClientImpl implements MultiplayerSessionClient
{
	/** The player ID we're currently logged as; null if not currently logged in */
	private Integer ourPlayerID;
	
	/** Name that we logged in using */
	private String ourPlayerName;
	
	/** Details of the session we're currently in; null if not currently in a session*/
	private SessionDescription sessionDescription;
	
	/** All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session */
	private GeneralPublicKnowledge generalPublicKnowledge;

	/** Publicly available information about all players in our current session; this is how we access our own public details too, by searching for our player ID */
	private final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
	
	/** Private knowledge about our player that is persisted to save game files; we can't access this for any other players */
	private PersistentPlayerPrivateKnowledge ourPersistentPlayerPrivateKnowledge;

	/** Private knowledge about our player that is not persisted to save game files; we can't access this for any other players */
	private TransientPlayerPrivateKnowledge ourTransientPlayerPrivateKnowledge;

	/** Event listeners */
	private final List<MultiplayerSessionClientEvent> eventListeners = new ArrayList<MultiplayerSessionClientEvent> ();

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
		final MultiplayerServerConnection conn = new MultiplayerServerConnection ("ServerConnection-" + socket);
		conn.setSocket (socket);
		conn.setClient (this);
		conn.setSendContext (getClientToServerContext ());
		conn.setReceiveContext (getServerToClientContext ());
		conn.setReceiveObjectFactoryArray (getServerToClientContextFactoryArray ());
		conn.setConversationTag (getConversationTag ());
		conn.setMarshallLogWriter (getMarshallLogWriter ());
		conn.setUnmarshallLogWriter (getUnmarshallLogWriter ());
		conn.setCompress (isCompress ());
		conn.setDecompress (isDecompress ());
		conn.start ();
		return conn;
	}

	/**
	 * @return The player ID we're currently logged as; null if not currently logged in
	 */
	@Override
	public final Integer getOurPlayerID ()
	{
		return ourPlayerID;
	}
	
	/**
	 * @param id The player ID we're currently logged as; null if not currently logged in
	 */
	@Override
	public final void setOurPlayerID (final Integer id)
	{
		ourPlayerID = id;
	}
	
	/**
	 * @return Name that we logged in using
	 */
	@Override
	public final String getOurPlayerName ()
	{
		return ourPlayerName;
	}

	/**
	 * @param name Name that we logged in using
	 */
	@Override
	public final void setOurPlayerName (final String name)
	{
		ourPlayerName = name;
	}
	
	/**
	 * @return Details of the session we're currently in; null if not currently in a session
	 */
	@Override
	public SessionDescription getSessionDescription ()
	{
		return sessionDescription;
	}
	
	/**
	 * @param sd Details of the session we're currently in; null if not currently in a session
	 */
	@Override
	public final void setSessionDescription (final SessionDescription sd)
	{
		sessionDescription = sd;
	}
	
	/**
	 * @return All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	@Override
	public GeneralPublicKnowledge getGeneralPublicKnowledge ()
	{
		return generalPublicKnowledge;
	}

	/**
	 * @param gpk All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	@Override
	public final void setGeneralPublicKnowledge (final GeneralPublicKnowledge gpk)
	{
		generalPublicKnowledge = gpk;
	}

	/**
	 * @return Publicly available information about all players in our current session; this is how we access our own public details too, by searching for our player ID
	 */
	@Override
	public final List<PlayerPublicDetails> getPlayers ()
	{
		return players;
	}
	
	/**
	 * @return Private knowledge about our player that is persisted to save game files; we can't access this for any other players
	 */
	@Override
	public PersistentPlayerPrivateKnowledge getOurPersistentPlayerPrivateKnowledge ()
	{
		return ourPersistentPlayerPrivateKnowledge;
	}

	/**
	 * @param ppk Private knowledge about our player that is persisted to save game files; we can't access this for any other players
	 */
	@Override
	public final void setOurPersistentPlayerPrivateKnowledge (final PersistentPlayerPrivateKnowledge ppk)
	{
		ourPersistentPlayerPrivateKnowledge = ppk;
	}

	/**
	 * @return Private knowledge about our player that is not persisted to save game files; we can't access this for any other players
	 */
	@Override
	public TransientPlayerPrivateKnowledge getOurTransientPlayerPrivateKnowledge ()
	{
		return ourTransientPlayerPrivateKnowledge;
	}

	/**
	 * @param tpk Private knowledge about our player that is not persisted to save game files; we can't access this for any other players
	 */
	@Override
	public final void setOurTransientPlayerPrivateKnowledge (final TransientPlayerPrivateKnowledge tpk)
	{
		ourTransientPlayerPrivateKnowledge = tpk;
	}

	/**
	 * @return Event listeners
	 */
	@Override
	public final List<MultiplayerSessionClientEvent> getEventListeners ()
	{
		return eventListeners;
	}
}