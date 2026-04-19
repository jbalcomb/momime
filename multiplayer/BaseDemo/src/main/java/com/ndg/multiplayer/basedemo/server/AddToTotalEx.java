package com.ndg.multiplayer.basedemo.server;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.BaseClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.basedemo.AddToTotal;
import com.ndg.multiplayer.basedemo.TotalSoFar;

/**
 * Add number to running total for this connection, to demonstrate doing something that maintains state against the connection thread
 */
public final class AddToTotalEx extends AddToTotal implements BaseClientToServerMessage
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
		final MultiplayerDemoServerThread thread = (MultiplayerDemoServerThread) sender;
		
		thread.totalSoFar = thread.totalSoFar + getValue ();
		
		final TotalSoFar response = new TotalSoFar ();
		response.setTotal (thread.totalSoFar);
		sender.sendMessageToClient (response);
	}
}
