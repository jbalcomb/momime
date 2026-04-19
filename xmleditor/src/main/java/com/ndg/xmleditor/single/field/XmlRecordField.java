package com.ndg.xmleditor.single.field;

import java.awt.Color;

import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.FieldEx;

/**
 * Interface which allows varying types of display components report their value in a unified way,
 * and report the xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing
 */
public interface XmlRecordField
{
	/**
	 * @return The xsd:attribute or xsd:element node from the XSD file whose value we are displaying/editing
	 */
	public FieldEx getFieldDefinition ();

	/**
	 * @param def The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing
	 */
	public void setFieldDefinition (final FieldEx def);
	
	/**
	 * @return The entered value from the edit box or similar control
	 */
	public String getValue ();

	/**
	 * @param newValue The value to set into the edit box or similar control
	 * @throws XmlEditorException If there is a problem parsing the field value (e.g. invalid date)
	 */
	public void setValue (final String newValue)
		throws XmlEditorException;

	/**
	 * @param newValue Whether the control should be enabled
	 */
	public void setEnabled (final boolean newValue);

	/**
	 * @param colour Colour to draw the control
	 */
	public void setBackground (final Color colour);
	
	/**
	 * @param mandatory True if the field is mandatory, false if it is optional
	 */
	public void setMandatory (final boolean mandatory);
}
