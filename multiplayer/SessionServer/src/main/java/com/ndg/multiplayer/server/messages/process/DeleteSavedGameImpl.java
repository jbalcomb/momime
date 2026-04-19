package com.ndg.multiplayer.server.messages.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.DeleteSavedGame;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameFailed;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameFailedReason;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameSuccessful;

/**
 * Requests that server delete all the save points for a particular saved session
 */
public final class DeleteSavedGameImpl extends DeleteSavedGame implements PreSessionClientToServerMessage
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
			final DeleteSavedGameFailed failed = new DeleteSavedGameFailed ();
			failed.setReason (DeleteSavedGameFailedReason.SAVED_GAMES_NOT_SUPPORTED);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getPlayerID () <= 0)
		{
			// Not logged in
			final DeleteSavedGameFailed failed = new DeleteSavedGameFailed ();
			failed.setReason (DeleteSavedGameFailedReason.NOT_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else if (sender.getSession () != null)
		{
			// Already in session, can't browse saved games with the intention of starting another
			final DeleteSavedGameFailed failed = new DeleteSavedGameFailed ();
			failed.setReason (DeleteSavedGameFailedReason.ALREADY_IN_SESSION);
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
				final DeleteSavedGameFailed failed = new DeleteSavedGameFailed ();
				failed.setReason (DeleteSavedGameFailedReason.SAVE_GAME_ID_NOT_FOUND);
				sender.sendMessageToClient (failed);
			}
			else
			{
				// What's the expected extension of the saved game files
				final String extension = server.isZipSavedGames () ?
					MultiplayerSessionThread.ZIPPED_SAVED_GAME_EXTENSION : MultiplayerSessionThread.RAW_SAVED_GAME_EXTENSION;
				
				// Find all save points
				final List<File> filenames = new ArrayList<File> ();
				for (final File filename : savedGameFolder.listFiles ())
					if ((!filename.isDirectory ()) && (filename.getName ().endsWith (extension)))
						filenames.add (filename);
					
				// Delete all save points
				for (final File filename : filenames)
					filename.delete ();
				
				// Delete the main folder
				savedGameFolder.delete ();
			
				// Send back success
				sender.sendMessageToClient (new DeleteSavedGameSuccessful ());
			}
		}
	}
}