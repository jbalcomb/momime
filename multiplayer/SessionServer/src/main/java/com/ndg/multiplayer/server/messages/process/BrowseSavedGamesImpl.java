package com.ndg.multiplayer.server.messages.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.persistence.PlayerCombinedPersistentInfo;
import com.ndg.multiplayer.persistence.SavedGame;
import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.BrowseSavedGames;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesFailed;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesFailedReason;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesSuccessful;
import com.ndg.multiplayer.sessionbase.SavedGameSession;

/**
 * Requests that server send us a list of sessions that we created and saved game file(s) exist for
 */
public final class BrowseSavedGamesImpl extends BrowseSavedGames implements PreSessionClientToServerMessage
{
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
			final BrowseSavedGamesFailed failed = new BrowseSavedGamesFailed ();
			failed.setReason (BrowseSavedGamesFailedReason.SAVED_GAMES_NOT_SUPPORTED);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final BrowseSavedGamesFailed failed = new BrowseSavedGamesFailed ();
			failed.setReason (BrowseSavedGamesFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't browse saved games with the intention of starting another
			final BrowseSavedGamesFailed failed = new BrowseSavedGamesFailed ();
			failed.setReason (BrowseSavedGamesFailedReason.ALREADY_IN_SESSION);
			sender.sendMessageToClient (failed);
		}
		else
		{
			// Start to build response message
			final BrowseSavedGamesSuccessful msg = new BrowseSavedGamesSuccessful ();
			
			// First subfolder indicates the ID of the player who created the game
			final File playerFolder = new File (server.getSavedGamesFolder (), Integer.valueOf (sender.getPlayerID ()).toString ());
			if (playerFolder.isDirectory ())
			{
				// What's the expected extension of the saved game files
				final String extension = server.isZipSavedGames () ?
					MultiplayerSessionThread.ZIPPED_SAVED_GAME_EXTENSION : MultiplayerSessionThread.RAW_SAVED_GAME_EXTENSION;
				
				// Find all save game folders
				for (final File savedGameFolder : playerFolder.listFiles ())
					if (savedGameFolder.isDirectory ())
					{
						// List all save point instances in this save game folder
						final List<File> filenames = new ArrayList<File> ();
						for (final File filename : savedGameFolder.listFiles ())
							if ((!filename.isDirectory ()) && (filename.getName ().endsWith (extension)))
								filenames.add (filename);
						
						if (filenames.size () > 0)
						{
							// Sort the filenames in ascending order
							Collections.sort (filenames);
							
							// Pick the latest one
							final File filename = filenames.get (filenames.size () - 1);
							
							// Read in the saved game XML
							final SavedGame src;
							if (server.isZipSavedGames ())
							{
								try (final FileInputStream in = new FileInputStream (filename))
								{
									try (final ZipInputStream zip = new ZipInputStream (in))
									{
										zip.getNextEntry ();
										src = (SavedGame) server.getSavedGamesUnmarshaller ().unmarshal (zip);
									}
								}
							}
							else
								src = (SavedGame) server.getSavedGamesUnmarshaller ().unmarshal (filename);
							
							// Copy it into the destination list
							final SavedGameSession dest = new SavedGameSession ();
							dest.setSessionDescription (src.getSessionDescription ());
							dest.setLatestSavedAt (src.getSavedAt ());
							
							for (final PlayerCombinedPersistentInfo player : src.getPlayer ())
								dest.getPlayer ().add (player.getPlayerDescription ());

							// Does the filename include an identifier?
							String name = filename.getName ();
							name = name.substring (0, name.length () - extension.length ());
							final int pos = name.indexOf (MultiplayerSessionThread.SAVED_GAME_IDENTIFIER_DELIMITER);
							if (pos >= 0)
								dest.setLatestSavedGameIdentifier (name.substring (pos + MultiplayerSessionThread.SAVED_GAME_IDENTIFIER_DELIMITER.length ()));
							
							// Add it to the list
							msg.getSavedGameSession ().add (dest);
						}
					}
			
				// Sort the saved game objects in the message in reverse savedGameID order
				Collections.sort (msg.getSavedGameSession (),
					(o1, o2) -> o2.getSessionDescription ().getSavedGameID () - o1.getSessionDescription ().getSavedGameID ()); 
			}

			// Finally send it
			sender.sendMessageToClient (msg);
		}
	}
}