package com.ndg.multiplayer.client.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.client.MultiplayerSessionClient;
import com.ndg.multiplayer.client.MultiplayerSessionClientEvent;
import com.ndg.multiplayer.sessionbase.SessionEnding;

/**
 * Server informing us that the session we are currently in is ending (TBD the conditions under which this happens)
 */
public final class SessionEndingImpl extends SessionEnding implements BaseServerToClientMessage
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
			listener.sessionEnding ();
		
		getClient ().setSessionDescription (null);
		getClient ().setGeneralPublicKnowledge (null);
		getClient ().setOurPersistentPlayerPrivateKnowledge (null);
		getClient ().setOurTransientPlayerPrivateKnowledge (null);
		
		getClient ().getPlayers ().clear ();
		
		// Keep player ID - we're out of the session, but still logged in
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