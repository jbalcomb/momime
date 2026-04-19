package com.ndg.multiplayer.base.exception;

import javax.xml.stream.XMLStreamException;

/**
 * Type of exception where we expect a particular kind of event next on the XML stream, but get something else.
 * For the event type int values, see XMLStreamConstants.
 */
public class XMLUnexpectedNextEvent extends XMLStreamException
{
	/** Type of event we expected next on the streams */
	private final int expectedEventType;
	
	/** Type of event we actually got */
	private final int actualEventType;
	
	/**
	 * @param msg Error message
	 * @param anExpectedEventType Type of event we expected next on the streams
	 * @param anActualEventType Type of event we actually got
	 */
	public XMLUnexpectedNextEvent (final String msg, final int anExpectedEventType, final int anActualEventType)
	{
		super (msg);
		expectedEventType = anExpectedEventType;
		actualEventType = anActualEventType;
	}

	/**
	 * @return Type of event we expected next on the streams
	 */
	public final int getExpectedEventType ()
	{
		return expectedEventType;
	}
	
	/**
	 * @return Type of event we actually got
	 */
	public final int getActualEventType ()
	{
		return actualEventType;
	}
}