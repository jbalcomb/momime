package com.ndg.utils.swing.mdi;

import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Menu component that handles the functionality expected of a standard "Windows" menu for MDI applications
 */
public class WindowMenu extends JMenu
{
	/**
	 * The MDI desktop to display the windows of
	 */
	private final MDIDesktopPane desktop;

	/**
	 * Menu item arrange the windows in a cascading formation
	 */
	private final JMenuItem cascade = new JMenuItem ("Cascade");

	/**
	 * Menu item arrange the windows in a tile formation
	 */
	private final JMenuItem tile = new JMenuItem ("Tile");

	/**
	 * Creates a menu component that handles the functionality expected of a standard "Windows" menu for MDI applications
	 * @param aDesktop The MDI desktop to display the windows of
	 */
	public WindowMenu (final MDIDesktopPane aDesktop)
	{
		desktop = aDesktop;
		setText ("Window");

		cascade.addActionListener ((ev) -> desktop.cascadeFrames ());

		tile.addActionListener ((ev) -> desktop.tileFrames ());

		addMenuListener (new MenuListener ()
		{
			@Override
			public final void menuCanceled (@SuppressWarnings ("unused") final MenuEvent e)
			{
			}

			@Override
			public final void menuDeselected (@SuppressWarnings ("unused") final MenuEvent e)
			{
				removeAll ();
			}

			@Override
			public final void menuSelected (@SuppressWarnings ("unused") final MenuEvent e)
			{
				buildChildMenus ();
			}
		});
	}

	/**
	 * Sets up the children menus depending on the current desktop state
	 */
	private void buildChildMenus ()
	{
		int i;
		ChildMenuItem menu;
		final JInternalFrame [] array = desktop.getAllFrames ();

		add (cascade);
		add (tile);
		if (array.length > 0)
			addSeparator ();
		cascade.setEnabled (array.length > 0);
		tile.setEnabled (array.length > 0);

		for (i = 0; i < array.length; i++)
		{
			menu = new ChildMenuItem (array [i]);
			menu.setState (i == 0);
			menu.addActionListener ((ev) ->
			{
				final JInternalFrame frame = ((ChildMenuItem) ev.getSource ()).getFrame ();
				frame.moveToFront ();
				try
				{
					frame.setSelected (true);
				}
				catch (final PropertyVetoException e)
				{
					e.printStackTrace ();
				}
			});
			menu.setIcon (array [i].getFrameIcon ());
			add (menu);
		}
	}

	/**
	 * This JCheckBoxMenuItem descendant is used to track the child frame that corresponds to a give menu.
	 */
	class ChildMenuItem extends JCheckBoxMenuItem
	{
		/**
		 * The frame that this menu item activates
		 */
		private final JInternalFrame frame;

		/**
		 * Creates a menu item that switches focus to a particular window when activated
		 * @param aFrame The frame that this menu item activates
		 */
		public ChildMenuItem (final JInternalFrame aFrame)
		{
			super (aFrame.getTitle ());
			frame = aFrame;
		}

		/**
		 * @return The frame that this menu item activates
		 */
		public JInternalFrame getFrame ()
		{
			return frame;
		}
	}
}