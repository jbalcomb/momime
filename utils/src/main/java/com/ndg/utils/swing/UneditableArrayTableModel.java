package com.ndg.utils.swing;

import javax.swing.table.AbstractTableModel;

/**
 * There's a constructor on JTable that just takes all the data and column headings in two arrays which is great for simple cases.
 * However it stupidly makes the data editable, and there's no way to override this behaviour without creating a whole new table model for it.
 */
public class UneditableArrayTableModel extends AbstractTableModel
{
	/**
	 * The data to appear in the table
	 */
	private final Object [] [] rowData;

	/**
	 * The column headings to display above the table
	 */
	private final Object [] columnHeadings;

	/**
	 * Creates a simple table model which displays data from an array, with column headings read from an additional array
	 * @param aRowData The data to appear in the table
	 * @param aColumnHeadings The column headings to display above the table
	 */
	public UneditableArrayTableModel (final Object [] [] aRowData, final Object [] aColumnHeadings)
	{
		super ();

		rowData = aRowData;
		columnHeadings = aColumnHeadings;
	}

	/**
	 * @return The number of columns to display in the table
	 */
	@Override
	public int getColumnCount ()
	{
		return columnHeadings.length;
	}

	/**
	 * @return The number of rows to display in the table
	 */
	@Override
	public int getRowCount ()
	{
		return rowData.length;
	}

	/**
	 * @return The value to display in a particular cell of the table
     * @param rowIndex The row whose value is to be queried
     * @param columnIndex The column whose value is to be queried
	 */
	@Override
	public Object getValueAt (final int rowIndex, final int columnIndex)
	{
		return rowData [rowIndex] [columnIndex];
	}

	/**
     * @param columnIndex	 The index of the column
	 * @return The heading to display above a particular column in the table
	 */
	@Override
	public String getColumnName (final int columnIndex)
	{
		return columnHeadings [columnIndex].toString ();
	}
}