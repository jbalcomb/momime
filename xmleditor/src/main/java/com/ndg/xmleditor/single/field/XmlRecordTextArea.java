package com.ndg.xmleditor.single.field;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ndg.xmleditor.schema.FieldEx;

/**
 * A multi line text area representing an attribute or element from an XML file
 */
public final class XmlRecordTextArea extends JScrollPane implements SimpleXmlRecordField
{
	/** The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing */
	private FieldEx fieldDefinition;

	/** The underlying text area within the scroll pane */
	private final JTextArea textArea;

	/**
	 * Creates a text field representing an attribute or element from an XML file
	 */
	public XmlRecordTextArea ()
	{
		super ();

		textArea = new JTextArea ();
		textArea.setFont (new JTextField ().getFont ());	// Otherwise defaults to a courier type font?
		setViewportView (textArea);
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
	 * @return The entered value from the edit box or similar control
	 */
	@Override
	public final String getValue ()
	{
		return textArea.getText ();
	}

	/**
	 * @param newValue The value to set into the edit box or similar control
	 */
	@Override
	public final void setValue (final String newValue)
	{
		textArea.setText (newValue);
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