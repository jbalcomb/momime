package com.ndg.multiplayer.basedemo.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.BaseClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.basedemo.CounterRequest;
import com.ndg.multiplayer.basedemo.CounterResponse;

/**
 * Example animation which ticks up and finishes by itself (or not, depending on pauseAfterwards setting)
 */
public final class CounterRequestEx extends CounterRequest implements BaseClientToServerMessage
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
		final CounterResponse resp = new CounterResponse ();
		resp.setNumberOfSeconds (getNumberOfSeconds ());
		resp.setPauseAfterwards (isPauseAfterwards ());
		
		sender.sendMessageToClient (resp);
	}
}