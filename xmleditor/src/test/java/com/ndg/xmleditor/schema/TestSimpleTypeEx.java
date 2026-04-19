package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.xmleditor.XmlEditorTestBase;

/**
 * Tests the SimpleTypeEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestSimpleTypeEx extends XmlEditorTestBase
{
	/**
	 * Tests the getEnumerations method on a simple type that isn't an enum
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetEnumerations_NonEnum () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		assertNull (xsd.findTopLevelSimpleType ("nonEnumSimpleType").getEnumerations (false));
	}

	/**
	 * Tests the getEnumerations method on an enum without descriptions
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetEnumerations_WithoutDescriptions () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		final String [] [] enums = xsd.findTopLevelSimpleType ("enumSimpleTypeWithoutDescriptions").getEnumerations (false);
		
		// Check results
		assertNotNull (enums);
		assertEquals (1, enums.length);
		assertEquals (3, enums [0].length);
		assertEquals ("1", enums [0] [0]);
		assertEquals ("2", enums [0] [1]);
		assertEquals ("3", enums [0] [2]);
	}

	/**
	 * Tests the getEnumerations method on an enum with descriptions
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetEnumerations_WithDescriptions () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		final String [] [] enums = xsd.findTopLevelSimpleType ("enumSimpleTypeWithDescriptions").getEnumerations (false);
		
		// Check results
		assertNotNull (enums);
		assertEquals (2, enums.length);
		assertEquals (3, enums [0].length);
		assertEquals ("1", enums [0] [0]);
		assertEquals ("2", enums [0] [1]);
		assertEquals ("3", enums [0] [2]);
		assertEquals ("One", enums [1] [0]);
		assertEquals ("2", enums [1] [1]);
		assertEquals ("Three", enums [1] [2]);
	}

	/**
	 * Tests the getEnumerations method on an enum without descriptions, including a blank record at the start
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetEnumerations_WithoutDescriptionsIncludeFiller () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		final String [] [] enums = xsd.findTopLevelSimpleType ("enumSimpleTypeWithoutDescriptions").getEnumerations (true);
		
		// Check results
		assertNotNull (enums);
		assertEquals (1, enums.length);
		assertEquals (4, enums [0].length);
		assertNull (enums [0] [0]);
		assertEquals ("1", enums [0] [1]);
		assertEquals ("2", enums [0] [2]);
		assertEquals ("3", enums [0] [3]);
	}

	/**
	 * Tests the getEnumerations method on an enum with descriptions, including a blank record at the start
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetEnumerations_WithDescriptionsIncludeFiller () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		final String [] [] enums = xsd.findTopLevelSimpleType ("enumSimpleTypeWithDescriptions").getEnumerations (true);
		
		// Check results
		assertNotNull (enums);
		assertEquals (2, enums.length);
		assertEquals (4, enums [0].length);
		assertNull (enums [0] [0]);
		assertEquals ("1", enums [0] [1]);
		assertEquals ("2", enums [0] [2]);
		assertEquals ("3", enums [0] [3]);
		assertNull (enums [1] [0]);
		assertEquals ("One", enums [1] [1]);
		assertEquals ("2", enums [1] [2]);
		assertEquals ("Three", enums [1] [3]);
	}
}