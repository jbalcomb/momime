package com.ndg.xmleditor.editor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * Action which opens up an editor grid for a top level entity when clicked
 */
public final class EditEntityAction extends AbstractAction
{
	/** The title/caption to appear on error messages */
	private final static String FORM_TITLE = "XML Editor";

	/** The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type */
	private final LocalElementEx entityElement;

	/** The xsd:complexType node of the entity being edited from the XSD */
	private final ComplexTypeReference typeDefinition;

	/** The main MDI window for the editor */
	private final XmlEditorMain mdiEditor;

	/**
	 * Creates an action which opens up an editor grid when clicked
	 * @param anEntityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aMdiEditor The main MDI window
	 */
	public EditEntityAction (final LocalElementEx anEntityElement, final ComplexTypeReference aTypeDefinition, final XmlEditorMain aMdiEditor)
	{
		super (aTypeDefinition.getComplexTypeDefinition ().getPluralName ());

		entityElement	= anEntityElement;
		typeDefinition	= aTypeDefinition;
		mdiEditor		= aMdiEditor;
	}

	/**
	 * Opens up the editor grid
	 * @param event Details of the event that triggered this action
	 */
	@Override
	public final void actionPerformed (@SuppressWarnings ("unused") final ActionEvent event)
	{
		try
		{
			mdiEditor.getDispatcher ().showGrid (entityElement, typeDefinition, null, null, null);
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
			JOptionPane.showMessageDialog (null, e.toString (), FORM_TITLE, JOptionPane.ERROR_MESSAGE);
		}
	}
}