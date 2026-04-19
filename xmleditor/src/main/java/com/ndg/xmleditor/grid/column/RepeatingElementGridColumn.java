package com.ndg.xmleditor.grid.column;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.grid.XmlTableModel;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * A single column for when we drill down into 0..unbounded occurrences of a simpleType. 
 */
public final class RepeatingElementGridColumn extends XmlGridColumn
{
	/** The XML tag/name of this element */
	private final String elementTag;

	/** The ultimate simple type of this element */
	private final QName elementType;

	/**
	 * @param elementDef The xsd:element definition of the 0..unbounded element
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @throws XmlEditorException If we cannot find one of the simple types along the chain
	 */
	public RepeatingElementGridColumn (final LocalElementEx elementDef, final ListOfXmlDocuments aXmlDocuments) throws XmlEditorException
	{
		super (null, aXmlDocuments);
		
		elementTag = elementDef.getName ();
		elementType = getXmlDocuments ().determineUltimateSimpleType (elementDef.getType ());
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
	 * @return The width to display this column
	 */
	@Override
	public final int getColumnWidth ()
	{
		return 400;
	}

	/**
	 * @return Simple data type of this column - not necessarily resolved its ultimate simple type - so this may be e.g. momimesvr:description, rather than xsd:string
	 */
	@Override
	public final QName getColumnType ()
	{
		return elementType;
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
	public final String getColumnValue (final Element record, final int rowIndex, final int columnIndex, final TableRowSorter<XmlTableModel> tableSorter)
	{
		return record.getText ();
	}
}