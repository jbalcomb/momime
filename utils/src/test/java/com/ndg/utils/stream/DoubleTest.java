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
public class DoubleTest
{
	/**
	 * Byte array representing the value 12.345 (obtained from Delphi)
	 */
	private final static byte [] DOUBLE_ARRAY_12_345 = {113,61,10,-41,-93,-80,40,64};		// 215 = -41, 163 = -93, 176 = -80

	/**
	 * Value of the DOUBLE_ARRAY_12_345 array
	 */
	private final static double DOUBLE_VALUE_12_345 = 12.345;

	/**
	 * Byte array representing the value 111222333.444555666 (obtained from Delphi)
	 */
	private final static byte [] DOUBLE_ARRAY_111222333_444555666 = {-102,57,-57,-11,120,-124,-102,65};		// 154 = -102, 199 = -57, 245 = -11, 132 = -124

	/**
	 * Value of the DOUBLE_ARRAY_111222333_444555666 array
	 */
	private final static double DOUBLE_VALUE_111222333_444555666 = 111222333.444555666;

	/**
	 * Byte array representing the value -0.000789 (obtained from Delphi)
	 */
	private final static byte [] DOUBLE_ARRAY_MINUS_0_000789 = {-110,90,40,-103,-100,-38,73,-65};		// 146 = -110, 153 = -103, 156 = -100, 218 = -38, 191 = -65

	/**
	 * Value of the DOUBLE_ARRAY_MINUS_0_000789 array
	 */
	private final static double DOUBLE_VALUE_MINUS_0_000789 = -0.000789;

	/**
	 * Tests a read operation
	 * @param inputArray The byte array to use as the input stream
	 * @param expectedValue The double value we expect to obtain
	 */
	private void testRead (final byte [] inputArray, final double expectedValue)
	{
		final ByteArrayInputStream in = new ByteArrayInputStream (inputArray);
		try
		{
			final double value = StreamUtils.read8ByteDoubleFromStream (in, "value");
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
	 * @param inputValue The double value to write to the stream
	 */
	private void testWrite (final byte [] expectedArray, final double inputValue)
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream ();
		try
		{
			StreamUtils.write8ByteDoubleToStream (out, inputValue);
			if (!Arrays.equals (out.toByteArray (), expectedArray))
				fail ("Arrays not equal");
		}
		catch (final IOException e)
		{
			fail ("IOException: " + e);
		}
	}


	/**
	 * Tests the read8ByteDoubleFromStream method, to prove that it reads doubles in the same format as Delphi
	 */
	@Test
	public void testDouble1 ()
	{
		testRead (DOUBLE_ARRAY_12_345, DOUBLE_VALUE_12_345);
		testWrite (DOUBLE_ARRAY_12_345, DOUBLE_VALUE_12_345);
	}

	/**
	 * Tests the read8ByteDoubleFromStream method, to prove that it reads doubles in the same format as Delphi
	 */
	@Test
	public void testDouble2 ()
	{
		testRead (DOUBLE_ARRAY_111222333_444555666, DOUBLE_VALUE_111222333_444555666);
		testWrite (DOUBLE_ARRAY_111222333_444555666, DOUBLE_VALUE_111222333_444555666);
	}

	/**
	 * Tests the read8ByteDoubleFromStream method, to prove that it reads doubles in the same format as Delphi
	 */
	@Test
	public void testDouble3 ()
	{
		testRead (DOUBLE_ARRAY_MINUS_0_000789, DOUBLE_VALUE_MINUS_0_000789);
		testWrite (DOUBLE_ARRAY_MINUS_0_000789, DOUBLE_VALUE_MINUS_0_000789);
	}
}