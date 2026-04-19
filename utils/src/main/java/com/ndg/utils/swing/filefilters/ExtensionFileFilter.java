package com.ndg.utils.swing.filefilters;

/*
 * @(#)ExampleFileFilter.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)ExampleFileFilter.java	1.16 04/07/26
 */

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out all files except
 * for those type extensions that it knows about.
 * 
 * Extensions are of the type ".foo", which is typically found on Windows and
 * Unix boxes, but not on Macinthosh. Case is ignored.
 * 
 * Example - create a new filter that filerts out all files but gif and jpg
 * image files:
 * 
 * JFileChooser chooser = new JFileChooser(); ExampleFileFilter filter = new
 * ExampleFileFilter( new String{"gif", "jpg"}, "JPEG & GIF Images")
 * chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 * 
 * @version 1.16 07/26/04
 * @author Jeff Dinkins
 */
public class ExtensionFileFilter extends FileFilter
{
	/**
	 * Stores the list of accepted extensions
	 * NDG 24/2/2007 - Use definite types
	 */
	private Hashtable<String, ExtensionFileFilter> filters = null; 
	
	/**
	 * The human readable description of this filter, without the extension list
	 */
	private String description = null;
	
	/**
	 * The human readable description of this filter, including the extension list if useExtensionsInDescription = true
	 */
	private String fullDescription = null;
	
	/**
	 * Whether the extension list (.jpg, .gif, etc) should show up in the human readable description
	 */
	private boolean useExtensionsInDescription = true;

	/**
	 * Creates a file filter. If no filters are added, then all files are
	 * accepted.
	 * 
	 * @see #addExtension
	 */
	public ExtensionFileFilter ()
	{
		this.filters = new Hashtable<String, ExtensionFileFilter> ();
	}

	/**
	 * Creates a file filter that accepts files with the given extension.
	 * Example: new ExampleFileFilter("jpg");
	 * 
	 * @param extension The file extension to accept, excluding the dot prefix
	 * @see #addExtension
	 */
	public ExtensionFileFilter (String extension)
	{
		this (extension, null);
	}

	/**
	 * Creates a file filter that accepts the given file type. Example: new
	 * ExampleFileFilter("jpg", "JPEG Image Images");
	 * 
	 * Note that the "." before the extension is not needed. If provided, it
	 * will be ignored.
	 * 
	 * @param extension The file extension to accept, excluding the dot prefix
	 * @param aDescription The description to prefix the list of extensions by
	 * @see #addExtension
	 */
	public ExtensionFileFilter (String extension, String aDescription)
	{
		this ();
		if (extension != null)
			addExtension (extension);
		if (aDescription != null)
			setDescription (aDescription);
	}

	/**
	 * Creates a file filter from the given string array. Example: new
	 * ExampleFileFilter(String {"gif", "jpg"});
	 * 
	 * Note that the "." before the extension is not needed adn will be ignored.
	 * 
	 * @param aFilters Array of the file extensions to accept, excluding the dot prefixes
	 * @see #addExtension
	 */
	public ExtensionFileFilter (String [] aFilters)
	{
		this (aFilters, null);
	}

	/**
	 * Creates a file filter from the given string array and description.
	 * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG
	 * Images");
	 * 
	 * Note that the "." before the extension is not needed and will be ignored.
	 * 
	 * @param aFilters Array of the file extensions to accept, excluding the dot prefixes
	 * @param aDescription The description to prefix the list of extensions by
	 * @see #addExtension
	 */
	public ExtensionFileFilter (String [] aFilters, String aDescription)
	{
		this ();
		for (int i = 0; i < aFilters.length; i++)
		{
			// add filters one by one
			addExtension (aFilters [i]);
		}
		if (aDescription != null)
			setDescription (aDescription);
	}

	/**
	 * Return true if this file should be shown in the directory pane, false if
	 * it shouldn't.
	 * 
	 * Files that begin with "." are ignored.
	 * 
	 * @param f The file to test
	 * @return Whether this file will be included or not
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	@Override
	public boolean accept (File f)
	{
		if (f != null)
		{
			if (f.isDirectory ())
				return true;
			
			String extension = getExtension (f);
			if (extension != null && filters.get (getExtension (f)) != null)
				return true;
		}
		return false;
	}

	/**
	 * Return the extension portion of the file's name
	 * 
	 * @param f The file to determine the extension of
	 * @return The extension portion of filename f
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public String getExtension (File f)
	{
		if (f != null)
		{
			String filename = f.getName ();
			int i = filename.lastIndexOf ('.');
			if (i > 0 && i < filename.length () - 1)
				return filename.substring (i + 1).toLowerCase ();
		}
		return null;
	}

	/**
	 * Adds a filetype "dot" extension to filter against.
	 * 
	 * For example: the following code will create a filter that filters out all
	 * files except those that end in ".jpg" and ".tif":
	 * 
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addExtension("jpg"); filter.addExtension("tif");
	 * 
	 * @param extension The file extension to accept, excluding the dot prefix
	 */
	public void addExtension (String extension)
	{
		if (filters == null)
		{
			filters = new Hashtable<String, ExtensionFileFilter> (5);
		}
		filters.put (extension.toLowerCase (), this);
		fullDescription = null;
	}

	/**
	 * @return The human readable description of this filter. For example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 * 
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	@Override
	public String getDescription ()
	{
		if (fullDescription == null)
		{
			if (description == null || isExtensionListInDescription ())
			{
				fullDescription = description == null ? "(" : description + " (";
				
				// build the description from the extension list
				Enumeration<String> extensions = filters.keys ();
				if (extensions != null)
				{
					fullDescription += "." + extensions.nextElement ();
					while (extensions.hasMoreElements ())
						fullDescription += ", ." + extensions.nextElement ();
				}
				fullDescription += ")";
			}
			else
				fullDescription = description;
		}
		return fullDescription;
	}

	/**
	 * @param aDescription The human readable description of this filter. For example: filter.setDescription("Gif and JPG Images");
	 * 
	 * @see #getDescription
	 * @see #setExtensionListInDescription
	 * @see #isExtensionListInDescription
	 */
	public void setDescription (String aDescription)
	{
		description = aDescription;
		fullDescription = null;
	}

	/**
	 * @param b Whether the extension list (.jpg, .gif, etc) should show up in the human readable description
	 * 
	 * Only relevent if a description was provided in the constructor or using
	 * setDescription();
	 * 
	 * @see #getDescription
	 * @see #setDescription
	 * @see #isExtensionListInDescription
	 */
	public void setExtensionListInDescription (boolean b)
	{
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * @return Whether the extension list (.jpg, .gif, etc) should show up in the human readable description
	 * 
	 * Only relevent if a description was provided in the constructor or using
	 * setDescription();
	 * 
	 * @see #getDescription
	 * @see #setDescription
	 * @see #setExtensionListInDescription
	 */
	public boolean isExtensionListInDescription ()
	{
		return useExtensionsInDescription;
	}
}
