package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;

import com.ndg.xmleditor.XmlEditorTestBase;

/**
 * Tests the TopLevelElementEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestTopLevelElementEx extends XmlEditorTestBase
{
	/**
	 * Tests the getKeyRefs method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetKeyRefs () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		// Run method
		final List<Keyref> keyRefs = xsd.getTopLevelElement ().getKeyRefs ();
				
		// Check results
		assertEquals (1, keyRefs.size ());
		assertEquals ("childWithReferencesFKtopLevelRef", keyRefs.get (0).getName ());
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

		// Run method
		final List<Keybase> keys = xsd.getTopLevelElement ().getKeys ();
				
		// Check results
		assertEquals (1, keys.size ());
		assertEquals ("topLevelRefPK", keys.get (0).getName ());
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

		// Run method
		assertNull (xsd.getTopLevelElement ().findKeyRef ("childWithReferences", "childLevelRefValue"));
		assertEquals ("childWithReferencesFKtopLevelRef", xsd.getTopLevelElement ().findKeyRef ("container/childWithReferences", "topLevelRefValue").getName ());
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

		// Run method
		assertNull (xsd.getTopLevelElement ().findKey ("childLevelRefPK"));
		assertEquals ("topLevelRefPK", xsd.getTopLevelElement ().findKey ("topLevelRefPK").getName ());
	}
}