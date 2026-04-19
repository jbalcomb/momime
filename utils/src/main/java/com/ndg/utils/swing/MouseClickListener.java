package com.ndg.utils.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Java mouseClicked events don't really work properly, they only get triggered if the user doesn't move the mouse even 1 pixel
 * between clicking and releasing the mouse.  This is inconsistent with Swing's own behaviour on buttons, which use the
 * pressed and released events, so this listener attempts to simply and fix that.
 * 
 * Just using actionListener only fixes part of the problem, because actionListener doesn't fire for right clicks.
 * 
 * From my own testing with this, Java seems to treat clicks (i.e. fire actionListener events) as when the mouse was
 * pressed and released anywhere within the same component, even if in the meantime the mouse left the component
 * and came back.
 */
public abstract class MouseClickListener extends MouseAdapter 
{
	/** The component the mouse was in when it was pressed */
	private Component componentClicked;
	
	/** The component the mouse is in now */
	private Component currentComponent;
	
	/**
	 * @param ev Mouse click event that works even if the mouse pointer is moved between pressing and releasing the mouse
	 */
	public abstract void fixMouseClicked (final MouseEvent ev);

	/**
	 * When the mouse is pressed, remember which component it was in
	 */
	@Override
	public void mousePressed (final MouseEvent ev)
	{
		componentClicked = ev.getComponent ();
	}

	/**
	 * If the mouse is released in the same component, consider it a click.
	 * Note the component indicated by the event is the component the mouse press occurred in, not necessarily where the mouse is now.
	 * While counter intuitive, this is rather useful because it means we still get notified of the mouseReleased event even if the mouse has
	 * moved onto some control that doesn't have this listener added onto it.
	 */
	@Override
	public void mouseReleased (final MouseEvent ev)
	{
		if ((currentComponent != null) && (currentComponent == componentClicked))
			fixMouseClicked (ev);
		
		componentClicked = null;
	}

	/**
	 * Record when the mouse enters a component
	 */
	@Override
	public void mouseEntered (final MouseEvent ev)
	{
		currentComponent = ev.getComponent ();
	}

	/**
	 * Record when the mouse leaves a component
	 */
	@Override
	public void mouseExited (@SuppressWarnings ("unused") final MouseEvent ev)
	{
		currentComponent = null;
	}
}