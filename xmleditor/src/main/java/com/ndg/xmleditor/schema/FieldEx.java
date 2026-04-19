package com.ndg.xmleditor.schema;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Describes operations that both LocalElementEx and AttributeEx must support
 */
public interface FieldEx
{
	/**
	 * @return Named attribute or element tag
	 */
	public String getName ();
	
	/**
	 * @return Data type of this element or attribute
	 */
	public QName getType ();
	
	/**
	 * @return Label to display for this field - taken from the nxe:label attribute if present, otherwise the attribute tag
	 */
	public String getLabel ();

	/**
	 * @return Singular description of an entity, used as the caption when editing a record from this entity
	 */
	public String getSingularName ();

	/**
	 * @return Plural description of an entity, used as the text on the menu option if a top level entity, or on the >> button if a child entity
	 */
	public String getPluralName ();
	
	/**
	 * @return True if any of label, singular name or plural name are defined; false if we're just defaulting to the name (XML tag) of the element
	 */
	public boolean isNameDefined ();
	
	/**
	 * Checks whether, for an element that is a foreign key to another entity, whether to display any of the columns from the referenced entity at all.
	 * @return If true, switches off display of referenced fields altogether
	 */
	public boolean isShowReferencedColumns ();

	/**
	 * If any such tags are present, they explicitly list which referenced columns are displayed from referenced entities via a foreign key.
	 * If no such tags are present, all columns from the referenced entity are displayed.
	 * 
	 * @return List of fields named in <nxe:showreferencedfield> tags, or null if none are specified
	 */
	public List<String> getReferencedFields ();
}
