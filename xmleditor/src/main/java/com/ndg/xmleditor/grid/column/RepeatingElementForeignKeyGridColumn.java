package com.ndg.xmleditor.grid.column;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.ComplexTypeEx;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * A "foreign key" column, which uses an element value from this record to locate a record of another entity, then shows the value of an element from that record
 */
public class RepeatingElementForeignKeyGridColumn extends ForeignKeyGridColumn
{
	/** The title to display in the header above this column */
	private final String columnHeading;
	
	/**
	 * Creates a grid column that uses an element from this record as a foreign key to look up another record, and displays a particular element from each record
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD 
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anIdentifyingElement The xsd:attribute node we are referring to
	 * @param aTargetElement The xsd:element node on the referenced entity
	 * @param aTargetEntity The xsd:complexType node of the referenced entity
	 * @param aContainer The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document
	 * @param sourceElement The xsd:element node for the foreign key value on this record
	 */
	public RepeatingElementForeignKeyGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments,
		final AttributeEx anIdentifyingElement, final LocalElementEx aTargetElement, final ComplexTypeEx aTargetEntity, final Element aContainer,
		final LocalElementEx sourceElement)
	{
		super (aTypeDefinition, aXmlDocuments, anIdentifyingElement, aTargetElement, aTargetEntity, aContainer);

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
		return record.getText ();
	}
}