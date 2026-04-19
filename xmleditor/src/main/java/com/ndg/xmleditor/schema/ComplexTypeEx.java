package com.ndg.xmleditor.schema;

import java.util.List;

import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.ComplexContent;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.editor.XmlEditorException;

/**
 * Describes operations that both TopLevelComplexTypeEx and LocalComplexTypeEx must support
 */
public interface ComplexTypeEx
{
	/**
	 * @return Singular description of an entity, used as the caption when editing a record from this entity
	 */
	public String getSingularName ();

	/**
	 * @return Plural description of an entity, used as the text on the menu option if a top level entity, or on the >> button if a child entity
	 */
	public String getPluralName ();

	/**
	 * @return For top level entities, specifies if the option for the entity will appear on a submenu; if null, the option will appear directly under the edit menu
	 */
	public String getSubMenu ();
	
	/**
	 * @return Long description (contents of annotation-documentation tags) if there is one, otherwise null
	 */
	public String getLongDescription ();
	
	/**
	 * @return List of all attributes for this complex type
	 */
	public List<AttributeEx> getAttributes ();
	
	/**
	 * @param attrName Attribute name to search for
	 * @return Attribute with this name, or null if not found
	 */
	public AttributeEx findAttribute (final String attrName);
	
	/**
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return List of all elements for this complex type, i.e. elements where maxOccurs != "unbounded" and its a simple type
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public List<LocalElementEx> getElements (final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException;
	
	/**
	 * @param elemName Element name to search for
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return Element with this name, or null if not found
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public LocalElementEx findElement (final String elemName, final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException;
	
	/**
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return List of child entities under this sequence, i.e. elements where maxOccurs = "unbounded" or its a complex type
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public List<LocalElementEx> getChildEntities (final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException;

	/**
	 * @return List of both elements and child entities under this sequence, i.e. all elements regardless of whether maxOccurs = "1" or "unbounded"
	 */
	public List<LocalElementEx> getAllElements ();
	
	/**
	 * @param entityName Entity name to search for
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return Child entity with this name, or null if not found
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	public LocalElementEx findChildEntity (final String entityName, final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException;

	/**
	 * @param foreignTopLevelElementType Type of the top level element of another schema
	 * @return Whether or not this schema includes an element ref to the given other namespaceURI
	 */
	public boolean includesElementRefPlaceholder (final QName foreignTopLevelElementType);
	
	/*
	 * Methods implemented by the xsd2java classes
	 */
	
	/**
	 * @return Name (local part only) of this complex type; inlined complex types tend to be anonymous so this will return null
	 */
	public String getName ();
	
	/**
	 * @return complexContent under this complexType, present if this extends from another complexType
	 */
	public ComplexContent getComplexContent ();
}