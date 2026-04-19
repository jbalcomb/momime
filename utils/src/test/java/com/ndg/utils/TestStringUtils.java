package com.ndg.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the StringUtils class
 */
@ExtendWith(MockitoExtension.class)
public class TestStringUtils
{
	/**
	 * Tests the countStringRepetitions method
	 */
	@Test
	public final void testCountStringRepetitions ()
	{
		assertEquals (0, StringUtils.countStringRepetitions ("C", "abcde"), "Zero repetitions");
		assertEquals (1, StringUtils.countStringRepetitions ("c", "abcde"), "One in the middle");
		assertEquals (2, StringUtils.countStringRepetitions ("c", "abcgcde"), "Two in the middle");
		assertEquals (2, StringUtils.countStringRepetitions ("c", "cgcde"), "Two including one at the start");
		assertEquals (2, StringUtils.countStringRepetitions ("c", "abcgc"), "Two including one at the end");
	}

	/**
	 * Tests the repeatString method
	 */
	@Test
	public final void testRepeatString ()
	{
		assertEquals ("", StringUtils.repeatString (0, "abc", null), "Zero repetitions, null");
		assertEquals ("", StringUtils.repeatString (0, "abc", "def"), "Zero repetitions, delimiter");
		assertEquals ("abc", StringUtils.repeatString (1, "abc", null), "One repetition, null");
		assertEquals ("abc", StringUtils.repeatString (1, "abc", "def"), "One repetition, delimiter");
		assertEquals ("abcabc", StringUtils.repeatString (2, "abc", null), "Two repetitions, null");
		assertEquals ("abcdefabc", StringUtils.repeatString (2, "abc", "def"), "Two repetitions, delimiter");
		assertEquals ("?????", StringUtils.repeatString (5, "?", null), "Five repetitions, null");
		assertEquals ("?,?,?,?,?", StringUtils.repeatString (5, "?", ","), "Five repetitions, delimiter");
	}

	/**
	 * Tests the safeStringCompare method
	 */
	@Test
	public final void testSafeStringCompare ()
	{
		assertTrue (StringUtils.safeStringCompare (null, null));
		assertTrue (StringUtils.safeStringCompare ("X", "X"));
		assertFalse (StringUtils.safeStringCompare (null, "X"));
		assertFalse (StringUtils.safeStringCompare ("X", null));
		assertFalse (StringUtils.safeStringCompare ("X", "Y"));
	}
}