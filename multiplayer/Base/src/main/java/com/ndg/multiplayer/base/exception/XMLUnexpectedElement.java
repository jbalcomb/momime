package com.ndg.multiplayer.base.exception;

import javax.xml.stream.XMLStreamException;

/**
 * Type of exception where we expect a particular element but get a different one 
 */
public class XMLUnexpectedElement extends XMLStreamException
{
	/** Element we expected */
	private final String expectedElement;
	
	/** Element we actually got */
	private final String actualElement;
	
	/** Whether or not the element in question is the start conversation tag */
	private final boolean startConversationTag;
	
	/**
	 * @param msg Error message
	 * @param anExpectedElement Element we expected
	 * @param anActualElement Element we actually got
	 * @param isStartConversationTag Whether or not the element in question is the start conversation tag
	 */
	public XMLUnexpectedElement (final String msg, final String anExpectedElement, final String anActualElement, final boolean isStartConversationTag)
	{
		super (msg);
		expectedElement = anExpectedElement;
		actualElement = anActualElement;
		startConversationTag = isStartConversationTag;
	}

	/**
	 * @return Element we expected
	 */
	public final String getExpectedElement ()
	{
		return expectedElement;
	}
	
	/**
	 * @return Element we actually got
	 */
	public final String getActualElement ()
	{
		return actualElement;
	}

	/**
	 * @return Whether or not the element in question is the start conversation tag
	 */
	public final boolean isStartConversationTag ()
	{
		return startConversationTag;
	}
}