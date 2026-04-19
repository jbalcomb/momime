package com.ndg.utils.swing.layoutmanagers.xmllayout;

import java.awt.Component;

/**
 * Internal class, used to store the name associated with each component added to an XML layout
 */
class XmlLayoutComponentName
{
	/** Component name */
	private final String name;
	
	/** Component */
	private final Component component;
	
	/**
	 * @param aName Component name
	 * @param aComponent Component
	 */
	XmlLayoutComponentName (final String aName, final Component aComponent)
	{
		super ();
		name = aName;
		component = aComponent;
	}

	/**
	 * @return Component name
	 */
	final String getName ()
	{
		return name;
	}
	
	/**
	 * @return Component
	 */
	final Component getComponent ()
	{
		return component;
	}
}