package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;

import com.ndg.xmleditor.XmlEditorTestBase;
import com.ndg.xmleditor.doc.ListOfXmlDocumentsImpl;
import com.ndg.xmleditor.doc.XmlDocument;

/**
 * Tests the LocalElementEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestLocalElementEx extends XmlEditorTestBase
{
	/**
	 * Tests the isShowInGrid method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testIsShowInGrid () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
	
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final List<LocalElementEx> elements = xsd.findTopLevelComplexType ("optionalChild").getElements (list);
		
		// Check results
		assertEquals ("elementOne", elements.get (0).getName ());
		assertEquals ("elementTwo", elements.get (1).getName ());
		assertTrue (elements.get (0).isShowInGrid ());
		assertFalse (elements.get (1).isShowInGrid ());
	}
	
	/**
	 * Tests the isShowReferencedColumns method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testIsShowReferencedColumns () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final List<LocalElementEx> elements = xsd.findTopLevelComplexType ("optionalChild").getElements (list);
		
		// Check results
		assertEquals ("elementOne", elements.get (0).getName ());
		assertEquals ("elementTwo", elements.get (1).getName ());
		assertFalse (elements.get (0).isShowReferencedColumns ());
		assertTrue (elements.get (1).isShowReferencedColumns ());
	}
	
	/**
	 * Tests the getLabel method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetLabel () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final List<LocalElementEx> elements = xsd.findTopLevelComplexType ("optionalChild").getElements (list);
		
		// Check results
		assertEquals ("elementOne", elements.get (0).getName ());
		assertEquals ("elementTwo", elements.get (1).getName ());
		assertEquals ("elementOne", elements.get (0).getLabel ());
		assertEquals ("Element two label", elements.get (1).getLabel ());
	}
	
	/**
	 * Tests the getGridWidth method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetGridWidth () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final List<LocalElementEx> elements = xsd.findTopLevelComplexType ("optionalChild").getElements (list);
		
		// Check results
		assertEquals ("elementOne", elements.get (0).getName ());
		assertEquals ("elementTwo", elements.get (1).getName ());
		assertEquals (150, elements.get (0).getGridWidth ());
		assertEquals (80, elements.get (1).getGridWidth ());
	}
	
	/**
	 * Tests the getKeyRefs method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetKeyRefs () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// Find the local element to test with
		final List<LocalElementEx> elements = xsd.getTopLevelTypeDefinition ().getAllElements ();
		boolean found = false;
		for (final LocalElementEx elem : elements)
			if (elem.getName ().equals ("container"))
			{
				// Ok, found the one to test with
				found = true;
				
				// Run method
				final List<Keyref> keyRefs = elem.getKeyRefs ();
				
				// Check results
				assertEquals (1, keyRefs.size ());
				assertEquals ("childWithReferencesFKchildLevelRef", keyRefs.get (0).getName ());
			}
		
		assertTrue (found);
	}

	/**
	 * Tests the getKeyRefs method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetKeys () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// Find the local element to test with
		final List<LocalElementEx> elements = xsd.getTopLevelTypeDefinition ().getAllElements ();
		boolean found = false;
		for (final LocalElementEx elem : elements)
			if (elem.getName ().equals ("container"))
			{
				// Ok, found the one to test with
				found = true;
				
				// Run method
				final List<Keybase> keys = elem.getKeys ();
				
				// Check results
				assertEquals (1, keys.size ());
				assertEquals ("childLevelRefPK", keys.get (0).getName ());
			}
		
		assertTrue (found);
	}
	
	/**
	 * Tests the toXpath method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testToXpath () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// First just check just the top level container entity
		LocalElementEx containerElement = null;
		for (final LocalElementEx elem : xsd.getTopLevelTypeDefinition ().getAllElements ())
			if (elem.getName ().equals ("container"))
				containerElement = elem;
		
		assertNotNull (containerElement);
		assertEquals ("container", containerElement.toXpath (null, null));
		
		// Now find the containerType complex type
		final TopLevelComplexTypeEx containerType = xsd.findTopLevelComplexType ("containerType");
		LocalElementEx childElement = null;
		for (final LocalElementEx elem : containerType.getAllElements ())
			if (elem.getName ().equals ("childLevelRef"))
				childElement = elem;

		assertNotNull (childElement);
		assertEquals ("container/childLevelRef", childElement.toXpath (new LocalElementEx [] {containerElement}, null));
		assertEquals ("childLevelRef", childElement.toXpath (new LocalElementEx [] {containerElement}, containerElement));
	}

	/**
	 * Tests the findKeyRef method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindKeyRef () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// Find the local element to test with
		final List<LocalElementEx> elements = xsd.getTopLevelTypeDefinition ().getAllElements ();
		boolean found = false;
		for (final LocalElementEx elem : elements)
			if (elem.getName ().equals ("container"))
			{
				// Ok, found the one to test with
				found = true;
				
				// Run method
				assertNull (elem.findKeyRef ("container/childWithReferences", "topLevelRefValue"));
				assertEquals ("childWithReferencesFKchildLevelRef", elem.findKeyRef ("childWithReferences", "childLevelRefValue").getName ());
			}
		
		assertTrue (found);
	}

	/**
	 * Tests the findKey method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindKey () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// Find the local element to test with
		final List<LocalElementEx> elements = xsd.getTopLevelTypeDefinition ().getAllElements ();
		boolean found = false;
		for (final LocalElementEx elem : elements)
			if (elem.getName ().equals ("container"))
			{
				// Ok, found the one to test with
				found = true;
				
				// Run method
				assertNull (elem.findKey ("topLevelRefPK"));
				assertEquals ("childLevelRefPK", elem.findKey ("childLevelRefPK").getName ());
			}
		
		assertTrue (found);
	}
	
	/**
	 * Tests the getReferencedFields method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetReferencedFields () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		boolean found1 = false;
		boolean found2 = false;
		final List<LocalElementEx> elements = xsd.findTopLevelComplexType ("mandatoryChild").getElements (list);
		for (final LocalElementEx elem : elements)
			
			// Look for the elements to test with
			if (elem.getName ().equals ("elementThree"))
			{
				found1 = true;
				assertNull (elem.getReferencedFields ());
			}
			else if (elem.getName ().equals ("elementFour"))
			{
				found2 = true;
				final List<String> refs = elem.getReferencedFields ();
				assertEquals (2, refs.size ());
				assertEquals ("showReferencedFieldOne", refs.get (0));
				assertEquals ("showReferencedFieldTwo", refs.get (1));
			}
		
		assertTrue (found1);
		assertTrue (found2);
	}
}