package com.ndg.xmleditor.grid;

import java.io.IOException;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.xmleditor.editor.XmlEditorException;

/**
 * Adds an import button to the standard grid
 * The import button does nothing - must override this class to set an action for it
 */
public abstract class XmlEditorGridWithImport extends XmlEditorGrid
{
	/** Button for importing records from an external source */
	private JButton importButton;

	/**
	 * Creates a new form with a grid showing one type of record from the XML file
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem loading the button images
	 */
	@Override
	public void init ()
		throws XmlEditorException, IOException
	{
		super.init ();
		
		// Add the import button
		importButton = new JButton ();
		getButtonPanel ().add (importButton, getUtils ().createConstraintsHorizontalFill (0, getButtonPanelY (), 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		setButtonPanelY (getButtonPanelY () + 1);
	}

	/**
	 * Descandant forms must call this to define the action to take when clicking the Import button
	 * @param importAction Action to take when clicking the Import button
	 * @throws IOException If there is a problem loading the button image
	 */
	protected void setImportAction (final Action importAction)
		throws IOException
	{
		importAction.putValue (Action.NAME, "Import");
		importAction.putValue (Action.SMALL_ICON, new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Import.gif")));
		importButton.setAction (importAction);
	}
}