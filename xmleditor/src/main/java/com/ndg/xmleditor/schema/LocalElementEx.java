package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.Annotation;
import org.w3._2001.xmlschema.Appinfo;
import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;
import org.w3._2001.xmlschema.LocalElement;
import org.w3c.dom.Element;

import com.ndg.xmleditor.constants.XmlEditorAppInfo;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class LocalElementEx extends LocalElement implements FieldEx
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
	 * @return List of xsd:keyrefs under this element
	 */
	public final List<Keyref> getKeyRefs ()
	{
		final List<Keyref> list = new ArrayList<Keyref> ();
		
		for (final Object obj : getIdentityConstraint ())
			if (obj instanceof Keyref)
				list.add ((Keyref) obj);
		
		return list;
	}

	/**
	 * @return List of xsd:keys under this element
	 */
	public final List<Keybase> getKeys ()
	{
		final List<Keybase> list = new ArrayList<Keybase> ();
		
		for (final Object obj : getIdentityConstraint ())
			if (obj instanceof JAXBElement)
			{
				final JAXBElement<?> container = (JAXBElement<?>) obj;
				if (container.getValue () instanceof Keybase)
					list.add ((Keybase) container.getValue ());
			}
		
		return list;
	}
	
	/**
	 * Builds a complete xpath to a particular entity, specifying each of the parents in turn, e.g. "wizard/wizardpickcount/wizardpick"
	 * 
	 * @param parentEntityElements Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 * @param withRespectTo This can either be null, in which case the xpath will be relative to the root of the XML document, or one of the parents from the parentEntityElements array, in which case
	 *		the xpath will state how to get from that parent to this entity, so e.g. if we have "account/transaction" and ask for the xpath with respect to "account", we'll get just "transaction"
	 * @return xpath to this entity
	 */
	public final String toXpath (final LocalElementEx [] parentEntityElements, final LocalElementEx withRespectTo)
	{
		// Start with the name of the final entity
		String xpath = getName ();

		// Then prefix it by each parent
		if (parentEntityElements != null)
		{
			// Work backwards up the list, from the immediate parent first
			// Then if we encounter the withRespectTo parent, we abort and don't include it or any higher up parents in the xpath
			int parentNo = parentEntityElements.length - 1;
			boolean abort = false;

			while ((!abort) && (parentNo >= 0))
			{
				final LocalElement thisParentElement = parentEntityElements [parentNo];
				if (thisParentElement == withRespectTo)
					abort = true;
				else
				{
					xpath = thisParentElement.getName () + "/" + xpath;
					parentNo--;
				}
			}
		}

		return xpath;
	}
	
	/**
	 * @param selectorXpath Selector xpath to search for - the chain of elements drilled down through to reach here
	 * @param fieldXpath The xpath to the field (for elements, this is simply the field tag, for attributes it must be prefixed by a @)
	 * @return Matching key ref if found, otherwise null
	 */
	public final Keyref findKeyRef (final String selectorXpath, final String fieldXpath)
	{
		Keyref match = null;
		final Iterator<Keyref> iter = getKeyRefs ().iterator ();
		while ((match == null) && (iter.hasNext ()))
		{
			final Keyref thisRef = iter.next ();
			if ((selectorXpath.equals (thisRef.getSelector ().getXpath ())) &&
					
				// Ensure its a FK based on only a single field - multi-key foreign keys aren't supported yet
				(thisRef.getField ().size () == 1) && (fieldXpath.equals (thisRef.getField ().get (0).getXpath ())))
				
				match = thisRef;
		}
		
		return match;
	}

	/**
	 * @param keyName Name of the key to search for (local part only)
	 * @return Matching key if found, otherwise null
	 */
	public final Keybase findKey (final String keyName)
	{
		Keybase match = null;
		final Iterator<Keybase> iter = getKeys ().iterator ();
		while ((match == null) && (iter.hasNext ()))
		{
			final Keybase thisKey = iter.next ();
			if (keyName.equals (thisKey.getName ()))
				match = thisKey;
		}
		
		return match;
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
	 * @return Element details, for debug messages
	 */
	@Override
	public final String toString ()
	{
		return ("Element " + getName () + " of type " + getType ()); 
	}
}