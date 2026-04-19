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
 * Test script for StreamUtils methods dealing with unsigned 2 byte values
 */
@ExtendWith(MockitoExtension.class)
public class Unsigned2ByteIntTest
{
	/**
	 * Byte array used by tests reading positive numeric values
	 */
	private final static byte [] POSITIVE_ARRAY = {1,2};

	/**
	 * Value of POSITIVE_ARRAY when read in little endian order
	 */
	private final static int POSITIVE_LITTLE_ENDIAN = 0x0201;

	/**
	 * Value of POSITIVE_ARRAY when read in big endian order
	 */
	private final static int POSITIVE_BIG_ENDIAN = 0x0102;

	/**
	 * Byte array used by tests reading negative numeric values
	 */
	private final static byte [] NEGATIVE_ARRAY = {-1,-2};	// It doesn't like 255,254...

	/**
	 * Value of NEGATIVE_ARRAY when read in little endian order
	 */
	private final static int NEGATIVE_LITTLE_ENDIAN = 65279;	// 0xFEFF, but specify as a decimal to ensure its treated as a positive value

	/**
	 * Value of NEGATIVE_ARRAY when read in big endian order
	 */
	private final static int NEGATIVE_BIG_ENDIAN = 65534;	// 0xFFFE, but specify as a decimal to ensure its treated as a positive value

	/**
	 * Tests a read operation
	 * @param inputArray The byte array to use as the input stream
	 * @param byteOrder The byte order to read the array using
	 * @param expectedValue The int value we expect to obtain
	 */
	private void testRead (final byte [] inputArray, final ByteOrder byteOrder, final int expectedValue)
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (inputArray);
		try
		{
			final int value = StreamUtils.readUnsigned2ByteIntFromStream (in, byteOrder, "value");
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
	 * @param inputValue The short value to write to the stream
	 */
	private void testWrite (final byte [] expectedArray, final ByteOrder byteOrder, final int inputValue)
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.writeUnsigned2ByteIntToStream (out, byteOrder, inputValue);
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