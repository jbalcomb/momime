package com.ndg.multiplayer.client.messages.process;

import java.io.IOException;
import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.client.MultiplayerSessionClient;
import com.ndg.multiplayer.client.MultiplayerSessionClientEvent;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.PlayerLeft;

/**
 * Server informing us of a player leaving our session.  This only gets sent if the server considers the player to have left.
 * If the player simply disconnected the server may keep them in the session - this behaviour is application specific
 */
public final class PlayerLeftImpl extends PlayerLeft implements BaseServerToClientMessage
{
	/** Multiplayer client */
	private MultiplayerSessionClient client;
	
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void start () throws JAXBException, XMLStreamException, IOException
	{
		for (final MultiplayerSessionClientEvent listener : getClient ().getEventListeners ())
			listener.beforePlayerLeft (getPlayerID ());
		
		// If it was us, take us out of the session too
		if (getPlayerID () == getClient ().getOurPlayerID ())
		{
			getClient ().setSessionDescription (null);
			getClient ().setGeneralPublicKnowledge (null);
			getClient ().setOurPersistentPlayerPrivateKnowledge (null);
			getClient ().setOurTransientPlayerPrivateKnowledge (null);
			
			getClient ().getPlayers ().clear ();
			
			// Leave playerID intact, we're still logged in, just no longer in a session
		}
		else
		{
			// Remove player from list, keep session intact
			final Iterator<PlayerPublicDetails> iter = getClient ().getPlayers ().iterator ();
			while (iter.hasNext ())
			{
				final PlayerPublicDetails thisPlayer = iter.next ();
				if (thisPlayer.getPlayerDescription ().getPlayerID () == getPlayerID ())
					iter.remove ();
			}
		}

		for (final MultiplayerSessionClientEvent listener : getClient ().getEventListeners ())
			listener.afterPlayerLeft (getPlayerID ());
	}

	/**
	 * @return Multiplayer client
	 */
	public final MultiplayerSessionClient getClient ()
	{
		return client;
	}

	/**
	 * @param clt Multiplayer client
	 */
	public final void setClient (final MultiplayerSessionClient clt)
	{
		client = clt;
	}
}