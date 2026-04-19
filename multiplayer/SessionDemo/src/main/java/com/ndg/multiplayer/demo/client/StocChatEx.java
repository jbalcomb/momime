package com.ndg.multiplayer.demo.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.demo.StocChat;
import com.ndg.multiplayer.session.MultiplayerSessionUtils;
import com.ndg.multiplayer.session.PlayerPublicDetails;

/**
 * Describes what the client needs to do when it receives a StocChat message
 */
public final class StocChatEx extends StocChat implements BaseServerToClientMessage
{
	/** Demo client */
	private DemoSessionClient client;
	
	/** Session utils */
	private MultiplayerSessionUtils multiplayerSessionUtils;
	
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void start () throws JAXBException, XMLStreamException, IOException
	{
		final PlayerPublicDetails from = getMultiplayerSessionUtils ().findPlayerWithID (getClient ().getPlayers (), getSenderPlayerID ());
		
		getClient ().addToTextArea (from.getPlayerDescription ().getPlayerName () + ": " + getChat ());
	}

	/**
	 * @return Demo client
	 */
	public final DemoSessionClient getClient ()
	{
		return client;
	}

	/**
	 * @param clt Demo client
	 */
	public final void setClient (final DemoSessionClient clt)
	{
		client = clt;
	}

	/**
	 * @return Session utils
	 */
	public final MultiplayerSessionUtils getMultiplayerSessionUtils ()
	{
		return multiplayerSessionUtils;
	}

	/**
	 * @param util Session utils
	 */
	public final void setMultiplayerSessionUtils (final MultiplayerSessionUtils util)
	{
		multiplayerSessionUtils = util;
	}
}