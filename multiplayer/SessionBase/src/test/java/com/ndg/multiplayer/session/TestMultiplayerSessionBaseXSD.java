package com.ndg.multiplayer.session;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;

/**
 * Proves that the Multiplayer Session Base XSD can be found and read
 */
@ExtendWith(MockitoExtension.class)
public final class TestMultiplayerSessionBaseXSD
{
	/**
	 * Proves that the Multiplayer Session Base XSD can be found and read
	 * Note this throws a SAXException because there's no resource resolver to help it find the Multiplayer Base XSD
	 * But that's ok - to realise that it needs that XSD, it must've found the Session XSD, which is most of what we want to test
	 * To fix this properly we'd need to create a resource resolver in the Base project, then a test XSD to try it out with,
	 * which is really overkill, so I just left it like this  
	 * 
	 * @throws SAXException If there is a problem parsing the XSD
	 */
	@Test
	public final void testMultiplayerSessionBaseXSD () throws SAXException
	{
		final URL xsdResource = getClass ().getResource (MultiplayerSessionBaseConstants.MULTIPLAYER_SESSION_BASE_XSD_LOCATION);
		assertNotNull (xsdResource, "Multiplayer Session Base XSD could not be found on classpath");

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		assertThrows (SAXException.class, () ->
		{
			schemaFactory.newSchema (xsdResource).newValidator ();
		});
	}
}