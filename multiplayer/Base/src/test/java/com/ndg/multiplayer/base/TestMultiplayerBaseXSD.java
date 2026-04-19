package com.ndg.multiplayer.base;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;

/**
 * Proves that the Multiplayer Base XSD can be found and read
 */
@ExtendWith(MockitoExtension.class)
public final class TestMultiplayerBaseXSD
{
	/**
	 * Proves that the Multiplayer Base XSD can be found and read
	 * @throws SAXException If there is a problem parsing the XSD
	 */
	@Test
	public final void testMultiplayerBaseXSD () throws SAXException
	{
		final URL xsdResource = getClass ().getResource (MultiplayerBaseConstants.MULTIPLAYER_BASE_XSD_LOCATION);
		assertNotNull (xsdResource, "Multiplayer Base XSD could not be found on classpath");

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.newSchema (xsdResource).newValidator ();
	}
}
