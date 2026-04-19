package com.ndg.xmleditor.grid.column;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.grid.XmlTableModel;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * An "attribute" column, showing the value of an attribute on the record
 */
public class AttributeGridColumn extends XmlGridColumn
{
	/** The xsd:attribute node under the complex type in the XSD */
	private final AttributeEx attributeDefinition;
	
	/** The XML tag/name of this attribute */
	private final String attributeTag;
	
	/** The title to display in the header above this column */
	private final String columnHeading;
	
	/** The width to display this column */
	private final int columnWidth;

	/**
	 * Creates a grid column that displays the value of an XML attribute from each record
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD 
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anAttributeDefinition The xsd:attribute node under the complex type in the XSD 
	 */
	public AttributeGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments, final AttributeEx anAttributeDefinition)
	{
		super (aTypeDefinition, aXmlDocuments);
		
		attributeDefinition = anAttributeDefinition;
		
		// The attribute tag is needed over and over, so look it up once only
		attributeTag = attributeDefinition.getName ();
		
		// Work out the title to display in the header above this column & the column width & store them once, rather than having to re-derive them each time they are needed
		columnHeading = attributeDefinition.getLabel ();
		columnWidth = attributeDefinition.getGridWidth ();
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
	 * @return The width to display this column
	 */
	@Override
	public int getColumnWidth ()
	{
		return columnWidth;
	}

	/**
	 * @return Simple data type of this column - not necessarily resolved its ultimate simple type - so this may be e.g. momimesvr:description, rather than xsd:string 
	 */
	@Override
	public QName getColumnType ()
	{
		return attributeDefinition.getType ();
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
	public String getColumnValue (Element record, int rowIndex, int columnIndex, TableRowSorter<XmlTableModel> tableSorter)
	{
		return record.getAttributeValue (attributeTag);
	}
}