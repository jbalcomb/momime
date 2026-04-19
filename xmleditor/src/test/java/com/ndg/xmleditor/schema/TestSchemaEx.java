package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.xmleditor.XmlEditorTestBase;
import com.ndg.xmleditor.editor.XmlEditorException;

/**
 * Tests the SchemaEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestSchemaEx extends XmlEditorTestBase
{
	/**
	 * Tests the findTopLevelComplexType method looking for a complex type that exists
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindTopLevelComplexType_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		assertEquals ("mainType", xsd.findTopLevelComplexType ("mainType").getName ());
	}

	/**
	 * Tests the findTopLevelComplexType method looking for a complex type that doesn't exist
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindTopLevelComplexType_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertThrows (XmlEditorException.class, () ->
		{
			xsd.findTopLevelComplexType ("mainType");
		});
	}
	
	/**
	 * Tests the findTopLevelSimpleType method looking for a simple type that exists
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindTopLevelSimpleType_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		assertEquals ("nonEnumSimpleType", xsd.findTopLevelSimpleType ("nonEnumSimpleType").getName ());
	}

	/**
	 * Tests the findTopLevelSimpleType method looking for a simple type that doesn't exist
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindTopLevelSimpleType_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertThrows (XmlEditorException.class, () ->
		{
			xsd.findTopLevelSimpleType ("nonEnumSimpleType");
		});
	}
	
	/**
	 * Tests the getTopLevelElement method on an XSD with 1 top level element
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelElement_One () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("mainElement", xsd.getTopLevelElement ().getName ());
	}

	/**
	 * Tests the getTopLevelElement method on an XSD with no top level elements
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelElement_Zero () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/BackupOnOpen.xsd"));
		
		// Run method
		assertNull (xsd.getTopLevelElement ());
	}

	/**
	 * Tests the getTopLevelElement method on an XSD with multple top level elements
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelElement_Multiple () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/MultipleTopLevelElements.xsd"));
		
		// Run method
		assertNull (xsd.getTopLevelElement ());
	}
	
	/**
	 * Tests the getTopLevelTypeDefinition method on an XSD where the main element references a separately defined complex type
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelTypeDefinition_SeparateComplexType () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		
		// Check results
		assertEquals (TopLevelComplexTypeEx.class.getName (), complexType.getClass ().getName ());
		assertEquals ("mainType", ((TopLevelComplexTypeEx) complexType).getName ());
	}

	/**
	 * Tests the getTopLevelTypeDefinition method on an XSD where the main element declares an inline complex type
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelTypeDefinition_InlinedComplexType () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		
		// Check results
		assertEquals (LocalComplexTypeEx.class.getName (), complexType.getClass ().getName ());
	}

	/**
	 * Tests the getTopLevelTypeDefinition method on an XSD where the main element doesn't declare a type
	 * Am somewhat surprised that this even passes schema validation
	 * 
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetTopLevelTypeDefinition_NoType () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/TopLevelElementWithNoType.xsd"));
		
		// Run method
		assertThrows (XmlEditorException.class, () ->
		{
			xsd.getTopLevelTypeDefinition ();
		});
	}
	
	/**
	 * Tests the isBackupOnOpen method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testIsBackupOnOpen () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/BackupOnOpen.xsd"));
		
		// Check results
		assertTrue (xsd.isBackupOnOpen ());
	}

	/**
	 * Tests the getDatabaseInfo method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetDatabaseInfo () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/BackupOnOpen.xsd"));
		
		// Run method
		assertNull (xsd.getDatabaseInfo ("http://www.ndg.com/xmleditor/test2"));
		final XmlDatabaseInfo dbInfo = xsd.getDatabaseInfo ("http://www.ndg.com/xmleditor/test");
		
		// Check results
		assertNotNull (dbInfo);
		assertEquals ("Unit Test", dbInfo.getTitle ());
		assertEquals ("..\\Unit Test.xml", dbInfo.getFilename ());
		assertEquals (XmlFilenameType.RELATIVE, dbInfo.getFilenameType ());
	}
}
