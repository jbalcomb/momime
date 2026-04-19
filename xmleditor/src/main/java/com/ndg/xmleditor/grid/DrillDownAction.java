package com.ndg.xmleditor.grid;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.jdom2.Element;

import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.editor.XmlEditorMain;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * Action which opens up an editor grid for a child entity of a record currently selected in another grid
 */
public final class DrillDownAction extends AbstractAction
{
	/** The title/caption to appear on error messages */
	private final static String FORM_TITLE = "XML Editor";

	/** The xsd:element node of the child entity we're drilling into, i.e. this will be a the entry under the xsd:sequence under the parent complex type */
	private final LocalElementEx childEntityElement;

	/** The xsd:complexType node of the child entity we're drilling down into */
	private final ComplexTypeReference childTypeDefinition;

	/** The container under which the parent record is located */
	private final Element parentContainer;

	/** The table displaying records including the parent we're drilling down into */
	private final JTable parentTable;

	/** The main MDI window for the editor */
	private final XmlEditorMain mdiEditor;

	/** Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private final LocalElementEx [] parentEntityElements;

	/** Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private final ComplexTypeReference [] parentTypeDefinitions;

	/** The XML tag/name of the parent entity we're drilling down from */
	private final String parentEntityTag;

	/**
	 * Creates an action which opens up an editor grid when clicked
	 * @param parentEntityElement The xsd:element node of the parent entity we're drilling down from
	 * @param aChildEntityElement The xsd:element node of the child entity we're drilling into, i.e. this will be a the entry under the xsd:sequence under the parent complex type
	 * @param aChildTypeDefinition The xsd:complexType node of the child entity we're drilling down into
	 * @param aParentContainer The container under which the parent record is located
	 * @param aParentTable The table displaying records including the parent we're drilling down into
	 * @param aParentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param aParentTypeDefinitions Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param aMdiEditor The main MDI window
	 */
	public DrillDownAction (final LocalElementEx parentEntityElement, final LocalElementEx aChildEntityElement , final ComplexTypeReference aChildTypeDefinition,
		final Element aParentContainer, final JTable aParentTable, final LocalElementEx [] aParentEntityElements, final ComplexTypeReference [] aParentTypeDefinitions, final XmlEditorMain aMdiEditor)
	{
		// If a name is defined specifically on the element then use that first, so many languageText items can specify different names for each
		// If there isn't one, then look on the complex type definition
		// If there isn't one, then as last resort use the element name
		super ((((aChildEntityElement.isNameDefined ()) || (aChildTypeDefinition == null)) ? aChildEntityElement.getPluralName () :
			aChildTypeDefinition.getComplexTypeDefinition ().getPluralName ()) + " >>");

		childEntityElement			= aChildEntityElement;
		childTypeDefinition			= aChildTypeDefinition;
		parentContainer				= aParentContainer;
		parentTable					= aParentTable;
		parentEntityElements		= aParentEntityElements;
		parentTypeDefinitions		= aParentTypeDefinitions;
		mdiEditor						= aMdiEditor;

		// We need the tag for the parent entity in order to locate the record to drill down ito
		parentEntityTag = parentEntityElement.getName ();
	}

	/**
	 * Opens up the editor grid
	 * @param event Details of the event that triggered this action
	 */
	@Override
	public final void actionPerformed (@SuppressWarnings ("unused") final ActionEvent event)
	{
		final List<Element> records = parentContainer.getChildren (parentEntityTag);

		Element recordToDrillDownInto = null;
		for (final int rowIndex : parentTable.getSelectedRows ())
			recordToDrillDownInto = records.get (parentTable.convertRowIndexToModel (rowIndex));

		try
		{
			mdiEditor.getDispatcher ().showGrid (childEntityElement, childTypeDefinition,
				recordToDrillDownInto, parentEntityElements, parentTypeDefinitions);
		}
		catch (final Exception e)
		{
			JOptionPane.showMessageDialog (null, e.toString (), FORM_TITLE, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace ();
		}
	}
}