package com.ndg.multiplayer.basedemo.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.BaseServerToClientMessage;
import com.ndg.multiplayer.basedemo.DateTimeResponse;

/**
 * Server telling us what time it is 
 */
public final class DateTimeResponseEx extends DateTimeResponse implements BaseServerToClientMessage
{
	/** Demo client */
	private MultiplayerDemoClient client;
	
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void start () throws JAXBException, XMLStreamException, IOException
	{
		getClient ().addToTextArea ("Server says time is now: " + getDateTime ().toString ());
	}

	/**
	 * @return Demo client
	 */
	public final MultiplayerDemoClient getClient ()
	{
		return client;
	}

	/**
	 * @param obj Demo client
	 */
	public final void setClient (final MultiplayerDemoClient obj)
	{
		client = obj;
	}
}