package com.ndg.xmleditor.editor;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jdom2.Element;

import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.grid.XmlEditorGrid;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.single.EditXmlRecord;
import com.ndg.xmleditor.single.XmlRecordPanel;
import com.ndg.xmleditor.single.field.SimpleXmlRecordField;

/**
 * Class with methods to open up grid displays and single record displays
 * By overriding this, specific editors can provide specialised forms for certain grids and single record displays
 */
public class XmlEditorDispatcher
{
	/** Interface allowing Spring to create frames from prototype definitions */
	private XmlEditorPrototypeFrameCreator creator;
	
	/** Map of XML tags to prototype grid bean names; there must always be an entry in the map for "default" */
	private Map<String, String> gridBeansMap;

	/** Map of XML tags to prototype record panel bean names; there must always be an entry in the map for "default" */
	private Map<String, String> recordPanelBeansMap;

	/** Map of XML tags to prototype edit frame bean names; there must always be an entry in the map for "default" */
	private Map<String, String> editRecordBeansMap;
	
	/** Map of XSD data types to prototype field bean names; there must always be an entry in the map for "default" */
	private Map<String, String> dataTypesMap;
	
	/**
	 * Creates a standard grid to display XML records of a particular type
	 * Specific editors can override this to provide specialised grids for certain record types
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param typeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param parentRecord If this is a child entity, this value holds the parent record; if this is a top level entity, this value will be null
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param parentTypeDefinitions Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem loading the button images or with missing prototype beans
	 */
	public void showGrid (final LocalElementEx entityElement, final ComplexTypeReference typeDefinition,
		final Element parentRecord, final LocalElementEx [] parentEntityElements, final ComplexTypeReference [] parentTypeDefinitions)
		throws XmlEditorException, IOException
	{
		// Is there a specific grid subclass for this entity?
		final String entityTag = entityElement.getName ();
		final String specificBeanName = getGridBeansMap ().get (entityTag);
		
		final XmlEditorGrid grid;
		if (specificBeanName != null)
			grid = (specificBeanName == null) ? null : getCreator ().createGrid (specificBeanName);
		else
		{
			// If not then use default
			final String defaultBeanName = getGridBeansMap ().get ("default");
			if (defaultBeanName == null)
				throw new IOException ("gridBeansMap must contain an entry with key \"default\"");
			
			grid = getCreator ().createGrid (defaultBeanName);
		}
		
		// Populate and initialize it
		grid.setEntityElement (entityElement);
		grid.setTypeDefinition (typeDefinition);
		grid.setParentRecord (parentRecord);
		grid.setParentEntityElements (parentEntityElements);
		grid.setParentTypeDefinitions (parentTypeDefinitions);
		grid.init ();
	}

	/**
	 * Creates a standard panel with fields to show one record from the XML file
	 * Specific editors can override this to lay out the controls differently
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param typeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param recordToDisplay The record to display, or null to leave all the edit boxes blank
	 * @param parentRecord The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record)
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param enabled Whether the created controls should be editable or not
	 * @return The newly created panel
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem with missing prototype beans
	 */
	public XmlRecordPanel createRecordPanel (final LocalElementEx entityElement, final ComplexTypeReference typeDefinition,
		final Element recordToDisplay, final Element parentRecord, final LocalElementEx [] parentEntityElements,
		final boolean enabled)
		throws XmlEditorException, IOException
	{
		// Is there a specific record panel subclass for this entity?
		final String entityTag = entityElement.getName ();
		final String specificBeanName = getRecordPanelBeansMap ().get (entityTag);
		
		final XmlRecordPanel panel;
		if (specificBeanName != null)
			panel = getCreator ().createRecordPanel (specificBeanName);
		else
		{
			// If not then use default
			final String defaultBeanName = getRecordPanelBeansMap ().get ("default");
			if (defaultBeanName == null)
				throw new IOException ("recordPanelBeansMap must contain an entry with key \"default\"");
			
			panel = getCreator ().createRecordPanel (defaultBeanName);
		}
		
		// Populate and initialize it
		panel.setEntityElement (entityElement);
		panel.setTypeDefinition (typeDefinition);
		panel.setRecordToDisplay (recordToDisplay);
		panel.setParentRecord (parentRecord);
		panel.setParentEntityElements (parentEntityElements);
		panel.setEnabled (enabled);
		panel.init ();
		
		return panel;
	}

	/**
	 * Creates a standard frame to display, edit or copy a single XML record
	 * Specific editors can override this to add extra controls outside of the panel, or to change the behaviour of the form
	 * Don't use this to change the layout of the panel or add fields to the panel that would also be appropriate if the record were a parent being displayed above a grid
	 * @param entityElement The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 * @param typeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param mdiEditor The main MDI window
	 * @param recordBeingEdited The record being edited, or null if we're adding a new record
	 * @param recordBeingCopied True if we're copying the specified record rather than editing it
	 * @param parentRecord The parent record which contains this child record, or null if this is a top level record (it most cases you can get this from recordBeingEdited.getParent (), except when creating a new record)
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param parentTypeDefinitions Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param grid Grid UI tat this record is being added/edited from
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem loading the button images
	 */
	@SuppressWarnings ("unused")
	public void showRecord (final LocalElementEx entityElement, final ComplexTypeReference typeDefinition,
		final XmlEditorMain mdiEditor, final Element recordBeingEdited, final boolean recordBeingCopied,
		final Element parentRecord, final LocalElementEx [] parentEntityElements, final ComplexTypeReference [] parentTypeDefinitions,
		final XmlEditorGrid grid)
		throws XmlEditorException, IOException
	{
		// Is there a specific record panel subclass for this entity?
		final String entityTag = entityElement.getName ();
		final String specificBeanName = getEditRecordBeansMap ().get (entityTag);
		
		final EditXmlRecord edit;
		if (specificBeanName != null)
			edit = getCreator ().createEditRecord (specificBeanName);
		else
		{
			// If not then use default
			final String defaultBeanName = getEditRecordBeansMap ().get ("default");
			if (defaultBeanName == null)
				throw new IOException ("editRecordBeansMap must contain an entry with key \"default\"");
			
			edit = getCreator ().createEditRecord (defaultBeanName);
		}
		
		// Populate and initialize it
		edit.setEntityElement (entityElement);
		edit.setTypeDefinition (typeDefinition);
		edit.setRecordBeingEdited (recordBeingEdited);
		edit.setRecordBeingCopied (recordBeingCopied);
		edit.setParentRecord (parentRecord);
		edit.setParentEntityElements (parentEntityElements);
		edit.setParentTypeDefinitions (parentTypeDefinitions);
		edit.setGrid (grid);
		edit.init ();
	}

	/**
	 * Creates and configures the most appropriate edit control for editing a field of a particular data type
	 *
	 * @param simpleType Data type to create control for, e.g. xsd:boolean
	 * @param sourceField The source field we want to create controls for - can be either an xsd:element or xsd:attribute node
	 * @param mandatory True if the field is mandatory, false if it is optional
	 * @return Accessor for editing this field
	 * @throws IOException If there is a problem with missing prototype beans
	 */
	public SimpleXmlRecordField createFieldControl (final QName simpleType, final FieldEx sourceField, final boolean mandatory) throws IOException
	{
		// Is there a specific field subclass for this data type?
		// Don't include the namespaceURI for the xsd: prefix
		final String simpleTypeKey = simpleType.getNamespaceURI ().equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI) ? simpleType.getLocalPart () : simpleType.toString ();
		final String specificBeanName = getDataTypesMap ().get (simpleTypeKey);
		
		final SimpleXmlRecordField accessor;
		if (specificBeanName != null)
			accessor = getCreator ().createField (specificBeanName);
		else
		{
			// If not then use default
			final String defaultBeanName = getDataTypesMap ().get ("default");
			if (defaultBeanName == null)
				throw new IOException ("dataTypesMap must contain an entry with key \"default\"");
			
			accessor = getCreator ().createField (defaultBeanName);
		}
		accessor.setFieldDefinition (sourceField);
		accessor.setMandatory (mandatory);

		return accessor;
	}

	/**
	 * @return Interface allowing Spring to create frames from prototype definitions
	 */
	public final XmlEditorPrototypeFrameCreator getCreator ()
	{
		return creator;
	}

	/**
	 * @param cre Interface allowing Spring to create frames from prototype definitions
	 */
	public final void setCreator (final XmlEditorPrototypeFrameCreator cre)
	{
		creator = cre;
	}

	/**
	 * @return Map of XML tags to prototype grid bean names; there must always be an entry in the map for "default"
	 */
	public final Map<String, String> getGridBeansMap ()
	{
		return gridBeansMap;
	}

	/**
	 * @param map Map of XML tags to prototype grid bean names; there must always be an entry in the map for "default"
	 */
	public final void setGridBeansMap (final Map<String, String> map)
	{
		gridBeansMap = map;
	}

	/**
	 * @return Map of XML tags to prototype record panel bean names; there must always be an entry in the map for "default"
	 */
	public final Map<String, String> getRecordPanelBeansMap ()
	{
		return recordPanelBeansMap;
	}

	/**
	 * @param map Map of XML tags to prototype record panel bean names; there must always be an entry in the map for "default"
	 */
	public final void setRecordPanelBeansMap (final Map<String, String> map)
	{
		recordPanelBeansMap = map;
	}

	/**
	 * @return Map of XML tags to prototype edit frame bean names; there must always be an entry in the map for "default"
	 */
	public final Map<String, String> getEditRecordBeansMap ()
	{
		return editRecordBeansMap;
	}

	/**
	 * @param map Map of XML tags to prototype edit frame bean names; there must always be an entry in the map for "default"
	 */
	public final void setEditRecordBeansMap (final Map<String, String> map)
	{
		editRecordBeansMap = map;
	}
	
	/**
	 * @return Map of XSD data types to prototype field bean names; there must always be an entry in the map for "default"
	 */
	public final Map<String, String> getDataTypesMap ()
	{
		return dataTypesMap;
	}

	/**
	 * @param map Map of XSD data types to prototype field bean names; there must always be an entry in the map for "default"
	 */
	public final void setDataTypesMap (final Map<String, String> map)
	{
		dataTypesMap = map;
	}
}