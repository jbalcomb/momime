package com.ndg.xmleditor.jdom;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Equivalent static methods to XmlUtils, but using Jdom classes instead of regular DOM interfaces
 */
public final class JdomUtils
{
	/**
	 * Prevent instatiation of this class
	 */
	private JdomUtils ()
	{
	}

	/**
	 * Searches for an XML element with no namespace which has a particular value of a particular attribute
	 * @param element The parent element to search under
	 * @param childNodeName The name of the node to search for, e.g. <person>
	 * @param attributeName The name of the attribute/value to check, e.g. <firstname>
	 * @param attributeValue The value to look for, e.g. Nigel
	 * @return XML node found that meets the requested criteria, or null if none could be found
	 */
	public final static Element findDomChildNodeWithTextAttribute (final Element element, final String childNodeName, final String attributeName, final String attributeValue)
	{
		Element result = null;

		if (element != null)
		{
			final Iterator<Element> children = element.getChildren (childNodeName).iterator ();
			while ((result == null) && (children.hasNext ()))
			{
				final Element thisElement = children.next ();

				final String thisAttributeValue = thisElement.getAttributeValue (attributeName);
				if (thisAttributeValue != null)
					if (thisAttributeValue.equals (attributeValue))
						result = thisElement;
			}
		}

		return result;
	}

	/**
	 * Searches for an XML element with a partcular namespace which has a particular value of a particular attribute
	 * @param element The parent element to search under
	 * @param childNodeName The name of the node to search for, e.g. <person>
	 * @param childNodeNamespace The namespace of the node to search for
	 * @param attributeName The name of the attribute/value to check, e.g. <firstname>
	 * @param attributeValue The value to look for, e.g. Nigel
	 * @return XML node found that meets the requested criteria, or null if none could be found
	 */
	public final static Element findDomChildNodeWithTextAttribute (final Element element, final String childNodeName, final Namespace childNodeNamespace, final String attributeName, final String attributeValue)
	{
		Element result = null;

		if (element != null)
		{
			final Iterator<Element> children = element.getChildren (childNodeName, childNodeNamespace).iterator ();
			while ((result == null) && (children.hasNext ()))
			{
				final Element thisElement = children.next ();

				final String thisAttributeValue = thisElement.getAttributeValue (attributeName);
				if (thisAttributeValue != null)
					if (thisAttributeValue.equals (attributeValue))
						result = thisElement;
			}
		}

		return result;
	}

	/**
	 * Finds all of a specific type of XML record under a certain container, and creates a
	 * 2D String array containing requested fields for every record
	 * The output is then suitable for creating linked combo boxes from
	 * @param container The element where all the XML records are to be found
	 * @param entityTag The XML tag for the entity/records
	 * @param fieldList A list of XML tags of all the attributes and elements we want; any attributes will be in xpath style, prefixed by a @
	 * @param includeBlankRecordAtStart Whether to insert an empty record at the start of the returned array
	 * @return 2D String array containing the field values for all the records
	 */
	public final static String [] [] loadXmlElementsIntoStringArray (final Element container, final String entityTag, final String [] fieldList, final boolean includeBlankRecordAtStart)
	{
		// Size the array
		final List<Element> records = container.getChildren (entityTag);
		int recordCount = records.size ();
		int recordNo = 0;
		if (includeBlankRecordAtStart)
		{
			recordCount++;
			recordNo++;
		}

		// Create the array
		final String [] [] values = new String [fieldList.length] [recordCount];

		// Populate the array
		for (int recordIndex = 0; recordIndex < records.size (); recordIndex++)
		{
			final Element thisRecord = records.get (recordIndex);

			for (int fieldNo = 0; fieldNo < fieldList.length; fieldNo++)
			{
				if (fieldList [fieldNo].substring (0, 1).equals ("@"))
					values [fieldNo] [recordNo] = thisRecord.getAttributeValue (fieldList [fieldNo].substring (1));		// Attribute
				else
				{
					// Element
					final Element valueElement = thisRecord.getChild (fieldList [fieldNo]);
					if (valueElement != null)
					{
						// Normally expect the child tag to just be like <value>this</value> so we just want the childText
						// But sometimes (MoM IME languageText) the text we want might be buried inside another tag
						final List<Element> children = valueElement.getChildren ();
						if (children.size () == 1)
							values [fieldNo] [recordNo] = children.get (0).getText ();
						else
							values [fieldNo] [recordNo] = valueElement.getText ();
					}
				}
			}

			recordNo++;
		}

		// Return the array
		return values;
	}

	/**
	 * @param value Boolean value
	 * @return String representation of boolean value consistent with xsd:boolean data type
	 */
	public final static String xmlBooleanToString (final boolean value)
	{
		String result;

		if (value)
			result = "true";
		else
			result = "false";

		return result;
	}

	/**
	 * @param value String representation of boolean value consistent with xsd:boolean data type
	 * @return Boolean value presented by the string
	 */
	public final static boolean stringToXmlBoolean (final String value)
	{
		if (value == null)
			throw new InvalidParameterException ("stringToXmlBoolean was passed a null value");

		else if (value.equals ("true"))
			return true;

		else if (value.equals ("false"))
			return false;

		else
			throw new InvalidParameterException ("stringToXmlBoolean was passed a value other than \"true\" or \"false\": \"" + value + "\"");
	}
}