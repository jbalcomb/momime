package com.ndg.xmleditor.grid.comparators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;

import com.ndg.utils.DateFormats;

/**
 * Implementation of comparator for sorting dates
 * Note because of the way the XML editor treats all columns as strings, the data type declared has to be String rather than Date
 */
public final class DateComparator implements Comparator<String>
{
	/**
	 * Compares two dates
	 * @param string1 The first date to compare
	 * @param string2 The second date to compare
	 * @return Result such that dates are sorted in ascending order
	 */
	@Override
	public int compare (final String string1, final String string2)
	{
		int result;

		// Deal with absolute equality (and both values being null)
		if (string1 == string2)
			result = 0;

		// Deal with one or other value being null - sort these at the top of the list
		else if (string1 == null)
			result = -1;

		else if (string2 == null)
			result = 1;

		// Both must now be valid dates - the values passed are the display format dates, e.g. 11 JUN 2007
		else
		{
			// Ignore ParseExceptions - treat these the same as nulls
			LocalDate date1 = null;
			try
			{
				date1 = LocalDate.parse (string1, DateFormats.STANDARD_DATE_FORMAT);
			}
			catch (final DateTimeParseException e)
			{
			}

			LocalDate date2 = null;
			try
			{
				date2 = LocalDate.parse (string2, DateFormats.STANDARD_DATE_FORMAT);
			}
			catch (final DateTimeParseException e)
			{
			}

			// Since we may have introduced nulls again by catching ParseExceptions, have to check for them again
			if (date1 == date2)
				result = 0;
			else if (date1 == null)
				result = -1;
			else if (date2 == null)
				result = 1;
			else if (date1.isBefore (date2))
				result = -1;
			else if (date1.isAfter (date2))
				result = 1;
			else
				result = 0;
		}

		return result;
	}

}
