package com.ndg.utils.swing.layoutmanagers.xmllayout;

/**
 * When unmarshalling layouts, make sure we create Ex versions of classes
 */
public final class XmlLayoutObjectFactory extends ObjectFactory
{
	/**
	 * Custom overriden container layout
	 */
	@Override
	public final XmlLayoutContainer createXmlLayoutContainer ()
	{
		return new XmlLayoutContainerExImpl ();
	}
}