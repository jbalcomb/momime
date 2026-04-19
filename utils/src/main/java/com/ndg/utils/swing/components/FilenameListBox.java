package com.ndg.utils.swing.components;

import java.awt.Dimension;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 * Like FilenameTextField, but allows picking multiple files
 */
public class FilenameListBox extends JLayeredPane
{
	/** Width to create the ... button */
	private final static int BUTTON_WIDTH = 18;

	/** Width to create the ... height */
	private final static int BUTTON_HEIGHT = 25;
	
	/** JLayeredPane layer where the scroll/list box is drawn */
	private final static int SCROLL_PANE_LAYER = 1;

	/** JLayeredPane layer where the ... button is drawn */
	private final static int DOT_DOT_DOT_BUTTON_LAYER = 2;
	
	/** The ... button, which opens the file dialog when clicked */
	private final JButton showDialogButton;

	/** Scroll pane */
	private final JScrollPane scrollPane;

	/** The actual open/save file dialog */
	private final JFileChooser fileChooser;

	/** Model for list of filenames */
	private final DefaultListModel<String> filenamesListModel;

	/** UI control for List of filenames */
	private final JList<String> filenamesList;
	
	/**
	 * Creates a specialized listbox for choosing multiple files
	 * @param listBoxSize Size to make the list box
	 */
	public FilenameListBox (final Dimension listBoxSize)
	{
		super ();

		setMinimumSize (listBoxSize);
		setMaximumSize (listBoxSize);
		setPreferredSize (listBoxSize);
		
		// Create list
		filenamesListModel = new DefaultListModel<String> ();
		filenamesList = new JList<String> (filenamesListModel);
		scrollPane = new JScrollPane (filenamesList);
		
		scrollPane.setBounds (0, 0, listBoxSize.width, listBoxSize.height);
		add (scrollPane, Integer.valueOf (SCROLL_PANE_LAYER));
		
		// Create file chooser
		fileChooser = new JFileChooser ();
		fileChooser.setMultiSelectionEnabled (true);

		// Create button
		showDialogButton = new JButton ();
		showDialogButton.setText ("...");

		showDialogButton.addActionListener ((ev) ->
		{
			populateFileChooserFromListBox (filenamesListModel, fileChooser);
			final int result = fileChooser.showOpenDialog (null);

			if (result == JFileChooser.APPROVE_OPTION)
				populateListBoxFromFileChooser (fileChooser, filenamesListModel);
		});

		showDialogButton.setBounds (listBoxSize.width - BUTTON_WIDTH - 2, 2, BUTTON_WIDTH, BUTTON_HEIGHT);
		add (showDialogButton, Integer.valueOf (DOT_DOT_DOT_BUTTON_LAYER));
	}
	
	/**
	 * @param source List model containing filenames
	 * @param dest File chooser to preload with the filenames
	 */
	protected void populateFileChooserFromListBox (final DefaultListModel<String> source, final JFileChooser dest)
	{
		final File [] selectedFiles = new File [source.size ()];
		for (int index = 0; index < source.size (); index++)
			selectedFiles [index] = new File (source.get (index).trim ());
		
		dest.setSelectedFiles (selectedFiles);
	}
	
	/**
	 * @param source File chooser to read selected files from
	 * @param dest List box to populate with filenames
	 */
	protected void populateListBoxFromFileChooser (final JFileChooser source, final DefaultListModel<String> dest)
	{
		dest.clear ();
		for (final File selectedFile : source.getSelectedFiles ())
			dest.addElement (selectedFile.getAbsolutePath ());
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
	 * @return Model for list of filenames, to allow the caller to get at the list of filenames
	 */
	public final DefaultListModel<String> getFilenamesListModel ()
	{
		return filenamesListModel;
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