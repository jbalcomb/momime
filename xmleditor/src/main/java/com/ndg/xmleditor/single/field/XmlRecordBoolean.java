package com.ndg.xmleditor.single.field;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.ndg.xmleditor.jdom.JdomUtils;
import com.ndg.xmleditor.schema.FieldEx;

/**
 * A checkbox representing an attribute or element with xsd:boolean data type from an XML file
 */
public final class XmlRecordBoolean extends JCheckBox implements SimpleXmlRecordField
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
	public final String getValue ()
	{
		return JdomUtils.xmlBooleanToString (isSelected ());
	}

	/**
	 * @param newValue The value to set into the edit box or similar control
	 */
	@Override
	public final void setValue (final String newValue)
	{
		if (newValue == null)
			setSelected (false);
		else
			setSelected (JdomUtils.stringToXmlBoolean (newValue));
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