package com.ndg.multiplayer.base.debug;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Debugs outgoing XML events out to a file
 */
public final class XMLStreamWriterDebug implements XMLStreamWriter
{
	/** Underlying writer we are writing to */
	private final  XMLStreamWriter writer;
	
	/** Writer to write debug info out to */
	private final Writer log;

	/**
	 * @param w Underlying writer we are writing to
	 * @param l Writer to write debug info out to
	 */
	public XMLStreamWriterDebug (final XMLStreamWriter w, final Writer l)
	{
		writer = w;
		log = l;
	}

	/**
	 * @param text Text to write to the log
	 */
	private final void log (final String text)
	{
		try
		{
			log.write (text + System.lineSeparator ());
			log.flush ();
		}
		catch (final IOException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 */
	@Override
	public final void writeStartElement (final String localName) throws XMLStreamException
	{
		log ("writeStartElement(" + localName + "):");
		writer.writeStartElement (localName);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeStartElement (final String namespaceURI, final String localName) throws XMLStreamException
	{
		log ("writeStartElement(" + localName + ", " + namespaceURI + "):");
		writer.writeStartElement (localName, namespaceURI);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeStartElement (final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
	{
		log ("writeStartElement(" + prefix + ", " + localName + ", " + namespaceURI + "):");
		writer.writeStartElement (prefix, localName, namespaceURI);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEmptyElement (final String namespaceURI, final String localName) throws XMLStreamException
	{
		log ("writeEmptyElement(" + namespaceURI + ", " + localName + "):");
		writer.writeEmptyElement (namespaceURI, localName);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEmptyElement (final String prefix, final String localName, final String namespaceURI) throws XMLStreamException
	{
		log ("writeEmptyElement(" + prefix + ", " + namespaceURI + ", " + localName + "):");
		writer.writeEmptyElement (prefix, namespaceURI, localName);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEmptyElement (final String localName) throws XMLStreamException
	{
		log ("writeEmptyElement(" + localName + "):");
		writer.writeEmptyElement (localName);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEndElement () throws XMLStreamException
	{
		log ("writeEndElement:");
		writer.writeEndElement ();
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEndDocument () throws XMLStreamException
	{
		log ("writeEndDocument:");
		writer.writeEndDocument ();
		log ("-done");
	}

	/**
	 */
	@Override
	public final void close () throws XMLStreamException
	{
		log ("close:");
		writer.close ();
		log ("-done");
	}

	/**
	 */
	@Override
	public final void flush () throws XMLStreamException
	{
		log ("flush:");
		writer.flush ();
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeAttribute (final String localName, final String value) throws XMLStreamException
	{
		log ("writeAttribute(" + localName + ", " + value + "):");
		writer.writeAttribute (localName, value);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeAttribute (final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException
	{
		log ("writeAttribute(" + prefix + ", " + namespaceURI + ", " + localName + ", " + value + "):");
		writer.writeAttribute (prefix, namespaceURI, localName, value);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeAttribute (final String namespaceURI, final String localName, final String value) throws XMLStreamException
	{
		log ("writeAttribute(" + namespaceURI + ", " + localName + ", " + value + "):");
		writer.writeAttribute (namespaceURI, localName, value);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeNamespace (final String prefix, final String namespaceURI) throws XMLStreamException
	{
		log ("writeNamespace(" + prefix + ", " + namespaceURI + "):");
		writer.writeNamespace (prefix, namespaceURI);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeDefaultNamespace (final String namespaceURI) throws XMLStreamException
	{
		log ("writeDefaultNamespace(" + namespaceURI + "):");
		writer.writeDefaultNamespace (namespaceURI);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeComment (final String data) throws XMLStreamException
	{
		log ("writeComment(" + data + "):");
		writer.writeComment (data);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeProcessingInstruction (final String target) throws XMLStreamException
	{
		log ("writeProcessingInstruction(" + target + "):");
		writer.writeProcessingInstruction (target);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeProcessingInstruction (final String target, final String data) throws XMLStreamException
	{
		log ("writeProcessingInstruction(" + target + ", " + data + "):");
		writer.writeProcessingInstruction (target, data);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeCData (final String data) throws XMLStreamException
	{
		log ("writeCData(" + data + "):");
		writer.writeCData (data);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeDTD (final String dtd) throws XMLStreamException
	{
		log ("writeDTD(" + dtd + "):");
		writer.writeDTD (dtd);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeEntityRef (final String name) throws XMLStreamException
	{
		log ("writeEntityRef(" + name + "):");
		writer.writeEntityRef (name);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeStartDocument () throws XMLStreamException
	{
		log ("writeStartDocument:");
		writer.writeStartDocument ();
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeStartDocument (final String version) throws XMLStreamException
	{
		log ("writeStartDocument(" + version + "):");
		writer.writeStartDocument (version);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeStartDocument (final String encoding, final String version) throws XMLStreamException
	{
		log ("writeStartDocument(" + encoding + ", " + version + "):");
		writer.writeStartDocument (encoding, version);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeCharacters (final String text) throws XMLStreamException
	{
		log ("writeCharacters(" + text + "):");
		writer.writeCharacters (text);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void writeCharacters (final char [] text, final int start, final int len) throws XMLStreamException
	{
		log ("writeCharacters(" + Arrays.toString (text) + ", " + start + ", " + len + "):");
		writer.writeCharacters (text, start, len);
		log ("-done");
	}

	/**
	 */
	@Override
	public final String getPrefix (final String uri) throws XMLStreamException
	{
		log ("getPrefix(" + uri + "):");
		
		final String s = writer.getPrefix (uri);
		log ("-" + s);
		
		return s;
	}

	/**
	 */
	@Override
	public final void setPrefix (final String prefix, final String uri) throws XMLStreamException
	{
		log ("setPrefix(" + prefix + ", " + uri + "):");
		writer.setPrefix (prefix, uri);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void setDefaultNamespace (final String uri) throws XMLStreamException
	{
		log ("setDefaultNamespace(" + uri + "):");
		writer.setDefaultNamespace (uri);
		log ("-done");
	}

	/**
	 */
	@Override
	public final void setNamespaceContext (final NamespaceContext context) throws XMLStreamException
	{
		log ("setNamespaceContext(" + context + "):");
		writer.setNamespaceContext (context);
		log ("-done");
	}

	/**
	 */
	@Override
	public final NamespaceContext getNamespaceContext ()
	{
		log ("getNamespaceContext:");
		
		final NamespaceContext ns = writer.getNamespaceContext ();
		log ("-" + ns);
		
		return ns;
	}

	/**
	 */
	@Override
	public final Object getProperty (final String name) throws IllegalArgumentException
	{
		log ("getProperty(" + name + "):");
		
		final Object o = writer.getProperty (name);
		log ("-" + o);
		
		return o;
	}
}