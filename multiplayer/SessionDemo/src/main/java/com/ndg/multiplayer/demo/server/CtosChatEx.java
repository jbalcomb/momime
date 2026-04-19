package com.ndg.multiplayer.demo.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.demo.CtosChat;
import com.ndg.multiplayer.demo.StocChat;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.server.session.PostSessionClientToServerMessage;

/**
 * Describes what the server needs to do when it receives a CtosChat message
 */
public final class CtosChatEx extends CtosChat implements PostSessionClientToServerMessage 
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (CtosChatEx.class);
	
	/**
	 * @param thread Thread for the session this message is for; from the thread, the processor can obtain the list of players, sd, gsk, gpl, etc
	 * @param sender Player who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerSessionThread thread, final PlayerServerDetails sender)
		throws JAXBException, XMLStreamException, IOException
	{
		// Bounce chat message back to everyone in session
		final StocChat reply = new StocChat ();
		reply.setChat (getChat ());
		reply.setSenderPlayerID (sender.getPlayerDescription ().getPlayerID ());

		for (final PlayerServerDetails thisPlayer : thread.getPlayers ())
		{
			if (thisPlayer.getConnection () == null)
				log.info ("Can't bounce chat from player ID " + sender.getPlayerDescription ().getPlayerID () + " to player ID " + thisPlayer.getPlayerDescription ().getPlayerID () + " because they are disconnected");
			else
			{
				log.info ("Bouncing chat from player ID " + sender.getPlayerDescription ().getPlayerID () + " to player ID " + thisPlayer.getPlayerDescription ().getPlayerID ());
				thisPlayer.getConnection ().sendMessageToClient (reply);
			}
		}
		
		// Save game?
		if ("save".equalsIgnoreCase (getChat ()))
			thread.saveGame (null);
		else if ("purge".equalsIgnoreCase (getChat ()))
			thread.deleteOldestSavePoints (5);		
	}
}