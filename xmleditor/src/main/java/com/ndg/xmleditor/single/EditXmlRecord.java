package com.ndg.xmleditor.single;

import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jdom2.Element;

import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.actions.MessageDialogAction;
import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.editor.XmlEditorMain;
import com.ndg.xmleditor.grid.XmlEditorGrid;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.single.field.XmlRecordField;

/**
 * Panel for displaying/editing a single record
 */
public class EditXmlRecord
{
	/** Typical inset used on this screen layout */
	protected final static int INSET = 3;
	
	/** The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type */
	private LocalElementEx entityElement;
	
	/** The xsd:complexType node of the entity being edited from the XSD */
	private ComplexTypeReference typeDefinition;

	/** The main MDI window for the editor */
	private XmlEditorMain mdiEditor;

	/** The XML tag/name of this entity */
	private String entityTag;

	/** The record being edited, or null if we're adding a new record */
	private Element recordBeingEdited;

	/** True if we're copying the specified record rather than editing it */
	private boolean recordBeingCopied;

	/** The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record) */
	private Element parentRecord;

	/** Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private LocalElementEx [] parentEntityElements;
	
	/** Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private ComplexTypeReference [] parentTypeDefinitions;
	
	/** Grid UI tat this record is being added/edited from */
	private XmlEditorGrid grid;

	/** The frame/dialog/form itself */
	private JDialog frame;

	/** The height to lock the form at */
	private int formHeight;

	/** The title to show on the form, and error messages */
	private String formTitle;

	/** The panel containing the editable controls */
	private XmlRecordPanel recordPanel;

	/** UI utils */
	private NdgUIUtils utils;
	
	/** Content pane */
	private JPanel contentPane;
	
	/**
	 * Creates a new panel with fields to edit one record from the XML file, plus an OK and Cancel button
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem loading the button images
	 */
	public void init () throws XmlEditorException, IOException
	{
		// The entity tag is needed over and over, so look it up once only
		entityTag = entityElement.getName ();

		// Work out the form title
		final String typeName = (typeDefinition != null) ? typeDefinition.getComplexTypeDefinition ().getSingularName () : entityTag;
		if (recordBeingEdited == null)
			formTitle = "New " + typeName;
		else if (recordBeingCopied)
			formTitle = "Copy " + typeName;
		else
			formTitle = "Edit " + typeName;

		// Initialize the modal dialog
		frame = new JDialog (mdiEditor.getFrame (), formTitle, true);
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

		// Initialize the content pane
		contentPane = new JPanel ();
		contentPane.setLayout (new GridBagLayout ());

		// Create the main pane
		recordPanel = getMdiEditor ().getDispatcher ().createRecordPanel (entityElement, typeDefinition, recordBeingEdited, parentRecord, parentEntityElements, true);
		contentPane.add (recordPanel.getPanel (), getUtils ().createConstraintsNoFill (0, 0, 1, 2, INSET, GridBagConstraintsNoFill.CENTRE));

		// OK action
		final Action okAction = new MessageDialogAction ("OK", (ev) ->
		{
			updateRecord ();
			frame.dispose ();
		});

		// Cancel action
		final Action cancelAction = new MessageDialogAction ("Cancel", (ev) -> frame.dispose ());

		// Create the buttons
		final JButton okButton = new JButton ();
		okButton.setAction (okAction);
		contentPane.add (okButton, getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.NORTH));

		final JButton cancelButton = new JButton ();
		cancelButton.setAction (cancelAction);
		contentPane.add (cancelButton, getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.NORTH));

		// Allow descendant forms to modify the form before packing + displaying it
		initialize ();

		// If copying a record, now we've loaded its details, forget about the record we got the details from so its treated just like a "New"
		if (recordBeingCopied)
			recordBeingEdited = null;

		// Show it
		frame.setContentPane (contentPane);
		frame.pack ();
		frame.setVisible (true);
	}

	/**
	 * Because the frame is modal, specialised forms can't call the super constructor and then add components to the form, since the super constructor
	 * does not exit until the form closes.  They need to override this instead.
	 */
	protected void initialize ()
	{
	}

	/**
	 * Can override this to allocate more space to the form
	 * @return The height to lock the form at
	 */
	public int getFormHeight ()
	{
		return formHeight;
	}

	/**
	 * Updates the record back in the XML file
	 * Triggered when the OK butotn is clicked
	 * @throws XmlEditorException If the type definition doesn't include an xsd:sequence entry
	 */
	private void updateRecord ()
		throws XmlEditorException
	{
		// Create the record if necessary
		if (recordBeingEdited == null)
		{
			// We need the type definition of our immediate parent, or of the top level database xsd:complexType if this is a top level entity
			ComplexTypeReference parentTypeDefinition;
			if (parentTypeDefinitions == null)
				parentTypeDefinition = new ComplexTypeReference (mdiEditor.getXmlDocuments ().get (0), mdiEditor.getXmlDocuments ().get (0).getXsd ().getTopLevelTypeDefinition ());
			else
				parentTypeDefinition = parentTypeDefinitions [parentTypeDefinitions.length - 1];

			// Also need to find the parent record in the XML file under which the new record will be added
			Element container;
			if (parentRecord == null)
				container = mdiEditor.getXmlDocuments ().get (0).getXml ();
			else
				container = parentRecord;

			// Now can find where to add the record
			int insertionPoint = mdiEditor.getXmlDocuments ().determineElementInsertionPoint (parentTypeDefinition, container, entityTag);
			insertionPoint = getGrid ().adjustInsertionPoint (insertionPoint);
			recordBeingEdited = new Element (entityTag);
			container.addContent (insertionPoint, recordBeingEdited);
		}

		// Deal with each field at a time
		for (final XmlRecordField thisField : recordPanel.getFields ())
			mdiEditor.getXmlDocuments ().updateFieldValue (recordBeingEdited, typeDefinition,
				thisField.getFieldDefinition (), thisField.getValue ());

		// Force any grids displaying records from this entity to reload themselves
		mdiEditor.fireTableDataChangedOnAllGridsShowingEntity ((typeDefinition == null) ? null : typeDefinition.getComplexTypeDefinition ());
	}

	/**
	 * @return The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 */
	public final LocalElementEx getEntityElement ()
	{
		return entityElement;
	}

	/**
	 * @param elem The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 */
	public final void setEntityElement (final LocalElementEx elem)
	{
		entityElement = elem;
	}

	/**
	 * @return The xsd:complexType node of the entity being edited from the XSD
	 */
	public final ComplexTypeReference getTypeDefinition ()
	{
		return typeDefinition;
	}

	/**
	 * @param def The xsd:complexType node of the entity being edited from the XSD
	 */
	public final void setTypeDefinition (final ComplexTypeReference def)
	{
		typeDefinition = def;
	}

	/**
	 * @return The record being edited, or null if we're adding a new record
	 */
	public final Element getRecordBeingEdited ()
	{
		return recordBeingEdited;
	}

	/**
	 * @param rec The record being edited, or null if we're adding a new record
	 */
	public final void setRecordBeingEdited (final Element rec)
	{
		recordBeingEdited = rec;
	}

	/**
	 * @return True if we're copying the specified record rather than editing it
	 */
	public final boolean isRecordBeingCopied ()
	{
		return recordBeingCopied;
	}

	/**
	 * @param copy True if we're copying the specified record rather than editing it
	 */
	public final void setRecordBeingCopied (final boolean copy)
	{
		recordBeingCopied = copy;
	}

	/**
	 * @return The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record)
	 */
	public final Element getParentRecord ()
	{
		return parentRecord;
	}

	/**
	 * @param rec The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record)
	 */
	public final void setParentRecord (final Element rec)
	{
		parentRecord = rec;
	}

	/**
	 * @return Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
 	 */
	public final LocalElementEx [] getParentEntityElements ()
	{
		return parentEntityElements;
	}

	/**
	 * @param elems Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
 	 */
	public final void setParentEntityElements (final LocalElementEx [] elems)
	{
		parentEntityElements = elems;
	}

	/**
	 * @return Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final ComplexTypeReference [] getParentTypeDefinitions ()
	{
		return parentTypeDefinitions;
	}

	/**
	 * @param defs Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final void setParentTypeDefinitions (final ComplexTypeReference [] defs)
	{
		parentTypeDefinitions = defs;
	}

	/**
	 * @return Grid UI tat this record is being added/edited from
	 */
	public final XmlEditorGrid getGrid ()
	{
		return grid;
	}

	/**
	 * @param g Grid UI tat this record is being added/edited from
	 */
	public final void setGrid (final XmlEditorGrid g)
	{
		grid = g;
	}
	
	/**
	 * @return The main MDI window for the editor
	 */
	public final XmlEditorMain getMdiEditor ()
	{
		return mdiEditor;
	}

	/**
	 * @param mdi The main MDI window for the editor
	 */
	public final void setMdiEditor (final XmlEditorMain mdi)
	{
		mdiEditor = mdi;
	}

	/**
	 * @return UI utils
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param obj UI utils
	 */
	public final void setUtils (final NdgUIUtils obj)
	{
		utils = obj;
	}

	/**
	 * @return Content pane
	 */
	public final JPanel getContentPane ()
	{
		return contentPane;
	}
}