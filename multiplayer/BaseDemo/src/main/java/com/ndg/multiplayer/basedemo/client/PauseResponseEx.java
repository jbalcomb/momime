package com.ndg.multiplayer.basedemo.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.CustomDurationServerToClientMessage;
import com.ndg.multiplayer.basedemo.PauseResponse;

/**
 * Example custom duration message, i.e. wait until a button is clicked
 */
public final class PauseResponseEx extends PauseResponse implements CustomDurationServerToClientMessage
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
		getClient ().addToTextArea ("Paused");
		getClient ().setPausedBy (this);
	}

	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void finish () throws JAXBException, XMLStreamException, IOException
	{
		getClient ().addToTextArea ("Unpaused");
		getClient ().setPausedBy (null);
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