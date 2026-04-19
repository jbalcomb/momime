package com.ndg.multiplayer.baseunittest.extension;

import java.io.IOException;

import javax.swing.SwingUtilities;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.baseunittest.Response;

/**
 * Example client-side message implementation
 */
public final class ResponseEx extends Response implements BaseServerToClientMessage
{
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void start ()
		throws JAXBException, XMLStreamException, IOException
	{
		System.out.println ("Server sent reply: " + getText () + ", is Swing thread: " + SwingUtilities.isEventDispatchThread ());
	}
}