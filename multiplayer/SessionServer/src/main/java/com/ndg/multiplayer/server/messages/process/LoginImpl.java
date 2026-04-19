package com.ndg.multiplayer.server.messages.process;

import java.io.IOException;
import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.server.MultiplayerClientConnection;
import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.PreSessionClientToServerMessage;
import com.ndg.multiplayer.server.messages.LoginSuccessfulCollectPlayerData;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.KickedByAnotherLogin;
import com.ndg.multiplayer.sessionbase.Login;
import com.ndg.multiplayer.sessionbase.LoginFailed;
import com.ndg.multiplayer.sessionbase.LoginFailedReason;
import com.ndg.multiplayer.sessionbase.LoginSuccessful;

/**
 * Authenticates player's username and password with the server user registry
 */
public final class LoginImpl extends Login implements PreSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (LoginImpl.class);
	
	/** Server-side session utils */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;
	
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
		if (sender.getPlayerID () > 0)
		{
			// Already logged in
			final LoginFailed failed = new LoginFailed ();
			failed.setReason (LoginFailedReason.YOUR_CONNECTION_IS_ALREADY_LOGGED_IN);
			sender.sendMessageToClient (failed);
		}
		else
		{
			final int playerID = server.getUserRegistry ().authenticateUser (getPlayerName (), getPlayerPassword ());
			if (playerID <= 0)
			{
				// Username or password incorrect
				final LoginFailed failed = new LoginFailed ();
				failed.setReason (LoginFailedReason.PLAYER_NAME_OR_PASSWORD_NOT_RECOGNIZED);
				sender.sendMessageToClient (failed);
			}
			else
			{
				synchronized (server.getClientConnections ())
				{
					MultiplayerClientConnection anotherConnection = null;
					final Iterator<MultiplayerBaseServerThread> iter = server.getClientConnections ().iterator ();
					while ((anotherConnection == null) && (iter.hasNext ()))
					{
						final MultiplayerClientConnection thisConnection = (MultiplayerClientConnection) iter.next ();
						if (thisConnection.getPlayerID () == playerID)
							anotherConnection = thisConnection;
					}

					if (anotherConnection != null)
					{
						// Another connection is already logged in with this Player ID - what we do here depends on whether the kick option was selected
						if ((isKickExistingConnection () != null) && (isKickExistingConnection ()))
						{
							// Another connection is already logged in with this Player ID - kick them
							anotherConnection.sendMessageToClient (new KickedByAnotherLogin ());
							
							if (anotherConnection.getPlayer () != null)
								anotherConnection.getPlayer ().setConnection (null);
							
							anotherConnection.setPlayerID (0);
							anotherConnection.setSession (null);
							anotherConnection.setPlayer (null);

							log.info (sender.getRemoteSocketAddress () + " is kicking previous connection from " + anotherConnection.getRemoteSocketAddress () +
								" off from using player ID " + playerID + " with name \"" + getPlayerName () + "\"");

							anotherConnection = null;
						}
						else
						{
							// Another connection is already logged in with this Player ID - just fail the request
							final LoginFailed failed = new LoginFailed ();
							failed.setReason (LoginFailedReason.ANOTHER_CONNECTION_USING_YOUR_PLAYER_ID);
							sender.sendMessageToClient (failed);
						}
					}

					if (anotherConnection == null)
					{
						log.info (sender.getRemoteSocketAddress () + " logged in as player ID " + playerID + " with name \"" + getPlayerName () + "\"");

						// Success
						sender.setPlayerID (playerID);

						final LoginSuccessful success = new LoginSuccessful ();
						success.setPlayerID (playerID);
						success.setPlayerName (getPlayerName ());

						// Find only sessions this player is already a member of
						synchronized (server.getSessions ())
						{
							// If there are no sessions, just send message from here, otherwise the final session to collect its data will send it
							if (server.getSessions ().size () == 0)
							{
								sender.sendMessageToClient (success);
								server.playerLoggedIn (playerID, sender);
							}
							else
							{
								final LoginSuccessfulCollectPlayerData collectData = new LoginSuccessfulCollectPlayerData ();
								collectData.setMessage (success);
								collectData.setSessionThreadsRemaining (server.getSessions ().size ());
								collectData.setMultiplayerSessionServerUtils (getMultiplayerSessionServerUtils ());
								
								for (final MultiplayerSessionThread thisSession : server.getSessions ())
									thisSession.addMessageToQueue (collectData, sender);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @return Server-side session utils
	 */
	public final MultiplayerSessionServerUtils getMultiplayerSessionServerUtils ()
	{
		return multiplayerSessionServerUtils;
	}

	/**
	 * @param utils Server-side session utils
	 */
	public final void setMultiplayerSessionServerUtils (final MultiplayerSessionServerUtils utils)
	{
		multiplayerSessionServerUtils = utils;
	}
}