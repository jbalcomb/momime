package com.ndg.xmleditor.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.xmleditor.XmlEditorTestBase;
import com.ndg.xmleditor.doc.ListOfXmlDocumentsImpl;
import com.ndg.xmleditor.doc.XmlDocument;

/**
 * Tests the LocalComplexTypeEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestLocalComplexTypeEx extends XmlEditorTestBase
{
	/**
	 * Tests the getChildEntities method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetChildEntities () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		assertEquals (LocalComplexTypeEx.class.getName (), complexType.getClass ().getName ());
		
		final List<LocalElementEx> childEntities = complexType.getChildEntities (list);
		
		// Check results
		assertEquals (4, childEntities.size ());
		assertEquals ("optionalChild", childEntities.get (0).getName ());
		assertEquals ("mandatoryChild", childEntities.get (1).getName ());

		assertEquals ("topLevelRef", childEntities.get (2).getName ());
		assertEquals ("container", childEntities.get (3).getName ());
	}

	/**
	 * Tests the getAllElements method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetAllElements () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		assertEquals (LocalComplexTypeEx.class.getName (), complexType.getClass ().getName ());
		
		final List<LocalElementEx> list = complexType.getAllElements ();
		
		// Check results
		assertEquals (6, list.size ());
		assertEquals ("optionalSingle", list.get (0).getName ());
		assertEquals ("optionalChild", list.get (1).getName ());
		assertEquals ("mandatorySingle", list.get (2).getName ());
		assertEquals ("mandatoryChild", list.get (3).getName ());
		assertEquals ("topLevelRef", list.get (4).getName ());
		assertEquals ("container", list.get (5).getName ());
	}

	/**
	 * Tests the findChildEntity method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindChildEntity () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		assertEquals (LocalComplexTypeEx.class.getName (), complexType.getClass ().getName ());

		assertNull (complexType.findChildEntity ("blah", list));
		assertNull (complexType.findChildEntity ("optionalSingle", list));		// This exists, but maxOccurs isn't unbounded so is ignored
		assertEquals ("optionalChild", complexType.findChildEntity ("optionalChild", list).getName ());
	}

	/**
	 * Tests the includesElementRefPlaceholder method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testIncludesElementRefPlaceholder () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		final ComplexTypeEx complexType = xsd.getTopLevelTypeDefinition ();
		assertEquals (LocalComplexTypeEx.class.getName (), complexType.getClass ().getName ());
		
		assertTrue (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced", "referencedElement")));
		assertFalse (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced2", "referencedElement")));
		assertFalse (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced", "referencedElement2")));
	}
}