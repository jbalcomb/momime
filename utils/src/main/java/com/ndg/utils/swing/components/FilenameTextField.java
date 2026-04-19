package com.ndg.utils.swing.components;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Specialized edit box for entering a filename.  Includes a ... button to open up a file dialog to browse for the file.
 */
public class FilenameTextField extends JTextField
{
	/** Width to create the ... button */
	private final static int BUTTON_WIDTH = 18;

	/** True to use a "save" dialog, False to use an "open" dialog */
	private boolean useSaveDialog;

	/** The ... button, which opens the file dialog when clicked */
	private final JButton showDialogButton;

	/** The actual open/save file dialog */
	private final JFileChooser fileChooser;

	/**
	 * Creates a specialized edit box for entering a filename
	 */
	public FilenameTextField ()
	{
		super ();

		// Create file chooser
		fileChooser = new JFileChooser ();

		// Create button
		showDialogButton = new JButton ();
		showDialogButton.setText ("...");

		showDialogButton.addActionListener ((ev) ->
		{
			populateFileChooserFromTextField (fileChooser);

			int result;
			if (useSaveDialog ())
				result = fileChooser.showSaveDialog (null);
			else
				result = fileChooser.showOpenDialog (null);

			if (result == JFileChooser.APPROVE_OPTION)
				populateTextFieldFromFileChooser (fileChooser);
		});

		add (showDialogButton);

		// Move the button around inside the text field when the text field is resized
		addComponentListener (new ComponentAdapter ()
		{
			@Override
			public void componentResized (@SuppressWarnings ("unused") final ComponentEvent e)
			{
				showDialogButton.setLocation (getWidth () - BUTTON_WIDTH - 2, 2);
				showDialogButton.setSize (BUTTON_WIDTH, getHeight () - 4);
			}
		});
	}

	/**
	 * @param dest File chooser to preload with the filename
	 */
	protected void populateFileChooserFromTextField (final JFileChooser dest)
	{
		if (getText () != null)
			if (!getText ().trim ().equals (""))
				dest.setSelectedFile (new File (getText ().trim ()));
	}
	
	/**
	 * @param source File chooser to read selected file from
	 */
	protected void populateTextFieldFromFileChooser (final JFileChooser source)
	{
		setText (source.getSelectedFile ().getAbsolutePath ());
	}
	
	/**
	 * Enable or disable the "..." button at the same time as the text field
	 * @param anEnabled Whether or not the filename text field should be enabled
	 */
	@Override
	public void setEnabled (final boolean anEnabled)
	{
		super.setEnabled (anEnabled);
		showDialogButton.setEnabled (anEnabled);
	}

	/**
	 * @return True if using a Save dialog, False if using an Open dialog
	 */
	public boolean useSaveDialog ()
	{
		return useSaveDialog;
	}

	/**
	 * @param aUseSaveDialog True if using a Save dialog, False if using an Open dialog
	 */
	public void setUseSaveDialog (final boolean aUseSaveDialog)
	{
		useSaveDialog = aUseSaveDialog;
	}

    /**
     * Adds a filter to the list of user choosable file filters
     * @param filter The file filter to add
     */
	public void addChoosableFileFilter (final FileFilter filter)
	{
		fileChooser.addChoosableFileFilter (filter);
	}
	
	/**
	 * @return The actual open/save file dialog
	 */
	public final JFileChooser getFileChooser ()
	{
		return fileChooser;
	}
}