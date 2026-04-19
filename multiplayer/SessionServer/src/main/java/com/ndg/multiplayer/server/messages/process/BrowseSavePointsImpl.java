package com.ndg.multiplayer.server.messages.process;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.BrowseSavePoints;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsFailed;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsFailedReason;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsSuccessful;
import com.ndg.multiplayer.sessionbase.SavedGamePoint;

/**
 * Requests that server send us a list of individual save points for a particular saved game
 */
public final class BrowseSavePointsImpl extends BrowseSavePoints implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (BrowseSavePointsImpl.class);
	
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
			final BrowseSavePointsFailed failed = new BrowseSavePointsFailed ();
			failed.setReason (BrowseSavePointsFailedReason.SAVED_GAMES_NOT_SUPPORTED);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final BrowseSavePointsFailed failed = new BrowseSavePointsFailed ();
			failed.setReason (BrowseSavePointsFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't browse saved games with the intention of starting another
			final BrowseSavePointsFailed failed = new BrowseSavePointsFailed ();
			failed.setReason (BrowseSavePointsFailedReason.ALREADY_IN_SESSION);
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
				final BrowseSavePointsFailed failed = new BrowseSavePointsFailed ();
				failed.setReason (BrowseSavePointsFailedReason.SAVE_GAME_ID_NOT_FOUND);
				sender.sendMessageToClient (failed);
			}
			else
			{
				// Start to build response message
				final BrowseSavePointsSuccessful msg = new BrowseSavePointsSuccessful ();

				// What's the expected extension of the saved game files
				final String extension = server.isZipSavedGames () ?
					MultiplayerSessionThread.ZIPPED_SAVED_GAME_EXTENSION : MultiplayerSessionThread.RAW_SAVED_GAME_EXTENSION;
				
				// Find all save points
				final List<File> filenames = new ArrayList<File> ();
				for (final File filename : savedGameFolder.listFiles ())
					if ((!filename.isDirectory ()) && (filename.getName ().endsWith (extension)))
						filenames.add (filename);
						
				if (filenames.size () > 0)
				{
					// Sort the filenames in ascending order
					Collections.sort (filenames);
					
					// Parse each of the filenames
					for (final File filename : filenames)
					{
						// Copy it into the destination list
						// Note am specifically trying to avoid here reading in every single XML file
						final SavedGamePoint dest = new SavedGamePoint ();
						dest.setSavedGameFilename (filename.getName ());
						
						// Does the filename include an identifier?
						String name = filename.getName ();
						name = name.substring (0, name.length () - extension.length ());
						final int pos = name.indexOf (MultiplayerSessionThread.SAVED_GAME_IDENTIFIER_DELIMITER);
						if (pos >= 0)
							dest.setSavedGameIdentifier (name.substring (pos + MultiplayerSessionThread.SAVED_GAME_IDENTIFIER_DELIMITER.length ()));
						
						// Parse out the date and time portion
						final String dateTimeString = (pos >= 0) ? name.substring (0, pos) : name;
						try
						{
							final GregorianCalendar savedAt = new GregorianCalendar ();
							savedAt.setTime (MultiplayerSessionThread.SAVED_GAME_IDENTIFIER_DATE_TIME_FORMAT.parse (dateTimeString));
							
							dest.setSavedAt (server.getDatatypeFactory ().newXMLGregorianCalendar (savedAt));
							
							// Add it to the head of the list, so in the process we reverse the sort order
							msg.getSavedGamePoint ().add (0, dest);
						}
						catch (final ParseException e)
						{
							log.error ("Saved game file doesn't have expected date and time prefix", e);
						}
					}
				}
			
				// Finally send it
				sender.sendMessageToClient (msg);
			}
		}
	}
}