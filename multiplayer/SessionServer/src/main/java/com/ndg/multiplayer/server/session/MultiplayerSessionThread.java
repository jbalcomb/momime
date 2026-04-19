package com.ndg.multiplayer.server.session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.ClientToServerMessage;
import com.ndg.multiplayer.persistence.PlayerCombinedPersistentInfo;
import com.ndg.multiplayer.persistence.SavedGame;
import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.ServerToClientSessionConnection;
import com.ndg.multiplayer.sessionbase.AdditionalPlayerJoined;
import com.ndg.multiplayer.sessionbase.GeneralPublicKnowledge;
import com.ndg.multiplayer.sessionbase.GeneralServerKnowledge;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSuccessful;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PlayerCombinedPublicInfo;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerLeft;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.multiplayer.sessionbase.SessionDescription;
import com.ndg.multiplayer.sessionbase.SessionEnding;
import com.ndg.multiplayer.sessionbase.TransientPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.TransientPlayerPublicKnowledge;
import com.ndg.multiplayer.sessionbase.UpdateHumanPlayerToAI;
import com.ndg.multiplayer.userregistry.Player;

import jakarta.xml.bind.JAXBException;

/**
 * Thread that processes all the messages for a particular session
 *
 * The messages from different clients are being received in different threads, so if we try to process the messages in those threads it
 * becomes a locking nightmare and tricky around if two messages are being executed at the same time
 *
 * So instead, the client threads just queue the messages up here in this thread, and then they can be executed in an unambigious order all in this thread
 */
public class MultiplayerSessionThread extends Thread
{
	/** Logger to write to debug text file when the debug log is enabled */
	private Log log = LogFactory.getLog (MultiplayerSessionThread.class);

	/** Date and time format used to generate save game filenames */
	private final static String SAVED_GAME_IDENTIFIER_DATE_TIME_FORMAT_STRING = "yyyy-MM-dd HH-mm-ss-SSS";

	/** Date and time format used to generate save game filenames */
	public final static SimpleDateFormat SAVED_GAME_IDENTIFIER_DATE_TIME_FORMAT =
		new SimpleDateFormat (SAVED_GAME_IDENTIFIER_DATE_TIME_FORMAT_STRING);

	/** Delimiter between the date and time, and the identifier; this must be such that it can't possibly appear in the date and time string */
	public final static String SAVED_GAME_IDENTIFIER_DELIMITER = " - ";

	/** Extension given to raw saved game files */
	public final static String RAW_SAVED_GAME_EXTENSION = ".xml";

	/** Extension given to zipped saved game files */
	public final static String ZIPPED_SAVED_GAME_EXTENSION = ".zip";
	
	/** Server that is controlling this sesson */
	private MultiplayerSessionServer multiplayerSessionServer;

	/** Details of this session */
	private SessionDescription sessionDescription;
	
	/** Server only helper methods for dealing with players in a session */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;

	/** Queue of messages to process in this thread - access to this must be synchronized on the list */
	private final List<MessageAndSender> messageQueue;
	
	/** Whether this session was created by reloading a saved game */
	private boolean loaded;

	/* All the below values can change over time while the session is active
	 * Instead of requiring that access to various data items must be synchronized, the thread model we use here is to say
	 * that all of the values below can only be accesses from within this session thread, therefore there can never
	 * be multiple threads access trying to read/update the same values
	 *
	 * If another thread needs access to these values, it must do so via posting a request for the values onto the message queue */

	/** All in progress information that the server records about this session, not available publicly to anyone */
	private GeneralServerKnowledge generalServerKnowledge;

	/** All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session */
	private GeneralPublicKnowledge generalPublicKnowledge;

	/** All information about all players in this session - some may be AI players or disconnected; note we store this here instead of in the
	   * client thread in case the client disconnects in which case the thread would end */
	private final List<PlayerServerDetails> players;

	/** Player ID to be allocated to next AI player to be added to this session */
	private int nextAvailableComputerPlayerID = -1;

	/**
	 * Creates the thread - various other values such as the messageProcessor must then be set prior to calling thread.start ()
	 */
	public MultiplayerSessionThread ()
	{
		super ("MultiplayerSessionThread");

		messageQueue = new ArrayList<MessageAndSender> ();
		players = new ArrayList<PlayerServerDetails> ();
	}

	/**
	 * Called immediately after createSessionThread when creating new games, prior to the creating player being added to it or the thread actually starting.
	 * Descendant server classes can use this to perform any initialization necessary.
	 * 
	 * @throws JAXBException If there is an error dealing with any XML files during creation
	 * @throws XMLStreamException If there is an error dealing with any XML files during creation
	 * @throws IOException Can be used for non-JAXB related errors
	 */
	public void initializeNewGame () throws JAXBException, XMLStreamException, IOException
	{
	}

	/**
	 * Called when loading games, prior to any players being readded or being sent any messages.
	 * Also note this is ran prior to the session's thread starting up.
	 *  
	 * @throws JAXBException If there is an error dealing with any XML files during creation
	 * @throws XMLStreamException If there is an error dealing with any XML files during creation
	 * @throws IOException Can be used for non-JAXB related errors
	 */
	public void preInitializeLoadedGame () throws JAXBException, XMLStreamException, IOException
	{
	}
	
	/**
	 * Called when loading games, after the players have been set up and the JoinSuccessful message sent to the client.
	 * This is ran in the session's thread prior to it accepting any messages from clients.
	 * If a multiplayer game, this is ran after only the host player is connected; other human player connections will still be null.
	 * Override this to perform whatever startup actions are necessary after reloading a saved game.  
	 * 
	 * @throws JAXBException If there is an error dealing with any XML files during creation
	 * @throws XMLStreamException If there is an error dealing with any XML files during creation
	 * @throws IOException Can be used for non-JAXB related errors
	 */
	public void initializeLoadedGame () throws JAXBException, XMLStreamException, IOException
	{
	}
	
	/**
	 * Processes messages for this session
	 */
	@Override
	public final void run ()
	{
		try
		{
			if (isLoaded ())
				initializeLoadedGame ();
			
			final boolean sessionEnded = false;
			while (!sessionEnded)
			{
				// Is there a message waiting to be processed
				final MessageAndSender messageToProcess;
				synchronized (messageQueue)
				{
					if (messageQueue.size () > 0)
					{
						// Remember the message, and remove it from the queue - we can actually process it outside of the
						// synch block since we no no other threads are allowed to be processing messages relating to this session
						// This then allows client threads to be posting messages onto the queue while we're processing the previous one
						messageToProcess = messageQueue.get (0);
						messageQueue.remove (0);
					}
					else
					{
						// No message to process, so wait until one of the client threads signals us to tell us that they've added a message
						messageQueue.wait ();
						messageToProcess = null;
					}
				}

				// Did we get one?
				if (messageToProcess != null)
					processSessionMessageFromClient (messageToProcess.getMessage (), messageToProcess.getSender ());
			}
		}
		catch (final InterruptedException e)
		{
			log.error ("Thread error processing session messages", e);
		}
		catch (final IOException e)
		{
			log.error ("Error processing session messages", e);
		}
		catch (final JAXBException e)
		{
			log.error ("Error generating reply to session messages", e);
		}
		catch (final XMLStreamException e)
		{
			log.error ("Error sending reply to session messages", e);
		}
	}

	/**
	 * Client listener threads call this to add a message to the queue to be processed by this session
	 * @param msg Message received from client that needs to be processed
	 * @param sender Player who sent the message
	 */
	public final void addMessageToQueue (final ClientToServerMessage msg, final MultiplayerClientConnection sender)
	{
		synchronized (messageQueue)
		{
			messageQueue.add (new MessageAndSender (msg, sender));

			// Wake up server thread if it was idle waiting for a new message
			messageQueue.notifyAll ();
		}
	}

	/**
	 * Processes XML message received from client in the session's thread
	 * @param msg Message received from client that needs to be processed
	 * @param sender Player who sent the message
	 * @throws IOException If we are unable to process the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 */
	protected void processSessionMessageFromClient (final ClientToServerMessage msg, final MultiplayerClientConnection sender)
		throws IOException, JAXBException, XMLStreamException
	{
		// Messages might implement either of the two interfaces
		// Important that we check 'post' because of LeaveSessionImpl, which implements both
		if (msg instanceof PostSessionClientToServerMessage)
			((PostSessionClientToServerMessage) msg).process (this, sender.getPlayer ());
		
		else if (msg instanceof PreSessionClientToServerMessage)
			((PreSessionClientToServerMessage) msg).process (getMultiplayerSessionServer (), sender, this);
		
		
		else
			throw new IOException (getClass ().getName () + ".processSessionMessageFromClient doesn't know how to process message of type " + msg.getClass ().getName ());
	}

	/**
	 * Applications can override this to add custom rules for allowing players to join or not
	 * @param playerID Player who wants to join this session
	 * @return Reason why they cannot join this session; or null if they can
	 */
	public JoinFailedReason canJoinSession (@SuppressWarnings ("unused") final int playerID)
	{
		final JoinFailedReason result;
		if (getSessionDescription ().getMaxPlayers () == null)
			result = null;
		else if (getSessionDescription ().getMaxPlayers () <= 0)
			result = null;
		else if (getPlayers ().size () < getSessionDescription ().getMaxPlayers ())
			result = null;
		else
			result = JoinFailedReason.SESSION_FULL;

		return result;
	}
	
	/**
	 * This is used for all cases where players join or rejoin sessions, so for newSession, joinSession, loadGame, adding both human and AI players.
	 * Note when it is being used by newSession and loadGame, we are breaking our rule by allowing it to be ran from another thread,
	 * and it accesses and updates the player list - however this is fine, because in that situation it is being ran prior to
	 * this thread even starting, so is still safe.
	 * 
	 * @param pd Description for joining player; this must have been completely populated including the human/AI flag and the playerID.
	 * 	The only exception is that AI players may be added with a null playerID, in which a playerID will be allocated to them.
	 * @param connection Connection to player who requested new session; only applicable if newPlayer.isHuman () is true, and even then, can be left null
	 * 	(as will be the case when reloading multiplayer games - the connection for the secondary players will initially be null until they join the session).
	 * @param reason The type of session the player joined into; ignored and can be null if connection is null OR sendMessages is false
	 * @param sendMessages Whether to send JoinSuccessful & AdditionalPlayerJoined messages
	 * @param existingPlayerDetails If the player has existing details (i.e. persistent/transient public/private details), pass them in here.
	 * 	The details will be added to the player list if not already present (for rejoins they'll already be in the list, for loading saved games they won't be).
	 * @throws JAXBException If there is a problem converting the reply into XML
	 * @throws XMLStreamException If there is a problem writing the reply to the XML stream
	 * @throws IOException If there is a problem sending any reply back to the client
	 * @return Newly created player
	 */
	public final PlayerServerDetails addPlayer (final PlayerDescription pd, final ServerToClientSessionConnection connection, final JoinSuccessfulReason reason,
		final boolean sendMessages, final PlayerServerDetails existingPlayerDetails)
		throws JAXBException, XMLStreamException, IOException
	{
		// Autogenerate a playerID for AI players?
		if ((pd.getPlayerType () == PlayerType.AI) && (pd.getPlayerID () == null))
		{
			pd.setPlayerID (nextAvailableComputerPlayerID);
			nextAvailableComputerPlayerID--;
		}
		
		// Set up complete player details
		final PlayerServerDetails playerServerDetails;
		if (existingPlayerDetails != null)
			playerServerDetails = existingPlayerDetails;
		else
			playerServerDetails = new PlayerServerDetails (pd,
				createPersistentPlayerPublicKnowledge (getSessionDescription (), pd),
				createPersistentPlayerPrivateKnowledge (getSessionDescription (), pd),
				createTransientPlayerPublicKnowledge (getSessionDescription (), pd),
				createTransientPlayerPrivateKnowledge (getSessionDescription (), pd));

		// Is it already in the list?
		if (!getPlayers ().contains (playerServerDetails))
			getPlayers ().add (playerServerDetails);

		// Are they connected yet?
		if (connection != null)
		{
			playerServerDetails.setConnection (connection);
			connection.setPlayer (playerServerDetails);
		}
			
		if (sendMessages)
		{
			// Send reply to the player who joined
			if (connection != null)
				sendJoinSuccessfulMessage (playerServerDetails, reason);
	
			// Inform other players already in session of the new player who joined
			// except in the case of a rejoin, because the players who didn't disconnect will already have
			// the rejoining player in their list
			if ((connection == null) || (existingPlayerDetails == null))
			{
				final PlayerCombinedPublicInfo playerPublicInfo = new PlayerCombinedPublicInfo ();
				playerPublicInfo.setPlayerDescription (pd);
				playerPublicInfo.setPersistentPlayerPublicKnowledge (playerServerDetails.getPersistentPlayerPublicKnowledge ());
				playerPublicInfo.setTransientPlayerPublicKnowledge (playerServerDetails.getTransientPlayerPublicKnowledge ());
		
				final AdditionalPlayerJoined additionalPlayerJoined = new AdditionalPlayerJoined ();
				additionalPlayerJoined.setPlayer (playerPublicInfo);
		
				for (final PlayerServerDetails thisPlayer : getPlayers ())
					if ((!thisPlayer.getPlayerDescription ().getPlayerID ().equals (pd.getPlayerID ())) && (thisPlayer.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN))
						thisPlayer.getConnection ().sendMessageToClient (additionalPlayerJoined);
			}
		}
		
		playerAdded (playerServerDetails, reason);
		
		return playerServerDetails;
	}
	
	/**
	 * Called after players are added to the session for any reason (when setting up a new game, joining, rejoining or loading)
	 *
	 * @param player The player who just joined the session
	 * @param reason The type of session the player joined into; ignored and can be null if connection is null OR sendMessages is false
	 * @throws JAXBException If there is a problem converting the reply into XML
	 * @throws XMLStreamException If there is a problem writing the reply to the XML stream
	 * @throws IOException If there is a problem sending any reply back to the client
	 */
	@SuppressWarnings ("unused")
	public void playerAdded (final PlayerServerDetails player, final JoinSuccessfulReason reason)
		throws JAXBException, XMLStreamException, IOException
	{
	}
	
	/**
	 * Builds a "join successful" message and sends it to the specified player.  This is in its own method because
	 * its needed from a number of places.
	 * 
	 * @param sendToPlayer The player to send the message to
	 * @param reason The type of session the player joined into
	 * @throws JAXBException If there is a problem converting the reply into XML
	 * @throws XMLStreamException If there is a problem writing the reply to the XML stream
	 */
	public final void sendJoinSuccessfulMessage (final PlayerServerDetails sendToPlayer, final JoinSuccessfulReason reason)
		throws JAXBException, XMLStreamException
	{
		final JoinSuccessful success = new JoinSuccessful ();
		success.setReason (reason);
		success.setSessionDescription (getSessionDescription ());
		success.setGeneralPublicKnowledge (getGeneralPublicKnowledge ());
		success.setPersistentPlayerPrivateKnowledge (sendToPlayer.getPersistentPlayerPrivateKnowledge ());
		success.setTransientPlayerPrivateKnowledge (sendToPlayer.getTransientPlayerPrivateKnowledge ());

		for (final PlayerServerDetails thisPlayer : getPlayers ())
		{
			final PlayerCombinedPublicInfo playerPublicInfo = new PlayerCombinedPublicInfo ();
			playerPublicInfo.setPlayerDescription (thisPlayer.getPlayerDescription ());
			playerPublicInfo.setPersistentPlayerPublicKnowledge (thisPlayer.getPersistentPlayerPublicKnowledge ());
			playerPublicInfo.setTransientPlayerPublicKnowledge (thisPlayer.getTransientPlayerPublicKnowledge ());
			success.getPlayer ().add (playerPublicInfo);
		}

		sendToPlayer.getConnection ().sendMessageToClient (success);
	}
	
	/**
	 * This gets called after a player leaves a session - the application can override it to change the behaviour about whether
	 * that causes the whole session to be ended.  By default, sessions end when there are no human players remaining.
	 * 
	 * @return Whether to end the session (kicking all remaining players)
	 */
	public boolean endSessionAfterPlayerLeft ()
	{
		boolean endSession = true;
		final Iterator<PlayerServerDetails> iter = getPlayers ().iterator ();
		while ((endSession) && (iter.hasNext ()))
			if (iter.next ().getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
				endSession = false;		// Found a remaining human player, so don't end the session
		
		return endSession;
	}
	
	/**
	 * This is triggered after a new session has been created and added to the server's list, the creating player added to the list, and the session is about to start.
	 * Descendant classes can override this to perform some action here.
	 * It is triggered while the thread has a lock on the server's session list, so guarantees that no two sessionAdded () or
	 * removingSession () methods can be triggered simultaneously.
	 */
	public void sessionAdded ()
	{
	}

	/**
	 * This is triggered after a session has been closed down, the player list emptied, and the sesson removed from the server's list.
	 * Descendant classes can override this to perform some action here.
	 * It is triggered while the thread has a lock on the server's session list, so guarantees that no two sessionAdded () or
	 * removingSession () methods can be triggered simultaneously.
	 */
	public void sessionRemoved ()
	{
	}
	
	/**
	 * Records a save game file of this session's state
	 * @param identifier Identifier of the sequence of saved game files for this session, such as a turn number; can be left null
	 * @throws IOException If saved games are not enabled on this server, or there is some error saving the file
	 * @throws JAXBException If there is an XML error trying to save the file
	 */
	public final void saveGame (final String identifier) throws IOException, JAXBException
	{
		if (!getMultiplayerSessionServer ().isSavedGamesEnabled ())
			throw new IOException ("Saved games are disabled on this server");
		
		if (getSessionDescription ().getSavedGameID () == null)
			throw new IOException ("This session has no ID to save the game as"); 
		
		// Find the first human player to save the game under
		final File playerFolder = new File (getMultiplayerSessionServer ().getSavedGamesFolder (), findHostPlayer ().getPlayerDescription ().getPlayerID ().toString ());
		
		// Second subfolder indicates the saved game ID
		final File savedGameFolder = new File (playerFolder, getSessionDescription ().getSavedGameID ().toString ());
		savedGameFolder.mkdirs ();
		
		// Generate an identifier if none was supplied
		final String useIdentifier = SAVED_GAME_IDENTIFIER_DATE_TIME_FORMAT.format (new Date ()) +
			((identifier == null) ? "" : (SAVED_GAME_IDENTIFIER_DELIMITER + identifier));
		
		// Generate the object to save
		final SavedGame savedGame = new SavedGame ();
		savedGame.setSessionDescription (getSessionDescription ());
		savedGame.setGeneralPublicKnowledge (getGeneralPublicKnowledge ());
		savedGame.setGeneralServerKnowledge (getGeneralServerKnowledge ());
		
		final GregorianCalendar now = new GregorianCalendar ();
		now.setTime (new Date ());

		savedGame.setSavedAt (getMultiplayerSessionServer ().getDatatypeFactory ().newXMLGregorianCalendar (now));
		
		for (final PlayerServerDetails srcPlayer : getPlayers ())
		{
			final PlayerCombinedPersistentInfo destPlayer = new PlayerCombinedPersistentInfo ();
			destPlayer.setPlayerDescription (srcPlayer.getPlayerDescription ());
			destPlayer.setPersistentPlayerPublicKnowledge (srcPlayer.getPersistentPlayerPublicKnowledge ());
			destPlayer.setPersistentPlayerPrivateKnowledge (srcPlayer.getPersistentPlayerPrivateKnowledge ());
			
			savedGame.getPlayer ().add (destPlayer);
		}
		
		// Save it
		if (getMultiplayerSessionServer ().isZipSavedGames ())
		{
			final File zipFilename = new File (savedGameFolder, useIdentifier + ZIPPED_SAVED_GAME_EXTENSION);
			try (final FileOutputStream out = new FileOutputStream (zipFilename))
			{
				try (final ZipOutputStream zip = new ZipOutputStream (out))
				{
					zip.putNextEntry (new ZipEntry (useIdentifier + RAW_SAVED_GAME_EXTENSION));
					getMultiplayerSessionServer ().getSavedGamesMarshaller ().marshal (savedGame, zip);
					zip.closeEntry ();
				}
			}
		}
		else
		{
			final File xmlFilename = new File (savedGameFolder, useIdentifier + RAW_SAVED_GAME_EXTENSION);
			getMultiplayerSessionServer ().getSavedGamesMarshaller ().marshal (savedGame, xmlFilename);
		}
	}

	/**
	 * Searches for all save points of the current session and deletes the oldest ones, keeping only the number of latest save points as specified
	 * @param keepCount Number of save points to keep
	 * @return Number that were deleted
	 */
	public final int deleteOldestSavePoints (final int keepCount)
	{
		if (keepCount < 1)
			throw new IllegalArgumentException ("deleteOldestSavePoints: keepCount must be at least 1, but was set to " + keepCount);
		
		// First subfolder indicates the ID of the player who created the game
		final File playerFolder = new File (getMultiplayerSessionServer ().getSavedGamesFolder (), getPlayers ().get (0).getPlayerDescription ().getPlayerID ().toString ());
		
		// Second subfolder indicates the saved game ID - if the folder isn't found, then we've got no save points to delete, so just silently exit
		int deleteCount = 0;
		final File savedGameFolder = new File (playerFolder, getSessionDescription ().getSavedGameID ().toString ());
		if (savedGameFolder.isDirectory ())
		{
			// What's the expected extension of the saved game files
			final String extension = getMultiplayerSessionServer ().isZipSavedGames () ?
				MultiplayerSessionThread.ZIPPED_SAVED_GAME_EXTENSION : MultiplayerSessionThread.RAW_SAVED_GAME_EXTENSION;
			
			// Find all save points
			final List<File> filenames = new ArrayList<File> ();
			for (final File filename : savedGameFolder.listFiles ())
				if ((!filename.isDirectory ()) && (filename.getName ().endsWith (extension)))
					filenames.add (filename);
					
			if (filenames.size () > keepCount)
			{
				deleteCount = filenames.size () - keepCount;
				
				// Sort the filenames in ascending order
				Collections.sort (filenames);
				
				// Delete the earliest files
				for (int fileNo = 0; fileNo < filenames.size () - keepCount; fileNo++)
					filenames.get (fileNo).delete ();
			}
		}
		
		return deleteCount;
	}
	
	/**
	 * @return Host player for this session, that is, the first human player in the list
	 */
	public final PlayerServerDetails findHostPlayer ()
	{
		PlayerServerDetails host = null;
		final Iterator<PlayerServerDetails> iter = getPlayers ().iterator ();
		while ((host == null) && (iter.hasNext ()))
		{
			final PlayerServerDetails thisPlayer = iter.next ();
			if (thisPlayer.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
				host = thisPlayer;
		}
		
		// If there's no human players at all, default to the first player in the list
		return (host != null) ? host : getPlayers ().get (0);
	}
	
	/**
	 * @param playerID Human player to convert to AI player
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is another kind of problem
	 */
	public final void updateHumanPlayerToAI (final int playerID)
		throws IOException, JAXBException, XMLStreamException
	{
		// Before we change it, check if the player was the old host player
		final boolean wasHost = (findHostPlayer ().getPlayerDescription ().getPlayerID () == playerID);
		
		// Update on server
		final PlayerServerDetails playerToConvert = getMultiplayerSessionServerUtils ().findPlayerWithID (getPlayers (), playerID, "updateHumanPlayerToAI");
		playerToConvert.getPlayerDescription ().setPlayerType (PlayerType.HUMAN_CONVERTED_TO_AI);
		
		// Update on other clients
		final UpdateHumanPlayerToAI msg = new UpdateHumanPlayerToAI ();
		msg.setPlayerID (playerID);
		
		for (final PlayerServerDetails player : getPlayers ())
			if (player.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
				player.getConnection ().sendMessageToClient (msg);
		
		// From the point of view of the person being converted, they are just leaving the session.
		// The fact that they've been converted to an AI player, or anything else further going on in the session, makes no difference to them.
		final PlayerLeft playerLeft = new PlayerLeft ();
		playerLeft.setPlayerID (playerID);
		playerToConvert.getConnection ().sendMessageToClient (playerLeft);
		
		// On the server, return the human player to a state where they are logged in, but not in a session - see how LeaveSessionImpl works
		final MultiplayerClientConnection conn = (MultiplayerClientConnection) playerToConvert.getConnection ();
		conn.setPlayer (null);
		conn.setSession (null);
		
		// Lose the connection from the player object, now its an AI player
		playerToConvert.setConnection (null);
		
		// End session as well?
		if ((!checkEndSessionAfterPlayerLeft ()) && (wasHost) && (getMultiplayerSessionServer ().isSavedGamesEnabled ()))
		{
			final PlayerServerDetails newHost = findHostPlayer ();
			
			// We can't assume the same savedGameID will be valid for the new host, so allocate a new one  
			final Player player = getMultiplayerSessionServer ().getUserRegistry ().findPlayerByID (newHost.getPlayerDescription ().getPlayerID ());
			if (player != null)
			{
				getSessionDescription ().setSavedGameID (player.getNextAvailableSavedGameID ());
				player.setNextAvailableSavedGameID (player.getNextAvailableSavedGameID () + 1);
				getMultiplayerSessionServer ().getUserRegistry ().saveUserRegistry ();
			}
		}
	}
	
	/**
	 * A player has just left the session.  Check if the session now meets the conditions to be closed down.
	 * 
	 * @return Whether the session was ended or not
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public final boolean checkEndSessionAfterPlayerLeft ()
		throws JAXBException, XMLStreamException
	{
		// End session as well?
		final boolean ending = endSessionAfterPlayerLeft ();
		log.debug ("endSessionAfterPlayerLeft = " + ending);
		if (ending)
		{
			// Send message to anyone who might be left
			final SessionEnding sessionEnding = new SessionEnding (); 
			for (final PlayerServerDetails thisPlayer : getPlayers ())
				if (thisPlayer.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
					thisPlayer.getConnection ().sendMessageToClient (sessionEnding);
			
			// Remove them all from the session
			for (final PlayerServerDetails thisPlayer : getPlayers ())
			{
				final MultiplayerClientConnection playerConn = (MultiplayerClientConnection) thisPlayer.getConnection ();
				if (playerConn != null)
				{
					playerConn.setSession (null);
					playerConn.setPlayer (null);
				}
			}
			
			// Tear down references to any players
			getPlayers ().clear ();
			
			// Remove the session from the server
			synchronized (getMultiplayerSessionServer ().getSessions ())
			{
				getMultiplayerSessionServer ().getSessions ().remove (this);
				sessionRemoved ();
			}
		}
		
		return ending;
	}
	
	/**
	 * @return Details of this session
	 */
	public SessionDescription getSessionDescription ()
	{
		return sessionDescription;
	}

	/**
	 * @param sd Details of this session
	 */
	public final void setSessionDescription (final SessionDescription sd)
	{
		sessionDescription = sd;
	}
	
	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Descendant of PersistentPlayerPublicKnowledge, or can be left as null if not required
	 * @throws IOException If there is a problem creating the data structure
	 */
	@SuppressWarnings ("unused")
	protected PersistentPlayerPublicKnowledge createPersistentPlayerPublicKnowledge (final SessionDescription sd, final PlayerDescription pd) throws IOException
	{
		return null;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Descendant of PersistentPlayerPrivateKnowledge, or can be left as null if not required
	 * @throws IOException If there is a problem creating the data structure
	 */
	@SuppressWarnings ("unused")
	protected PersistentPlayerPrivateKnowledge createPersistentPlayerPrivateKnowledge (final SessionDescription sd, final PlayerDescription pd) throws IOException
	{
		return null;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Descendant of TransientPlayerPublicKnowledge, or can be left as null if not required
	 * @throws IOException If there is a problem creating the data structure
	 */
	@SuppressWarnings ("unused")
	public TransientPlayerPublicKnowledge createTransientPlayerPublicKnowledge (final SessionDescription sd, final PlayerDescription pd) throws IOException
	{
		return null;
	}

	/**
	 * @param sd Session description, in case any info here is needed to populate the player knowledge structure
	 * @param pd Player description, in case any info here is needed to populate the player knowledge structure
	 * @return Descendant of TransientPlayerPrivateKnowledge, or can be left as null if not required
	 * @throws IOException If there is a problem creating the data structure
	 */
	@SuppressWarnings ("unused")
	public TransientPlayerPrivateKnowledge createTransientPlayerPrivateKnowledge (final SessionDescription sd, final PlayerDescription pd) throws IOException
	{
		return null;
	}

	/**
	 * @return All in progress information that the server records about this session, not available publicly to anyone; can be null
	 */
	public GeneralServerKnowledge getGeneralServerKnowledge ()
	{
		return generalServerKnowledge;
	}

	/**
	 * @param gsk All in progress information that the server records about this session, not available publicly to anyone; can be null
	 */
	public final void setGeneralServerKnowledge (final GeneralServerKnowledge gsk)
	{
		generalServerKnowledge = gsk;
	}

	/**
	 * @return All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	public GeneralPublicKnowledge getGeneralPublicKnowledge ()
	{
		return generalPublicKnowledge;
	}

	/**
	 * @param gpk All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	public final void setGeneralPublicKnowledge (final GeneralPublicKnowledge gpk)
	{
		generalPublicKnowledge = gpk;
	}
	
	/**
	 * @return All information about all players in this session - some may be AI players or disconnected
	 */
	public final List<PlayerServerDetails> getPlayers ()
	{
		return players;
	}

	/**
	 * @return Server only helper methods for dealing with players in a session
	 */
	public final MultiplayerSessionServerUtils getMultiplayerSessionServerUtils ()
	{
		return multiplayerSessionServerUtils;
	}

	/**
	 * @param obj Server only helper methods for dealing with players in a session
	 */
	public final void setMultiplayerSessionServerUtils (final MultiplayerSessionServerUtils obj)
	{
		multiplayerSessionServerUtils = obj;
	}
	
	/**
	 * @return Server that is controlling this sesson
	 */
	public final MultiplayerSessionServer getMultiplayerSessionServer ()
	{
		return multiplayerSessionServer;
	}

	/**
	 * @param server Server that is controlling this sesson
	 */
	public final void setMultiplayerSessionServer (final MultiplayerSessionServer server)
	{
		multiplayerSessionServer = server;
	}

	/**
	 * @return Whether this session was created by reloading a saved game
	 */
	public final boolean isLoaded ()
	{
		return loaded;
	}

	/**
	 * @param l Whether this session was created by reloading a saved game
	 */
	public final void setLoaded (final boolean l)
	{
		loaded = l;
	}
	
	/**
	 * Stores both a message and who sent it
	 */
	private final class MessageAndSender
	{
		/** Message to process, if a network message */
		private final ClientToServerMessage message;

		/** Player who sent message */
		private final MultiplayerClientConnection sender;

		/**
		 * @param aMessage Message to process
		 * @param aSender Player who sent message
		 */
		public MessageAndSender (final ClientToServerMessage aMessage, final MultiplayerClientConnection aSender)
		{
			super ();

			message = aMessage;
			sender = aSender;
		}

		/**
		 * @return Message to process
		 */
		public final ClientToServerMessage getMessage ()
		{
			return message;
		}

		/**
		 * @return Player who sent message
		 */
		public final MultiplayerClientConnection getSender ()
		{
			return sender;
		}
	}
}