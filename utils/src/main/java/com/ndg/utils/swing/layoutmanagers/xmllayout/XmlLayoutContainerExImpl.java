package com.ndg.utils.swing.layoutmanagers.xmllayout;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides a find method for locating components by name
 */
public final class XmlLayoutContainerExImpl extends XmlLayoutContainer implements XmlLayoutContainerEx
{
	/** Map of component names */
	private Map<String, XmlLayoutComponent> componentNamesMap;
	
	/**
	 * Builds all the hash maps to enable finding records faster
	 */
	@Override
	public final void buildMaps ()
	{
		componentNamesMap = getComponent ().stream ().collect (Collectors.toMap (c -> c.getName (), c -> c));
	}

	/**
	 * @param name Name to search for
	 * @return Component with requested name, or null if none was found
	 */
	@Override
	public final XmlLayoutComponent findComponent (final String name)
	{
		return componentNamesMap.get (name);
	}
	
	/**
	 * @param x X coordinate of component to search for
	 * @param y Y coordinate of component to search for
	 * @return Component at this location or null if none was found; NB. this will only find coordinates with a fixed width and height specified in the XML layout
	 */
	@Override
	public final XmlLayoutComponent findComponentAt (final int x, final int y)
	{
		XmlLayoutComponent found = null;
		final Iterator<XmlLayoutComponent> iter = getComponent ().iterator ();
		while ((found == null) && (iter.hasNext ()))
		{
			final XmlLayoutComponent comp = iter.next ();
			if ((comp.getWidth () != null) && (comp.getHeight () != null) && (x >= comp.getLeft ()) && (y >= comp.getTop ()) &&
				(x < comp.getLeft () + comp.getWidth ()) && (y < comp.getTop () + comp.getHeight ()))
				
				found = comp;
		}
		
		return found;
	}
}