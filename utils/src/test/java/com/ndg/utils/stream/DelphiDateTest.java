package com.ndg.utils.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.utils.StreamUtils;

/**
 * Test script for StreamUtils class
 * This currently only tests the "read" method - the "write" methods, copyBetweenStreams and some of the String methods have no test cases yet
 * Every method should have 4 test cases - one for each of positive & negative, and little & big endian
 */
@ExtendWith(MockitoExtension.class)
public class DelphiDateTest
{
	/**
	 * Byte array representing a test date/time
	 */
	private final static byte [] DATE_TIME_ARRAY_05OCT1974_145711_789 = {117,42,26,-32,-25,-86,-38,64};		// 224 = -32, 231 = -25, 170 = -86, 218 = -38

	/**
	 * Value represented by the above array (can only set this up in the constructor)
	 */
	private final Date DATE_TIME_VALUE_05OCT1974_145711_789;

	/**
	 * Set up the date to compare against
	 */
	public DelphiDateTest ()
	{
		super ();

		final GregorianCalendar calendar = new GregorianCalendar (1974, 9, 5, 14, 57, 11);
		calendar.set (Calendar.MILLISECOND, 789);
		DATE_TIME_VALUE_05OCT1974_145711_789 = calendar.getTime ();
	}

	/**
	 * Tests the readDelphiDateTimeFromStream method, to prove that it reads Delphi date/time values correctly
	 */
	@Test
	public void testReadDelphiDateTimeFromStream ()
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (DATE_TIME_ARRAY_05OCT1974_145711_789);
		try
		{
			final Date value = StreamUtils.readDelphiDateTimeFromStream (in, "value");
			assertEquals (value, DATE_TIME_VALUE_05OCT1974_145711_789);
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests the readDelphiDateTimeFromStream method, to prove that it reads Delphi date/time values correctly
	 */
	@Test
	public void testWriteDelphiDateTimeTotream ()
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeDelphiDateTimeToStream (out, DATE_TIME_VALUE_05OCT1974_145711_789);
			if (!Arrays.equals (out.toByteArray (), DATE_TIME_ARRAY_05OCT1974_145711_789))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}
}