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
 * Tests the TopLevelComplexTypeEx class
 */
@ExtendWith(MockitoExtension.class)
public final class TestTopLevelComplexTypeEx extends XmlEditorTestBase
{
	/**
	 * Tests the getSingularName method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetSingularName () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("Optional Child", xsd.findTopLevelComplexType ("optionalChild").getSingularName ());
	}

	/**
	 * Tests the getPluralName method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetPluralName () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("Optional Children", xsd.findTopLevelComplexType ("optionalChild").getPluralName ());
	}

	/**
	 * Tests the getSubMenu method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetSubMenu () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("Some sub menu", xsd.findTopLevelComplexType ("optionalChild").getSubMenu ());
	}

	/**
	 * Tests the getLongDescription method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetLongDescription () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("Here's a long description", xsd.findTopLevelComplexType ("optionalChild").getLongDescription ());
	}
	
	/**
	 * Tests the getAttributes method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetAttributes () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		final List<AttributeEx> attr1 = xsd.findTopLevelComplexType ("optionalChild").getAttributes ();
		final List<AttributeEx> attr2 = xsd.findTopLevelComplexType ("mandatoryChild").getAttributes ();
		
		// Check results
		assertEquals (2, attr1.size ());
		assertEquals ("attributeOne", attr1.get (0).getName ());
		assertEquals ("attributeTwo", attr1.get (1).getName ());
		
		assertEquals (2, attr2.size ());
		assertEquals ("attributeThree", attr2.get (0).getName ());
		assertEquals ("attributeFour", attr2.get (1).getName ());
	}

	/**
	 * Tests the findAttribute method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Run method
		assertEquals ("attributeOne", xsd.findTopLevelComplexType ("optionalChild").findAttribute ("attributeOne").getName ());
		assertNull (xsd.findTopLevelComplexType ("optionalChild").findAttribute ("attributeOneX"));
		
		assertEquals ("attributeThree", xsd.findTopLevelComplexType ("mandatoryChild").findAttribute ("attributeThree").getName ());
		assertNull (xsd.findTopLevelComplexType ("mandatoryChild").findAttribute ("attributeThreeX"));
	}

	/**
	 * Tests the getElements method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetElements () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final List<LocalElementEx> elem1 = xsd.findTopLevelComplexType ("optionalChild").getElements (list);
		final List<LocalElementEx> elem2 = xsd.findTopLevelComplexType ("mandatoryChild").getElements (list);
		
		// Check results
		assertEquals (2, elem1.size ());
		assertEquals ("elementOne", elem1.get (0).getName ());
		assertEquals ("elementTwo", elem1.get (1).getName ());
		
		assertEquals (2, elem2.size ());
		assertEquals ("elementThree", elem2.get (0).getName ());
		assertEquals ("elementFour", elem2.get (1).getName ());
	}

	/**
	 * Tests the findElement method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));

		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run method
		final TopLevelComplexTypeEx complexType = xsd.findTopLevelComplexType ("optionalChild");
		assertEquals ("elementOne", complexType.findElement ("elementOne", list).getName ());
		assertNull (complexType.findElement ("elementList", list));
	}

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
		final List<LocalElementEx> children1 = xsd.findTopLevelComplexType ("optionalChild").getChildEntities (list);
		final List<LocalElementEx> children2 = xsd.findTopLevelComplexType ("mandatoryChild").getChildEntities (list);
		
		// Check results
		assertEquals (1, children1.size ());
		assertEquals ("elementList", children1.get (0).getName ());

		assertEquals (1, children2.size ());
		assertEquals ("anotherElementList", children2.get (0).getName ());
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
		final List<LocalElementEx> list = xsd.findTopLevelComplexType ("optionalChild").getAllElements ();
		
		// Check results
		assertEquals (3, list.size ());
		assertEquals ("elementOne", list.get (0).getName ());
		assertEquals ("elementTwo", list.get (1).getName ());
		assertEquals ("elementList", list.get (2).getName ());
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
		final TopLevelComplexTypeEx complexType = xsd.findTopLevelComplexType ("optionalChild");

		assertNull (complexType.findChildEntity ("blah", list));
		assertNull (complexType.findChildEntity ("elementOne", list));		// This exists, but maxOccurs isn't unbounded so is ignored
		assertEquals ("elementList", complexType.findChildEntity ("elementList", list).getName ());
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
		final TopLevelComplexTypeEx complexType = xsd.findTopLevelComplexType ("elementRefChild");
		
		assertTrue (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced", "referencedElement")));
		assertFalse (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced2", "referencedElement")));
		assertFalse (complexType.includesElementRefPlaceholder (new QName ("http://www.ndg.com/xmleditor/testReferenced", "referencedElement2")));
	}	
}