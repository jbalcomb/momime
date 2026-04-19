package com.ndg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Equivalents of my StringUtils unit in Delphi
 */
public final class StringUtils
{
	/**
	 * Prevent instatiation of this class
	 */
	private StringUtils ()
	{
	}

	/**
	 * Splits a string at a certain piece of text into separate strings, e.g. split a line from a CSV file into each of its values split by commas
	 * @param stringToSplit The string to split up, e.g. 3,5,7
	 * @param splitAt The string to split at, e.g. comma
	 * @return List of strings
	 */
	public final static List<String> splitStringIntoList (final String stringToSplit, final String splitAt)
	{
		final ArrayList<String> result = new ArrayList<String> ();
		String remaining = stringToSplit;

		int pos = remaining.indexOf (splitAt);
		while (pos >= 0)
		{
			result.add (remaining.substring (0, pos));
			remaining = remaining.substring (pos + splitAt.length ());

			pos = remaining.indexOf (splitAt);
		}

		// Any left?
		if (!remaining.equals (""))
			result.add (remaining);

		return result;
	}

	/**
	 * Ensures a string is a particular length by adding to the start of it
	 * @param s Base string to start from, or this can be left null
	 * @param addChar String to add to increase the length
	 * @param desiredLength The desired length to make the string
	 * @return String with padding added to the start to make it the desired length
	 */
	public final static String padStart (final String s, final String addChar, final int desiredLength)
	{
		String s2;
		if (s == null)
			s2 = "";
		else
			s2 = s;

		while (s2.length () < desiredLength)
			s2 = addChar + s2;

		return s2;
	}

	/**
	 * Ensures a string is a particular length by adding to the end of it
	 * @param s Base string to start from, or this can be left null
	 * @param addChar String to add to increase the length
	 * @param desiredLength The desired length to make the string
	 * @return String with padding added to the end to make it the desired length
	 */
	public final static String padEnd (final String s, final String addChar, final int desiredLength)
	{
		String s2;
		if (s == null)
			s2 = "";
		else
			s2 = s;

		while (s2.length () < desiredLength)
			s2 = s2 + addChar;

		return s2;
	}

	/**
	 * Converts a list of strings to an array
	 * @param list List of strings
	 * @return Array of strings, or null if the list is null or empty
	 */
	public final static String [] stringListToStringArray (final List<String> list)
	{
		String [] result = null;
		if (list != null)
			if (list.size () > 0)
			{
				result = new String [list.size ()];
				for (int itemNo = 0; itemNo < list.size (); itemNo++)
					result [itemNo] = list.get (itemNo);
			}

		return result;
	}

	/**
	 * @param substring String to search for
	 * @param stringToSearch String to search in
	 * @return The number of times substring appears in stringToSearch
	 */
	public final static int countStringRepetitions (final String substring, final String stringToSearch)
	{
		final int substringLength = substring.length ();
		int count = 0;

		for (int startPosition = 0; startPosition <= stringToSearch.length () - substringLength; startPosition++)
		{
			if (stringToSearch.substring (startPosition, startPosition + substringLength).equals (substring))
				count++;
		}

		return count;
	}

	/**
	 * Builds a string containing a number of repetitions of another string, optionally with another string inserted in between
	 * Wrote this for building SQL statements, e.g. final String sql = "SELECT ACCOUNTRY FROM R1ACREP WHERE ACCTRYCODE IN (" + repeatString (countriesList.size (), "?", ",") + ")";
	 *
	 * @param numberOccurrences Number of times to repeat the string
	 * @param stringToRepeat String to repeat
	 * @param delimiter Optional string to insert in between each repetition, this can be left null
	 * @return Combined string
	 */
	public final static String repeatString (final int numberOccurrences, final String stringToRepeat, final String delimiter)
	{
		String result = "";
		for (int n = 0; n < numberOccurrences; n++)
		{
			if ((n > 0) && (delimiter != null))
				result = result + delimiter;

			result = result + stringToRepeat;
		}

		return result;
	}

	/**
	 * Safe string comparison that works even with either string is null
	 * @param first First string to compare
	 * @param second Second string to compare
	 * @return True if both strings are null, or both are non-null and identical
	 */
	public final static boolean safeStringCompare (final String first, final String second)
	{
		if ((first == null) && (second == null))
			return true;

		else if ((first == null) || (second == null))
			return false;

		else
			return first.equals (second);
	}

	/**
	 * Searches an array of strings for the first string with a particular value
	 * @param list Array of strings to search
	 * @param value Value to search for
	 * @return Index into the array of strings of the first string that matches the requested value, or -1 if none match
	 */
	public final static int indexOf (final String [] list, final String value)
	{
		int result = -1;
		int index = 0;

		while ((result < 0) && (index < list.length))
		{
			if (list [index].equals (value))
				result = index;
			else
				index++;
		}

		return result;
	}

	/**
	 * Searches a list of strings for the first string with a particular prefix
	 * @param list List of strings to search
	 * @param prefix Prefix to search for
	 * @return Index into the list of strings of the first string that starts with the requested prefix, or -1 if none match
	 */
	public final static int indexOfStartsWith (final List<String> list, final String prefix)
	{
		int result = -1;
		int index = 0;

		while ((result < 0) && (index < list.size ()))
		{
			if (list.get (index).startsWith (prefix))
				result = index;
			else
				index++;
		}

		return result;
	}

	/**
	 * Reads a complete text file into a string, preserving CRLFs
	 * @param filename Filename to read in
	 * @return String read from file
	 * @throws IOException If there is a problem reading in the file
	 */
	public final static String readTextFileIntoString (final File filename)
		throws IOException
	{
		String result = "";
		try (final BufferedReader reader = new BufferedReader (new FileReader (filename)))
		{
			boolean finished = false;

			while (!finished)
			{
				final String thisLine = reader.readLine ();
				if (thisLine == null)
					finished = true;
				else
					result = result + thisLine + System.lineSeparator ();
			}

			reader.close ();
		}
		return result;
	}

	/**
	 * Creates a file containing the specified text
	 * @param filename Filename to create
	 * @param text Text to put in the file
	 * @throws IOException If there is a problem writing to the file
	 */
	public final static void writeStringIntoTextFile (final File filename, final String text)
		throws IOException
	{
		try (final FileWriter writer = new FileWriter (filename))
		{
			writer.write (text);
			writer.close ();
		}
	}
}