package com.ndg.multiplayer.baseunittest.extension;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.BaseClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.baseunittest.Request;
import com.ndg.multiplayer.baseunittest.Response;

/**
 * Example server-side message implementation
 */
public final class RequestEx extends Request implements BaseClientToServerMessage
{
	/**
	 * @param sender Client who sent the message
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the client
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the client
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void process (final MultiplayerBaseServerThread sender)
		throws JAXBException, XMLStreamException, IOException
	{
		System.out.println ("Server received request with count " + getCount ());

		final Response resp = new Response ();
		resp.setText ("Response " + getCount ());
		sender.sendMessageToClient (resp);
	}
}
