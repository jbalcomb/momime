package com.ndg.utils.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
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
public class Signed8ByteLongTest
{
	/**
	 * Byte array used by tests reading positive numeric values
	 */
	private final static byte [] POSITIVE_ARRAY = {1,2,3,4,5,6,7,8};

	/**
	 * Value of POSITIVE_ARRAY when read in little endian order
	 */
	private final static long POSITIVE_LITTLE_ENDIAN = 0x0807060504030201l;

	/**
	 * Value of POSITIVE_ARRAY when read in big endian order
	 */
	private final static long POSITIVE_BIG_ENDIAN = 0x0102030405060708l;

	/**
	 * Byte array used by tests reading negative numeric values
	 */
	private final static byte [] NEGATIVE_ARRAY = {-1,-2,-3,-4,-5,-6,-7,-8};	// It doesn't like 255,254...

	/**
	 * Value of NEGATIVE_ARRAY when read in little endian order
	 */
	private final static long NEGATIVE_LITTLE_ENDIAN = -506097522914230529l;		// 0xF8F9FAFBFCFDFEFF, but specify as a decimal to ensure its treated as a negative value

	/**
	 * Value of NEGATIVE_ARRAY when read in big endian order
	 */
	private final static long NEGATIVE_BIG_ENDIAN = -283686952306184l;		// 0xFFFEFDFCFBFAF9F8, but specify as a decimal to ensure its treated as a negative value

	/**
	 * Tests a read operation
	 * @param inputArray The byte array to use as the input stream
	 * @param byteOrder The byte order to read the array using
	 * @param expectedValue The long value we expect to obtain
	 */
	private void testRead (final byte [] inputArray, final ByteOrder byteOrder, final long expectedValue)
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (inputArray);
		try
		{
			final long value = StreamUtils.readSigned8ByteLongFromStream (in, byteOrder, "value");
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
	 * @param byteOrder The byte order to read the array using
	 * @param inputValue The int value to write to the stream
	 */
	private void testWrite (final byte [] expectedArray, final ByteOrder byteOrder, final long inputValue)
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeSigned8ByteLongToStream (out, byteOrder, inputValue);
			if (!Arrays.equals (out.toByteArray (), expectedArray))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}

	/**
	 * Test reading and writing a positive value in little endian byte order
	 */
	@Test
	public void testPositiveLittleEndian ()
	{
		testRead (POSITIVE_ARRAY, ByteOrder.LITTLE_ENDIAN, POSITIVE_LITTLE_ENDIAN);
		testWrite (POSITIVE_ARRAY, ByteOrder.LITTLE_ENDIAN, POSITIVE_LITTLE_ENDIAN);
	}

	/**
	 * Test reading and writing a positive value in big endian byte order
	 */
	@Test
	public void testPositiveBigEndian ()
	{
		testRead (POSITIVE_ARRAY, ByteOrder.BIG_ENDIAN, POSITIVE_BIG_ENDIAN);
		testWrite (POSITIVE_ARRAY, ByteOrder.BIG_ENDIAN, POSITIVE_BIG_ENDIAN);
	}

	/**
	 * Test reading and writing a negative value in little endian byte order
	 */
	@Test
	public void testNegativeLittleEndian ()
	{
		testRead (NEGATIVE_ARRAY, ByteOrder.LITTLE_ENDIAN, NEGATIVE_LITTLE_ENDIAN);
		testWrite (NEGATIVE_ARRAY, ByteOrder.LITTLE_ENDIAN, NEGATIVE_LITTLE_ENDIAN);
	}

	/**
	 * Test reading and writing a negative value in big endian byte order
	 */
	@Test
	public void testNegativeBigEndian ()
	{
		testRead (NEGATIVE_ARRAY, ByteOrder.BIG_ENDIAN, NEGATIVE_BIG_ENDIAN);
		testWrite (NEGATIVE_ARRAY, ByteOrder.BIG_ENDIAN, NEGATIVE_BIG_ENDIAN);
	}
}