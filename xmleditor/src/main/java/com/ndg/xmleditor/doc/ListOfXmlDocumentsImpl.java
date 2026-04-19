package com.ndg.xmleditor.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.w3._2001.xmlschema.ComplexContent;
import org.w3._2001.xmlschema.ExtensionType;
import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Restriction;
import org.w3._2001.xmlschema.Union;

import com.ndg.xmleditor.constants.XmlEditorAppInfo;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.ComplexTypeEx;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.schema.SchemaEx;
import com.ndg.xmleditor.schema.SimpleTypeEx;
import com.ndg.xmleditor.schema.TopLevelComplexTypeEx;

/**
 * Adds operations for the editor that require the whole set of XML documents and corresponding XSDs to operate over
 */
public final class ListOfXmlDocumentsImpl extends ArrayList<XmlDocument> implements ListOfXmlDocuments
{
	/**
	 * @param namespaceURI Namespace URI to search for
	 * @return Document with requested namespace prefix
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public final XmlDocument findDocumentWithNamespaceURI (final String namespaceURI)
		throws XmlEditorException
	{
		XmlDocument result = null;
		final Iterator<XmlDocument> iter = iterator ();
		while ((result == null) && (iter.hasNext ()))
		{
			final XmlDocument doc = iter.next ();
			if (doc.getXsd ().getTargetNamespace ().equals (namespaceURI))
				result = doc;
		}

		if (result == null)
			throw new XmlEditorException ("Document with namespace URI \"" + namespaceURI + "\" not found");

		return result;
	}

	/**
	 * Checks whether a type represents a complex type or a simple type
	 * @param type The name of the type including the namespace prefix
	 * @return Whether this is a complex type (false means its a simple type)
	 * @throws XmlEditorException If the namespace prefix cannot be found
	 */
	@Override
	public final boolean isComplexType (final QName type)
		throws XmlEditorException
	{
		TopLevelComplexTypeEx typeDefinition = null;
		if (!type.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI))
		{
			// Find the correct XSD
			final XmlDocument xsd = findDocumentWithNamespaceURI (type.getNamespaceURI ());
	
			// Find the type definition, if its a complex type
			final Iterator<OpenAttrs> iter = xsd.getXsd ().getSimpleTypeOrComplexTypeOrGroup ().iterator ();
			while ((typeDefinition == null) && (iter.hasNext ()))
			{
				final OpenAttrs obj = iter.next ();
				if (obj instanceof TopLevelComplexTypeEx)
				{
					final TopLevelComplexTypeEx thisComplexType = (TopLevelComplexTypeEx) obj;
					if (thisComplexType.getName ().equals (type.getLocalPart ()))
						typeDefinition = thisComplexType;
				}
			}
		}
		
		return (typeDefinition != null);
	}
	
	/**
	 * Finds the definition for a complex type, across any of the XSDs
	 * @param type The name of the type including the namespace prefix
	 * @return The Element representing the definition of this complex type
	 * @throws XmlEditorException If the complex type is not found
	 */
	@Override
	public final ComplexTypeReference findDefinitionOfComplexType (final QName type)
		throws XmlEditorException
	{
		// Find the correct XSD
		final XmlDocument xsd = findDocumentWithNamespaceURI (type.getNamespaceURI ());

		// Find the type definition
		TopLevelComplexTypeEx typeDefinition = null;
		final Iterator<OpenAttrs> iter = xsd.getXsd ().getSimpleTypeOrComplexTypeOrGroup ().iterator ();
		while ((typeDefinition == null) && (iter.hasNext ()))
		{
			final OpenAttrs obj = iter.next ();
			if (obj instanceof TopLevelComplexTypeEx)
			{
				final TopLevelComplexTypeEx thisComplexType = (TopLevelComplexTypeEx) obj;
				if (thisComplexType.getName ().equals (type.getLocalPart ()))
					typeDefinition = thisComplexType;
			}
		}
		
		if (typeDefinition == null)
			throw new XmlEditorException ("Cannot find definition in XSD file for complex type \"" + type + "\"");

		return new ComplexTypeReference (xsd, typeDefinition);
	}

	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @return List containing all of the xsd:complexType definitions that this type inherits from; the final type will always be the last in the list
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	@Override
	public final List<ComplexTypeReference> determineInheritancePath (final ComplexTypeReference finalTypeDefinition)
		throws XmlEditorException
	{
		// First create the list and add the final type into it
		final List<ComplexTypeReference> inheritancePath = new ArrayList<ComplexTypeReference> ();
		inheritancePath.add (finalTypeDefinition);

		// Then recursively check this type defintion to see if it is an extension of another complex type
		ComplexTypeReference typeDefinitionToCheck = finalTypeDefinition;
		while (typeDefinitionToCheck != null)
		{
			ComplexTypeReference parentTypeDefinition = null;

			final ComplexContent complexContent = typeDefinitionToCheck.getComplexTypeDefinition ().getComplexContent ();
			if (complexContent != null)
			{
				final ExtensionType extension = complexContent.getExtension ();
				if (extension != null)
				{
					final QName parentTypeName = extension.getBase ();
					parentTypeDefinition = findDefinitionOfComplexType (parentTypeName);

					// Add it
					inheritancePath.add (0, parentTypeDefinition);
				}
			}

			// Does this parent itself have a parent?
			typeDefinitionToCheck = parentTypeDefinition;
		}

		return inheritancePath;
	}

	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @param attributeName Name of attribute we are searching for
	 * @return Attribute definition element
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	@Override
	public final AttributeEx findAttributeForComplexTypeOrParent (final ComplexTypeReference finalTypeDefinition, final String attributeName)
		throws XmlEditorException
	{
		AttributeEx attributeDef = null;
		final Iterator<ComplexTypeReference> inheritancePath = determineInheritancePath (finalTypeDefinition).iterator ();
		while ((attributeDef == null) && (inheritancePath.hasNext ()))
		{
			final ComplexTypeEx thisComplexType = inheritancePath.next ().getComplexTypeDefinition ();
			attributeDef = thisComplexType.findAttribute (attributeName);
		}

		if (attributeDef == null)
			throw new XmlEditorException ("findAttributeForComplexTypeOrParent could not find definition for attribute " + attributeName + " on any parent");

		return attributeDef;
	}
	
	/**
	 * @param finalTypeDefinition xsd:complexType definition of the record we want to edit
	 * @param elementName Name of element we are searching for
	 * @return Element definition
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	@Override
	public final LocalElementEx findElementForComplexTypeOrParent (final ComplexTypeReference finalTypeDefinition, final String elementName)
		throws XmlEditorException
	{
		LocalElementEx elementDef = null;
		final Iterator<ComplexTypeReference> inheritancePath = determineInheritancePath (finalTypeDefinition).iterator ();
		while ((elementDef == null) && (inheritancePath.hasNext ()))
		{
			final ComplexTypeEx thisComplexType = inheritancePath.next ().getComplexTypeDefinition ();
			elementDef = thisComplexType.findElement (elementName, this);
		}

		if (elementDef == null)
			throw new XmlEditorException ("findElementForComplexTypeOrParent could not find definition for element " + elementName + " on any parent");

		return elementDef;
	}
	
	/**
	 * Takes a user defined type such as momimesvr:experiencelevelnumber and resolves it until we reach a base xsd: data type, such as xsd:unsignedByte
	 * @param dataType The user defined type such as momimesvr:experiencelevelnumber
	 * @return Base xsd: data type, such as xsd:unsignedByte, including the xsd: prefix
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	@Override
	public final QName determineUltimateSimpleType (final QName dataType)
		throws XmlEditorException
	{
		QName thisDataType = dataType;

		// What's the namespace of the data type we have now?
		while ((thisDataType != null) && (!thisDataType.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)))
		{
			final SchemaEx xsd = findDocumentWithNamespaceURI (thisDataType.getNamespaceURI ()).getXsd ();
			final SimpleTypeEx simpleType = xsd.findTopLevelSimpleType (thisDataType.getLocalPart ());

			if (simpleType == null)
				throw new XmlEditorException ("XmlEditorUtils.determineUltimateSimpleType: While searching for the ultimate simple type of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which cannot be found");

			final Restriction restriction = simpleType.getRestriction ();
			final Union union = simpleType.getUnion ();
			if (restriction != null)
			{
				final QName base = restriction.getBase ();
	
				if (base == null)
					throw new XmlEditorException ("XmlEditorUtils.determineUltimateSimpleType: While searching for the ultimate simple type of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found and has an xsd:restriction element, but has no base type");
	
				thisDataType = base;
			}
			else if (union != null)
			{
				if ((union.getMemberTypes () == null) || (union.getMemberTypes ().size () == 0))
					throw new XmlEditorException ("XmlEditorUtils.determineUltimateSimpleType: While searching for the ultimate simple type of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found and has an xsd:union, but no types to union are specified");
				
				thisDataType = union.getMemberTypes ().get (0);
			}
			else
				throw new XmlEditorException ("XmlEditorUtils.determineUltimateSimpleType: While searching for the ultimate simple type of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found but has no xsd:restriction or xsd:union element");
		}

		return thisDataType;
	}

	/**
	 * Takes a user defined type such as momimesvr:roundingdirectionid and resolved it until we find a type with xsd:enumeration elements defined for it
	 * @param dataType The user defined type such as momimesvr:roundingdirectionid
	 * @param includeBlankRecordAtStart Whether to insert an empty record at the start of the returned array
	 * @return Two dimensional array of value-description pairs for the found enumerations, or null if no enumerations were found
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	@Override
	public final String [] [] findEnumerationsForType (final QName dataType, final boolean includeBlankRecordAtStart)
		throws XmlEditorException
	{
		String [] [] result = null;
		QName thisDataType = dataType;

		// If we reach a data type with an xsd: prefix, this obviously won't have any enumerations, nor can we search any higher, so give up
		while ((result == null) && (!thisDataType.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)))
		{
			final SchemaEx xsd = findDocumentWithNamespaceURI (thisDataType.getNamespaceURI ()).getXsd ();
			final SimpleTypeEx simpleType = xsd.findTopLevelSimpleType (thisDataType.getLocalPart ());

			final Restriction restriction = simpleType.getRestriction ();
			final Union union = simpleType.getUnion ();
			if (restriction != null)
			{
				result = simpleType.getEnumerations (includeBlankRecordAtStart);
	
				if (result == null)
				{
					// Didn't find an enumeration, so keep looking up the type tree
					final QName base = restriction.getBase ();
	
					if (base == null)
						throw new XmlEditorException ("XmlEditorUtils.findEnumerationsForType: While searching for enumeration values of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found and has an xsd:restriction element, but has no base type");
	
					thisDataType = base;
				}
			}
			else if (union != null)
			{
				if ((union.getMemberTypes () == null) || (union.getMemberTypes ().size () == 0))
					throw new XmlEditorException ("XmlEditorUtils.findEnumerationsForType: While searching for enumeration values of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found and has an xsd:union, but no types to union are specified");
				
				thisDataType = union.getMemberTypes ().get (0);
			}
			else
				throw new XmlEditorException ("XmlEditorUtils.findEnumerationsForType: While searching for enumeration values of \"" + dataType + "\", encountered type \"" + thisDataType + "\" which is found but has no xsd:restriction or xsd:union element");
		}

		return result;
	}

	/**
	 * For a foreign key reference to another entity, derives the list of fields on the referred to entity which should be displayed
	 * @param referringField The xsd:element or xsd:attribute node for the foreign key field that refers to the other entity
	 * @param refersToEntity The xsd:complexType node for the referred to entity
	 * @return List of xsd:element nodes for the field definitions under the referred to entity which should be displayed
	 * @throws XmlEditorException If one of the parent types is not found
	 */
	@Override
	public final List<LocalElementEx> getReferencedFieldList (final FieldEx referringField, final ComplexTypeReference refersToEntity)
		throws XmlEditorException
	{
		final List<LocalElementEx> finalList = new ArrayList<LocalElementEx> ();

		// Check if we want any at all
		if (referringField.isShowReferencedColumns ())
		{
			// Check if a specific list is defined
			final List<String> restrictedList = referringField.getReferencedFields ();

			// Get the actual list of fields under the entity
			final List<ComplexTypeReference> inheritancePath = determineInheritancePath (refersToEntity);
			for (final ComplexTypeReference refersToEntityComplexType : inheritancePath)
			{
				// If we have explicitly named fields, then accept them even if they're "unbounded".
				// If we don't have explicitly named fields and just including every FK column, ignore "unbounded".
				final List<LocalElementEx> elementsToCheck;
				if (restrictedList == null)
					elementsToCheck = refersToEntityComplexType.getComplexTypeDefinition ().getElements (this);
				else
					elementsToCheck = refersToEntityComplexType.getComplexTypeDefinition ().getAllElements ();
				
				for (final LocalElementEx thisRefersToElement : elementsToCheck)
				{
					// What's the tag for this field?
					final String referredToFieldTag = thisRefersToElement.getName ();
					if ((referredToFieldTag == null) || (restrictedList == null) || (restrictedList.contains (referredToFieldTag)))
						finalList.add (thisRefersToElement);
				}
			}
		}

		return finalList;
	}

	/**
	 * This is a key routine for making the edited XML file conform to the XSD.  When creating a new element, it determines the
	 * location in the XML file to add the new entry, so that the entry matches the xsd:sequence defined in the XSD.
	 * @param typeDefinition The xsd:complexType definition for the container (if the element being added is a new record, this would typically be the main type definition; if adding a field, this would be the entity definition)
	 * @param container The container where the new element will be inserted (if the element being added is a new record, this would typically be the root node of the main XML document; if adding a field, this would be the record)
	 * @param elementToAdd The XML tag of the element to add
	 * @return Index to pass to addContent so that the sequence of elements under the container matches the XSD
	 * @throws XmlEditorException If the type definition doesn't include an xsd:sequence entry
	 */
	@Override
	public final int determineElementInsertionPoint (final ComplexTypeReference typeDefinition, final Element container, final String elementToAdd)
		throws XmlEditorException
	{
		// If there's other elements with the same tag then then its easy, we just add it after last one
		// (This isn't too appropriate when adding field elements to a record, but it does no harm)
		int insertionPoint;
		final List<Element> otherRecords = container.getChildren (elementToAdd);
		if (otherRecords.size () > 0)
		{
			final Element lastCurrentRecord = otherRecords.get (otherRecords.size () - 1);
			insertionPoint = container.indexOf (lastCurrentRecord) + 1;
		}
		else
		{
			// Get a list of all fields - need to take into account elements both on this entity and all parent(s)
			final List<LocalElementEx> elementList = new ArrayList<LocalElementEx> ();
			final List<ComplexTypeReference> inheritancePath = determineInheritancePath (typeDefinition);
			for (final ComplexTypeReference thisType : inheritancePath)
				elementList.addAll (thisType.getComplexTypeDefinition ().getAllElements ());
			
			// Find the element we are adding in the list
			int index = -1;
			int check = 0;
			while ((index < 0) && (check < elementList.size ()))
			{
				final LocalElementEx thisEntity = elementList.get (check);
				if (thisEntity.getName ().equals (elementToAdd))
					index = check;
				else
					check++;
			}
			
			if (index < 0)
				throw new XmlEditorException ("determineElementInsertionPoint: Tried to add element \"" + elementToAdd + "\" to record \"" +
					container.getName () + "\" but that element doesn't seem to exist in the XSD definition for that entity");
			
			// No go forwards, looking through elements AFTER the one we're inserting to see if we find any.
			// If we do, we slot the new element in just before it.
			insertionPoint = -1;
			while ((insertionPoint < 0) && (index < elementList.size ()))
			{
				final LocalElementEx thisEntity = elementList.get (index);
				final Element laterRecord = container.getChild (thisEntity.getName ());
				if (laterRecord != null)
					insertionPoint = container.indexOf (laterRecord);
				else
					index++;
			}

			// If there is no later record at all, then add the new record at the end of the container's children
			if (insertionPoint < 0)
				insertionPoint = container.getContentSize ();
		}

		return insertionPoint;
	}
	
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
	@Override
	public final void updateFieldValue (final Element recordBeingEdited, final ComplexTypeReference typeDefinition, final FieldEx fieldDefinition, final String fieldValue)
		throws XmlEditorException
	{
		String useFieldValue = fieldValue;
		if (fieldValue != null)
			if (fieldValue.equals (""))
				useFieldValue = null;

		if (fieldDefinition instanceof AttributeEx)
		{
			// Attribute - straightforward, since we don't need to worry about order
			final String attributeName = fieldDefinition.getName ();
			if (useFieldValue == null)
				recordBeingEdited.removeAttribute (attributeName);
			else
				recordBeingEdited.setAttribute (attributeName, useFieldValue);
		}
		else
		{
			// Element
			final String elementName = fieldDefinition.getName ();

			// If we've got no type definition then this is a 0..unbounded simple type that we've drilled down into,
			// in which case the element to update is the record itself
			if (typeDefinition == null)
				recordBeingEdited.setText (useFieldValue);
			
			// Element - if its value is now null, this is straightforward since we don't need to
			// worry about ordering - just stripping out the value if it was previously present
			else if (useFieldValue == null)
				recordBeingEdited.removeChildren (elementName);
			
			else
			{
				// Element that now has a value
				// See if it had a value before
				final Element existingElement = recordBeingEdited.getChild (elementName);
				if (existingElement != null)
					existingElement.setText (useFieldValue);
				else
				{
					// This is the complex situation - we didn't have a value for this element before,
					// but now we do, so we have to figure out the correct position to insert it
					final int insertionPoint = determineElementInsertionPoint (typeDefinition, recordBeingEdited, elementName);
					final Element newFieldElement = new Element (elementName);
					newFieldElement.setText (useFieldValue);
					recordBeingEdited.addContent (insertionPoint, newFieldElement);
				}
			}
		}
	}

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
	@Override
	public final void updateFieldValue (final Element recordBeingEdited, final ComplexTypeReference typeDefinition, final String fieldTag, final String fieldValue)
		throws XmlEditorException
	{
		// Look for it as an attribute
		FieldEx fieldDefinition = null;
		try
		{
			fieldDefinition = findAttributeForComplexTypeOrParent (typeDefinition, fieldTag);
		}
		catch (final XmlEditorException e)
		{
			// Maybe its an element
		}
		
		if (fieldDefinition == null)
			
			// Look for it as an element
			fieldDefinition = findElementForComplexTypeOrParent (typeDefinition, fieldTag);

		updateFieldValue (recordBeingEdited, typeDefinition, fieldDefinition, fieldValue);
	}
	
	/**
	 * Attempts to find a child under a 2nd parent that matches (i.e. same element name & attributes) a child under a 1st parent.
	 * The child can be an arbitrary depth under the parent (i.e. could be a grandchild, great-grandchild, etc.)
	 * This is used e.g. to find the languagecategory in the server XML file which matches the one we have in the language XML file
	 * @param childOne The child to match against
	 * @param parentOne The parent to match against
	 * @param parentTwo The parent to search under
	 * @return Matching child from under parentTwo, or null if one can't be found
	 */
	@Override
	public final Element findMatchingChild (final Element childOne, final Element parentOne, final Element parentTwo)
	{
		// First get a list of all the parents, in reverse order
		final List<Element> parentOnes = new ArrayList<Element> ();
		Element searchChildOne = childOne;
		while ((searchChildOne != null) && (searchChildOne != parentOne))
		{
			parentOnes.add (0, searchChildOne);
			searchChildOne = searchChildOne.getParentElement ();
		}

		// Then try to find matching records under parentTwo
		// If there's an error, childTwo gets set to null so the loop exits
		Element childTwo = parentTwo;
		final Iterator<Element> searchParentOnes = parentOnes.iterator ();
		while ((childTwo != null) && (searchParentOnes.hasNext ()))
		{
			final Element thisParentOne = searchParentOnes.next ();

			// What attribute & value are we searching for
			if (thisParentOne.getAttributes ().size () != 1)
				childTwo = null;
			else
			{
				final Attribute attributeToMatch = thisParentOne.getAttributes ().get (0);

				// Get a list of matching elements
				final Iterator<Element> matchingElements = childTwo.getChildren (thisParentOne.getName ()).iterator ();
				boolean found = false;
				while ((!found) && (matchingElements.hasNext ()))
				{
					final Element potentialMatch = matchingElements.next ();

					final String attributeValue = potentialMatch.getAttributeValue (attributeToMatch.getName ());
					if (attributeValue != null)
						if (attributeValue.equals (attributeToMatch.getValue ()))
						{
							childTwo = potentialMatch;
							found = true;
						}
				}

				if (!found)
					childTwo = null;
			}
		}

		return childTwo;
	}

	/**
	 * Checks the XSD to see if a particular field is a foreign key reference to another entity
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param parentRecord The parent record containing the record we are searching for references from, or null if the record we are searching for references from it a top level entity
	 * @param fieldXpath The xpath to the field (for elements, this is simply the field tag, for attributes it must be prefixed by a @)
	 * @return Details of the foreign key reference if there is one, if not then null
	 * @throws XmlEditorException If a namespace prefix is encountered that cannot be found
	 */
	@Override
	public final XmlForeignKeyReference checkIfFieldIsForeignKey (final LocalElementEx [] parentEntityElements,
		final LocalElementEx entityElement, final Element parentRecord, final String fieldXpath)
		throws XmlEditorException
	{
		// The FK could be specified on any parent, or at the top level - so try the lowest level (most immediate) parent first
		// e.g. example of reference at top level would be transaction>currency, where there is only one list of currencies
		// e.g. example of reference at a lower level would be transaction>regularTransaction, where regularTransactions are different from one account to the next
		XmlForeignKeyReference match = null;
		if (parentEntityElements != null)
		{
			// Passing in parentRecord instead of the current record allows us to find the correct parent containers even when hitting "New"
			Element thisParentRecord = parentRecord;
			int parentNo = parentEntityElements.length - 1;
			while ((match == null) && (parentNo >= 0))
			{
				final LocalElementEx thisParentElement = parentEntityElements [parentNo];
				final String selectorXpath = entityElement.toXpath (parentEntityElements, thisParentElement);
				final Keyref thisKeyRef = thisParentElement.findKeyRef (selectorXpath, fieldXpath);
				
				if (thisKeyRef != null)
				{
					// Found a matching xsd:keyref, now look for the xsd:key it refers to
					final Keybase primaryKey = thisParentElement.findKey (thisKeyRef.getRefer ().getLocalPart ());
					if (primaryKey != null)
						match = new XmlForeignKeyReference (primaryKey, get (0), parentRecord);
				}

				parentNo--;
				thisParentRecord = (Element) thisParentRecord.getParent ();
			}
		}

		// If we failed to find an FK at any of the parent level, then search the top level element
		final String desiredSelectorXpath = entityElement.toXpath (parentEntityElements, null);
		Keyref thisKeyRef = get (0).getXsd ().getTopLevelElement ().findKeyRef (desiredSelectorXpath, fieldXpath);
		
		// Also look for keys to the element itself, with . as the field xpath
		if (thisKeyRef == null)
			thisKeyRef = get (0).getXsd ().getTopLevelElement ().findKeyRef (desiredSelectorXpath + "/" + fieldXpath, ".");
		
		if (thisKeyRef == null)
			thisKeyRef = get (0).getXsd ().getTopLevelElement ().findKeyRef
				(XmlEditorAppInfo.KEY_REF_SELECTOR_XPATH_PREFIX_MULTI_LEVEL_FK_TO_SECONDARY_DB + desiredSelectorXpath, fieldXpath);

		if (thisKeyRef != null)
		{
			// Found a matching xsd:keyref, now look for the xsd:key it refers to
			// Find which database the PK is in - could be the same DB as the keyref or a different one
			final XmlDocument pkDatabase = findDocumentWithNamespaceURI (thisKeyRef.getRefer ().getNamespaceURI ());

			// If a reference to a multi-level primary key, we need to drill down through each level until we get to the bottom level
			if (thisKeyRef.getSelector ().getXpath ().equals (XmlEditorAppInfo.KEY_REF_SELECTOR_XPATH_PREFIX_MULTI_LEVEL_FK_TO_SECONDARY_DB + desiredSelectorXpath))
			{
				final Element container = findMatchingChild (parentRecord, get (0).getXml (), pkDatabase.getXml ());

				if (container != null)
				{
					// Tricky part is locating the key in the XSD, because it won't be in the regular place with the top level keys
					final String parentEntityName = desiredSelectorXpath.substring (0, desiredSelectorXpath.indexOf ("/"));
					final LocalElementEx parentEntityElement = pkDatabase.getXsd ().getTopLevelTypeDefinition ().findChildEntity (parentEntityName, this);
					
					if (parentEntityElement != null)
					{
						final Keybase primaryKey = parentEntityElement.findKey (thisKeyRef.getRefer ().getLocalPart ());
						if (primaryKey != null)
							match = new XmlForeignKeyReference (primaryKey, pkDatabase, container);
					}
				}
			}
			else
			{
				// Regular single level FK
				final Keybase primaryKey = pkDatabase.getXsd ().getTopLevelElement ().findKey (thisKeyRef.getRefer ().getLocalPart ());
				if (primaryKey != null)
					match = new XmlForeignKeyReference (primaryKey, pkDatabase, pkDatabase.getXml ());
			}
		}

		return match;
	}
}