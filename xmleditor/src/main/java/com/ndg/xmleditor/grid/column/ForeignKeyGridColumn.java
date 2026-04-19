package com.ndg.xmleditor.grid.column;

import java.util.List;

import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.grid.XmlTableModel;
import com.ndg.xmleditor.jdom.JdomUtils;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.ComplexTypeEx;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * A "foreign key" column, which uses a value from this record to locate a record of another entity, then shows the value of an element from that record
 */
abstract class ForeignKeyGridColumn extends XmlGridColumn
{
	/** The xsd:attribute node we are referring to, i.e. the primary key of the referenced entity */
	private final AttributeEx identifyingElement;

	/** The XML tag/name of the primary key on the referenced entity */
	private final String identifyingElementTag;

	/** The xsd:element node on the referenced entity, i.e. the field whose value we actually want to display */
	private final LocalElementEx targetElement;

	/** The XML tag/name of the field on the referenced entity */
	private final String targetElementTag;

	/** The xsd:complexType node of the referenced entity */
	private final ComplexTypeEx targetEntity;

	/** The XML tag/name of the referenced entity records */
	private final String targetEntityTag;

	/** The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document */
	private final Element container;

	/** The width to display this column */
	private final int columnWidth;

	/**
	 * Creates a grid column that uses a value from this record as a foreign key to look up another record, and displays a particular element from each record
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aXmlDocuments A list of the main XML document being edited, plus any referenced documents
	 * @param anIdentifyingElement The xsd:attribute node we are referring to
	 * @param aTargetElement The xsd:element node on the referenced entity
	 * @param aTargetEntity The xsd:complexType node of the referenced entity
	 * @param aContainer The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document
	 */
	public ForeignKeyGridColumn (final ComplexTypeEx aTypeDefinition, final ListOfXmlDocuments aXmlDocuments,
		final AttributeEx anIdentifyingElement, final LocalElementEx aTargetElement, final ComplexTypeEx aTargetEntity, final Element aContainer)
	{
		super (aTypeDefinition, aXmlDocuments);

		identifyingElement = anIdentifyingElement;
		targetElement = aTargetElement;
		targetEntity = aTargetEntity;
		container = aContainer;

		// The element tags are needed over and over, so look them up once only
		identifyingElementTag	= identifyingElement.getName ();
		targetElementTag		= targetElement.getName ();
		targetEntityTag			= targetEntity.getName ();

		// Work out the column width & store it once, rather than having to re-derive it each time it is needed
		// The old Delphi XML editor used to fix foreign key columns at width 150 - this is a little more clever, as it actually finds the definition of the target element from the
		// XSD and uses its defined column width
		columnWidth = targetElement.getGridWidth ();
	}

	/**
	 * @return The xsd:attribute node we are referring to, i.e. the primary key of the referenced entity
	 */
	public AttributeEx getIdentifyingElement ()
	{
		return identifyingElement;
	}

	/**
	 * @return The xsd:element node on the referenced entity, i.e. the field whose value we actually want to display
	 */
	public LocalElementEx getTargetElement ()
	{
		return targetElement;
	}

	/**
	 * @return The xsd:complexType node of the referenced entity
	 */
	public ComplexTypeEx getTargetEntity ()
	{
		return targetEntity;
	}

	/**
	 * @return The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document
	 */
	public Element getContainer ()
	{
		return container;
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
		return targetElement.getType ();
	}

	/**
	 * @param record The XML node representing this record
	 * @return The unresolved column value to for this record, i.e. the foreign key value which we need to look up
	 */
	public abstract String getUnresolvedColumnValue (Element record);

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

		final String foreignKeyValue = getUnresolvedColumnValue (record);
		if (foreignKeyValue != null)
		{
			final Element refersToRecord = JdomUtils.findDomChildNodeWithTextAttribute (container,
				targetEntityTag, identifyingElementTag, foreignKeyValue);

			if (refersToRecord == null)
				value = "FK not found [ " + foreignKeyValue + " ]";
			else
			{
				final Element valueElement = refersToRecord.getChild (targetElementTag);
				if (valueElement != null)
				{
					// Normally expect the child tag to just be like <value>this</value> so we just want the childText
					// But sometimes (MoM IME languageText) the text we want might be buried inside another tag
					final List<Element> children = valueElement.getChildren ();
					if (children.size () == 1)
						value = children.get (0).getText ();
					else
						value = valueElement.getText ();
				}
			}
		}

		return value;
	}
}