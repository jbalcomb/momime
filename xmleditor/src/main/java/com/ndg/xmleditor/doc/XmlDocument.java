package com.ndg.xmleditor.doc;

import javax.swing.JTextField;

import org.jdom2.Document;
import org.jdom2.Element;

import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.schema.SchemaEx;

/**
 * Class which stores all the details about one XML document being used/edited/referenced in the editor
 */
public final class XmlDocument
{
	/** The XML schema defining the structure of this document */
	private final SchemaEx xsd;

	/** Indicates whether an XML document is required for this XSD (yes if is the main XSD or has an import placeholder, no if just a bunch of type imports) */
	private boolean xmlFileRequiredForThisXsd;

	/** Text field used for entering the full path to this XML file */
	private JTextField fileNameTextField;

	/** Actual loaded XML document */
	private Document xmlDocument;

	/**
	 * Namespace is the only thing set at creation time
	 * @param anXsd The XML schema defining the structure of this document
	 * @throws XmlEditorException If a problem with the XSD prevents us from finding the values
	 */
	public XmlDocument (final SchemaEx anXsd)
		throws XmlEditorException
	{
		super ();

		xsd = anXsd;
		xmlDocument = null;
	}

	/**
	 * @return Text describing this XSD and XML document, for debugging purposes
	 */
	@Override
	public final String toString ()
	{
		return "Namespace URI " + getXsd ().getTargetNamespace () + " " +
			((xmlDocument == null) ? " has no XML loaded" : " has XML loaded");
	}

	/**
	 * @return The XML schema defining the structure of this document
	 */
	public final SchemaEx getXsd ()
	{
		return xsd;
	}

	/**
	 * @return Indicates whether an XML document is required for this XSD (yes if is the main XSD or has an import placeholder, no if just a bunch of type imports)
	 */
	public final boolean isXmlFileRequiredForThisXsd ()
	{
		return xmlFileRequiredForThisXsd;
	}

	/**
	 * @param required Indicates whether an XML document is required for this XSD (yes if is the main XSD or has an import placeholder, no if just a bunch of type imports)
	 */
	public final void setXmlFileRequiredForThisXsd (final boolean required)
	{
		xmlFileRequiredForThisXsd = required;
	}

	/**
	 * @return The filename edit box into which the full path to this XML file will be entered
	 */
	public final JTextField getFileNameTextField ()
	{
		return fileNameTextField;
	}

	/**
	 * @param aFileNameTextField The filename edit box into which the full path to this XML file will be entered
	 */
	public final void setFileNameTextField (final JTextField aFileNameTextField)
	{
		fileNameTextField = aFileNameTextField;
	}

	/**
	 * @return Actual loaded XML document
	 */
	public final Document getXmlDocument ()
	{
		return xmlDocument;
	}

	/**
	 * @param anXmlDocument Actual loaded XML document
	 */
	public final void setXmlDocument (final Document anXmlDocument)
	{
		xmlDocument = anXmlDocument;
	}

	/**
	 * @return The root of the loaded XML document
	 */
	public final Element getXml ()
	{
		return getXmlDocument ().getRootElement ();
	}
}