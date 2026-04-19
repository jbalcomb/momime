package com.ndg.xmleditor.single.field;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JComponent;

import com.github.lgooddatepicker.components.DatePicker;
import com.ndg.utils.DateFormats;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.FieldEx;

/**
 * A date field representing an attribute or element from an XML file
 */
public final class XmlRecordDateField implements SimpleXmlRecordField
{
	/** The xsd:attribute or xsd:element node from the XSD file whose value they are displaying/editing */
	private FieldEx fieldDefinition;

	/** Date control */
	private final DatePicker dateControl;

	/**
	 * Creates a date field representing an attribute or element from an XML file
	 */
	public XmlRecordDateField ()
	{
		super ();

		// Create date control
		dateControl = new DatePicker ();
		dateControl.setDate (LocalDate.now ());
		dateControl.getSettings ().setFormatForDatesCommonEra (DateFormats.STANDARD_DATE_FORMAT);
		dateControl.getSettings ().setFormatForTodayButton (DateFormats.STANDARD_DATE_FORMAT);
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
	 * @return The chosen date, in XML format
	 */
	@Override
	public final String getValue ()
	{
		final LocalDate date = dateControl.getDate ();

		String dateAsText = null;
		if (date != null)
			dateAsText = DateFormats.XML_DATE_FORMAT.format (date);

		return dateAsText;
	}

	/**
	 * @param newValue XML format date to set into the control
	 * @throws XmlEditorException If the date is an invalid format
	 */
	@Override
	public final void setValue (final String newValue)
		throws XmlEditorException
	{
		LocalDate date = null;

		if (newValue != null)
			try
			{
				date = LocalDate.parse (newValue, DateFormats.XML_DATE_FORMAT);
			}
			catch (final DateTimeParseException e)
			{
				throw new XmlEditorException ("Invalid format XML date: \"" + newValue + "\"");
			}

		dateControl.setDate (date);
	}

	/**
	 * Delegate to the underlying date control
	 * @param newValue Whether the control should be enabled or not
	 */
	@Override
	public final void setEnabled (final boolean newValue)
	{
		getComponent ().setEnabled (newValue);
	}

	/**
	 * @param colour Colour to draw the control
	 */
	@Override
	public final void setBackground (final Color colour)
	{
		dateControl.setBackground (colour);
	}

	/**
	 * Delegate to the underlying date control
	 * @param newValue Whether the control should allow a date to be typed in
	 */
	public final void setEditable (final boolean newValue)
	{
		dateControl.setEnabled (newValue);
	}

	/**
	 * @return Visual component for this field
	 */
	@Override
	public final JComponent getComponent ()
	{
		return dateControl;
	}

	/**
	 * @param mandatory True if the field is mandatory, false if it is optional
	 */
	@Override
	public final void setMandatory (final boolean mandatory)
	{
		dateControl.getSettings ().setVisibleClearButton (!mandatory);
	}
}