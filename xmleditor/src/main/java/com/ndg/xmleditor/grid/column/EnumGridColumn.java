package com.ndg.xmleditor.grid.column;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.utils.StringUtils;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.grid.XmlTableModel;
import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * An "enum" column, which uses a value from this record to look up a description in a two dimensional array of strings
 */
abstract class EnumGridColumn extends XmlGridColumn
{
	/** Two dimensional array of enum values and descriptions */
	private final String [] [] enumerations;

	/**
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anEnumerations Two dimensional array of enum values and descriptions
	 */
	public EnumGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments, final String [] [] anEnumerations)
	{
		super (aTypeDefinition, aXmlDocuments);

		enumerations = anEnumerations;
	}

	/**
	 * @return The width to display this column
	 */
	@Override
	public final int getColumnWidth ()
	{
		return 120;
	}

	/**
	 * @return Simple data type of this column
	 */
	@Override
	public final QName getColumnType ()
	{
		return new QName (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI, XsdConstants.VALUE_DATA_TYPE_SINGLE_LINE_STRING);
	}

	/**
	 * @param record The XML node representing this record
	 * @return The unresolved column value to for this record, i.e. the enum code value which we need to look up
	 */
	public abstract String getUnresolvedColumnValue (final Element record);

	/**
	 * @param record The XML node representing this record
     * @param rowIndex The row whose value is to be queried (most implementations can ignore this)
     * @param columnIndex The column whose value is to be queried (most implementations can ignore this)
     * @param tableSorter The sort/filter used in displaying the data (most implementations can ignore this)
	 * @return The column value to display for this record
	 */
	@SuppressWarnings ("unused")
	@Override
	public String getColumnValue (final Element record, final int rowIndex, final int columnIndex, final TableRowSorter<XmlTableModel> tableSorter)
	{
		String value = "";

		final String enumCode = getUnresolvedColumnValue (record);
		if (enumCode != null)
		{
			final int index = StringUtils.indexOf (enumerations [0], enumCode);
			if (index >= 0)
				value = enumerations [1] [index];
		}

		return value;
	}
}