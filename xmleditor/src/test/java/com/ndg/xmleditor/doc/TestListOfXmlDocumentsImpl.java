package com.ndg.xmleditor.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.xmleditor.XmlEditorTestBase;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.schema.SchemaEx;

/**
 * Tests the ListOfXmlDocumentsImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestListOfXmlDocumentsImpl extends XmlEditorTestBase
{
	/**
	 * Tests the findDocumentWithNamespaceURI method on a namespace that does exist
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Test
	public final void testFindDocumentWithNamespaceURI_Exists () throws XmlEditorException
	{
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();		
		for (int n = 1; n <= 3; n++)
		{
			final SchemaEx xsd = new SchemaEx ();
			xsd.setTargetNamespace ("http://www.ndg.com/xmleditor/test/" + n);
			
			final XmlDocument doc = new XmlDocument (xsd);
			doc.setXmlFileRequiredForThisXsd (n == 2);		// Mark the right one
			list.add (doc);
		}		
		
		// Run test
		assertTrue (list.findDocumentWithNamespaceURI ("http://www.ndg.com/xmleditor/test/2").isXmlFileRequiredForThisXsd ());
	}

	/**
	 * Tests the findDocumentWithNamespaceURI method on a namespace that does exist
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Test
	public final void testFindDocumentWithNamespaceURI_NotExists () throws XmlEditorException
	{
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();		
		for (int n = 1; n <= 3; n++)
		{
			final SchemaEx xsd = new SchemaEx ();
			xsd.setTargetNamespace ("http://www.ndg.com/xmleditor/test/" + n);
			
			final XmlDocument doc = new XmlDocument (xsd);
			doc.setXmlFileRequiredForThisXsd (n == 2);		// Mark the right one
			list.add (doc);
		}		
		
		// Run test
		assertThrows (XmlEditorException.class, () ->
		{
			list.findDocumentWithNamespaceURI ("http://www.ndg.com/xmleditor/test/4");
		});
	}

	/**
	 * Tests the isComplexType method on a complex type
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testisComplexType_Yes () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		assertTrue (list.isComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild")));
	}
	
	/**
	 * Tests the isComplexType method on a simple type
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testisComplexType_No () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		assertFalse (list.isComplexType (new QName ("http://www.ndg.com/xmleditor/test", "exampleSimpleType")));
	}
	
	/**
	 * Tests the findDefinitionOfComplexType method on a type that does exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindDefinitionOfComplexType_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		
		// Check result
		assertEquals ("mandatoryChild", ref.getComplexTypeDefinition ().getName ());
	}

	/**
	 * Tests the findDefinitionOfComplexType method on a type that does exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindDefinitionOfComplexType_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		assertThrows (XmlEditorException.class, () ->
		{
			list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "blah"));
		});
	}

	/**
	 * Tests the determineInheritancePath method
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testDetermineInheritancePath () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		final List<ComplexTypeReference> refs = list.determineInheritancePath (ref);
		
		// Check result
		assertEquals (2, refs.size ());
		assertEquals ("optionalChild", refs.get (0).getComplexTypeDefinition ().getName ());
		assertEquals ("mandatoryChild", refs.get (1).getComplexTypeDefinition ().getName ());
	}

	/**
	 * Tests the findAttributeForComplexTypeOrParent method looking for attributes that exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindAttributeForComplexTypeOrParent_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		assertEquals ("attributeThree", list.findAttributeForComplexTypeOrParent (ref, "attributeThree").getName ());	// This attribute is on the child entity
		assertEquals ("attributeOne", list.findAttributeForComplexTypeOrParent (ref, "attributeOne").getName ());			// This attribute is on the parent entity
	}

	/**
	 * Tests the findAttributeForComplexTypeOrParent method looking for an attributes that doesn't exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindAttributeForComplexTypeOrParent_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		assertThrows (XmlEditorException.class, () ->
		{
			list.findAttributeForComplexTypeOrParent (ref, "attributeFive");
		});
	}
	
	/**
	 * Tests the findElementForComplexTypeOrParent method looking for elements that exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindElementForComplexTypeOrParent_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		assertEquals ("elementThree", list.findElementForComplexTypeOrParent (ref, "elementThree").getName ());		// This element is on the child entity
		assertEquals ("elementOne", list.findElementForComplexTypeOrParent (ref, "elementOne").getName ());			// This element is on the parent entity
	}

	/**
	 * Tests the findElementForComplexTypeOrParent method looking for an elements that doesn't exist
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testFindElementForComplexTypeOrParent_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference ref = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		assertThrows (XmlEditorException.class, () ->
		{
			list.findElementForComplexTypeOrParent (ref, "elementFive");
		});
	}
	
	/**
	 * Tests the determineUltimateSimpleType method looking for a valid simple type
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testDetermineUltimateSimpleType_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));

		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final QName result = list.determineUltimateSimpleType (new QName
			("http://www.ndg.com/xmleditor/test", "extensionFromSimpleType"));
		
		// Check results
		assertEquals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI, result.getNamespaceURI ());
		assertEquals ("normalizedString", result.getLocalPart ());
	}
	
	/**
	 * Tests the determineUltimateSimpleType method looking for a valid simple type
	 * @throws Exception If there if a problem
	 */
	@Test
	public final void testDetermineUltimateSimpleType_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));

		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		assertThrows (XmlEditorException.class, () ->
		{
			list.determineUltimateSimpleType (new QName
				("http://www.ndg.com/xmleditor/test", "brokenExtensionFromSimpleType"));
		});
	}
	
	/**
	 * Tests the findEnumerationsForType method on a simple type that isn't an enum
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindEnumerationsForType_NonEnum () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));

		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));

		// Run test
		final String [] [] values = list.findEnumerationsForType (new QName
			("http://www.ndg.com/xmleditor/test", "nonEnumSimpleType"), false);
		
		// Check results
		assertNull (values);
	}
	
	/**
	 * Tests the findEnumerationsForType method directly on an enum, where the values have no descriptions, and we include no blank record at start
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindEnumerationsForType_NoDescriptions () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));

		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));

		// Run test
		final String [] [] values = list.findEnumerationsForType (new QName
			("http://www.ndg.com/xmleditor/test", "enumSimpleTypeWithoutDescriptions"), false);
		
		// Check results
		assertEquals (1, values.length);
		assertEquals (3, values [0].length);
		assertEquals ("1", values [0] [0]);
		assertEquals ("2", values [0] [1]);
		assertEquals ("3", values [0] [2]);
	}
	
	/**
	 * Tests the findEnumerationsForType method directly a type that inherits from an enum,
	 * where some of the values have descriptions, and we include a blank record at start
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFindEnumerationsForType_WithDescriptions () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/SeparateComplexType.xsd"));

		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));

		// Run test
		final String [] [] values = list.findEnumerationsForType (new QName
			("http://www.ndg.com/xmleditor/test", "extensionFromSimpleType"), true);
		
		// Check results
		assertEquals (2, values.length);
		assertEquals (4, values [0].length);
		assertNull (values [0] [0]);
		assertEquals ("1", values [0] [1]);
		assertEquals ("2", values [0] [2]);
		assertEquals ("3", values [0] [3]);
		assertNull (values [1] [0]);
		assertEquals ("One", values [1] [1]);
		assertEquals ("2", values [1] [2]);
		assertEquals ("Three", values [1] [3]);
	}
	
	/**
	 * Tests the getReferencedFieldList method when we want no fields from the referenced entity
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetReferencedFieldList_None () throws Exception
	{
		// Mock details about the referring field
		final FieldEx field = mock (FieldEx.class);
		when (field.isShowReferencedColumns ()).thenReturn (false);
		
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final List<LocalElementEx> result = list.getReferencedFieldList
			(field, list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild")));
		
		// Check results
		assertEquals (0, result.size ());
	}
	
	/**
	 * Tests the getReferencedFieldList method when we just take the complete list of elements on the referenced entity
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetReferencedFieldList_Unrestricted () throws Exception
	{
		// Mock details about the referring field
		final FieldEx field = mock (FieldEx.class);
		when (field.isShowReferencedColumns ()).thenReturn (true);
		when (field.getReferencedFields ()).thenReturn (null);
		
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final List<LocalElementEx> result = list.getReferencedFieldList
			(field, list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild")));
		
		// Check results
		assertEquals (4, result.size ());
		assertEquals ("elementOne", result.get (0).getName ());
		assertEquals ("elementTwo", result.get (1).getName ());
		assertEquals ("elementThree", result.get (2).getName ());
		assertEquals ("elementFour", result.get (3).getName ());
	}
	
	/**
	 * Tests the getReferencedFieldList method when we restrict the list of elements on the referenced entity
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetReferencedFieldList_Restricted () throws Exception
	{
		// Mock details about the referring field
		final FieldEx field = mock (FieldEx.class);
		when (field.isShowReferencedColumns ()).thenReturn (true);
		
		final List<String> restrictedList = new ArrayList<String> ();
		restrictedList.add ("elementTwo");
		restrictedList.add ("elementFour");
		restrictedList.add ("elementSix");
		when (field.getReferencedFields ()).thenReturn (restrictedList);
		
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final List<LocalElementEx> result = list.getReferencedFieldList
			(field, list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild")));
		
		// Check results
		assertEquals (2, result.size ());
		assertEquals ("elementTwo", result.get (0).getName ());
		assertEquals ("elementFour", result.get (1).getName ());
	}
	
	/**
	 * Tests the determineElementInsertionPoint method in various valid situations
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testDetermineElementInsertionPoint_Exists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Start with adding to a totally empty record
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		final Element record = new Element ("Dummy");
		
		assertEquals (0, list.determineElementInsertionPoint (typeDef, record, "elementThree"));
		
		// Add a field that we add after
		record.addContent (new Element ("elementOne"));
		assertEquals (1, list.determineElementInsertionPoint (typeDef, record, "elementThree"));
		
		// Add a field that we add before
		record.addContent (new Element ("elementFour"));
		assertEquals (1, list.determineElementInsertionPoint (typeDef, record, "elementThree"));

		// Add a field that we add after, note we have to insert the elementTwo at the right place to make it valid
		record.addContent (1, new Element ("elementTwo"));
		assertEquals (2, list.determineElementInsertionPoint (typeDef, record, "elementThree"));
	}
	
	/**
	 * Tests the determineElementInsertionPoint method trying to insert an element that isn't in the XSD
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testDetermineElementInsertionPoint_NotExists () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		final Element record = new Element ("Dummy");
		
		assertThrows (XmlEditorException.class, () ->
		{
			list.determineElementInsertionPoint (typeDef, record, "elementFive");
		});
	}
	
	/**
	 * Tests the updateFieldValue method to add an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_AddAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Set up field definition
		final AttributeEx fieldDef = new AttributeEx ();
		fieldDef.setName ("attributeTwo");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef, "2");
		
		// Check results
		assertEquals (3, record.getAttributes ().size ());
		assertEquals ("1", record.getAttributeValue ("attributeOne"));
		assertEquals ("2", record.getAttributeValue ("attributeTwo"));
		assertEquals ("3", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to remove an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_RemoveAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Set up field definition
		final AttributeEx fieldDef = new AttributeEx ();
		fieldDef.setName ("attributeOne");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef, "");
		
		// Check results
		assertEquals (1, record.getAttributes ().size ());
		assertEquals ("3", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to update an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_UpdateAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Set up field definition
		final AttributeEx fieldDef = new AttributeEx ();
		fieldDef.setName ("attributeThree");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef, "New");
		
		// Check results
		assertEquals (2, record.getAttributes ().size ());
		assertEquals ("1", record.getAttributeValue ("attributeOne"));
		assertEquals ("New", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to add elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_AddElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementTwo"));
		
		// Set up field definitions
		final LocalElementEx fieldDef1 = new LocalElementEx ();
		fieldDef1.setName ("elementOne");

		final LocalElementEx fieldDef3 = new LocalElementEx ();
		fieldDef3.setName ("elementThree");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef1, "1");
		list.updateFieldValue (record, typeDef, fieldDef3, "3");
		
		// Check results
		assertEquals (3, record.getChildren ().size ());
		assertEquals ("elementOne", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementTwo", (record.getChildren ().get (1)).getName ());
		assertEquals ("elementThree", (record.getChildren ().get (2)).getName ());
	}
	
	/**
	 * Tests the updateFieldValue method to remove elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_RemoveElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to remove from
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementOne"));
		record.addContent (new Element ("elementTwo"));
		record.addContent (new Element ("elementThree"));
		record.addContent (new Element ("elementFour"));
		
		// Set up field definitions
		final LocalElementEx fieldDef1 = new LocalElementEx ();
		fieldDef1.setName ("elementOne");

		final LocalElementEx fieldDef3 = new LocalElementEx ();
		fieldDef3.setName ("elementThree");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef1, "");
		list.updateFieldValue (record, typeDef, fieldDef3, "");
		
		// Check results
		assertEquals (2, record.getChildren ().size ());
		assertEquals ("elementTwo", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementFour", (record.getChildren ().get (1)).getName ());
	}
	
	/**
	 * Tests the updateFieldValue method to update existing elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_UpdateElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to remove from
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementOne"));
		record.addContent (new Element ("elementTwo"));
		record.addContent (new Element ("elementThree"));
		record.addContent (new Element ("elementFour"));
		
		// Set up field definitions
		final LocalElementEx fieldDef1 = new LocalElementEx ();
		fieldDef1.setName ("elementOne");

		final LocalElementEx fieldDef3 = new LocalElementEx ();
		fieldDef3.setName ("elementThree");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, fieldDef1, "1");
		list.updateFieldValue (record, typeDef, fieldDef3, "3");
		
		// Check results
		assertEquals (4, record.getChildren ().size ());
		assertEquals ("elementOne", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementTwo", (record.getChildren ().get (1)).getName ());
		assertEquals ("elementThree", (record.getChildren ().get (2)).getName ());
		assertEquals ("elementFour", (record.getChildren ().get (3)).getName ());
	}
	
	/**
	 * Tests the updateFieldValue method to add an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_AddAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "attributeTwo", "2");
		
		// Check results
		assertEquals (3, record.getAttributes ().size ());
		assertEquals ("1", record.getAttributeValue ("attributeOne"));
		assertEquals ("2", record.getAttributeValue ("attributeTwo"));
		assertEquals ("3", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to remove an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_RemoveAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "attributeOne", "");
		
		// Check results
		assertEquals (1, record.getAttributes ().size ());
		assertEquals ("3", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to update an attribute, where we presupply the attribute def
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_UpdateAttribute () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.setAttribute ("attributeOne", "1");
		record.setAttribute ("attributeThree", "3");
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "attributeThree", "New");
		
		// Check results
		assertEquals (2, record.getAttributes ().size ());
		assertEquals ("1", record.getAttributeValue ("attributeOne"));
		assertEquals ("New", record.getAttributeValue ("attributeThree"));		
	}
	
	/**
	 * Tests the updateFieldValue method to add elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_AddElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to add to
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementTwo"));
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "elementOne", "1");
		list.updateFieldValue (record, typeDef, "elementThree", "3");
		
		// Check results
		assertEquals (3, record.getChildren ().size ());
		assertEquals ("elementOne", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementTwo", (record.getChildren ().get (1)).getName ());
		assertEquals ("elementThree", (record.getChildren ().get (2)).getName ());
	}
	
	/**
	 * Tests the updateFieldValue method to remove elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_RemoveElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to remove from
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementOne"));
		record.addContent (new Element ("elementTwo"));
		record.addContent (new Element ("elementThree"));
		record.addContent (new Element ("elementFour"));
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "elementOne", "");
		list.updateFieldValue (record, typeDef, "elementThree", "");
		
		// Check results
		assertEquals (2, record.getChildren ().size ());
		assertEquals ("elementTwo", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementFour", (record.getChildren ().get (1)).getName ());
	}
	
	/**
	 * Tests the updateFieldValue method to update existing elements defined on both the child and parent, where we presupply the element defs
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testUpdateFieldValue_Lookup_UpdateElement () throws Exception
	{
		// Load in the schema to test with
		final SchemaEx xsd = (SchemaEx) createSchemaUnmarshaller ().unmarshal (getClass ().getResource ("/InlinedComplexType.xsd"));
		
		// Set up list to test against
		final ListOfXmlDocumentsImpl list = new ListOfXmlDocumentsImpl ();
		list.add (new XmlDocument (xsd));
		
		// Set up record to remove from
		final Element record = new Element ("Dummy");
		record.addContent (new Element ("elementOne"));
		record.addContent (new Element ("elementTwo"));
		record.addContent (new Element ("elementThree"));
		record.addContent (new Element ("elementFour"));
		
		// Run test
		final ComplexTypeReference typeDef = list.findDefinitionOfComplexType (new QName ("http://www.ndg.com/xmleditor/test", "mandatoryChild"));
		list.updateFieldValue (record, typeDef, "elementOne", "1");
		list.updateFieldValue (record, typeDef, "elementThree", "3");
		
		// Check results
		assertEquals (4, record.getChildren ().size ());
		assertEquals ("elementOne", (record.getChildren ().get (0)).getName ());
		assertEquals ("elementTwo", (record.getChildren ().get (1)).getName ());
		assertEquals ("elementThree", (record.getChildren ().get (2)).getName ());
		assertEquals ("elementFour", (record.getChildren ().get (3)).getName ());
	}
	
	/**
	 * Tests the findMatchingChild method to search for an immediate child
	 */
	@Test
	public final void testFindMatchingChild_OneLevel ()
	{
		// First parent
		final Element parentOne = new Element ("P1");

		// First child
		final Element childOne = new Element ("C");
		childOne.setAttribute ("A", "2");
		parentOne.addContent (childOne);
		
		// Second parent
		final Element parentTwo = new Element ("P2");

		// Second child
		Element expectedResult = null;
		for (int n = 1; n <= 3; n++)
		{
			final Element childTwo = new Element ("C");
			childTwo.setAttribute ("A", Integer.valueOf (n).toString ());
			parentTwo.addContent (childTwo);
			
			if (n == 2)
				expectedResult = childTwo;
		}
		
		// Run method
		assertSame (expectedResult, new ListOfXmlDocumentsImpl ().findMatchingChild (childOne, parentOne, parentTwo));
	}

	/**
	 * Tests the findMatchingChild method to search for a grandchild.  NB. the element names and attribute values at all levels must match.
	 */
	@Test
	public final void testFindMatchingChild_TwoLevels ()
	{
		// First parent
		final Element parentOne = new Element ("P1");

		// First child
		final Element childOne = new Element ("C");
		childOne.setAttribute ("A", "2");
		parentOne.addContent (childOne);
		
		// First grandchild
		final Element grandchildOne = new Element ("G");
		grandchildOne.setAttribute ("B", "5");
		childOne.addContent (grandchildOne);
		
		// Second parent
		final Element parentTwo = new Element ("P2");

		// Second child
		final Element childTwo = new Element ("C");
		childTwo.setAttribute ("A", "2");
		parentTwo.addContent (childTwo);
		
		// Second grandchild
		final Element grandchildTwo = new Element ("G");
		grandchildTwo.setAttribute ("B", "5");
		childTwo.addContent (grandchildTwo);
		
		// Run method
		assertSame (grandchildTwo, new ListOfXmlDocumentsImpl ().findMatchingChild (grandchildOne, parentOne, parentTwo));
	}
}