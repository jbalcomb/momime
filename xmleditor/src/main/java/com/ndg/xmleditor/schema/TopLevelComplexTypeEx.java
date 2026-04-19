package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.Annotated;
import org.w3._2001.xmlschema.Documentation;
import org.w3._2001.xmlschema.ExplicitGroup;
import org.w3._2001.xmlschema.LocalElement;
import org.w3._2001.xmlschema.TopLevelComplexType;

import com.ndg.xmleditor.constants.XmlEditorAppInfo;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.editor.XmlEditorException;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class TopLevelComplexTypeEx extends TopLevelComplexType implements ComplexTypeEx
{
	/**
	 * @return Singular description of an entity, used as the caption when editing a record from this entity
	 */
	@Override
	public final String getSingularName ()
	{
		final String value = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_SINGULAR);
		return (value != null) ? value : getName ();
	}

	/**
	 * @return Plural description of an entity, used as the text on the menu option if a top level entity, or on the >> button if a child entity
	 */
	@Override
	public final String getPluralName ()
	{
		final String value = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_PLURAL);
		return (value != null) ? value : getSingularName () + "s";
	}

	/**
	 * @return For top level entities, specifies if the option for the entity will appear on a submenu; if null, the option will appear directly under the edit menu
	 */
	@Override
	public final String getSubMenu ()
	{
		return findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_APPEARS_ON_SUB_MENU);
	}
	
	/**
	 * Searches for attribute values in the editor's namespace, i.e. the extension attributes with nxe: prefix
	 * 
	 * @param attributeName Attribute name to search for
	 * @return Value of the requested attribute, or null if the attribute wasn't found
	 */
	final String findAttributeValue (final String attributeName)
	{
		return getOtherAttributes ().get (new QName (XmlEditorAppInfo.NAMESPACE_URI, attributeName));
	}
	
	/**
	 * @return Long description (contents of annotation-documentation tags) if there is one, otherwise null
	 */
	@Override
	public final String getLongDescription ()
	{
		final StringBuilder desc = new StringBuilder ();
		
		if (getAnnotation () != null)
			for (final Object obj : getAnnotation ().getAppinfoOrDocumentation ())
				if (obj instanceof Documentation)
				{
					final Documentation doc = (Documentation) obj;
					for (final Object obj2 : doc.getContent ())
						if (obj2 instanceof String)
							desc.append ((String) obj2);
				}
		
		return (desc.length () == 0) ? null : desc.toString ();
	}
	
	/**
	 * @return List of all attributes for this complex type
	 */
	@Override
	public final List<AttributeEx> getAttributes ()
	{
		final List<AttributeEx> attributes = new ArrayList<AttributeEx> ();
		
		// Are the attributes directly under the complexType, or under a complexContent-extension?
		final List<Annotated> attrList;
		if ((getComplexContent () != null) && (getComplexContent ().getExtension () != null))
			attrList = getComplexContent ().getExtension ().getAttributeOrAttributeGroup ();
		else
			attrList = getAttributeOrAttributeGroup ();
		
		for (final Annotated anno : attrList)
			if (anno instanceof AttributeEx)
				attributes.add ((AttributeEx) anno);
		
		return attributes;
	}
	
	/**
	 * @param attrName Attribute name to search for
	 * @return Attribute with this name, or null if not found
	 */
	@Override
	public final AttributeEx findAttribute (final String attrName)
	{
		AttributeEx match = null;
		final Iterator<AttributeEx> iter = getAttributes ().iterator ();
		
		while ((match == null) && (iter.hasNext ()))
		{
			final AttributeEx attr = iter.next ();
			if (attrName.equals (attr.getName ()))
				match = attr;
		}
		
		return match;
	}
	
	/**
	 * The sequence may be directly under the complex type, or it may be under complexContent - extension - sequence.
	 * Note this is named findSequence () just to differentiate it from getSequence () which doesn't take complexContent-extension into account.
	 * 
	 * @return Sequence element under the complex type, or null if there isn't one (which is quite valid, we can have entities with attributes only)
	 */
	final ExplicitGroup findSequence ()
	{
		final ExplicitGroup seq;
		if ((getComplexContent () != null) && (getComplexContent ().getExtension () != null) && (getComplexContent ().getExtension ().getSequence () != null))
			seq = getComplexContent ().getExtension ().getSequence ();
		else
			seq = getSequence ();
		
		return seq;
	}

	/**
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return List of all elements for this complex type, i.e. elements where maxOccurs != "unbounded" and its a simple type
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public final List<LocalElementEx> getElements (final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException
	{
		final List<LocalElementEx> elements = new ArrayList<LocalElementEx> ();

		// Don't include child entities, i.e. elements where maxOccurs = "unbounded"
		for (final LocalElementEx elem : getAllElements ())
			if ((!XsdConstants.VALUE_MAX_OCCURS_UNBOUNDED.equalsIgnoreCase (elem.getMaxOccurs ())) &&
				(elem.getType () != null) && (!xmlDocuments.isComplexType (elem.getType ())))
				
				elements.add (elem);
		
		return elements;
	}
	
	/**
	 * @param elemName Element name to search for
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return Element with this name, or null if not found
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public final LocalElementEx findElement (final String elemName, final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException
	{
		LocalElementEx match = null;
		final Iterator<LocalElementEx> iter = getElements (xmlDocuments).iterator ();
		
		while ((match == null) && (iter.hasNext ()))
		{
			final LocalElementEx elem = iter.next ();
			if (elemName.equals (elem.getName ()))
				match = elem;
		}
		
		return match;
	}
	
	/**
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return List of child entities under this sequence, i.e. elements where maxOccurs = "unbounded" or its a complex type
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public List<LocalElementEx> getChildEntities (final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException
	{
		final List<LocalElementEx> childEntities = new ArrayList<LocalElementEx> ();
		
		// Only include child entities, i.e. elements where maxOccurs = "unbounded" or its a complex type
		for (final LocalElementEx elem : getAllElements ())
			if ((XsdConstants.VALUE_MAX_OCCURS_UNBOUNDED.equalsIgnoreCase (elem.getMaxOccurs ())) ||
				((elem.getType () != null) && (xmlDocuments.isComplexType (elem.getType ()))))
				
				childEntities.add (elem);
		
		return childEntities;
	}

	/**
	 * Recursively adds elements to a list, that are either part of the given sequence, or a child sequence any level of depth under it
	 * 
	 * @param seq Sequence containing elements to add
	 * @param elements List to add to
	 */
	final void addElementsToList (final ExplicitGroup seq, final List<LocalElementEx> elements)
	{
		for (final Object obj : seq.getParticle ())
			if (obj instanceof JAXBElement)
			{
				final JAXBElement<?> container = (JAXBElement<?>) obj;
				if (container.getValue () instanceof LocalElementEx)
				{
					final LocalElementEx elem = (LocalElementEx) container.getValue ();
					
					// Only include proper named elements - this is so we ignore element ref placeholders
					if (elem.getName () != null)
						elements.add (elem);
				}

				// Or is it another container, like a choice
				else if (container.getValue () instanceof ExplicitGroup)
					addElementsToList ((ExplicitGroup) container.getValue (), elements);
			}
	}
	
	/**
	 * @return List of both elements and child entities under this sequence, i.e. all elements regardless of whether maxOccurs = "1" or "unbounded"
	 */
	@Override
	public final List<LocalElementEx> getAllElements ()
	{
		final List<LocalElementEx> elements = new ArrayList<LocalElementEx> ();
		
		// Are the elements directly under the complexType, or under a complexContent-extension?
		final ExplicitGroup seq = findSequence ();
		if (seq != null)
			addElementsToList (seq, elements);
		
		return elements;
	}
	
	/**
	 * @param entityName Entity name to search for
	 * @param xmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @return Child entity with this name, or null if not found
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public LocalElementEx findChildEntity (final String entityName, final ListOfXmlDocuments xmlDocuments)
		throws XmlEditorException
	{
		LocalElementEx match = null;
		final Iterator<LocalElementEx> iter = getChildEntities (xmlDocuments).iterator ();
		
		while ((match == null) && (iter.hasNext ()))
		{
			final LocalElementEx elem = iter.next ();
			if (entityName.equals (elem.getName ()))
				match = elem;
		}
		
		return match;
	}

	/**
	 * @param foreignTopLevelElementType Type of the top level element of another schema
	 * @return Whether or not this schema includes an element ref to the given other namespaceURI
	 */
	@Override
	public final boolean includesElementRefPlaceholder (final QName foreignTopLevelElementType)
	{
		boolean found = false;
		final ExplicitGroup seq = findSequence ();
		
		if ((seq != null) && (foreignTopLevelElementType != null))
		{
			final Iterator<Object> iter = seq.getParticle ().iterator ();
			while ((!found) && (iter.hasNext ()))
			{
				final Object obj = iter.next ();
				if (obj instanceof JAXBElement)
				{
					final JAXBElement<?> container = (JAXBElement<?>) obj;
					if (container.getValue () instanceof LocalElement)
					{
						final LocalElement elem = (LocalElement) container.getValue ();
						if (foreignTopLevelElementType.equals (elem.getRef ()))
							found = true;
					}
				}
			}
		}
		
		return found;
	}
}