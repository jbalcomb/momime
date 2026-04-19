package com.ndg.multiplayer.server.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.ndg.multiplayer.session.MultiplayerSessionXsdResourceResolver;
import com.ndg.multiplayer.userregistry.Player;
import com.ndg.multiplayer.userregistry.UserRegistry;

/**
 * Tests the MultiplayerSessionUserRegistryImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class MultiplayerSessionUserRegistryImplTest
{
	/**
	 * Tests the newUserRegistry method
	 */
	@Test
	public final void testNewUserRegistry ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		
		// Set up a new registry, put a user in it, and verify that the user is present
		reg.newUserRegistry ();
		assertEquals (1, reg.createAccount ("first player", "first password"));
		assertEquals ("first player", reg.findPlayerByID (1).getPlayerName ());
		
		// Now if we prove that newUserRegistry blanks it out
		reg.newUserRegistry ();
		assertNull (reg.findPlayerByID (1));
	}
	
	/**
	 * Tests the saveUserRegistry and loadUserRegistry methods
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSaveAndLoadUserRegistry () throws Exception
	{
		// Set up schema factory
		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (new MultiplayerSessionXsdResourceResolver (DOMImplementationRegistry.newInstance ()));

		// Set up marshaller
		final JAXBContext userRegistryContext = JAXBContext.newInstance (UserRegistry.class);

		final Marshaller userRegistryMarshaller = userRegistryContext.createMarshaller ();
		userRegistryMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
		// Set up unmarshaller
		// Use XSD to validate user registry 
		final URL xsdURL = getClass ().getResource ("/com.ndg.multiplayer.userregistry/MultiplayerSessionUserRegistry.xsd");
		if (xsdURL == null)
			throw new IOException ("Could not locate user registry XSD on classpath");

		final Schema schema = schemaFactory.newSchema (xsdURL);
				
		final Unmarshaller userRegistryUnmarshaller = userRegistryContext.createUnmarshaller ();
		userRegistryUnmarshaller.setSchema (schema);
		
		// Generate a temporary filename to test with
		final File xmlFilename = File.createTempFile ("MultiplayerSessionUserRegistryImplTest_testSaveAndLoadUserRegistry", ".xml");
		xmlFilename.delete ();	// We don't actually *want* the temporary file, we just want a temporary name allocated

		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setXmlFilename (xmlFilename);
		reg.setUserRegistryMarshaller (userRegistryMarshaller);
		reg.setUserRegistryUnmarshaller (userRegistryUnmarshaller);
		reg.setUserRegistryObjectFactory (() -> new Player ());
		
		// Set up a new registry, put a user in it, and verify that the user is present
		reg.newUserRegistry ();
		assertEquals (1, reg.createAccount ("first player", "first password"));
		assertEquals ("first player", reg.findPlayerByID (1).getPlayerName ());
		
		// Save the registry to a file
		reg.saveUserRegistry ();
		
		// Blank the registry out again
		reg.newUserRegistry ();
		assertNull (reg.findPlayerByID (1));
		
		// Reload it, and prove that the user was loaded correctly
		reg.loadUserRegistry ();
		assertEquals ("first player", reg.findPlayerByID (1).getPlayerName ());
		
		// Tidy up temporary file
		xmlFilename.delete ();
	}

	/**
	 * Proves that saveUserRegistry can updating existing user registries by overwriting them with no warning
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSaveUserRegistry_AlreadyExists () throws Exception
	{
		// Set up schema factory
		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (new MultiplayerSessionXsdResourceResolver (DOMImplementationRegistry.newInstance ()));
		
		// Set up marshaller
		final JAXBContext userRegistryContext = JAXBContext.newInstance (UserRegistry.class);

		final Marshaller userRegistryMarshaller = userRegistryContext.createMarshaller ();
		userRegistryMarshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
		// Set up unmarshaller
		// Use XSD to validate user registry 
		final URL xsdURL = getClass ().getResource ("/com.ndg.multiplayer.userregistry/MultiplayerSessionUserRegistry.xsd");
		if (xsdURL == null)
			throw new IOException ("Could not locate user registry XSD on classpath");

		final Schema schema = schemaFactory.newSchema (xsdURL);
				
		final Unmarshaller userRegistryUnmarshaller = userRegistryContext.createUnmarshaller ();
		userRegistryUnmarshaller.setSchema (schema);
		
		// Generate a temporary filename to test with, and don't delete it
		final File xmlFilename = File.createTempFile ("MultiplayerSessionUserRegistryImplTest_testSaveUserRegistry_AlreadyExists", ".xml");

		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setXmlFilename (xmlFilename);
		reg.setUserRegistryMarshaller (userRegistryMarshaller);
		reg.setUserRegistryUnmarshaller (userRegistryUnmarshaller);
		reg.newUserRegistry ();
		
		// Try to save it to a file that already exists
		reg.saveUserRegistry ();
		
		// Tidy up temporary file
		xmlFilename.delete ();
	}

	/**
	 * Test trying to read a registry file that doesn't exist
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testLoadUserRegistry_DoesntExist () throws Exception
	{
		// Generate a temporary filename to test with
		final File xmlFilename = File.createTempFile ("MultiplayerSessionUserRegistryImplTest_testLoadUserRegistry_DoesntExist", ".xml");
		xmlFilename.delete ();	// We don't actually *want* the temporary file, we just want a temporary name allocated

		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setXmlFilename (xmlFilename);
		
		// Try to load in a file that doesn't exist
		assertThrows (IOException.class, () ->
		{
			reg.loadUserRegistry ();
		});
	}

	/**
	 * Tests the findPlayerByID method
	 */
	@Test
	public final void testFindPlayerByID ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Try to find the player before they exist
		assertNull (reg.findPlayerByID (1));
		
		// Add player
		assertEquals (1, reg.createAccount ("first player", "first password"));

		// Now it should exist
		assertEquals ("first player", reg.findPlayerByID (1).getPlayerName ());
	}
	
	/**
	 * Tests the findPlayerByName method
	 */
	@Test
	public final void testFindPlayerByName ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Try to find the player before they exist
		assertNull (reg.findPlayerByName ("first player"));
		
		// Add player
		assertEquals (1, reg.createAccount ("first player", "first password"));

		// Now it should exist
		assertEquals (1, reg.findPlayerByName ("first player").getPlayerID ());
	}
	
	/**
	 * Tests the authenticateUser method with valid credentials
	 */
	@Test
	public final void testAuthenticateUser_Valid ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Add player
		assertEquals (1, reg.createAccount ("first player", "first password"));

		// Run method
		assertEquals (1, reg.authenticateUser ("first player", "first password"));
	}

	/**
	 * Tests the authenticateUser method with a player name that doesn't exist
	 */
	@Test
	public final void testAuthenticateUser_PlayerDoesntExist ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Add player
		assertEquals (1, reg.createAccount ("first player", "first password"));
		
		// Run method
		assertEquals (-1, reg.authenticateUser ("second player", "first password"));
	}

	/**
	 * Tests the authenticateUser method with an incorrect password
	 */
	@Test
	public final void testAuthenticateUser_WrongPassword ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Add player
		assertEquals (1, reg.createAccount ("first player", "first password"));
		
		// Run method
		assertEquals (-1, reg.authenticateUser ("first player", "second password"));
	}

	/**
	 * Tests creating a registry and adding multiple players to it and then authenticating the players
	 */
	@Test
	public final void testAuthenticateUser_MultiplePlayers ()
	{
		// Set up object to test
		final MultiplayerSessionUserRegistryImpl reg = new MultiplayerSessionUserRegistryImpl ();
		reg.setUserRegistryObjectFactory (() -> new Player ());
		reg.newUserRegistry ();

		// Add players
		for (int n = 1; n <= 10; n++)
			assertEquals (n, reg.createAccount ("player #" + n, "password #" + n));

		// Authenticate players
		for (int n = 1; n <= 10; n++)
			assertEquals (n, reg.authenticateUser ("player #" + n, "password #" + n));
	}
}