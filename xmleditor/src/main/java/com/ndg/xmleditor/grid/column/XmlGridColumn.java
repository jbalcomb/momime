package com.ndg.xmleditor.grid.column;

import java.util.Comparator;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.grid.XmlTableModel;
import com.ndg.xmleditor.grid.comparators.DateComparator;
import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * A column in a grid can either display:
 *		i) An "attribute" column, showing the value of an attribute on the record
 *		ii) An "element" column, showing the value of an element on the record
 *		iii) A "foreign key" column, showing a value pulled from another entity referenced by a foreign key
 *		iv) A "usage" column, showing all the records from one other entity which refer to this record
 *
 * There is a descendant class of this column object for each of the above situations
 */
public abstract class XmlGridColumn
{
	/** The xsd:complexType node of the entity being edited from the XSD */
	private final ComplexTypeEx typeDefinition;

	/** A list of the main XML document being edited, plus any referenced documents */
	private final ListOfXmlDocuments xmlDocuments;

	/**
	 * Stores information descendant classes will need to implement different types of grid columns
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 */
	public XmlGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments)
	{
		super ();

		typeDefinition = aTypeDefinition;
		xmlDocuments = aXmlDocuments;
	}

	/**
	 * @return The xsd:complexType node of the entity being edited from the XSD
	 */
	public final ComplexTypeEx getTypeDefinition ()
	{
		return typeDefinition;
	}

	/**
	 * @return A list of the main XML document being edited, plus any referenced documents
	 */
	public final ListOfXmlDocuments getXmlDocuments ()
	{
		return xmlDocuments;
	}

	/**
	 * Checks the data type of this column to see if we need a specialised comparator for it, or the standard String comparator will do
	 * Descendant classes can override this to create specialised comparators if they need to
	 * @return A specialised comparator, or null to use the default
	 */
	public Comparator<String> checkForSpecialisedComparator ()
	{
		final QName columnType = getColumnType ();
		
		final Comparator<String> comparator;
		if (columnType == null)
			comparator = null;
		else if ((columnType.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)) && (columnType.getLocalPart ().equals (XsdConstants.VALUE_DATA_TYPE_DATE)))
			comparator = new DateComparator ();
		else
			comparator = null;

		return comparator;
	}

	/**
	 * @return The title to display in the header above this column
	 */
	public abstract String getColumnHeading ();

	/**
	 * @return The width to display this column
	 */
	public abstract int getColumnWidth ();

	/**
	 * @return Simple data type of this column - not necessarily resolved its ultimate simple type - so this may be e.g. momimesvr:description, rather than xsd:string
	 */
	public abstract QName getColumnType ();

	/**
	 * @return Class hint, so the correct rendeder can be selected
	 */
	public Class<?> getColumnClass ()
	{
		return String.class;
	}
	
	/**
	 * @param record The XML node representing this record
     * @param rowIndex The row whose value is to be queried (most implementations can ignore this)
     * @param columnIndex The column whose value is to be queried (most implementations can ignore this)
     * @param tableSorter The sort/filter used in displaying the data (most implementations can ignore this)
	 * @return The column value to display for this record
	 */
	@SuppressWarnings ("unused")
	public String getColumnValue (Element record, int rowIndex, int columnIndex, TableRowSorter<XmlTableModel> tableSorter)
	{
		return null;
	}

	/**
	 * @param record The XML node representing this record
     * @param rowIndex The row whose value is to be queried (most implementations can ignore this)
     * @param columnIndex The column whose value is to be queried (most implementations can ignore this)
     * @param tableSorter The sort/filter used in displaying the data (most implementations can ignore this)
	 * @return The column value to display for this record
	 */
	@SuppressWarnings ("unused")
	public Object getColumnValueObj (Element record, int rowIndex, int columnIndex, TableRowSorter<XmlTableModel> tableSorter)
	{
		return null;
	}
}