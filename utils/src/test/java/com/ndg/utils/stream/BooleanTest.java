package com.ndg.utils.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
public class BooleanTest
{
	/**
	 * Byte array representing "false" (obtained from Delphi)
	 */
	private final static byte [] FALSE_ARRAY = {0};

	/**
	 * Byte array representing "true" (obtained from Delphi)
	 */
	private final static byte [] TRUE_ARRAY = {1};

	/**
	 * Tests a read operation
	 * @param inputArray The byte array to use as the input stream
	 * @param expectedValue The boolean value we expect to obtain
	 */
	private void testRead (final byte [] inputArray, final boolean expectedValue)
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (inputArray);
		try
		{
			final boolean value = StreamUtils.readBooleanFromStream (in, "value");
			assertEquals (value, expectedValue);
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests a write operation
	 * @param expectedArray The byte array we expect the stream to contain
	 * @param inputValue The boolean value to write to the stream
	 */
	private void testWrite (final byte [] expectedArray, final boolean inputValue)
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeBooleanToStream (out, inputValue);
			if (!Arrays.equals (out.toByteArray (), expectedArray))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests reading and writing False values
	 */
	@Test
	public void testFalse ()
	{
		testRead (FALSE_ARRAY, false);
		testWrite (FALSE_ARRAY, false);
	}

	/**
	 * Tests reading and writing True values
	 */
	@Test
	public void testTrue ()
	{
		testRead (TRUE_ARRAY, true);
		testWrite (TRUE_ARRAY, true);
	}
}