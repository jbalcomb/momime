package com.ndg.xmleditor.single.field;

import com.ndg.utils.swing.components.LinkedComboBox;
import com.ndg.xmleditor.schema.FieldEx;

/**
 * A set of linked combo boxes representing an attribute or element from an XML file
 */
public final class XmlRecordLinkedComboBox extends LinkedComboBox implements XmlRecordField
{
	/** The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing */
	private FieldEx fieldDefinition;

	/**
	 * Creates a set of linked combo boxes representing an attribute or element from an XML file
	 * @param items The data to appear in the combo boxes
	 */
	public XmlRecordLinkedComboBox (final String [] [] items)
	{
		super (items);
	}

	/**
	 * @return The xsd:attribute or xsd:element node from the XSD file whose value we are displaying/editing
	 */
	@Override
	public final FieldEx getFieldDefinition ()
	{
		return fieldDefinition;
	}

	/**
	 * @param def The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing
	 */
	@Override
	public final void setFieldDefinition (final FieldEx def)
	{
		fieldDefinition = def;
	}
	
	/**
	 * @return The chosen value from the combo boxes
	 */
	@Override
	public final String getValue ()
	{
		final Object obj = getSelectedItem ();
		String value;

		if (obj == null)
			value = null;
		else
			value = obj.toString ();

		return value;
	}

	/**
	 * @param newValue Picks the value in the primary combo box; the others will reflect the update
	 */
	@Override
	public final void setValue (final String newValue)
	{
		setSelectedItem (newValue);
	}

	/**
	 * @param mandatory True if the field is mandatory, false if it is optional
	 */
	@Override
	public final void setMandatory (@SuppressWarnings ("unused") final boolean mandatory)
	{
		// Ignore
	}
}