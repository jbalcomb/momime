package com.ndg.multiplayer.base.debug;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Debugs incoming XML events out to a file
 */
public final class XMLEventReaderDebug implements XMLEventReader
{
	/** Underlying reader we are reading from */
	private final XMLEventReader reader;
	
	/** Writer to write debug info out to */
	private final Writer log;

	/**
	 * @param r Underlying reader we are reading from
	 * @param l Writer to write debug info out to
	 */
	public XMLEventReaderDebug (final XMLEventReader r, final Writer l)
	{
		reader = r;
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
	public final Object next ()
	{
		log ("next:");
		
		final Object o = reader.next ();
		log ("-" + o);
		
		return o;
	}
	
	/**
	 */
	@Override
	public final XMLEvent nextEvent () throws XMLStreamException
	{
		log ("nextEvent:");
		
		final XMLEvent e = reader.nextEvent ();
		log ("-" + e);
		
		return e;
	}

	/**
	 */
	@Override
	public final boolean hasNext ()
	{
		log ("hasNext:");
		
		final boolean h = reader.hasNext ();
		log ("-" + h);
		
		return h;
	}

	/**
	 */
	@Override
	public final XMLEvent peek () throws XMLStreamException
	{
		log ("peek:");
		
		final XMLEvent e = reader.peek ();
		log ("-" + e);
		
		return e;
	}

	/**
	 */
	@Override
	public final String getElementText () throws XMLStreamException
	{
		log ("getElementText:");
		
		final String s = reader.getElementText ();
		log ("-" + s);
		
		return s;
	}

	/**
	 */
	@Override
	public final XMLEvent nextTag () throws XMLStreamException
	{
		log ("nextTag");
		
		final XMLEvent e = reader.nextTag ();
		log ("-" + e);
		
		return e;
	}

	/**
	 */
	@Override
	public final Object getProperty (final String name) throws IllegalArgumentException
	{
		log ("getProperty(" + name + "):");
		
		final Object o = reader.getProperty (name);
		log ("-" + o);
		
		return o;
	}

	/**
	 */
	@Override
	public final void close () throws XMLStreamException
	{
		log ("close");
		reader.close ();
		log ("-done");
	}
}