package com.ndg.utils.math;

import com.ndg.utils.Holder;

/**
 * Methods for dealing with Roman Numerals
 */
public final class RomanNumerals
{
	/**
	 * @param value Value to convert to roman numerals
	 * @param one Roman numeral letter to use for 1
	 * @param five Roman numeral letter to use for 5
	 * @param ten Roman numeral letter to use for 10
	 * @return Roman numerals representation of v
	 */
	private final static String digitToRoman (final int value, final String one, final String five, final String ten)
	{
		switch (value)
		{
			case 1: return one;
			case 2: return one + one;
			case 3: return one + one + one;
			case 4: return one + five;
			case 5: return five;
			case 6: return five + one;
			case 7: return five + one + one;
			case 8: return five + one + one + one;
			case 9: return one + ten;
			default: return "";
		}
	}

	/**
	 * @param value Value to convert to roman numerals
	 * @return Roman numeral representation of value
	 */
	public final static String intToRoman (final int value)
	{
		// Units
		String result = digitToRoman (value % 10, "I", "V", "X");
		int remainingValue = value / 10;

		// Tens
		result = digitToRoman (remainingValue % 10, "X", "L", "C") + result;
		remainingValue = remainingValue / 10;

		// Hundereds
		result = digitToRoman (remainingValue % 10, "C", "D", "M") + result;
		remainingValue = remainingValue / 10;

		// Thousands
		while (remainingValue > 0)
		{
			result = "M" + result;
			remainingValue--;
		}

		return result;
	}

	/**
	 * @param roman Number in roman numerals - the letters we consume get removed from here
	 * @param one Roman numeral letter to use for 1
	 * @param five Roman numeral letter to use for 5
	 * @param ten Roman numeral letter to use for 10
	 * @return One decimal digit
	 */
	private final static int romanToDigit (final Holder<String> roman, final String one, final String five, final String ten)
	{
		// Find the first occurrence of the 1 or 5 letter in the string, then consume everything from that point on.
		// That enables us to correctly parse things like XIX and realise the first X is part of the tes and the second
		// X is part of the units.
		final int onePos = roman.getValue ().indexOf (one);
		final int fivePos = roman.getValue ().indexOf (five);
		final int pos;
		if ((onePos >= 0) && (fivePos >= 0))
			pos = Math.min (onePos, fivePos);
		else if (onePos >= 0)
			pos = onePos;
		else
			pos = fivePos;
		
		final int value;
		if (pos < 0)
			value = 0;
		else
		{
			final String piece = roman.getValue ().substring (pos);
			roman.setValue (roman.getValue ().substring (0, pos));
			
			// Now just convert "piece"
			if (piece.equals (one)) value = 1;
			else if (piece.equals (one + one)) value = 2;
			else if (piece.equals (one + one + one)) value = 3;
			else if (piece.equals (one + five)) value = 4;
			else if (piece.equals (five)) value = 5;
			else if (piece.equals (five + one)) value = 6;
			else if (piece.equals (five + one + one)) value = 7;
			else if (piece.equals (five + one + one + one)) value = 8;
			else if (piece.equals (one + ten)) value = 9;
			else
				throw new NumberFormatException ("romanToDigit tried to convert \"" + piece + "\"");
		}
		
		return value;
	}

	/**
	 * @param roman Number in roman numerals
	 * @return Converted value
	 */
	public final static int romanToInt (final String roman)
	{
		final Holder<String> holder = new Holder<String> (roman);
		
		// Units
		int result = romanToDigit (holder, "I", "V", "X");

		// Tens
		result = result + (10 * romanToDigit (holder, "X", "L", "C"));

		// Hundereds
		result = result + (100 * romanToDigit (holder, "C", "D", "M"));

		// Thousands - everything left had better be all Ms
		for (int n = 0; n < holder.getValue ().length (); n++)
			if (!holder.getValue ().substring (n, n+1).equals ("M"))
				throw new NumberFormatException ("romanToInt tried to convert \"" + roman + "\"");
		
		result = result + (1000 * holder.getValue ().length ());

		return result;
	}
	
	/**
	 * Prevent instantiation
	 */
	private RomanNumerals ()
	{
	}
}