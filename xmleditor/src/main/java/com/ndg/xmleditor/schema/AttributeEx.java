package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.Annotation;
import org.w3._2001.xmlschema.Appinfo;
import org.w3._2001.xmlschema.Attribute;
import org.w3c.dom.Element;

import com.ndg.xmleditor.constants.XmlEditorAppInfo;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class AttributeEx extends Attribute implements FieldEx
{
	/**
	 * Checks whether to show a particular element or attribute in the grid.  Returning false only stops the main column from being included.  If it
	 * a foreign key reference, the fields from the referred to entity will still be inlcluded.  Typically used when the field is a foreign key
	 * reference to another entity and we want to show a description from that entity instead of the key itself.
	 * 
	 * @return Whether to include this column in the grid
	 */
	public final boolean isShowInGrid ()
	{
		return !XmlEditorAppInfo.VALUE_SHOW_MAIN_COLUMN_IN_GRID_FALSE.equalsIgnoreCase
			(findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_SHOW_FIELD_MAIN_COLUMN_IN_GRID));
	}
	
	/**
	 * Checks whether, for an element that is a foreign key to another entity, whether to display any of the columns from the referenced entity at all.
	 * @return If true, switches off display of referenced fields altogether
	 */
	@Override
	public final boolean isShowReferencedColumns ()
	{
		return !XmlEditorAppInfo.VALUE_SHOW_MAIN_COLUMN_IN_GRID_FALSE.equalsIgnoreCase
			(findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_SHOW_FIELD_REFERENCED_COLUMNS_IN_GRID));
	}
	
	/**
	 * @return Label to display for this field - taken from the nxe:label attribute if present, otherwise the attribute tag
	 */
	@Override
	public final String getLabel ()
	{
		final String label = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_FIELD_LABEL);
		return (label != null) ? label : getName ();
	}	

	/**
	 * @return Singular description of an entity, used as the caption when editing a record from this entity
	 */
	@Override
	public final String getSingularName ()
	{
		final String value = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_SINGULAR);
		return (value != null) ? value : getLabel ();
	}

	/**
	 * @return Plural description of an entity, used as the text on the menu option if a top level entity, or on the >> button if a child entity
	 */
	@Override
	public final String getPluralName ()
	{
		final String value = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_PLURAL);
		return (value != null) ? value : getSingularName () + "s";
	}
	
	/**
	 * @return True if any of label, singular name or plural name are defined; false if we're just defaulting to the name (XML tag) of the element
	 */
	@Override
	public final boolean isNameDefined ()
	{
		return (findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_FIELD_LABEL) != null) ||
			(findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_SINGULAR) != null) ||
			(findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_PLURAL) != null);
	}
	
	/**
	 * @return Width of grid column for this field - taken from the nxe:gridwidth attribute if present, otherwise the attribute tag
	 */
	public final int getGridWidth ()
	{
		final String gridWidth = findAttributeValue (XmlEditorAppInfo.TAG_ATTRIBUTE_FIELD_GRID_WIDTH);
		return (gridWidth != null) ? Integer.parseInt (gridWidth) : 150;
	}	

	/**
	 * Searches for attribute values in the editor's namespace, i.e. the extension attributes with nxe: prefix
	 * 
	 * @param attributeName Attribute name to search for
	 * @return Value of the requested attribute, or null if the attribute wasn't found
	 */
	final String findAttributeValue (final String attributeName)
	{
		return getOtherAttributes ().get (new QName (XmlEditorAppInfo.NAMESPACE_URI, attributeName));
	}

	/**
	 * @return Annotation > AppInfo structure if present, otherwise null
	 */
	final Appinfo getAppInfo ()
	{
		final Annotation anno = getAnnotation ();
		Appinfo appInfo = null;
		
		if (anno != null)
		{
			final Iterator<Object> iter = anno.getAppinfoOrDocumentation ().iterator ();
			while ((appInfo == null) && (iter.hasNext ()))
			{
				final Object obj = iter.next ();
				if (obj instanceof Appinfo)
					appInfo = (Appinfo) obj;
			}
		}
		
		return appInfo;
	}
	
	/**
	 * If any such tags are present, they explicitly list which referenced columns are displayed from referenced entities via a foreign key.
	 * If no such tags are present, all columns from the referenced entity are displayed.
	 * 
	 * @return List of fields named in <nxe:showreferencedfield> tags, or null if none are specified
	 */
	@Override
	public final List<String> getReferencedFields ()
	{
		final Appinfo appInfo = getAppInfo ();
		final List<String> fields = new ArrayList<String> ();
		
		if (appInfo != null)
			for (final Object obj : appInfo.getContent ())
				if (obj instanceof Element)
				{
					final Element elem = (Element) obj;
					if ((XmlEditorAppInfo.NAMESPACE_URI.equals (elem.getNamespaceURI ())) &&
						(XmlEditorAppInfo.TAG_ELEMENT_ELEMENT_SHOW_REFERENCED_FIELD.equals (elem.getLocalName ())))
					{
						final String text = elem.getTextContent ();
						if ((text != null) && (!text.equals ("")))
							fields.add (text);
					}
				}
		
		return (fields.size () == 0) ? null : fields;
	}

	/**
	 * @return Attribute details, for debug messages
	 */
	@Override
	public final String toString ()
	{
		return ("Attribute " + getName () + " of type " + getType ()); 
	}
}