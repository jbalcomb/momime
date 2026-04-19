package com.ndg.xmleditor.grid.column;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.grid.XmlTableModel;

/**
 * Dummy column that just displays "Record", so that if there's a record with no fields at all (just a container for child entities) then there's at least something to display
 */
public final class DummyGridColumn extends XmlGridColumn
{
	/**
	 * Type and document references aren't even needed, so just set to null
	 */
	public DummyGridColumn ()
	{
		super (null, null);
	}

	/**
	 * @return The title to display in the header above this column
	 */
	@Override
	public final String getColumnHeading ()
	{
		return "Dummy";
	}

	/**
	 * @return The width to display this column
	 */
	@Override
	public final int getColumnWidth ()
	{
		return 80;
	}

	/**
	 * @return Simple data type of this column - not necessarily resolved its ultimate simple type - so this may be e.g. momimesvr:description, rather than xsd:string
	 */
	@Override
	public final QName getColumnType ()
	{
		return new QName (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI, XsdConstants.VALUE_DATA_TYPE_SINGLE_LINE_STRING);
	}
	
	/**
	 * @param record The XML node representing this record
     * @param rowIndex The row whose value is to be queried (most implementations can ignore this)
     * @param columnIndex The column whose value is to be queried (most implementations can ignore this)
     * @param tableSorter The sort/filter used in displaying the data (most implementations can ignore this)
	 * @return The column value to display for this record
	 */
	@SuppressWarnings ("unused")
	@Override
	public final String getColumnValue (Element record, int rowIndex, int columnIndex, TableRowSorter<XmlTableModel> tableSorter)
	{
		return "Record";
	}
}