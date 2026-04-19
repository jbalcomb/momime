package com.ndg.utils;

import java.io.File;

/**
 * Equivalents of some of the filename handling functions in Delphi 
 */
public final class FileNameUtils
{
	/**
	 * Prevent instatiation of this class
	 */
	private FileNameUtils ()
	{
	}

	/**
	 * @param fileName Filename to extract extension of
	 * @return Extension part of a filename, including the .
	 */
	public final static String extractFileExt (final String fileName)
	{
		// Strip off any path part, so that we correctly return blanks for filenames such as "C:\Folder with a.dot in the name\filename"
		String result = new File (fileName).getName ();

		// Then look for the dot
		int dotPos = result.lastIndexOf (".");
		if (dotPos < 0)
			result = "";
		else
			result = result.substring (dotPos);

		return result;
	}

	/**
	 * @param fileName Filename to change extension of
	 * @param newExtension Extension to change filename to, including the leading . (unless it is the blank extension)
	 * @return fileName, with the extension changed to newExtension
	 */
	public final static String changeFileExt (final String fileName, final String newExtension)
	{
		// Strip off any path part, so that we don't end up replacing part of the path if working on filenames such as "C:\Folder with a.dot in the name\filename"
		String newName = new File (fileName).getName ();

		// Then look for the dot
		final int dotPos = newName.lastIndexOf (".");
		if (dotPos >= 0)
			newName = newName.substring (0, dotPos);
		
		newName = newName + newExtension;
		
		// Then reassemble the relevant parts
		final File parent = new File (fileName).getParentFile ();
		return (parent == null) ? newName : new File (parent, newName).getPath ();
	}
	
	/**
	 * Resolve 'path up' sequences, i.e. .. by removing the .. and the folder that would be backed out of e.g. converts a/b/c/d/../../e to a/b/e
	 * If there are more .. than folders, some .. will remain, e.g. converts a/b/../../../c to ../c
	 * @param fileName The unresolved path
	 * @param fileSeparator File separator to use - typically pass in null to use the O/S dependant value
	 * @return Path as input, with each .. and the path it refers to removed
	 */
	public final static String resolvePathUps (final String fileName, final String fileSeparator)
	{
		String useFileSeparator = fileSeparator;
		if (useFileSeparator == null)
			useFileSeparator = File.separator;
		
		String searchString = useFileSeparator + "..";
		
		// Note the indexof intentionally won't pick up .. at the start of the path to handle the second example above
		String result = fileName;
		int secondPos = result.indexOf (searchString);
		while (secondPos > 0)
		{
			String bitBeforeSlash = result.substring (0, secondPos);
			int firstPos = bitBeforeSlash.lastIndexOf (useFileSeparator);
			
			// firstPos might be -1 if the folder to remove is right at the start
			if (firstPos < 0)
				result = result.substring (secondPos + searchString.length () + useFileSeparator.length ());
			else
				result = result.substring (0, firstPos) + result.substring (secondPos + searchString.length ());			
			
			// Search again
			secondPos = result.indexOf (searchString);
		}
		
		return result;
	}
}