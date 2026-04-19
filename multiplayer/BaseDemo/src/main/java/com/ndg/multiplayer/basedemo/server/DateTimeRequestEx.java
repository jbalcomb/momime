package com.ndg.multiplayer.basedemo.server;

import java.io.IOException;
import java.util.GregorianCalendar;

import jakarta.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.server.BaseClientToServerMessage;
import com.ndg.multiplayer.base.server.MultiplayerBaseServerThread;
import com.ndg.multiplayer.basedemo.DateTimeRequest;
import com.ndg.multiplayer.basedemo.DateTimeResponse;

/**
 * Respond to client with the date/time when they ask for it
 */
public final class DateTimeRequestEx extends DateTimeRequest implements BaseClientToServerMessage
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
		final DatatypeFactory factory;
		try
		{
			factory = DatatypeFactory.newInstance ();
		}
		catch (final DatatypeConfigurationException e)
		{
			throw new IOException (e.getMessage (), e);
		}
		
		final DateTimeResponse response = new DateTimeResponse ();
		response.setDateTime (factory.newXMLGregorianCalendar (new GregorianCalendar ()));
		
		sender.sendMessageToClient (response);
	}
}
