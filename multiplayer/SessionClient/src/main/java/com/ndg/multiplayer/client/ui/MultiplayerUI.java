package com.ndg.multiplayer.client.ui;

import java.io.IOException;

import com.ndg.utils.swing.NdgUIUtils;

/**
 * Ancestor used by all of the UI dialog and frames.
 */
public abstract class MultiplayerUI
{
	/** Helper methods and constants for creating and laying out Swing components */
	private NdgUIUtils utils;
	
	/** Icon to use for the dialog or frame, or null to not use one */
	private String iconFilename;
	
	/** Monitor number to display dialog or frame on, or null to use default */
	private Integer monitorNumber;
	
	/**
	 * Descendant classes put code here to create and lay out all the Swing components
	 * @throws IOException If a resource cannot be found
	 */
	protected abstract void init () throws IOException;

	/**
	 * @return Whether this screen is currently displayed or not
	 */
	public abstract boolean isVisible ();
	
	/**
	 * @param v Whether to display or hide this screen
	 * @throws IOException If a resource cannot be found
	 */
	public abstract void setVisible (final boolean v) throws IOException;
	
	/**
	 * @return Helper methods and constants for creating and laying out Swing components
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param util Helper methods and constants for creating and laying out Swing components
	 */
	public final void setUtils (final NdgUIUtils util)
	{
		utils = util;
	}

	/**
	 * @return Icon to use for the dialog or frame, or null to not use one
	 */
	public final String getIconFilename ()
	{
		return iconFilename;
	}

	/**
	 * @param i Icon to use for the dialog or frame, or null to not use one
	 */
	public final void setIconFilename (final String i)
	{
		iconFilename = i;
	}

	/**
	 * @return Monitor number to display dialog or frame on, or null to use default
	 */
	public final Integer getMonitorNumber ()
	{
		return monitorNumber;
	}

	/**
	 * @param n Monitor number to display dialog or frame on, or null to use default
	 */
	public final void setMonitorNumber (final Integer n)
	{
		monitorNumber = n;
	}
}