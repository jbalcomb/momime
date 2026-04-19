package com.ndg.xmleditor.single.field;

import javax.swing.JComponent;

/**
 * Interface for simple types of field that are represented by a straightforward single visual component
 */
public interface SimpleXmlRecordField extends XmlRecordField
{
	/** @return Visual component for this field */
	public JComponent getComponent ();
}
