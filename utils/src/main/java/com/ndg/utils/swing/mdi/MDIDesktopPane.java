package com.ndg.utils.swing.mdi;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * An extension of WDesktopPane that supports often used MDI functionality. This class also handles setting scroll bars for when windows move too far to the right or bottom, providing the MDIDesktopPane is in a ScrollPane.
 */
public class MDIDesktopPane extends JDesktopPane
{
	/**
	 * When adding or cascading frames, the space to set each frame below and right from the previous frame
	 */
	private static int FRAME_OFFSET = 20;

	/**
	 * The underlying desktop manager
	 */
	private MDIDesktopManager manager;

	/**
	 * Creates a new desktop pane with MDI functionality such as making scroll bars appear when windows move too far to the right or bottom
	 */
	public MDIDesktopPane ()
	{
		super ();

		manager = new MDIDesktopManager (this);
		setDesktopManager (manager);
		setDragMode (JDesktopPane.OUTLINE_DRAG_MODE);
	}

	/**
	 * When the size of the main window changes, check if we need to alter the scrollbars
     * @param x the new x-coordinate of this component
     * @param y the new y-coordinate of this component
     * @param w the new width of this component
     * @param h the new height of this component
	 */
	@Override
	public void setBounds (int x, int y, int w, int h)
	{
		super.setBounds (x, y, w, h);
		checkDesktopSize ();
	}

	/**
	 * Sets the position and default size of newly added windows
	 * @param frame The new window to add to the MDI
	 * @return The same frame object as passed in
	 */
	public Component add (JInternalFrame frame)
	{
		JInternalFrame [] array = getAllFrames ();
		Point p;
		int w;
		int h;

		Component retval = super.add (frame);
		checkDesktopSize ();
		if (array.length > 0)
		{
			p = array [0].getLocation ();
			p.x = p.x + FRAME_OFFSET;
			p.y = p.y + FRAME_OFFSET;
		}
		else
		{
			p = new Point (0, 0);
		}
		frame.setLocation (p.x, p.y);
		if (frame.isResizable ())
		{
			w = getWidth () - (getWidth () / 3);
			h = getHeight () - (getHeight () / 3);
			if (w < frame.getMinimumSize ().getWidth ())
				w = (int) frame.getMinimumSize ().getWidth ();
			if (h < frame.getMinimumSize ().getHeight ())
				h = (int) frame.getMinimumSize ().getHeight ();
			frame.setSize (w, h);
		}
		moveToFront (frame);
		frame.setVisible (true);
		try
		{
			frame.setSelected (true);
		}
		catch (PropertyVetoException e)
		{
			frame.toBack ();
		}
		return retval;
	}

	/**
	 * When a window is removed from the MDI, we may need to alter the scrollbars
	 * @param c The window to remove
	 */
	@Override
	public void remove (Component c)
	{
		super.remove (c);
		checkDesktopSize ();
	}

	/**
	 * Cascade all internal frames
	 */
	public void cascadeFrames ()
	{
		int x = 0;
		int y = 0;
		JInternalFrame allFrames[] = getAllFrames ();

		manager.setNormalSize ();
		int frameHeight = (getBounds ().height - 5) - allFrames.length * FRAME_OFFSET;
		int frameWidth = (getBounds ().width - 5) - allFrames.length * FRAME_OFFSET;
		for (int i = allFrames.length - 1; i >= 0; i--)
		{
			allFrames [i].setSize (frameWidth, frameHeight);
			allFrames [i].setLocation (x, y);
			x = x + FRAME_OFFSET;
			y = y + FRAME_OFFSET;
		}
	}

	/**
	 * Tile all internal frames
	 */
	public void tileFrames ()
	{
		java.awt.Component allFrames[] = getAllFrames ();
		manager.setNormalSize ();
		int frameHeight = getBounds ().height / allFrames.length;
		int y = 0;
		for (int i = 0; i < allFrames.length; i++)
		{
			allFrames [i].setSize (getBounds ().width, frameHeight);
			allFrames [i].setLocation (0, y);
			y = y + frameHeight;
		}
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the given dimension.
	 * @param d The size to set all the component size properties to
	 */
	public void setAllSize (Dimension d)
	{
		setMinimumSize (d);
		setMaximumSize (d);
		setPreferredSize (d);
	}

	/**
	 * Sets all component size properties ( maximum, minimum, preferred) to the given width and height.
	 * @param width The width to set all the component size properties to
	 * @param height The height to set all the component size properties to
	 */
	public void setAllSize (int width, int height)
	{
		setAllSize (new Dimension (width, height));
	}

	/**
	 * Sets or hides the scroll bars when the size of the desktop changes or the windows inside it change
	 */
	private void checkDesktopSize ()
	{
		if (getParent () != null && isVisible ())
			manager.resizeDesktop ();
	}
}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane. Used to provide scrollbar functionality.
 */
class MDIDesktopManager extends DefaultDesktopManager
{
	/**
	 * The MDI desktop we are managing
	 */
	private MDIDesktopPane desktop;

	/**
	 * Creates a new specialised MDI desktop manager
	 * @param aDesktop The MDI desktop we are managing
	 */
	public MDIDesktopManager (MDIDesktopPane aDesktop)
	{
		desktop = aDesktop;
	}

	/**
	 * When windows/internal frames are resized within the MDI, we may need to alter the scrollbars
	 * @param f The Window being resized
	 */
	@Override
	public void endResizingFrame (JComponent f)
	{
		super.endResizingFrame (f);
		resizeDesktop ();
	}

	/**
	 * When windows/internal frames are dragged in the MDI, we may need to alter the scrollbars
	 * @param f The Window being dragged
	 */
	@Override
	public void endDraggingFrame (JComponent f)
	{
		super.endDraggingFrame (f);
		resizeDesktop ();
	}

	/**
	 * Sets the size of the scroll bars from the size of the main MDI window... I think
	 */
	public void setNormalSize ()
	{
		JScrollPane scrollPane = getScrollPane ();
		int x = 0;
		int y = 0;
		Insets scrollInsets = getScrollPaneInsets ();

		if (scrollPane != null)
		{
			Dimension d = scrollPane.getVisibleRect ().getSize ();
			if (scrollPane.getBorder () != null)
			{
				d.setSize (d.getWidth () - scrollInsets.left - scrollInsets.right, d.getHeight () - scrollInsets.top - scrollInsets.bottom);
			}

			d.setSize (d.getWidth () - 20, d.getHeight () - 20);
			desktop.setAllSize (x, y);
			scrollPane.invalidate ();
			scrollPane.validate ();
		}
	}

	/**
	 * @return The size of the scroll pane border
	 */
	private Insets getScrollPaneInsets ()
	{
		JScrollPane scrollPane = getScrollPane ();
		Insets result;
		
		if (scrollPane == null)
			result = new Insets (0, 0, 0, 0);
		else
			result = getScrollPane ().getBorder ().getBorderInsets (scrollPane);
		
		return result;
	}

	/**
	 * @return The scroll pane this desktop is embedded inside of
	 */
	private JScrollPane getScrollPane ()
	{
		if (desktop.getParent () instanceof JViewport)
		{
			JViewport viewPort = (JViewport) desktop.getParent ();
			if (viewPort.getParent () instanceof JScrollPane)
				return (JScrollPane) viewPort.getParent ();
		}
		return null;
	}

	/**
	 * Resizes the scroll pane as appropriate for the current MDI window size and the size and positions of all the child windows
	 */
	protected void resizeDesktop ()
	{
		int x = 0;
		int y = 0;
		JScrollPane scrollPane = getScrollPane ();
		Insets scrollInsets = getScrollPaneInsets ();

		if (scrollPane != null)
		{
			JInternalFrame allFrames[] = desktop.getAllFrames ();
			for (int i = 0; i < allFrames.length; i++)
			{
				if (allFrames [i].getX () + allFrames [i].getWidth () > x)
				{
					x = allFrames [i].getX () + allFrames [i].getWidth ();
				}
				if (allFrames [i].getY () + allFrames [i].getHeight () > y)
				{
					y = allFrames [i].getY () + allFrames [i].getHeight ();
				}
			}
			Dimension d = scrollPane.getVisibleRect ().getSize ();
			if (scrollPane.getBorder () != null)
			{
				d.setSize (d.getWidth () - scrollInsets.left - scrollInsets.right, d.getHeight () - scrollInsets.top - scrollInsets.bottom);
			}

			if (x <= d.getWidth ())
				x = ((int) d.getWidth ()) - 20;
			if (y <= d.getHeight ())
				y = ((int) d.getHeight ()) - 20;
			desktop.setAllSize (x, y);
			scrollPane.invalidate ();
			scrollPane.validate ();
		}
	}
}