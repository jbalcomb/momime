package com.ndg.xmleditor.doc;

import java.util.List;

import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * Adds operations for the editor that require the whole set of XML documents and corresponding XSDs to operate over
 */
public interface ListOfXmlDocuments extends List<XmlDocument>
{
	/**
	 * @param namespaceURI Namespace URI to search for
	 * @return Document with requested namespace prefix
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public XmlDocument findDocumentWithNamespaceURI (final String namespaceURI)
		throws XmlEditorException;
	
	/**
	 * Checks whether a type represents a complex type or a simple type
	 * @param type The name of the type including the namespace prefix
	 * @return Whether this is a complex type (false means its a simple type)
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public boolean isComplexType (final QName type)
		throws XmlEditorException;
	
	/**
	 * Finds the definition for a complex type, across any of the XSDs
	 * @param type The name of the type including the namespace prefix
	 * @return The Element representing the definition of this complex type
	 * @throws XmlEditorException If the complex type is not found
	 */
	public ComplexTypeReference findDefinitionOfComplexType (final QName type)
		throws XmlEditorException;
	
	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @return List containing all of the xsd:complexType definitions that this type inherits from; the final type will always be the last in the list
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	public List<ComplexTypeReference> determineInheritancePath (final ComplexTypeReference finalTypeDefinition)
		throws XmlEditorException;
	
	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @param attributeName Name of attribute we are searching for
	 * @return Attribute definition element
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	public AttributeEx findAttributeForComplexTypeOrParent (final ComplexTypeReference finalTypeDefinition, final String attributeName)
		throws XmlEditorException;

	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @param elementName Name of element we are searching for
	 * @return Element definition
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	public LocalElementEx findElementForComplexTypeOrParent (final ComplexTypeReference finalTypeDefinition, final String elementName)
		throws XmlEditorException;
	
	/**
	 * Takes a user defined type such as momimesvr:experiencelevelnumber and resolves it until we reach a base xsd: data type, such as xsd:unsignedByte
	 * @param dataType The user defined type such as momimesvr:experiencelevelnumber
	 * @return Base xsd: data type, such as xsd:unsignedByte, including the xsd: prefix
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	public QName determineUltimateSimpleType (final QName dataType)
		throws XmlEditorException;

	/**
	 * Takes a user defined type such as momimesvr:roundingdirectionid and resolved it until we find a type with xsd:enumeration elements defined for it
	 * @param dataType The user defined type such as momimesvr:roundingdirectionid
	 * @param includeBlankRecordAtStart Whether to insert an empty record at the start of the returned array
	 * @return Two dimensional array of value-description pairs for the found enumerations, or null if no enumerations were found
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	public String [] [] findEnumerationsForType (final QName dataType, final boolean includeBlankRecordAtStart)
		throws XmlEditorException;
	
	/**
	 * For a foreign key reference to another entity, derives the list of fields on the referred to entity which should be displayed
	 * @param referringField The xsd:element or xsd:attribute node for the foreign key field that refers to the other entity
	 * @param refersToEntity The xsd:complexType node for the referred to entity
	 * @return List of xsd:element nodes for the field definitions under the referred to entity which should be displayed
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	public List<LocalElementEx> getReferencedFieldList (final FieldEx referringField, final ComplexTypeReference refersToEntity)
		throws XmlEditorException;

	/**
	 * This is a key routine for making the edited XML file conform to the XSD.  When creating a new element, it determines the
	 * location in the XML file to add the new entry, so that the entry matches the xsd:sequence defined in the XSD.
	 * @param typeDefinition The xsd:complexType definition for the container (if the element being added is a new record, this would typically be the main type definition; if adding a field, this would be the entity definition)
	 * @param container The container where the new element will be inserted (if the element being added is a new record, this would typically be the root node of the main XML document; if adding a field, this would be the record)
	 * @param elementToAdd The XML tag of the element to add
	 * @return Index to pass to addContent so that the sequence of elements under the container matches the XSD
	 * @throws XmlEditorException If the type definition doesn't include an xsd:sequence entry
	 */
	public int determineElementInsertionPoint (final ComplexTypeReference typeDefinition, final Element container, final String elementToAdd)
		throws XmlEditorException;

	/**
	 * This is a key routine for making the edited XML file conform to the XSD.  It updates the value of a field within an XML record.
	 * It handled checking whether the field is an xsd:attribute or xsd:element, and if it is an element that was not previously
	 * present, checking the xsd:complexType of the record to find the correct sequence for the element to be inserted.
	 * @param recordBeingEdited The record to update the field value against
	 * @param typeDefinition The xsd:complexType entry from the XSD describing the record
	 * @param fieldDefinition The xsd:attribute or xsd:element entry from the XSD describing the field value to update
	 * @param fieldValue The value to update the field to
	 * @throws XmlEditorException If the field is an element, and the type definition doesn't include an xsd:sequence entry
	 */
	public void updateFieldValue (final Element recordBeingEdited, final ComplexTypeReference typeDefinition, final FieldEx fieldDefinition, final String fieldValue)
		throws XmlEditorException;

	/**
	 * Simpler version to call if only updating a single value - will do the work of finding the field definition as well
	 * If updating the same field on a whole bunch of records, better to find the field definition yourself and call the
	 * other version - repeatedly calling this version will repeatedly have to search for the field definition, which will make it slow
	 * @param recordBeingEdited The record to update the field value against
	 * @param typeDefinition The xsd:complexType entry from the XSD describing the record
	 * @param fieldTag The name/XML tag of the field to update
	 * @param fieldValue The value to update the field to
	 * @throws XmlEditorException If the field definition cannot be found
	 */
	public void updateFieldValue (final Element recordBeingEdited, final ComplexTypeReference typeDefinition, final String fieldTag, final String fieldValue)
		throws XmlEditorException;
	
	/**
	 * Attempts to find a child under a 2nd parent that matches (i.e. same element name & attributes) a child under a 1st parent.
	 * The child can be an arbitrary depth under the parent (i.e. could be a grandchild, great-grandchild, etc.)
	 * This is used e.g. to find the languagecategory in the server XML file which matches the one we have in the language XML file
	 * @param childOne The child to match against
	 * @param parentOne The parent to match against
	 * @param parentTwo The parent to search under
	 * @return Matching child from under parentTwo, or null if one can't be found
	 */
	public Element findMatchingChild (final Element childOne, final Element parentOne, final Element parentTwo);

	/**
	 * Checks the XSD to see if a particular field is a foreign key reference to another entity
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param parentRecord The parent record containing the record we are searching for references from, or null if the record we are searching for references from it a top level entity
	 * @param fieldXpath The xpath to the field (for elements, this is simply the field tag, for attributes it must be prefixed by a @)
	 * @return Details of the foreign key reference if there is one, if not then null
	 * @throws XmlEditorException If a namespace prefix is encountered that cannot be found
	 */
	public XmlForeignKeyReference checkIfFieldIsForeignKey (final LocalElementEx [] parentEntityElements,
		final LocalElementEx entityElement, final Element parentRecord, final String fieldXpath)
		throws XmlEditorException;
}