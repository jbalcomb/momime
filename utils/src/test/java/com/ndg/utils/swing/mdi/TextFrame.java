package com.ndg.utils.swing.mdi;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Demo MDI child
 */
public class TextFrame extends JInternalFrame
{
	/**
	 * Notepad-like text area
	 */
	private final JTextArea textArea = new JTextArea ();

	/**
	 * Scroll area for moving around the text editor
	 */
	private final JScrollPane scrollPane = new JScrollPane ();

	/**
	 * Creates the demo MDI child
	 */
	public TextFrame ()
	{
		setSize (200, 300);
		setTitle ("Edit Text");
		setMaximizable (true);
		setIconifiable (true);
		setClosable (true);
		setResizable (true);
		scrollPane.getViewport ().add (textArea);
		getContentPane ().setLayout (new BorderLayout ());
		getContentPane ().add (scrollPane, BorderLayout.CENTER);
	}
}