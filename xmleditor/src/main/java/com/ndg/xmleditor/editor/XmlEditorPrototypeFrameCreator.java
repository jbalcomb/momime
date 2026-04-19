package com.ndg.xmleditor.editor;

import com.ndg.xmleditor.grid.XmlEditorGrid;
import com.ndg.xmleditor.single.EditXmlRecord;
import com.ndg.xmleditor.single.XmlRecordPanel;
import com.ndg.xmleditor.single.field.SimpleXmlRecordField;

/**
 * Interface to allow Spring to create grids + single record frames from prototype defintions and perform dependancy injections 
 */
public interface XmlEditorPrototypeFrameCreator
{
	/**
	 * @param id Name of the grid prototype bean to create; the default XmlEditorGrid will usually be in a bean named "grid" but there may be specialist derivatives of this also
	 * @return Newly created grid window
	 */
	public XmlEditorGrid createGrid (final String id);
	
	/**
	 * @param id Name of the record panel prototype bean to create; the default XmlRecordPanel will usually be in a bean named "panel" but there may be specialist derivatives of this also
	 * @return Newly created record panel
	 */
	public XmlRecordPanel createRecordPanel (final String id);

	/**
	 * @param id Name of the edit record prototype bean to create; the default EditXmlRecord will usually be in a bean named "edit" but there may be specialist derivatives of this also
	 * @return Newly created record panel
	 */
	public EditXmlRecord createEditRecord (final String id);
	
	/**
	 * @param id Name of the field prototype bean to create
	 * @return Newly created field editor
	 */
	public SimpleXmlRecordField createField (final String id);
}
