package com.ndg.multiplayer.client.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.client.MultiplayerSessionClient;
import com.ndg.multiplayer.client.MultiplayerSessionClientEvent;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.JoinSuccessful;
import com.ndg.multiplayer.sessionbase.PlayerCombinedPublicInfo;

/**
 * Server replying that a client has successfully joined a session - can be a reply to a new, join or rejoin request.
 * 
 * Provides the list of players now in the session - this will include the player who sent the join request, but they
 * cannot assume that they will be the last player in the list, since in the case of a rejoin they will be reinserted
 * back into their original order slot.
 * 
 * Also provides to the client all their private knowledge - especially in case of rejoining after a disconnect/crash.
 */
public final class JoinSuccessfulImpl extends JoinSuccessful implements BaseServerToClientMessage
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
		getClient ().setSessionDescription (getSessionDescription ());
		getClient ().setGeneralPublicKnowledge (getGeneralPublicKnowledge ());
		getClient ().setOurPersistentPlayerPrivateKnowledge (getPersistentPlayerPrivateKnowledge ());
		getClient ().setOurTransientPlayerPrivateKnowledge (getTransientPlayerPrivateKnowledge ());
		
		getClient ().getPlayers ().clear ();
		for (final PlayerCombinedPublicInfo playerInfo : getPlayer ())
			getClient ().getPlayers ().add (new PlayerPublicDetails
				(playerInfo.getPlayerDescription (), playerInfo.getPersistentPlayerPublicKnowledge (), playerInfo.getTransientPlayerPublicKnowledge ()));
		
		for (final MultiplayerSessionClientEvent listener : getClient ().getEventListeners ())
			listener.joinedSession (getReason ());
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