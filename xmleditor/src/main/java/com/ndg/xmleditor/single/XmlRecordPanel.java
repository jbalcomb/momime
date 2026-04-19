package com.ndg.xmleditor.single;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.w3._2001.xmlschema.Field;
import org.w3._2001.xmlschema.Selector;

import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.doc.XmlForeignKeyReference;
import com.ndg.xmleditor.editor.XmlEditorDispatcher;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.editor.XmlEditorMain;
import com.ndg.xmleditor.jdom.JdomUtils;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.single.field.SimpleXmlRecordField;
import com.ndg.xmleditor.single.field.XmlRecordField;
import com.ndg.xmleditor.single.field.XmlRecordLinkedComboBox;

/**
 * Creates a JPanel which displays/edits a single record from the XML file
 * Used both for the actual single record display, and on child grids, to display the details of the parent record
 */
public class XmlRecordPanel
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (XmlRecordPanel.class);
	
	/** Colour to draw mandatory fields in, so they stand out */
	protected final static Color MANDATORY_FIELD_COLOUR = new Color (255, 255, 215);	// pale yellow

	/** Typical inset used on this screen layout */
	protected final static int INSET = 1;
	
	/** Special zero inset for layout components */
	protected final static int NO_INSET = 0;
	
	/** Mininum width to force fields to be, so the form doesn't look all cramped up; note this is a half-width because its the width of each linked combo box */
	protected final static int FIELD_MINIMUM_WIDTH = 200;
	
	/** The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type */
	private LocalElementEx entityElement;
	
	/** The xsd:complexType node of the entity being edited from the XSD */
	private ComplexTypeReference typeDefinition;

	/** The record to display, or null to leave all the edit boxes blank */
	private Element recordToDisplay;
	
	/** The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record) */
	private Element parentRecord;

	/** Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private LocalElementEx [] parentEntityElements;
	
	/** Whether the created controls should be editable or not */
	private boolean enabled;

	/** The main MDI window for the editor */
	private XmlEditorMain mdiEditor;

	/** The record panel itself */
	private JPanel singleRecordPanel;

	/** List of all the fields on the form, so we can extract the edited values back off the form */
	private List<XmlRecordField> fields;
	
	/** Current y position in the grid bag layout, increased as we lay controls out down the panel */
	protected int layouty;
	
	/** UI utils */
	private NdgUIUtils utils;

	/**
	 * Creates a new panel with fields to show one record from the XML file
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem with missing prototype beans
	 */
	public void init () throws XmlEditorException, IOException
	{
		// Initialize the panel
		singleRecordPanel = new JPanel ();
		singleRecordPanel.setLayout (new GridBagLayout ());
		enforceMinimumFieldColumnWidths ();

		// Build complete inheritance path for this type
		final List<ComplexTypeReference> inheritancePath = mdiEditor.getXmlDocuments ().determineInheritancePath (typeDefinition);

		// Search for all the fields - this is similar to XmlEditorGrid.buildColumnsList ()
		fields = new ArrayList<XmlRecordField> ();
		layouty = 1;
		beforeSearchingForFields ();

		for (final ComplexTypeReference thisTypeDefinition : inheritancePath)
		{
			// Split into sections if inherits from parent entity(s)
			if (inheritancePath.size () > 1)
			{
				final JLabel elementLabel = new JLabel ();
				elementLabel.setFont (elementLabel.getFont ().deriveFont (Font.BOLD));
				elementLabel.setText (thisTypeDefinition.getXmlDocument ().getXsd ().getTargetNamespace () + "." +
					thisTypeDefinition.getComplexTypeDefinition ().getName () + ":");

				singleRecordPanel.add (elementLabel, getUtils ().createConstraintsNoFill
					(0, layouty, getFieldColumnCount () + 1, 1, INSET, GridBagConstraintsNoFill.WEST));
				layouty++;
			}

			if (thisTypeDefinition == null)
			{
				// Drilling down into a 0..unbounded list of a simple type, so there is no complex type def in which to look for attributes or elements.
				// So in this case we just add a dummy field to represent the repeating simple type.
				log.debug ("XmlRecordPanel repeating single type " + getEntityElement ().getName () + " of type " + getEntityElement ().getType ()); 

				// To check if this is a foreign key, we need to wind back the last entry of parentEntityElements and treat this as the entityElement to pass in.
				// NB. Although the editor supports repeating single field foreign keys, the XML Schema implementation itself doesn't really :(
				// If you try to set this up and then use the XSD to validate an XML with repeating foregin key fields, it complains the xpath doesn't resolve to a unique field.
				final XmlForeignKeyReference ref = mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey
					(Arrays.copyOf (parentEntityElements, parentEntityElements.length - 1),
					parentEntityElements [parentEntityElements.length - 1], (Element) parentRecord.getParent (), getEntityElement ().getName ());
				
				final XmlRecordField elementControl = createControlsForField (getEntityElement (), true, ref, mdiEditor.getDispatcher ());

				if (enabled)
					elementControl.setBackground (MANDATORY_FIELD_COLOUR);

				elementControl.setEnabled (enabled);
				fields.add (elementControl);

				// Set initial value
				if (recordToDisplay != null)
					elementControl.setValue (recordToDisplay.getText ());
			}
			else
			{
				// Attributes
				for (final AttributeEx thisAttribute : thisTypeDefinition.getComplexTypeDefinition ().getAttributes ())
				{
					log.debug ("XmlRecordPanel adding attribute " + thisAttribute.getName () + " of type " + thisAttribute.getType () + " from complex type " +
						thisTypeDefinition.getComplexTypeDefinition ().getName () + " in XSD " + thisTypeDefinition.getXmlDocument ().getXsd ().getTargetNamespace ()); 
					
					final String attributeTag = thisAttribute.getName ();
					final boolean mandatory = thisAttribute.getUse ().equals (XsdConstants.VALUE_ATTRIBUTE_USE_REQUIRED);
	
					// Create control
					final XmlRecordField attributeControl = createControlsForField (thisAttribute, mandatory,
						mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey (parentEntityElements, entityElement, parentRecord, "@" + attributeTag), mdiEditor.getDispatcher ());
	
					// Set up the attribute - do this outside of createControlsForField so that when we override it, descendant functions don't have to worry about this
					if (enabled && mandatory)
						attributeControl.setBackground (MANDATORY_FIELD_COLOUR);
	
					attributeControl.setEnabled (enabled);
					fields.add (attributeControl);
	
					// Set initial value
					if (recordToDisplay != null)
						attributeControl.setValue (recordToDisplay.getAttributeValue (attributeTag));
				}
	
				// Leave a small gap between attributes and elements
	
				// Elements - finding these is a little more involved, so there's a special routine to do it for us
				for (final LocalElementEx thisElement : thisTypeDefinition.getComplexTypeDefinition ().getElements (mdiEditor.getXmlDocuments ()))
				{
					log.debug ("XmlRecordPanel adding element " + thisElement.getName () + " of type " + thisElement.getType () + " from complex type " +
						thisTypeDefinition.getComplexTypeDefinition ().getName () + " in XSD " + thisTypeDefinition.getXmlDocument ().getXsd ().getTargetNamespace ()); 
	
					final String elementTag = thisElement.getName ();
					final boolean mandatory = thisElement.getMinOccurs ().equals (BigInteger.ONE);
	
					// Create control
					final XmlRecordField elementControl = createControlsForField (thisElement, mandatory,
						mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey (parentEntityElements, entityElement, parentRecord, elementTag), mdiEditor.getDispatcher ());
	
					// Set up the element - do this outside of createControlsForField so that when we override it, descendant functions don't have to worry about this
					if (enabled && mandatory)
						elementControl.setBackground (MANDATORY_FIELD_COLOUR);
	
					elementControl.setEnabled (enabled);
					fields.add (elementControl);
	
					// Set initial value
					if (recordToDisplay != null)
						elementControl.setValue (recordToDisplay.getChildText (elementTag));
				}
			}
		}
	}

	/**
	 * By default does nothing
	 * Specialised forms can override this to do any processing before fields are searched for/added
	 */
	protected void beforeSearchingForFields ()
	{
	}

	/**
	 * @return How many columns in the GridBagLayout to leave for the fields themselves; by default 2, to accomodate for linked combo boxes
	 */
	protected int getFieldColumnCount ()
	{
		return 2;
	}
	
	/**
	 * Put in a sly top row to force the minimum width for all field controls.
	 * By default this makes all the columns at least FIELD_MINIMUM_WIDTH wide, but descendant panels can override or eliminate this behaviour if needed
	 */
	protected void enforceMinimumFieldColumnWidths ()
	{
		for (int x = 1; x <= getFieldColumnCount (); x++)
			singleRecordPanel.add (Box.createRigidArea (new Dimension (FIELD_MINIMUM_WIDTH, 0)),
				getUtils ().createConstraintsNoFill (x, 0, 1, 1, NO_INSET, GridBagConstraintsNoFill.EAST));
	}
	
	/**
	 * Creates a control (or if linked combo boxes, a set of controls) to represent a particular field, including linking in any foreign keys if appropriate
	 * Specialised panels could override this to create specialised controls for certain fields, or override the layout
	 * @param sourceField The source field we want to create controls for - can be either an xsd:element or xsd:attribute node
	 * @param mandatory True if the field is mandatory, false if it is optional
	 * @param foreignKeyReference Details of the foreign key reference if there is one, if not then null
	 * @param dispatcher Dispatcher to use to open grid and single record displays with
	 * @return Interface to the new control
	 * @throws XmlEditorException If a foreign key reference is to a complex type that cannot be found
	 * @throws IOException If there is a problem with missing prototype beans
	 */
	protected XmlRecordField createControlsForField (final FieldEx sourceField, final boolean mandatory, final XmlForeignKeyReference foreignKeyReference,
		final XmlEditorDispatcher dispatcher)
		throws XmlEditorException, IOException
	{
		// Determine the text to show on the label for this field
		String labelText = sourceField.getLabel ();

		// Create the label
		final JLabel elementLabel = new JLabel ();
		elementLabel.setText (labelText + ":");
		singleRecordPanel.add (elementLabel, getUtils ().createConstraintsNoFill (0, layouty, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		
		XmlRecordField createdControl = null;

		// Look up characteristics of the data type for this field
		final QName simpleType = mdiEditor.getXmlDocuments ().determineUltimateSimpleType (sourceField.getType ());
		final String [] [] enumerations = mdiEditor.getXmlDocuments ().findEnumerationsForType (sourceField.getType (), true);

		// There's more than one way we can end up with linked combo boxes
		XmlRecordLinkedComboBox linkedComboBox = null;

		// Check if this element is a foreign key

		// Note using combo boxes for foreign keys take preference over using specialised controls for dates, etc.
		// since regardless of the special type of control, we still need to pick one of the foreign key values

		// This isn't perfect, since sometimes the internal format of items (which is how they'll appear in the combo box)
		// isn't how we really want to display them - e.g. dates will appear as 2007-06-11 instead of 11 JUN 2007 - but using a
		// date as a foreign key would be a bit unusual, so live with this
		if (foreignKeyReference != null)
		{
			// Get the selector & field it refers to
			// Ensure its a PK based on only a single field - multi-key primary keys are only supported as constraints, they can't be used as foreign key targets
			final List<Field> pkFields = foreignKeyReference.getPrimaryKey ().getField ();
			final Selector pkSelector = foreignKeyReference.getPrimaryKey ().getSelector ();
			if ((pkSelector != null) && (pkFields.size () == 1))
			{
				final Field pkField = pkFields.get (0);
				final String selectorText = pkSelector.getXpath ();
				final String fieldText = pkField.getXpath ();

				// Find the definition of the referred-to entity
				// Same comment as XmlEditorGrid.addForeignKeyColumns, this isn't really done correctly
				final ComplexTypeReference refersToEntity = new ComplexTypeReference (foreignKeyReference.getReferencedDocument (),
					foreignKeyReference.getReferencedDocument ().getXsd ().findTopLevelComplexTypeAndTryOtherSchemas (selectorText, mdiEditor.getXmlDocuments ()));

				// Find the referenced entity's attributes that we want to display
				final List<LocalElementEx> refersToFields = mdiEditor.getXmlDocuments ().getReferencedFieldList (sourceField, refersToEntity);

				// Convert the element list into a list of field tags, plus we need to add the primary key itself
				final String [] refersToFieldTags = new String [refersToFields.size () + 1];
				refersToFieldTags [0] = fieldText;	// This already includes the @
				for (int fieldNo = 0; fieldNo < refersToFields.size (); fieldNo++)
					refersToFieldTags [fieldNo + 1] = refersToFields.get (fieldNo).getName ();

				// Create the combo boxes
				final String [] [] comboBoxData = JdomUtils.loadXmlElementsIntoStringArray
					(foreignKeyReference.getContainer (), selectorText, refersToFieldTags, true);

				linkedComboBox = new XmlRecordLinkedComboBox (comboBoxData);
				linkedComboBox.setFieldDefinition (sourceField);
			}
		}

		// Is this field an enum?
		else if (enumerations != null)
		{
			linkedComboBox = new XmlRecordLinkedComboBox (enumerations);
			linkedComboBox.setFieldDefinition (sourceField);
		}

		// Did we create combo boxes?  If so then we need to arrange them on the form
		if (linkedComboBox != null)
		{
			createdControl = linkedComboBox;

			// Position the primary key combo
			int spanx = (linkedComboBox.getComboBoxCount () == 1) ? getFieldColumnCount () : 1;
			singleRecordPanel.add (linkedComboBox.getComboBox (0),
				getUtils ().createConstraintsHorizontalFill (1, layouty, spanx, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));

			// Position the foreign key combos
			spanx = getFieldColumnCount () - 1;
			for (int comboBoxIndex = 1; comboBoxIndex < linkedComboBox.getComboBoxCount (); comboBoxIndex++)
			{
				singleRecordPanel.add (linkedComboBox.getComboBox (comboBoxIndex),
					getUtils ().createConstraintsHorizontalFill (2, layouty, spanx, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
				layouty++;
			}
			
			// If there were no linked combos, just the single double width main combo, then layouty will never have gotten increased, so do it now
			if (linkedComboBox.getComboBoxCount () == 1)
				layouty++;
		}
		else
		{
			// If we didn't create any combo boxes, then use method on dispatcher to create control, so that
			// additional specialised controls for data types can be plugged in
			final SimpleXmlRecordField elementField = dispatcher.createFieldControl (simpleType, sourceField, mandatory);
			createdControl = elementField;

			// Note they're 2 wide, so they're fully as wide as pairs of linked combo boxes
			singleRecordPanel.add (elementField.getComponent (),
				getUtils ().createConstraintsHorizontalFill (1, layouty, getFieldColumnCount (), 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
			layouty++;
		}

		return createdControl;
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
	 * @return The record to display, or null to leave all the edit boxes blank
	 */
	public final Element getRecordToDisplay ()
	{
		return recordToDisplay;
	}

	/**
	 * @param rec The record to display, or null to leave all the edit boxes blank
	 */
	public final void setRecordToDisplay (final Element rec)
	{
		recordToDisplay = rec;
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
	 * @return Whether the created controls should be editable or not
	 */
	public final boolean isEnabled ()
	{
		return enabled;
	}

	/**
	 * @param value Whether the created controls should be editable or not
	 */
	public final void setEnabled (final boolean value)
	{
		enabled = value;
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
	 * Allows external access to the panel
	 * @return The created panel
	 */
	public final JPanel getPanel ()
	{
		return singleRecordPanel;
	}

	/**
	 * Allows external access to the values entered in each field
	 * @return Interfaces to allow pulling the entered value out from each field
	 */
	public final List <XmlRecordField> getFields ()
	{
		return fields;
	}
}