package com.ndg.xmleditor.grid.column;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * An "enum" column, which uses an attribute value from this record to look up a description in a two dimensional array of strings
 */
public class AttributeEnumGridColumn extends EnumGridColumn
{
	/** The XML tag/name of the field on the main record */
	private final String sourceAttributeTag;

	/** The title to display in the header above this column */
	private String columnHeading;

	/**
	 * Creates a grid column that uses an element from this record as a foreign key to look up another record, and displays a particular element from each record
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anEnumerations Two dimensional array of enum values and descriptions
	 * @param sourceAttribute The xsd:attribute node for the foreign key value on this record
	 */
	public AttributeEnumGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments, final String [] [] anEnumerations, final AttributeEx sourceAttribute)
	{
		super (aTypeDefinition, aXmlDocuments, anEnumerations);

		// We need the element tag every time we want to look up a value, so determine and store it once only
		sourceAttributeTag = sourceAttribute.getName ();

		// Work out the title to display in the header above this column & store it once, rather than having to re-derive it each time it is needed
		columnHeading = sourceAttribute.getLabel ();
	}

	/**
	 * @return The title to display in the header above this column
	 */
	@Override
	public String getColumnHeading ()
	{
		return columnHeading;
	}

	/**
	 * @param record The XML node representing this record
	 * @return The unresolved column value to for this record, i.e. the foreign key value which we need to look up
	 */
	@Override
	public String getUnresolvedColumnValue (final Element record)
	{
		return record.getAttributeValue (sourceAttributeTag);
	}
}