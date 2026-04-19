package com.ndg.multiplayer.server.messages.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.persistence.PlayerCombinedPersistentInfo;
import com.ndg.multiplayer.persistence.SavedGame;
import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.LoadGame;
import com.ndg.multiplayer.sessionbase.LoadGameFailed;
import com.ndg.multiplayer.sessionbase.LoadGameFailedReason;

/**
 * Requests that a game be started back up from a saved game XML held on the server
 */
public final class LoadGameImpl extends LoadGame implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (LoadGameImpl.class);
	
	/**
	 * @param server The server handling player connections
	 * @param sender Player who sent the message
	 * @param thread Thread for the session this message is for; if the message is being executed in the client connection's thread instead, this will be null
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerSessionServer server, final MultiplayerClientConnection sender, @SuppressWarnings ("unused") final MultiplayerSessionThread thread)
		throws JAXBException, XMLStreamException, IOException
	{
		if (!server.isSavedGamesEnabled ())
		{
			// Server isn't configured for saved games
			final LoadGameFailed failed = new LoadGameFailed ();
			failed.setReason (LoadGameFailedReason.SAVED_GAMES_NOT_SUPPORTED);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final LoadGameFailed failed = new LoadGameFailed ();
			failed.setReason (LoadGameFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't browse saved games with the intention of starting another
			final LoadGameFailed failed = new LoadGameFailed ();
			failed.setReason (LoadGameFailedReason.ALREADY_IN_SESSION);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// First subfolder indicates the ID of the player who created the game
			final File playerFolder = new File (server.getSavedGamesFolder (), Integer.valueOf (sender.getPlayerID ()).toString ());
			
			// Second subfolder indicates the saved game ID
			final File savedGameFolder = new File (playerFolder, Integer.valueOf (getSavedGameID ()).toString ());
			if (!savedGameFolder.isDirectory ())
			{
				// Saved game ID doesn't exist
				final LoadGameFailed failed = new LoadGameFailed ();
				failed.setReason (LoadGameFailedReason.SAVE_GAME_ID_NOT_FOUND);
				sender.sendMessageToClient (failed);
			}
			else
			{
				// What's the expected extension of the saved game files
				final String extension = server.isZipSavedGames () ?
					MultiplayerSessionThread.ZIPPED_SAVED_GAME_EXTENSION : MultiplayerSessionThread.RAW_SAVED_GAME_EXTENSION;
				
				 // Find the specific saved game file
				final File filename = new File (savedGameFolder, getSavedGameFilename ());
				if ((!filename.exists ()) || (filename.isDirectory ()) || (!filename.getName ().endsWith (extension)))
				{
					// Saved game filename doesn't exist
					final LoadGameFailed failed = new LoadGameFailed ();
					failed.setReason (LoadGameFailedReason.SAVE_GAME_FILENAME_NOT_FOUND);
					sender.sendMessageToClient (failed);
				}
				else
				{
					// Read in the saved game XML
					final SavedGame savedGame;
					if (server.isZipSavedGames ())
					{
						try (final FileInputStream in = new FileInputStream (filename))
						{
							try (final ZipInputStream zip = new ZipInputStream (in))
							{
								zip.getNextEntry ();
								savedGame = (SavedGame) server.getSavedGamesUnmarshaller ().unmarshal (zip);
							}
						}
					}
					else
						savedGame = (SavedGame) server.getSavedGamesUnmarshaller ().unmarshal (filename);

					synchronized (server.getSessions ())
					{
						// Fill in remaining fields into player and session description
						log.info ("Player ID " + sender.getPlayerID () + " started new session with session ID " + server.getNextAvailableSessionID () + " reloading from saved game file");

						// It'll get a different session ID this time around
						savedGame.getSessionDescription ().setSessionID (server.getNextAvailableSessionID () );
						server.setNextAvailableSessionID (server.getNextAvailableSessionID () + 1);

						// Create the sesson
						final MultiplayerSessionThread sessionThread = server.createSessionThread (savedGame.getSessionDescription ());
						sessionThread.setLoaded (true);
						sessionThread.setMultiplayerSessionServer (server);
						sessionThread.setGeneralPublicKnowledge (savedGame.getGeneralPublicKnowledge ());
						sessionThread.setGeneralServerKnowledge (savedGame.getGeneralServerKnowledge ());
						sessionThread.preInitializeLoadedGame ();
						
						server.getSessions ().add (sessionThread);
						sender.setSession (sessionThread);

						// Add all the players into the thread
						PlayerServerDetails senderPlayer = null;
						for (final PlayerCombinedPersistentInfo src : savedGame.getPlayer ())
						{
							final PlayerServerDetails dest = new PlayerServerDetails (src.getPlayerDescription (),
								src.getPersistentPlayerPublicKnowledge (), src.getPersistentPlayerPrivateKnowledge (),
								sessionThread.createTransientPlayerPublicKnowledge (savedGame.getSessionDescription (), src.getPlayerDescription ()),
								sessionThread.createTransientPlayerPrivateKnowledge (savedGame.getSessionDescription (), src.getPlayerDescription ()));
							
							final boolean isRequestingPlayer = (sender.getPlayerID () == src.getPlayerDescription ().getPlayerID ().intValue ());
							sessionThread.addPlayer (src.getPlayerDescription (), isRequestingPlayer ? sender : null, null, false, dest);
							if (isRequestingPlayer)
								senderPlayer = dest;
						}
						
						// Notify the player of the successful join
						// This isn't done by the addPlayer method as normal, because we want the complete list of players set up first
						if (senderPlayer != null)
							sessionThread.sendJoinSuccessfulMessage (senderPlayer, JoinSuccessfulReason.LOADED_SAVED_GAME);

						// Start up the thread handling the new session
						sessionThread.sessionAdded ();
						sessionThread.start ();
					}
				}
			}
		}
	}
}