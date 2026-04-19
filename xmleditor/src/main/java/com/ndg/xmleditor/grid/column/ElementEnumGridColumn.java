package com.ndg.xmleditor.grid.column;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.schema.ComplexTypeEx;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * An "enum" column, which uses an element value from this record to look up a description in a two dimensional array of strings
 */
public class ElementEnumGridColumn extends EnumGridColumn
{
	/** The XML tag/name of the field on the main record */
	private final String sourceElementTag;

	/** The title to display in the header above this column */
	private final String columnHeading;

	/**
	 * Creates a grid column that uses an element from this record as a foreign key to look up another record, and displays a particular element from each record
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anEnumerations Two dimensional array of enum values and descriptions
	 * @param sourceElement The xsd:element node for the foreign key value on this record
	 */
	public ElementEnumGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments, final String [] [] anEnumerations, final LocalElementEx sourceElement)
	{
		super (aTypeDefinition, aXmlDocuments, anEnumerations);

		// We need the element tag every time we want to look up a value, so determine and store it once only
		sourceElementTag = sourceElement.getName ();

		// Work out the title to display in the header above this column & store it once, rather than having to re-derive it each time it is needed
		columnHeading = sourceElement.getLabel ();
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
		return record.getChildText (sourceElementTag);
	}
}