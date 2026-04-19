package com.ndg.xmleditor.editor;

/**
 * Class for all exceptions raised by the editor
 */
public class XmlEditorException extends Exception
{
	/**
	 * Creates a new exception with no message
	 */
	public XmlEditorException ()
	{
	}

	/**
	 * Creates a new exception with a message
	 * @param message Exception message
	 */
	public XmlEditorException (final String message)
	{
		super (message);
	}
}