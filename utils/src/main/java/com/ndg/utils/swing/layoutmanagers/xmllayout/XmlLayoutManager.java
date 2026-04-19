package com.ndg.utils.swing.layoutmanagers.xmllayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is basically like a null layout manager, where the size of the container is fixed and the components are
 * all set in fixed locations, except that the locations are held off in an XML file so is easier to edit separately from the code.
 * 
 * Only component locations are set by this layout manager.  All components are just set to their preferred sizes.  
 * 
 * Wrote this for MoM IME, where almost all of the forms have custom bitmap backgrounds, hence have fixed sizes, and
 * controls must be lined up at exact pixel locations against the bitmap background or it looks terrible.  Using a varying
 * layout manager on such forms makes them very brittle and susceptible to losing their correct layout as a result of
 * what should be trivial changes, such as changing the font size; or moving one component a few pixels to the side
 * having a knock on effect on other components.
 * 
 * Don't use this for containers where the size may vary.
 */
public final class XmlLayoutManager implements LayoutManager2
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (XmlLayoutManager.class);

	/** The details read from the XML file */
	private final XmlLayoutContainerEx layout;
	
	/**
	 * Stores names of components as they get added.  Its a little weird having the key as the component and the name as the value,
	 * but it makes sense when you look at removeLayoutComponent so it doesn't have to iterate over a full list, and consider that
	 * multiple components are allowed to have the same name (see comments in XSD).
	 */
	private final Map<Component, String> componentNames;
	
	/**
	 * @param aLayout The details read from the XML file
	 */
	public XmlLayoutManager (final XmlLayoutContainerEx aLayout)
	{
		super ();
		layout = aLayout;
		componentNames = new HashMap<Component, String> ();
	}

	/**
	 * @return The details read from the XML file
	 */
	public final XmlLayoutContainerEx getLayout ()
	{
		return layout;
	}

	/**
	 * @return Names of components that have been added
	 */
	final Map<Component, String> getComponentNames ()
	{
		return componentNames;
	}
	
	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension preferredLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return new Dimension (getLayout ().getFormWidth (), getLayout ().getFormHeight ());
	}

	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension minimumLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return new Dimension (getLayout ().getFormWidth (), getLayout ().getFormHeight ());
	}
	
	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension maximumLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return new Dimension (getLayout ().getFormWidth (), getLayout ().getFormHeight ());
	}

	/**
	 * @param name Name the component was added with
	 * @param comp Component
	 */
	@SuppressWarnings ("unused")
	@Override
	public final void addLayoutComponent (final String name, final Component comp)
	{
		throw new UnsupportedOperationException ("addLayoutComponent (String, Component) called - should always call addLayoutComponent (Component, Object) instead");
	}

	/**
	 * @param comp Component to add
	 * @param name Name the component was added with
	 */
	@Override
	public final void addLayoutComponent (final Component comp, final Object name)
	{
		getComponentNames ().put (comp, name.toString ());
	}
	
	/**
	 * @param comp Component to remove
	 */
	@Override
	public final void removeLayoutComponent (final Component comp)
	{
		getComponentNames ().remove (comp);
	}

	/**
	 * Sets the sizes and positions of all the components
	 */
	@Override
	public final void layoutContainer (final Container container)
	{
		synchronized (container.getTreeLock ())
		{
			// Go through all components in the layout manager
			for (final Entry<Component, String> comp : getComponentNames ().entrySet ())
			{
				// Find the corresponding entry in the XML
				final XmlLayoutComponent xml = getLayout ().findComponent (comp.getValue ());
				
				// Set size
				final Dimension size = comp.getKey ().getPreferredSize ();
				if ((xml != null) && (xml.getWidth () != null))
					size.width = xml.getWidth ();

				if ((xml != null) && (xml.getHeight () != null))
					size.height = xml.getHeight ();
				
				comp.getKey ().setSize (size);
				
				if (xml == null)
					log.warn ("No entry in XML layout was found for component with name \"" + comp.getValue () + "\"");
				else
				{
					// Set alignment
					final float horizontalAlignment = (xml.getHorizontalAlignment () != null) ? xml.getHorizontalAlignment () : comp.getKey ().getAlignmentX ();
					final float verticalAlignment = (xml.getVerticalAlignment () != null) ? xml.getVerticalAlignment () : comp.getKey ().getAlignmentY ();
					
					// Set position
					comp.getKey ().setBounds (xml.getLeft () - ((int) (size.width * horizontalAlignment)), xml.getTop () - ((int) (size.height * verticalAlignment)), size.width, size.height);
				}
			}
		}
	}

	/**
	 * Not using this at the moment
	 */
	@Override
	public final float getLayoutAlignmentX (@SuppressWarnings ("unused") final Container container)
	{
		return 0;
	}

	/**
	 * Not using this at the moment
	 */
	@Override
	public final float getLayoutAlignmentY (@SuppressWarnings ("unused") final Container container)
	{
		return 0;
	}

	/**
	 * Nothing is cached by the layout manager, so nothing to do here
	 */
	@Override
	public final void invalidateLayout (@SuppressWarnings ("unused") final Container container)
	{
	}
}