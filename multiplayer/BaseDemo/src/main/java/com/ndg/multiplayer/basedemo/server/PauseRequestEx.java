package com.ndg.multiplayer.basedemo.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.BaseClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.basedemo.PauseRequest;
import com.ndg.multiplayer.basedemo.PauseResponse;

/**
 * Example custom duration message, i.e. wait until a button is clicked
 */
public final class PauseRequestEx extends PauseRequest implements BaseClientToServerMessage
{
	/**
	 * @param sender Player who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerBaseServerThread sender)
		throws JAXBException, XMLStreamException, IOException
	{
		sender.sendMessageToClient (new PauseResponse ());
	}
}