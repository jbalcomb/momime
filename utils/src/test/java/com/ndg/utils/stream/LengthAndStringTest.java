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
public class LengthAndStringTest
{
	/** Byte array used by tests reading length prefixed strings */
	private final static byte [] STRING_ARRAY = {5,0,0,0,65,66,67,68,69};

	/** Value represented by the above byte array */
	private final static String STRING_VALUE = "ABCDE";

	/** Byte array for an empty string */
	private final static byte [] NULL_ARRAY = {0,0,0,0};

	/**
	 * Tests the readLengthAndStringFromStream method
	 */
	@Test
	public void testReadLengthAndStringFromStream ()
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (STRING_ARRAY);
		try
		{
			final String value = StreamUtils.readLengthAndStringFromStream (in, "value");
			assertEquals (value, STRING_VALUE);
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests the writeLengthAndStringToStream method
	 */
	@Test
	public void testWriteLengthAndStringToStream ()
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeLengthAndStringToStream (out, STRING_VALUE);
			if (!Arrays.equals (out.toByteArray (), STRING_ARRAY))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests the readLengthAndStringFromStream method with a null
	 */
	@Test
	public void testReadNullLengthAndStringFromStream ()
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (NULL_ARRAY);
		try
		{
			final String value = StreamUtils.readLengthAndStringFromStream (in, "value");
			assertEquals (value, null);
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests the writeLengthAndStringToStream method with a null
	 */
	@Test
	public void testWriteNullLengthAndStringToStream ()
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeLengthAndStringToStream (out, null);
			if (!Arrays.equals (out.toByteArray (), NULL_ARRAY))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Tests the writeLengthAndStringToStream method with an empty string
	 */
	@Test
	public void testWriteEmptyLengthAndStringToStream ()
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeLengthAndStringToStream (out, "");
			fail ("No exception thrown for empty string");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
		catch (final IllegalArgumentException e)
		{
			// Expected result
		}
	}
}