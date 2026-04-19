package com.ndg.utils.swing.mdi;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

/**
 * Demo MDI parent
 */
public class Notepad extends JFrame
{
	/**
	 * The MDI frame itself
	 */
	private final MDIDesktopPane desktop = new MDIDesktopPane ();

	/**
	 * Menu on the MDI
	 */
	private final JMenuBar menuBar = new JMenuBar ();

	/**
	 * File menu
	 */
	private final JMenu fileMenu = new JMenu ("File");

	/**
	 * New menu
	 */
	private final JMenuItem newMenu = new JMenuItem ("New");

	/**
	 * Scroll pane around the main MDI area
	 */
	private final JScrollPane scrollPane = new JScrollPane ();

	/**
	 * Creates the demo MDI parent
	 */
	public Notepad ()
	{
		menuBar.add (fileMenu);
		menuBar.add (new WindowMenu (desktop));
		fileMenu.add (newMenu);
		setJMenuBar (menuBar);
		setTitle ("MDI Test");
		scrollPane.getViewport ().add (desktop);
		getContentPane ().setLayout (new BorderLayout ());
		getContentPane ().add (scrollPane, BorderLayout.CENTER);

		addWindowListener (new WindowAdapter ()
		{
			@Override
			public void windowClosing (@SuppressWarnings ("unused") final WindowEvent e)
			{
				System.exit (0);
			}
		});
		
		newMenu.addActionListener ((ev) -> desktop.add (new TextFrame ()));
	}

	/**
	 * Kicks off the MDI demo
	 * @param args Command line arguments - ignored
	 */
	public static void main (final String [] args)
	{
		final Notepad notepad = new Notepad ();
		notepad.setSize (600, 400);
		notepad.setVisible (true);
	}
}