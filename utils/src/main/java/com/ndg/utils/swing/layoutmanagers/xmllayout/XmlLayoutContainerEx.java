package com.ndg.utils.swing.layoutmanagers.xmllayout;

/**
 * Provides a find method for locating components by name
 */
public interface XmlLayoutContainerEx
{
	/**
	 * @return Fixed width for this container
	 */
	public int getFormWidth ();

	/**
	 * @param width Fixed width for this container
	 */
	public void setFormWidth (final int width);
	
	/**
	 * @return Fixed height for this container
	 */
	public int getFormHeight ();
	
	/**
	 * @param height Fixed height for this container
	 */
	public void setFormHeight (final int height);
	
	/**
	 * Builds all the hash maps to enable finding records faster
	 */
	public void buildMaps ();
	
	/**
	 * @param name Name to search for
	 * @return Component with requested name, or null if none was found
	 */
	public XmlLayoutComponent findComponent (final String name);
	
	/**
	 * @param x X coordinate of component to search for
	 * @param y Y coordinate of component to search for
	 * @return Component at this location or null if none was found; NB. this will only find coordinates with a fixed width and height specified in the XML layout
	 */
	public XmlLayoutComponent findComponentAt (final int x, final int y);
}