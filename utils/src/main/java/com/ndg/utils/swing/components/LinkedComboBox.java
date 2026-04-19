package com.ndg.utils.swing.components;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

/**
 * A set of linked combo boxes, all with the same number of values, such that when the nth value is picked in one combo box, the others are also changed to the nth value
 * Intended usage is for selecting a database or XML record, where the record can be selected by any of its fields
 */
public class LinkedComboBox
{
	/**
	 * The created combo boxes
	 */
	private final List<JComboBox<String>> comboBoxes;

	/**
	 * Calling setSelectedIndex on the other combo boxes triggers further listener events, ending in a recursive loop
	 * So we need to disable processing of the listener events temporarily
	 */
	private boolean syncListenerEnabled;

	/**
	 * Creates a set of linked combo boxes.  The created combo boxes do not actually get added to a panel, the caller must do this by repeated calls to getComboBox ()
	 * @param items The data to appear in the combo boxes
	 */
	public LinkedComboBox (final String [] [] items)
	{
		super ();

		// Create the combo boxes
		comboBoxes = new ArrayList<JComboBox<String>> ();
		for (int index = 0; index < items.length; index++)
		{
			final JComboBox<String> thisComboBox = new JComboBox<String> (items [index]);
			comboBoxes.add (thisComboBox);
			thisComboBox.addItemListener (new LinkedComboBoxItemListener (thisComboBox));
		}

		// Enable syncing
		syncListenerEnabled = true;
	}

	/**
	 * Allows external access to the created combo boxes
	 * @param index Number of the combo box to retrieve
	 * @return The created combo box
	 */
	public JComboBox<String> getComboBox (final int index)
	{
		return comboBoxes.get (index);
	}

	/**
	 * Allows external access to the created combo boxes
	 * @return The number of combo boxes
	 */
	public int getComboBoxCount ()
	{
		return comboBoxes.size ();
	}

	/**
	 * Allows external access to the created combo boxes
	 * @return The array of created combo boxes
	 */
	public List<JComboBox<String>> getComboBoxes ()
	{
		return comboBoxes;
	}

	/**
	 * @return Index into the list of items of the currently selected item, or -1 if no item is selected
	 */
	public int getSelectedIndex ()
	{
		return comboBoxes.get (0).getSelectedIndex ();
	}

	/**
	 * @return The currently selected item
	 */
	public Object getSelectedItem ()
	{
		return comboBoxes.get (0).getSelectedItem ();
	}

	/**
	 * @param index The index of into the list of items of the desired item to select
	 */
	public void setSelectedIndex (final int index)
	{
		comboBoxes.get (0).setSelectedIndex (index);
		// The sync listener will take care of updating the others
	}

	/**
	 * @param item The item to select
	 */
	public void setSelectedItem (final Object item)
	{
		comboBoxes.get (0).setSelectedItem (item);
		// The sync listener will take care of updating the others
	}

	/**
	 * Enables or disables all the linked combo boxes
	 * @param enabled Whether the combo boxes should be enabled or not
	 */
	public void setEnabled (final boolean enabled)
	{
		for (final JComboBox<String> thisComboBox : comboBoxes)
			thisComboBox.setEnabled (enabled);
	}

	/**
	 * Sets the colour of all the linked combo boxes
	 * @param colour Colour to draw the control
	 */
	public void setBackground (final Color colour)
	{
		for (final JComboBox<String> thisComboBox : comboBoxes)
			thisComboBox.setBackground (colour);
	}

	/**
	 * Special item listener which syncs the selected record in all the other combo boxes whenever one of the chosen values changes
	 */
	private class LinkedComboBoxItemListener implements ItemListener
	{
		/**
		 * The combo box we are listening to, i.e. if this listener is triggered, this is the combo box which changed
		 */
		private final JComboBox<String> changedComboBox;

		/**
		 * Creates an item listener which which syncs the selected record in all the other combo boxes whenever one of the chosen values changes
		 * @param aChangedComboBox The combo box we are watching for selected item events
		 */
		public LinkedComboBoxItemListener (final JComboBox<String> aChangedComboBox)
		{
			super ();

			changedComboBox = aChangedComboBox;
		}

		/**
		 * Triggered when the user picks a different entry in the combo box - update the others to match
		 * @param e The details of the change
		 */
		@Override
	    public void itemStateChanged (@SuppressWarnings ("unused") final ItemEvent e)
	    {
	    	if (syncListenerEnabled)
	    	{
	    		final int desiredItemIndex = changedComboBox.getSelectedIndex ();

	    		syncListenerEnabled = false;
	    		try
	    		{
	    			// Update all the other combo boxes to match
	    			for (final JComboBox<String> thisComboBox : comboBoxes)
	    				if (thisComboBox != changedComboBox)
	    					thisComboBox.setSelectedIndex (desiredItemIndex);
	    		}
	    		finally
	    		{
	    			syncListenerEnabled = true;
	    		}
	    	}
	    }
	}

}
