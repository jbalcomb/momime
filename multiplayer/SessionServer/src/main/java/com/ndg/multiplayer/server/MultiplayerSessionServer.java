package com.ndg.multiplayer.server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLStreamException;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.ndg.multiplayer.base.server.MultiplayerBaseServer;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.users.MultiplayerSessionUserRegistry;
import com.ndg.multiplayer.sessionbase.SessionDescription;

/**
 * Main server class to listen for client connection requests and manage list of sessions
 */
@ManagedResource
public class MultiplayerSessionServer extends MultiplayerBaseServer
{
	/** User registry */
	private MultiplayerSessionUserRegistry userRegistry;

	/** List of active sessions - access to this list must be synchronized */
	private final List<MultiplayerSessionThread> sessions;

	/** The next free session ID on this server - access to this value must be synchronized on the session list */
	private int nextAvailableSessionID;
	
	/** Folder to store saved game files in; leaving this null disables support for saved games */
	private File savedGamesFolder;
	
	/** Unmarshaller for loading saved games */
	private Unmarshaller savedGamesUnmarshaller;
	
	/** Marshaller for writing saved games */
	private Marshaller savedGamesMarshaller;
	
	/** Whether to zip saved game files, or leave as raw XML */
	private boolean zipSavedGames;

	/** Factory for creating XML data types */
	private final DatatypeFactory datatypeFactory;
	
	/**
	 * @param threadName Name to give to the server thread
	 * @throws DatatypeConfigurationException If there is a problem creating the DatatypeFactory
	 */
	public MultiplayerSessionServer (final String threadName) throws DatatypeConfigurationException
	{
		super (threadName);

		sessions = new ArrayList<MultiplayerSessionThread> ();
		nextAvailableSessionID = 1;
		datatypeFactory = DatatypeFactory.newInstance ();
	}

	/**
	 * @param socket Socket on which a new client has connected
	 * @return Thread object to handle requests from this client
	 * @throws InterruptedException If there is a problem waiting for the thread to start up
	 * @throws JAXBException If there is a problem sending something to connecting client
	 * @throws XMLStreamException If there is a problem sending something to connecting client
	 */
	@Override
	protected MultiplayerBaseServerThread createAndStartClientThread (final Socket socket) throws InterruptedException, JAXBException, XMLStreamException
	{
		final MultiplayerClientConnection conn = new MultiplayerClientConnection ("ClientConnection-" + socket);
		conn.setServer (this);
		conn.setSocket (socket);
		conn.setSendContext (getServerToClientContext ());
		conn.setReceiveContext (getClientToServerContext ());
		conn.setReceiveObjectFactoryArray (getClientToServerContextFactoryArray ());
		conn.setConversationTag (getConversationTag ());
		conn.setMarshallLogWriter (getMarshallLogWriter ());
		conn.setUnmarshallLogWriter (getUnmarshallLogWriter ());
		conn.setCompress (isCompress ());
		conn.setDecompress (isDecompress ());
		conn.start ();
		return conn;
	}

	/**
	 * @return Details about client connections
	 */
	@ManagedAttribute
	public final List<String> getPlayerConnections ()
	{
		final List<String> list = new ArrayList<String> ();
		
		synchronized (getClientConnections ())
		{
			getClientConnections ().stream ().map (conn -> (MultiplayerClientConnection) conn).filter (conn -> conn.getPlayerID () > 0).forEach (conn ->
			{
				if (conn.getPlayer () == null)
					list.add ("Player ID " + conn.getPlayerID ());
				else
					list.add ("Player ID " + conn.getPlayerID () + " " + conn.getPlayer ().getPlayerDescription ().getPlayerName () + ", in session ID " +
						conn.getSession ().getSessionDescription ().getSessionID () + " " + conn.getSession ().getSessionDescription ().getSessionName ());
			});
		}
		
		return list;
	}
	
	/**
	 * Descendant server classes may want to override to create alternate thread subclasses
	 * 
	 * @param sessionDescription Description of the new session
	 * @return Thread object to handle requests for this session
	 */
	public MultiplayerSessionThread createSessionThread (final SessionDescription sessionDescription)
	{
		final MultiplayerSessionThread newSessionThread = new MultiplayerSessionThread ();
		newSessionThread.setSessionDescription (sessionDescription);
		
		return newSessionThread;
	}
	
	/**
	 * Caller must have already synchronized the session list
	 * @param sessionID Session ID to search for
	 * @return Session thread that is running the requested session ID, or null if not found
	 */
	public final MultiplayerSessionThread findSession (final int sessionID)
	{
		MultiplayerSessionThread found = null;
		final Iterator<MultiplayerSessionThread> iter = getSessions ().iterator ();
		while ((found == null) && (iter.hasNext ()))
		{
			final MultiplayerSessionThread thisSession = iter.next ();
			if (thisSession.getSessionDescription ().getSessionID () == sessionID)
				found = thisSession;
		}

		return found;
	}

	/**
	 * Called after player successfully logs in, before they join a session
	 *
	 * @param playerID The player who just logged in
	 * @param sender Connection to the player who just logged in
	 * @throws JAXBException If there is a problem converting the reply into XML
	 * @throws XMLStreamException If there is a problem writing the reply to the XML stream
	 * @throws IOException If there is a problem sending any reply back to the client
	 */
	@SuppressWarnings ("unused")
	public void playerLoggedIn (final int playerID, final MultiplayerClientConnection sender) throws JAXBException, XMLStreamException, IOException
	{
	}
	
	/**
	 * @return True if enough information is available to allow saving and loading game data
	 */
	@ManagedAttribute
	public final boolean isSavedGamesEnabled ()
	{
		return (getSavedGamesFolder () != null) && (getSavedGamesUnmarshaller () != null) && (getSavedGamesMarshaller () != null);
	}

	/**
	 * @return List of active sessions - access to this list must be synchronized
	 */
	public final List<MultiplayerSessionThread> getSessions ()
	{
		return sessions;
	}

	/**
	 * @return Details about active sessions
	 */
	@ManagedAttribute
	public final List<String> getActiveSessions ()
	{
		final List<String> list = new ArrayList<String> ();
		
		synchronized (getSessions ())
		{
			getSessions ().forEach (session ->
			{
				final StringBuilder s = new StringBuilder ();
				session.getPlayers ().forEach (p ->
				{
					if (s.length () > 0)
						s.append (", ");
					
					s.append ("Player ID " + p.getPlayerDescription ().getPlayerID () + " " + p.getPlayerDescription ().getPlayerName ());
				});
				
				list.add ("Session ID " + session.getSessionDescription ().getSessionID () + " " +
					session.getSessionDescription ().getSessionName () + ": " + s);
			});
		}
		
		return list;
	}

	/**
	 * @return The next free session ID on this server - access to this value must be synchronized on the session list
	 */
	public final int getNextAvailableSessionID ()
	{
		return nextAvailableSessionID;
	}

	/**
	 * @param next The next free session ID on this server - access to this value must be synchronized on the session list
	 */
	public final void setNextAvailableSessionID (final int next)
	{
		nextAvailableSessionID = next;
	}	
	
	/**
	 * @return User registry
	 */
	public final MultiplayerSessionUserRegistry getUserRegistry ()
	{
		return userRegistry;
	}		

	/**
	 * @param reg User registry
	 */
	public final void setUserRegistry (final MultiplayerSessionUserRegistry reg)
	{
		userRegistry = reg;
	}

	/**
	 * @return Factory for creating XML data types
	 */
	public final DatatypeFactory getDatatypeFactory ()
	{
		return datatypeFactory;
	}

	/**
	 * @return Folder to store saved game files in; leaving this null disables support for saved games
	 */
	@ManagedAttribute
	public final File getSavedGamesFolder ()
	{
		return savedGamesFolder;
	}

	/**
	 * @param folder Folder to store saved game files in; leaving this null disables support for saved games
	 */
	public final void setSavedGamesFolder (final File folder)
	{
		savedGamesFolder = folder;
	}

	/**
	 * @return Unmarshaller for loading saved games
	 */
	public final Unmarshaller getSavedGamesUnmarshaller ()
	{
		return savedGamesUnmarshaller;
	}

	/**
	 * @param unmarshaller Unmarshaller for loading saved games
	 */
	public final void setSavedGamesUnmarshaller (final Unmarshaller unmarshaller)
	{
		savedGamesUnmarshaller = unmarshaller;
	}
	
	/**
	 * @return Marshaller for writing saved games
	 */
	public final Marshaller getSavedGamesMarshaller ()
	{
		return savedGamesMarshaller;
	}

	/**
	 * @param marshaller Marshaller for writing saved games
	 */
	public final void setSavedGamesMarshaller (final Marshaller marshaller)
	{
		savedGamesMarshaller = marshaller;
	}
	
	/**
	 * @return Whether to zip saved game files, or leave as raw XML
	 */
	@ManagedAttribute
	public final boolean isZipSavedGames ()
	{
		return zipSavedGames;
	}

	/**
	 * @param zip Whether to zip saved game files, or leave as raw XML
	 */
	public final void setZipSavedGames (final boolean zip)
	{
		zipSavedGames = zip;
	}
}