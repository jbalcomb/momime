package com.ndg.utils.swing.layoutmanagers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.List;

/**
 * Layout manager that makes all components take up the full space of the container.
 * This is intended to be used with LayeredPane, where you want each pane to take up the full space.  Its a bit bizarre that this doesn't happen by default.
 */
public final class LayeredPaneLayoutManager implements LayoutManager2
{
	/** Stores names of components as they get added */
	private final List<Component> components = new ArrayList<Component> ();
	
	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension preferredLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return container.getPreferredSize ();
	}

	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension minimumLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return container.getMinimumSize ();
	}
	
	/**
	 * @return Fixed container size
	 */
	@Override
	public final Dimension maximumLayoutSize (@SuppressWarnings ("unused") final Container container)
	{
		return container.getMaximumSize ();
	}

	/**
	 * @param name Name the component was added with
	 * @param comp Component
	 */
	@SuppressWarnings ("unused")
	@Override
	public final void addLayoutComponent (final String name, final Component comp)
	{
		components.add (comp);
	}

	/**
	 * @param comp Component to add
	 * @param name Name the component was added with
	 */
	@SuppressWarnings ("unused")
	@Override
	public final void addLayoutComponent (final Component comp, final Object name)
	{
		components.add (comp);
	}
	
	/**
	 * @param comp Component to remove
	 */
	@Override
	public final void removeLayoutComponent (final Component comp)
	{
		components.remove (comp);
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
			for (final Component comp : components)
				comp.setBounds (0, 0, container.getWidth (), container.getHeight ());
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