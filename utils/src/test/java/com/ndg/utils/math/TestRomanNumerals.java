package com.ndg.utils.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for the RomanNumerals class
 */
@ExtendWith(MockitoExtension.class)
public final class TestRomanNumerals
{
	/**
	 * Tests the intToRoman method
	 */
	@Test
	public final void testIntToRoman ()
	{
		// Got these examples from http://mathforum.org/dr.math/faq/faq.roman.html
		assertEquals ("XXX", RomanNumerals.intToRoman (30));
		assertEquals ("LXI", RomanNumerals.intToRoman (61));
		assertEquals ("IV", RomanNumerals.intToRoman (4));
		assertEquals ("CMLXXXII", RomanNumerals.intToRoman (982));

		assertEquals ("VIII", RomanNumerals.intToRoman (8));
		assertEquals ("XIX", RomanNumerals.intToRoman (19));
		assertEquals ("XIV", RomanNumerals.intToRoman (14));

		assertEquals ("MMDLXIV", RomanNumerals.intToRoman (2564));
		assertEquals ("MCMXCIX", RomanNumerals.intToRoman (1999));
		assertEquals ("MMMCMXCIX", RomanNumerals.intToRoman (3999));
	}

	/**
	 * Tests the romanToInt method method
	 */
	@Test
	public final void testRomanToInt ()
	{
		// Got these examples from http://mathforum.org/dr.math/faq/faq.roman.html
		assertEquals (30, RomanNumerals.romanToInt ("XXX"));
		assertEquals (61, RomanNumerals.romanToInt ("LXI"));
		assertEquals (4, RomanNumerals.romanToInt ("IV"));
		assertEquals (982, RomanNumerals.romanToInt ("CMLXXXII"));

		assertEquals (8, RomanNumerals.romanToInt ("VIII"));
		assertEquals (19, RomanNumerals.romanToInt ("XIX"));
		assertEquals (14, RomanNumerals.romanToInt ("XIV"));

		assertEquals (2564, RomanNumerals.romanToInt ("MMDLXIV"));
		assertEquals (1999, RomanNumerals.romanToInt ("MCMXCIX"));
		assertEquals (3999, RomanNumerals.romanToInt ("MMMCMXCIX"));
	}
}