package com.ndg.multiplayer.client.messages.process;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.client.MultiplayerSessionClient;
import com.ndg.multiplayer.client.MultiplayerSessionClientEvent;
import com.ndg.multiplayer.sessionbase.LogoutFailed;

/**
 * Server explaining why logout attempt failed
 */
public final class LogoutFailedImpl extends LogoutFailed implements BaseServerToClientMessage
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
			listener.failed (null, null, getReason (), null, null, null, null, null, null, null);
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