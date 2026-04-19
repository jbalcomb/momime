package com.ndg.xmleditor.grid;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.w3._2001.xmlschema.LocalElement;

import com.ndg.utils.DateFormats;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.editor.XmlEditorMain;
import com.ndg.xmleditor.grid.column.XmlGridColumn;

/**
 * A table model which uses XmlGridColumns to show data from the XML file(s)
 */
public class XmlTableModel extends AbstractTableModel
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (XmlTableModel.class);
	
	/** The main MDI window for the editor */
	private final XmlEditorMain mdiEditor;

	/**
	 * The container where we should look for records
	 * For top level entities, this is the root node of the XML document
	 * For child entities, this is the parent record (from the XML file, rather than the parent entity definition)
	 */
	private final Element container;

	/** The XML tag/name of this entity */
	private final String entityTag;

	/** The list of columns to display in the grid */
	private final List<XmlGridColumn> columns;

	/** Sort and filter interface for the table */
	private TableRowSorter<XmlTableModel> tableSorter;

	/**
	 * Creates a table model which uses XmlGridColumns to show data from the XML file(s)
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param aContainer The container where we should look for records
	 * @param aColumns The list of columns to display in the grid
	 * @param aMdiEditor The main MDI window
	 */
	public XmlTableModel (final LocalElement entityElement, final Element aContainer, final List<XmlGridColumn> aColumns, final XmlEditorMain aMdiEditor)
	{
		super ();

		container = aContainer;
		columns = aColumns;
		mdiEditor = aMdiEditor;

		// The entity tag is needed over and over, so look it up once only
		entityTag = entityElement.getName ();
	}

	/**
	 * @return For child entities, this is the parent record (from the XML file, rather than the parent entity definition)
	 */
	public final Element getContainer ()
	{
		return container;
	}

	/**
	 * @return The XML tag/name of this entity
	 */
	public final String getEntityTag ()
	{
		return entityTag;
	}

	/**
	 * @return The list of columns to display in the grid
	 */
	public final List<XmlGridColumn> getColumns ()
	{
		return columns;
	}


	/**
	 * The sorter is created after the table model, so we have to be able to store its value after creation
	 * @param aTableSorter Sort and filter interface for the table
	 */
	public final void setTableSorter (final TableRowSorter <XmlTableModel> aTableSorter)
	{
		tableSorter = aTableSorter;
	}

	/**
	 * @return Sort and filter interface for the table
	 */
	public final TableRowSorter <XmlTableModel> getTableSorter ()
	{
		return tableSorter;
	}
	/**
	 * @return The number of fields for this entity
	 */
	@Override
	public int getColumnCount ()
	{
		return columns.size ();
	}

	/**
	 * @return The number of records of this type in the XML document
	 */
	@Override
	public final int getRowCount ()
	{
		return container.getChildren (entityTag).size ();
	}
	
	/**
	 * @param rowIndex Row index to return
	 * @return The requested row
	 */
	public final Element getRowAt (final int rowIndex)
	{
		return container.getChildren (entityTag).get (rowIndex);
	}
	
	/**
	 * @param row Row to search for
	 * @return Index of the row within the row list, or -1 if not found
	 */
	public final int indexOfRow (final Element row)
	{
		return container.getChildren (entityTag).indexOf (row);
	}

	/**
     * @param rowIndex The row whose value is to be queried
     * @param columnIndex The column whose value is to be queried
	 * @return The text to display in the grid for this cell
	 */
	@Override
	public final Object getValueAt (final int rowIndex, final int columnIndex)
	{
		final Element record = getRowAt (rowIndex);

		final XmlGridColumn thisColumn = columns.get (columnIndex);
		String value = thisColumn.getColumnValue (record, rowIndex, columnIndex, tableSorter);
		
		// Allow non-string objects to be output
		Object result;
		if (value == null)
			result = thisColumn.getColumnValueObj (record, rowIndex, columnIndex, tableSorter);
		else
		{
			// Convert 2007-06-11 to 11 JUN 2007
			try
			{
				if (value != null)
				{
					final QName type = mdiEditor.getXmlDocuments ().determineUltimateSimpleType (thisColumn.getColumnType ());
					if ((type != null) && (type.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)) && (type.getLocalPart ().equals (XsdConstants.VALUE_DATA_TYPE_DATE)))
					{
						final LocalDate date = LocalDate.parse (value, DateFormats.XML_DATE_FORMAT);
						value = DateFormats.STANDARD_DATE_FORMAT.format (date);
					}
				}
			}
			catch (final XmlEditorException e)
			{
				// Just ignore this.  It means we can't find the SimpleType of the column, which we only need to see if its an xsd:date.
				// This happens on a normal basis now that you can refine FK columns which are not simple types (MoM IME languageText).
			}
			catch (final DateTimeParseException e)
			{
				value = "Conversion error [ " + value + " ]";
				log.error ("XmlTableModel (" + columnIndex + ", " + rowIndex + " " + value, e);
			}
			result = value;
		}

		return result;
	}

	/**
     * @param columnIndex	 The index of the column
	 * @return The title to display in the header above this column
	 */
	@Override
	public String getColumnName (final int columnIndex)
	{
		return columns.get (columnIndex).getColumnHeading ();
	}

	/**
     * @param columnIndex	 The index of the column
	 * @return Class hint, so the correct rendeder can be selected
	 */
	@Override
	public Class<?> getColumnClass (final int columnIndex)
	{
		return columns.get (columnIndex).getColumnClass ();
	}
}