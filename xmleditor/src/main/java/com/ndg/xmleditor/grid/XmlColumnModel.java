package com.ndg.xmleditor.grid;

import java.util.List;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.ndg.xmleditor.grid.column.XmlGridColumn;

/**
 * A table column model which uses XmlGridColumns to set column widths as defined in the XSD
 */
public class XmlColumnModel extends DefaultTableColumnModel
{
	/**
	 * The list of columns to display in the grid
	 */
	private List<XmlGridColumn> columns;

	/**
	 * Creates a table model which uses XmlGridColumns to show data from the XML file(s)
	 * @param aColumns The list of columns to display in the grid
	 */
	public XmlColumnModel (List<XmlGridColumn> aColumns)
	{
		super ();

		columns = aColumns;
	}

	/**
	 * Set column widths as columns are added
	 * @param aColumn The new column to add
	 */
	@Override
	public void addColumn (TableColumn aColumn)
	{
		int columnIndex = getColumnCount ();
		int width = columns.get (columnIndex).getColumnWidth ();
		aColumn.setPreferredWidth (width);
		
		super.addColumn (aColumn);
	}
}