package com.ndg.multiplayer.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.debug.XMLEventReaderDebug;
import com.ndg.multiplayer.base.exception.XMLUnexpectedElement;
import com.ndg.multiplayer.base.exception.XMLUnexpectedNextEvent;
import com.ndg.utils.XmlConstants;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * See comments in MultipleMarshallStream
 */
public final class MultipleUnmarshallStream
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultipleUnmarshallStream.class);

	/** JAXB context that is aware of all the object types we expect to unmarshall from the stream */
	private JAXBContext jaxbContext;
	
	/** Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave as null if not required */
	private Object [] objectFactoryArray;
	
	/** Unmarshaller used to read objects */
	private Unmarshaller unmarshaller;

	/** Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null */
	private String conversationTag;
	
	/** Stream to read objects from */
	private InputStream stream;
	
	/** XML stream to read objects from */
	private XMLEventReader input;

	/** Writer to write debug info out to */
	private Writer logWriter;
	
	/** Character encoding to expect on stream */
	private String encoding = StandardCharsets.UTF_8.name ();
	
	/** Whether to decompress data */
	private boolean decompress;
	
	/**
	 * @throws JAXBException If there is a problem setting up the unmarshaller
	 * @throws XMLStreamException If there is a problem reading from the XML stream
	 * @throws IOException If there is a problem setting up decompression
	 */
	@SuppressWarnings ("resource")
	public final void init () throws JAXBException, XMLStreamException, IOException
	{
		// Create unmarshaller
		unmarshaller = getJaxbContext ().createUnmarshaller ();
		if (getObjectFactoryArray () != null)
			unmarshaller.setProperty (XmlConstants.OBJECT_FACTORY, getObjectFactoryArray ());

		// Decompressing data?
		final InputStream decompressStream;
		if (isDecompress ())
			decompressStream = new GZIPInputStream (getStream ());
		else
			decompressStream = getStream ();
		
		// Initialize intput
		final XMLEventReader realReader = XMLInputFactory.newInstance ().createXMLEventReader (decompressStream, getEncoding ());
		if (getLogWriter () == null)
			input = realReader;
		else
			input = new XMLEventReaderDebug (realReader, getLogWriter ());

		// Wait for the initial handshake
		log.debug ("MultipleUnmarshallStream waiting for \"" + getConversationTag () + "\" tag");
		input.nextEvent ();												// read the start document
		final XMLEvent startTag = input.nextEvent ();		// get to the first <conversation> tag
		if (!startTag.isStartElement ())
			throw new XMLUnexpectedNextEvent ("MultipleUnmarshallStream expected first event to be a start element but was type " + startTag.getEventType (),
				XMLStreamConstants.START_ELEMENT, startTag.getEventType ());

		final StartElement startElement = (StartElement) startTag;
		if (!getConversationTag ().equals (startElement.getName ().getLocalPart ()))
			throw new XMLUnexpectedElement ("MultipleUnmarshallStream expected first start element to be \"" +
				getConversationTag () + "\" but was \"" + startElement.getName ().getLocalPart () + "\"",
				getConversationTag (), startElement.getName ().getLocalPart (), true);
		
		// Read the comment padding after the handshake (see comments in MultipleMarshallStream.init)
		final XMLEvent comment = input.nextEvent ();
		if (comment.getEventType () != XMLStreamConstants.COMMENT)
			throw new XMLUnexpectedNextEvent ("MultipleUnmarshallStream expected first event after handshake to be padding comment but was a type " + comment.getEventType (),
				XMLStreamConstants.COMMENT, comment.getEventType ());
	}

	/**
	 * @return Next object read from the stream, or null if the end of the stream has been reached
	 * @throws JAXBException If there is a problem converting the object from XML
	 * @throws XMLStreamException If there is a problem reading from the XML stream
	 */
	public final Object readObject () throws JAXBException, XMLStreamException
	{
		// Wait for next message, or end of stream marker
		final Object obj;
		if (input.peek ().isStartElement ())
		{
			// If it is a start element, then its the start of a new object, so return it
			obj = unmarshaller.unmarshal (input);
		}
		else
		{
			// If it is anything other than a start element, then expect the closing conversation tag
			final XMLEvent endTag = input.nextEvent ();
			if (!endTag.isEndElement ())
				throw new XMLUnexpectedNextEvent ("MultipleUnmarshallStream expected final event to be an end element but was a type " + endTag.getEventType (),
					XMLStreamConstants.END_ELEMENT, endTag.getEventType ());

			final EndElement endElement = (EndElement) endTag;
			if (!getConversationTag ().equals (endElement.getName ().getLocalPart ()))
				throw new XMLUnexpectedElement ("MultipleUnmarshallStream expected final end element to be \"" +
					getConversationTag () + "\" but was \"" + endElement.getName ().getLocalPart () + "\"",
					getConversationTag (), endElement.getName ().getLocalPart (), false);

			// End of stream reached normally
			input.close ();
			obj = null;
		}

		return obj;
	}

	/**
	 * @return JAXB context that is aware of all the object types we expect to unmarshall from the stream
	 */
	public final JAXBContext getJaxbContext ()
	{
		return jaxbContext;
	}

	/**
	 * @param context JAXB context that is aware of all the object types we expect to unmarshall from the stream
	 */
	public final void setJaxbContext (final JAXBContext context)
	{
		jaxbContext = context;
	}

	/**
	 * @return Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave as null if not required
	 */
	public final Object [] getObjectFactoryArray ()
	{
		return objectFactoryArray;
	}

	/**
	 * @param facs Array of object factory overrides if we want to create subclasses of any of the unmarshalled types; can leave as null if not required
	 */
	public final void setObjectFactoryArray (final Object [] facs)
	{
		objectFactoryArray = facs;
	}
	
	/**
	 * @return Stream to read objects from
	 */
	public final InputStream getStream ()
	{
		return stream;
	}

	/**
	 * @param obj Stream to read objects from
	 */
	public final void setStream (final InputStream obj)
	{
		stream = obj;
	}
	
	/**
	 * @return Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null
	 */
	public final String getConversationTag ()
	{
		return (conversationTag == null) ? "conversation" : conversationTag;
	}

	/**
	 * @param tag Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null
	 */
	public final void setConversationTag (final String tag)
	{
		conversationTag = tag;
	}

	/**
	 * @return Writer to write debug info out to
	 */
	public final Writer getLogWriter ()
	{
		return logWriter;
	}

	/**
	 * @param w Writer to write debug info out to
	 */
	public final void setLogWriter (final Writer w)
	{
		logWriter = w;
	}

	/**
	 * @return Character encoding to expect on stream
	 */
	public final String getEncoding ()
	{
		return encoding;
	}

	/**
	 * @param e Character encoding to expect on stream
	 */
	public final void setEncoding (final String e)
	{
		encoding = e;
	}

	/**
	 * @return Whether to decompress data
	 */
	public final boolean isDecompress ()
	{
		return decompress;
	}

	/**
	 * @param d Whether to decompress data
	 */
	public final void setDecompress (final boolean d)
	{
		decompress = d;
	}
}