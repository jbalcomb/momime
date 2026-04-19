package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.JAXBElement;

import org.w3._2001.xmlschema.Keybase;
import org.w3._2001.xmlschema.Keyref;
import org.w3._2001.xmlschema.TopLevelElement;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class TopLevelElementEx extends TopLevelElement
{
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
}