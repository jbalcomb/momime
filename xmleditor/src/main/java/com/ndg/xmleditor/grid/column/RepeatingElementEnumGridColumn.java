package com.ndg.xmleditor.grid.column;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * An "enum" column, for use when drilling down into an 0..unbounded list of enum values
 */
public class RepeatingElementEnumGridColumn extends EnumGridColumn
{
	/** The XML tag/name of this element */
	private final String elementTag;

	/**
	 * @param elementDef The xsd:element definition of the 0..unbounded element
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anEnumerations Two dimensional array of enum values and descriptions
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	public RepeatingElementEnumGridColumn (final LocalElementEx elementDef, final ListOfXmlDocuments aXmlDocuments, final String [] [] anEnumerations)
		throws XmlEditorException
	{
		super (null, aXmlDocuments, anEnumerations);
		
		elementTag = elementDef.getName ();
	}

	/**
	 * @return The title to display in the header above this column
	 */
	@Override
	public final String getColumnHeading ()
	{
		return elementTag;
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