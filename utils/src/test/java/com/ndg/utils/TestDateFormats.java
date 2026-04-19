package com.ndg.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the date/time formats defined in DateFormats behave as expected.
 * This started being a problem at some point between JDK 11.0.12 and 11.0.16, see
 * https://bugs.openjdk.org/browse/JDK-8211321?focusedCommentId=14213153&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14213153
 */
@ExtendWith(MockitoExtension.class)
public final class TestDateFormats
{
	/**
	 * Tests formatting a date in XML date format 
	 */
	@Test
	public final void testXmlDateFormat ()
	{
		final LocalDate date = LocalDate.of (2022, 7, 24);
		final String text = DateFormats.XML_DATE_FORMAT.format (date);
		assertEquals ("2022-07-24", text);
	}

	/**
	 * Tests parsing a date in XML date format
	 * @throws ParseException If there date cannot be parsedd 
	 */
	@Test
	public final void testXmlDateParse () throws ParseException
	{
		final LocalDate date = LocalDate.parse ("2022-07-24", DateFormats.XML_DATE_FORMAT);
		assertEquals (LocalDate.of (2022, 7, 24), date);
	}

	/**
	 * Tests formatting a date in standard date format 
	 */
	@Test
	public final void testStandardDateFormat ()
	{
		final LocalDate date = LocalDate.of (2022, 7, 24);
		final String text = DateFormats.STANDARD_DATE_FORMAT.format (date);
		assertEquals ("24 Jul 2022", text);
	}

	/**
	 * Tests parsing a date in standard date format
	 * @throws ParseException If there date cannot be parsedd 
	 */
	@Test
	public final void testStandardDateParse () throws ParseException
	{
		final LocalDate date = LocalDate.parse ("24 Jul 2022", DateFormats.STANDARD_DATE_FORMAT);
		assertEquals (LocalDate.of (2022, 7, 24), date);
	}

	/**
	 * Tests formatting a date in no dashes date format 
	 */
	@Test
	public final void testNoDashesDateFormat ()
	{
		final LocalDate date = LocalDate.of (2022, 7, 24);
		final String text = DateFormats.NO_DASHES_DATE_FORMAT.format (date);
		assertEquals ("20220724", text);
	}

	/**
	 * Tests parsing a date in no dashes date format
	 * @throws ParseException If there date cannot be parsedd 
	 */
	@Test
	public final void testNoDashesDateParse () throws ParseException
	{
		final LocalDate date = LocalDate.parse ("20220724", DateFormats.NO_DASHES_DATE_FORMAT);
		assertEquals (LocalDate.of (2022, 7, 24), date);
	}

	/**
	 * Tests formatting a time in standard time format 
	 */
	@Test
	public final void testStandardTimeFormat ()
	{
		final LocalTime time = LocalTime.of (20, 33, 45);
		final String text = DateFormats.STANDARD_TIME_FORMAT.format (time);
		assertEquals ("20:33:45", text);
	}

	/**
	 * Tests formatting a time in no dashes time format 
	 */
	@Test
	public final void testNoDashesTimeFormat ()
	{
		final LocalTime time = LocalTime.of (20, 33, 45);
		final String text = DateFormats.NO_DASHES_TIME_FORMAT.format (time);
		assertEquals ("203345", text);
	}
}