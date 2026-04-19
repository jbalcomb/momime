package com.ndg.multiplayer.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.base.debug.XMLStreamWriterDebug;

/**
 * This class avoids the problem of keeping an output stream open and marshalling multiple objects onto it using JAXB
 * While the output from doing so is mostly valid, the receiving process trying to unmarshall expects to read the entire stream on the first call
 *
 * This then allows multiple objects to be marshalled down a socket stream with a time delay between each object being sent
 *
 * This is taken from the xml-channel example of the JAXB distribution
 */
public final class MultipleMarshallStream
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultipleMarshallStream.class);

	/** JAXB context that is aware of all the object types we plan to marshall onto the stream */
	private JAXBContext jaxbContext;
	
	/** Marshaller used to output objects */
	private Marshaller marshaller;

	/** Start and end tag to wrap the conversation in, defaults to 'conversation' if left as null */
	private String conversationTag;
	
	/** Stream to write objects out to */
	private OutputStream stream;
	
	/** XML stream to write objects out to */
	private XMLStreamWriter output;
	
	/** Writer to write debug info out to */
	private Writer logWriter;
	
	/** Character encoding to use on stream */
	private String encoding = StandardCharsets.UTF_8.name ();

	/** Whether to compress data */
	private boolean compress;
	
	/**
	 * @throws JAXBException If there is a problem setting up the marshaller
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is a problem setting up compression
	 */
	@SuppressWarnings ("resource")
	public final void init () throws JAXBException, XMLStreamException, IOException
	{
		// Create marshaller
		marshaller = getJaxbContext ().createMarshaller ();
		marshaller.setProperty (Marshaller.JAXB_FRAGMENT, Boolean.TRUE);	// Don't put the <xml...> tag at the start of each message

		// Compressing data?
		final OutputStream compressStream;
		if (isCompress ())
			compressStream = new GZIPOutputStream (getStream (), true);
		else
			compressStream = getStream ();
		
		// Initialize output
		final XMLStreamWriter realWriter = XMLOutputFactory.newInstance ().createXMLStreamWriter (compressStream, getEncoding ());
		if (getLogWriter () == null)
			output = realWriter;
		else
			output = new XMLStreamWriterDebug (realWriter, getLogWriter ());

        // Write the dummy start tag
		log.debug ("MultipleMarshallStream sending \"" + getConversationTag () + "\" tag");

		output.writeStartDocument (getEncoding (), "1.0");
		output.writeStartElement (getConversationTag ());
		
		// There seems to be a bug in the event writer that if all we do is write the initial handshake
		// and do a flush, all that gets sent is <conversation, missing the ending >.  So just throw an
		// empty comment out here to force the flush to work properly.
		output.writeComment (null);
		output.flush ();
	}

	/**
	 * @param obj Object to add to the stream
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public final void writeObject (final Object obj) throws JAXBException, XMLStreamException
	{
		marshaller.marshal (obj, output);
		output.flush ();	// Send it immediately
	}

	/**
	 * @throws XMLStreamException If there is a problem writing to or closing the XML stream
	 */
	public final void close () throws XMLStreamException
	{
		output.writeEndElement ();
		output.writeEndDocument ();
		output.flush ();
		output.close ();
	}

	/**
	 * @return JAXB context that is aware of all the object types we plan to marshall onto the stream
	 */
	public final JAXBContext getJaxbContext ()
	{
		return jaxbContext;
	}

	/**
	 * @param context JAXB context that is aware of all the object types we plan to marshall onto the stream
	 */
	public final void setJaxbContext (final JAXBContext context)
	{
		jaxbContext = context;
	}

	/**
	 * @return Stream to write objects out to
	 */
	public final OutputStream getStream ()
	{
		return stream;
	}

	/**
	 * @param obj Stream to write objects out to
	 */
	public final void setStream (final OutputStream obj)
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
	 * @return Character encoding to use on stream
	 */
	public final String getEncoding ()
	{
		return encoding;
	}

	/**
	 * @param e Character encoding to use on stream
	 */
	public final void setEncoding (final String e)
	{
		encoding = e;
	}

	/**
	 * @return Whether to compress data
	 */
	public final boolean isCompress ()
	{
		return compress;
	}

	/**
	 * @param c Whether to compress data
	 */
	public final void setCompress (final boolean c)
	{
		compress = c;
	}
}