package com.ndg.utils.swing.filefilters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Filter which only matches a specific filename
 */
public class SpecificFilenameFilter extends FileFilter
{
	/**
	 * Filename the filter is looking for
	 */
	private final String filename;
	
	/**
	 * Description for the filter
	 */
	private final String description;
	
	/**
	 * @param aFilename Filename the filter is looking for
	 * @param aDescription Description for the filter
	 */
	public SpecificFilenameFilter (String aFilename, String aDescription)
	{
		super ();
		
		filename = aFilename.toUpperCase ();
		
		if (aDescription == null)
			description = aFilename;
		else
			description = aDescription;
	}

	/**
	 * @param f File to test
	 * @return Whether or not this file 
	 */
	@Override
	public boolean accept (File f)
	{
		if (f != null)
		{
			if (f.isDirectory ())
				return true;
			
			String name = f.getName ();
			if ((name != null) && (name.toUpperCase ().equals (filename)))
				return true;
		}
		return false;
	}

	/**
	 * @return Description for the filter 
	 */
	@Override
	public String getDescription ()
	{
		return description;
	}

}
