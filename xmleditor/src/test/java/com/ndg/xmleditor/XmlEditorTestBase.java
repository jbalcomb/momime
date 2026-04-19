package com.ndg.xmleditor;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.w3._2001.xmlschema.Schema;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.ndg.utils.XmlConstants;
import com.ndg.xmleditor.schema.ObjectFactoryEx;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

/**
 * Helper methods for XML Editor unit tests
 */
public class XmlEditorTestBase
{
	/**
	 * There's a lot of plumbing involved to set up the Unmarshaller correctly to read in the test XSD files, so declared here so we don't duplicate this in every test.
	 * @return Unmarshaller for reading .XSD files
	 * @throws Exception If there is a problem
	 */
	public final Unmarshaller createSchemaUnmarshaller () throws Exception
	{
		final DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance ();
		
		final XmlResourceResolver resourceResolver = new XmlResourceResolver ();
		resourceResolver.setDomImplementationRegistry (reg);
		
		final URL xsdURL = getClass ().getResource ("/com.ndg.xmleditor.schema/XMLSchema.xsd");
		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (resourceResolver);
		
		final javax.xml.validation.Schema schema = schemaFactory.newSchema (xsdURL);
		
		final Unmarshaller unmarshaller = JAXBContext.newInstance (Schema.class).createUnmarshaller ();
		unmarshaller.setSchema (schema);
		unmarshaller.setProperty (XmlConstants.OBJECT_FACTORY, new Object [] {new ObjectFactoryEx ()});
		
		return unmarshaller;
	}
}