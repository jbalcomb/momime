package com.ndg.xmleditor.single.field;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.ndg.xmleditor.schema.FieldEx;

/**
 * A single line text field representing an attribute or element from an XML file
 */
public class XmlRecordTextField extends JTextField implements SimpleXmlRecordField
{
	/** The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing */
	private FieldEx fieldDefinition;

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
	 * @return The entered value from the edit box or similar control
	 */
	@Override
	public String getValue ()
	{
		return getText ();
	}

	/**
	 * @param newValue The value to set into the edit box or similar control
	 */
	@Override
	public void setValue (final String newValue)
	{
		setText (newValue);
	}

	/**
	 * @return Visual component for this field
	 */
	@Override
	public final JComponent getComponent ()
	{
		return this;
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