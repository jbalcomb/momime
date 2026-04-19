package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.xmleditor.XmlEditorTestBase;

/**
 * Tests the AttributeEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestAttributeEx extends XmlEditorTestBase
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
		
		// Run method
		final List<AttributeEx> attributes = xsd.findTopLevelComplexType ("optionalChild").getAttributes ();
		
		// Check results
		assertEquals ("attributeOne", attributes.get (0).getName ());
		assertEquals ("attributeTwo", attributes.get (1).getName ());
		assertTrue (attributes.get (0).isShowInGrid ());
		assertFalse (attributes.get (1).isShowInGrid ());
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
		
		// Run method
		final List<AttributeEx> attributes = xsd.findTopLevelComplexType ("optionalChild").getAttributes ();
		
		// Check results
		assertEquals ("attributeOne", attributes.get (0).getName ());
		assertEquals ("attributeTwo", attributes.get (1).getName ());
		assertFalse (attributes.get (0).isShowReferencedColumns ());
		assertTrue (attributes.get (1).isShowReferencedColumns ());
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
		
		// Run method
		final List<AttributeEx> attributes = xsd.findTopLevelComplexType ("optionalChild").getAttributes ();
		
		// Check results
		assertEquals ("attributeOne", attributes.get (0).getName ());
		assertEquals ("attributeTwo", attributes.get (1).getName ());
		assertEquals ("attributeOne", attributes.get (0).getLabel ());
		assertEquals ("Attribute two label", attributes.get (1).getLabel ());
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
		
		// Run method
		final List<AttributeEx> attributes = xsd.findTopLevelComplexType ("optionalChild").getAttributes ();
		
		// Check results
		assertEquals ("attributeOne", attributes.get (0).getName ());
		assertEquals ("attributeTwo", attributes.get (1).getName ());
		assertEquals (150, attributes.get (0).getGridWidth ());
		assertEquals (90, attributes.get (1).getGridWidth ());
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
		
		// Run method
		final List<String> list1 = xsd.findTopLevelComplexType ("mandatoryChild").findAttribute ("attributeThree").getReferencedFields ();
		final List<String> list2 = xsd.findTopLevelComplexType ("mandatoryChild").findAttribute ("attributeFour").getReferencedFields ();
		
		// Check results
		assertNull (list1);

		assertEquals (2, list2.size ());
		assertEquals ("showReferencedFieldThree", list2.get (0));
		assertEquals ("showReferencedFieldFour", list2.get (1));
	}
}