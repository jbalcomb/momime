package com.ndg.utils;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Workaround for problem with SchemaFactory.setResourceResolver with JDK 17.
 * SchemaFactory is an abstract class, the actual impl is an internal sun class.
 * 
 * When we create a SchemaFactory and try to call setResourceResolver on it in Spring,
 * Spring finds the impl of the method on the internal class and tries to call it directly,
 * which is forbidden with JDK 17.  So as long as don't let Spring call setResourceResolver, we are fine. 
 */
public final class SchemaFactoryFactory
{
	/**
	 * @param resourceResolver Resource resolver to set on the created schema factory
	 * @return Newly created schema factory
	 */
	public final SchemaFactory createSchemaFactory (final LSResourceResolver resourceResolver)
	{
		final SchemaFactory factory = SchemaFactory.newInstance ("http://www.w3.org/2001/XMLSchema");
		factory.setResourceResolver (resourceResolver);
		
		return factory;
	}
	
	/**
	 * For some reason, with JDK17 can access the other SchemaFactory.newSchema methods, but not the one that takes multiple schemas as input
	 * 
	 * @param factory Schema factory
	 * @param schemas List of schemas to combine
	 * @return Newly created schema
	 * @throws SAXException If there is a problem
	 */
	public final Schema newSchema (final SchemaFactory factory, final Source [] schemas) throws SAXException
	{
		return factory.newSchema (schemas);
	}
}