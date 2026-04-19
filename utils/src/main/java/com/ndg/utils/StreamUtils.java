package com.ndg.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Static helper methods for dealing with streams
 * "false" for the msbFirst parameters is the same as ByteOrder.LITTLE_ENDIAN; "true" is the same as ByteOrder.BIG_ENDIAN
 * 
 * NB. Now this isn't being used for network comms, tried to remove it out of the MoM IME tree on SourceForge, but found it is
 * still used by too many relevant parts (e.g. reading/writing .ndgarc files, reading of .lbx files by the import buttons in the editor) so have reinstated it
 */
public final class StreamUtils
{
	/**
	 * The read (byte b []) and read (byte b [], int off, int len) of InputStreams (especially SocketInputStreams) only block for one byte - they
	 * then return whatever bytes are immediately available and exit, possibly not supplying the full number of bytes that were requested
	 *
	 * This method calls read repeatedly, blocking until the requested number of bytes is available.  Delphi equivalent is NdgReadFromSocket.
	 *
	 * So there will be one of the following outcomes:
	 * The buffer is totally filled, returns buffer.length
	 * The buffer is partially filled and the end of the stream if reached, returns how many bytes were actually read
	 * We don't get a single byte before the end of the stream was reached, so 0 is returned
	 * An exception if the stream could not be read for some reason other than the end of the stream being reached
	 *
	 * @param stream InputStream to read byte array from
	 * @param buffer Byte array to fill
	 * @return Number of bytes actually read, between [0,buffer.size]
	 * @throws IOException If the stream could not be read for some reason other than the end of the stream being reached
	 */
	private final static int blockingReadByteArray (final InputStream stream, final byte [] buffer)
		throws IOException
	{
		int bytesRead = 0;
		boolean endOfStream = false;

		while ((bytesRead < buffer.length) && (!endOfStream))
		{
			final int bytesReadThisTime = stream.read (buffer, bytesRead, buffer.length - bytesRead);
			if (bytesReadThisTime < 0)
				endOfStream = true;
			else
				bytesRead = bytesRead + bytesReadThisTime;
		}

		return bytesRead;
	}

	// 1 BYTE VALUES (SHORTS)

	/**
	 * Reads an unsigned single byte value from a stream
	 * However there are no unsigned types in Java, so to store up the maximum possible value we have to return a short
	 *
	 * @param stream InputStream to read int from
	 * @param dataItemName Name of int data item, used in exception message
	 * @return Value read from the stream
	 * @throws IOException If error reading stream
	 */
	public static short readUnsigned1ByteShortFromStream (final InputStream stream, final String dataItemName)
		throws IOException
	{
		final short value = (short) stream.read ();
		if (value < 0)
			throw new EndOfStreamException ("Ran out of bytes when reading 1 byte unsigned value " + dataItemName);

		return value;
	}

	/**
	 * Writes a byte out to a stream, this is pretty nonsense since there's already a method to do it for us, however its declared for consistency with all the other methods
	 * NB. only writing out 1 byte, but this assumes the data is unsigned, and there are no unsigned types in Java so we need to input a 2 byte data type
	 * However the stream.write value takes a 4 byte value... so to avoid routines with an int casting it to a short so that we can cast it back to an int, input is simply an int
	 *
	 * @param stream OutputStream to write int to
	 * @param value Short value to output
	 * @throws IOException If error writing to stream
	 */
	public final static void writeUnsigned1ByteShortToStream (final OutputStream stream, final int value)
		throws IOException
	{
		stream.write (value);
	}

	// 2 BYTE VALUES (SHORTS)

	/**
	 * Reads an unsigned 2 byte "short" value from a stream
	 * However there are no unsigned types in Java, so to store up the maximum possible value we have to return an int
	 *
	 * @param stream InputStream to read int from
	 * @param byteOrder The byte order to treat the bytes as
	 * @param dataItemName Name of int data item, used in exception message
	 * @return Value read from the stream
	 * @throws IOException If error reading stream
	 */
	public static int readUnsigned2ByteIntFromStream (final InputStream stream, final ByteOrder byteOrder, final String dataItemName)
		throws IOException
	{
		final int byte1 = stream.read ();
		final int byte2 = stream.read ();
		if ((byte1 < 0) || (byte2 < 0))
			throw new EndOfStreamException ("Ran out of bytes when reading 2 byte unsigned value " + dataItemName);

		int combined;
		if (byteOrder == ByteOrder.BIG_ENDIAN)
			combined = (byte1 << 8) | byte2;
		else
			combined = (byte2 << 8) | byte1;

		return combined;
	}

	/**
	 * Convenience method for splitting an int into two bytes and outputting
	 * NB. 2 bytes is really a short, but this assumes the data is unsigned, and there are no unsigned types in Java
	 *
	 * @param stream OutputStream to write int to
	 * @param byteOrder The byte order to write the bytes as
	 * @param value Int value to output
	 * @throws IOException If error writing to stream
	 */
	public final static void writeUnsigned2ByteIntToStream (final OutputStream stream, final ByteOrder byteOrder, final int value)
		throws IOException
	{
		if (byteOrder == ByteOrder.BIG_ENDIAN)
		{
			stream.write (value >> 8);
			stream.write (value & 0xFF);
		}
		else
		{
			stream.write (value & 0xFF);
			stream.write (value >> 8);
		}
	}

	/**
	 * Reads a signed 2 byte short value from a stream.
	 * @param in the stream to read the short from
	 * @param byteOrder The byte order to treat the bytes as
	 * @param dataItemName Name of long data item, used in exception message
	 * @return the short value read from the current position in the stream.
	 * @throws IOException if there is an error reading from the stream.
	 */
	public static short readSigned2ByteShortFromStream (final InputStream in, final ByteOrder byteOrder, final String dataItemName)
		throws IOException
	{
		final byte [] bytes  = new byte [2];
		if (blockingReadByteArray (in, bytes) != bytes.length)
			throw new EndOfStreamException ("Ran out of bytes when reading 2 byte signed value " + dataItemName);

		return ByteBuffer.wrap (bytes).order (byteOrder).getShort ();
	}

	/**
	 * Writes a signed 2 byte short value out to a stream
	 * @param out The stream to write the short to
	 * @param byteOrder The byte order to write the bytes with
	 * @param value The short value to write to the stream
	 * @throws IOException If there is a problem writing to the stream
	 */
	public final static void writeSigned2ByteShortToStream (final OutputStream out, final ByteOrder byteOrder, final short value)
		throws IOException
	{
		final ByteBuffer buffer = ByteBuffer.allocate (2);
		buffer.order (byteOrder).putShort (value);
		out.write (buffer.array ());
	}

	// 4 BYTE VALUES (INTS)

	/**
	 * Reads an unsigned 4 byte "int" value from a stream
	 * However there are no unsigned types in Java, so to store up the maximum possible value we have to return a long
	 *
	 * @param stream InputStream to read long from
	 * @param byteOrder The byte order to treat the bytes as
	 * @param dataItemName Name of long data item, used in exception message
	 * @return Value read from the stream
	 * @throws IOException If error reading stream
	 */
	public static long readUnsigned4ByteLongFromStream (final InputStream stream, final ByteOrder byteOrder, final String dataItemName)
		throws IOException
	{
		final long byte1 = stream.read ();
		final long byte2 = stream.read ();
		final long byte3 = stream.read ();
		final long byte4 = stream.read ();
		if ((byte1 < 0) || (byte2 < 0) || (byte3 < 0) || (byte4 < 0))
			throw new EndOfStreamException ("Ran out of bytes when reading 4 byte unsigned value " + dataItemName);

		long combined;
		if (byteOrder == ByteOrder.BIG_ENDIAN)
			combined = (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
		else
			combined = (byte4 << 24) | (byte3 << 16) | (byte2 << 8) | byte1;

		return combined;
	}

	/**
	 * Convenience method for splitting a long into four bytes and outputting
	 * NB. 4 bytes is really a int, but this assumes the data is unsigned, and there are no unsigned types in Java
	 *
	 * @param stream OutputStream to write long to
	 * @param byteOrder The byte order to write the bytes as
	 * @param value Long value to output
	 * @throws IOException If error writing to stream
	 */
	public final static void writeUnsigned4ByteLongToStream (final OutputStream stream, final ByteOrder byteOrder, final long value)
		throws IOException
	{
		if (byteOrder == ByteOrder.BIG_ENDIAN)
		{
			stream.write ((int) (value >> 24));
			stream.write ((int) ((value >> 16) & 0xFF));
			stream.write ((int) ((value >> 8) & 0xFF));
			stream.write ((int) (value & 0xFF));
		}
		else
		{
			stream.write ((int) (value & 0xFF));
			stream.write ((int) ((value >> 8) & 0xFF));
			stream.write ((int) ((value >> 16) & 0xFF));
			stream.write ((int) (value >> 24));
		}
	}

	/**
	 * Reads a signed 4 byte int value from a stream
	 * @param in the stream to read the int from
	 * @param byteOrder The byte order to treat the bytes as
	 * @param dataItemName Name of int  data item, used in exception message
	 * @return the int value read from the current position in the stream
	 * @throws IOException if there is an error reading from the stream
	 */
	public static int readSigned4ByteIntFromStream (final InputStream in, final ByteOrder byteOrder, final String dataItemName)
		throws IOException
	{
		final byte [] bytes  = new byte [4];
		if (blockingReadByteArray (in, bytes) != bytes.length)
			throw new EndOfStreamException ("Ran out of bytes when reading 4 byte signed value " + dataItemName);

		return ByteBuffer.wrap (bytes).order (byteOrder).getInt ();
	}

	/**
	 * Writes a signed 4 byte int value out to a stream
	 * @param out The stream to write the int to
	 * @param byteOrder The byte order to write the bytes with
	 * @param value The int value to write to the stream
	 * @return Number of bytes written
	 * @throws IOException If there is a problem writing to the stream
	 */
	public final static long writeSigned4ByteIntToStream (final OutputStream out, final ByteOrder byteOrder, final int value)
		throws IOException
	{
		final ByteBuffer buffer = ByteBuffer.allocate (4);
		buffer.order (byteOrder).putInt (value);
		out.write (buffer.array ());

		return 4;
	}

	// 8 BYTE VALUES (LONGS)

	/**
	 * Reads a signed 8 byte long value from a stream
	 * @param in InputStream to read long from
	 * @param byteOrder The byte order to treat the bytes as
	 * @param dataItemName Name of long data item, used in exception message
	 * @return the long value read from the current position in the stream.
	 * @throws IOException if there is an error reading from the stream
	 */
	public static long readSigned8ByteLongFromStream (final InputStream in, final ByteOrder byteOrder, final String dataItemName)
		throws IOException
	{
		final byte [] bytes  = new byte [8];
		if (blockingReadByteArray (in, bytes) != bytes.length)
			throw new EndOfStreamException ("Ran out of bytes when reading 8 byte signed value " + dataItemName);

		return ByteBuffer.wrap (bytes).order (byteOrder).getLong ();
	}

	/**
	 * Writes a signed 8 byte long value out to a stream
	 * @param out The stream to write the long to
	 * @param byteOrder The byte order to write the bytes with
	 * @param value The long value to write to the stream
	 * @throws IOException If there is a problem writing to the stream
	 */
	public final static void writeSigned8ByteLongToStream (final OutputStream out, final ByteOrder byteOrder, final long value)
		throws IOException
	{
		final ByteBuffer buffer = ByteBuffer.allocate (8);
		buffer.order (byteOrder).putLong (value);
		out.write (buffer.array ());
	}

	// 8 BYTE FLOATING POINT (DOUBLES)

	/**
	 * Reads an 8 byte double value from a stream
	 * @param in InputStream to read double from
	 * @param dataItemName Name of double data item, used in exception message
	 * @return the double value read from the current position in the stream.
	 * @throws IOException if there is an error reading from the stream
	 */
	public static double read8ByteDoubleFromStream (final InputStream in, final String dataItemName)
		throws IOException
	{
		final long value = readSigned8ByteLongFromStream (in, ByteOrder.LITTLE_ENDIAN, dataItemName);
		return Double.longBitsToDouble (value);
	}

	/**
	 * Writes an 8 byte double value out to a stream
	 * @param out The stream to write the double to
	 * @param value The double value to write to the stream
	 * @throws IOException If there is a problem writing to the stream
	 */
	public final static void write8ByteDoubleToStream (final OutputStream out, final double value)
		throws IOException
	{
		final long longValue = Double.doubleToLongBits (value);
		writeSigned8ByteLongToStream (out, ByteOrder.LITTLE_ENDIAN, longValue);
	}

	/**
	 * Reads a Delphi TDateTime value, stored as an 8 byte double of the number of days (and fractions of days) since the start of 30 Dec 1899
	 * @param in InputStream to read date from
	 * @param dataItemName Name of date data item, used in exception message
	 * @return the date read from the current position in the stream.
	 * @throws IOException if there is an error reading from the stream
	 */
	public static Date readDelphiDateTimeFromStream (final InputStream in, final String dataItemName)
		throws IOException
	{
		// Tried this by converting to a number of milliseconds, and passing this into new Date (), but this
		// fouls up because of time zones (because new Date () is explicitly a number of seconds since
		// 1 Jan 1970 midnight *GMT*)

		// So instead build up the year, month, day, etc. values and use a Calendar object to create the Date

		// Number of days and fractions of days since the start of 30 Dec 1899
		double value = read8ByteDoubleFromStream (in, dataItemName);

		// Deal with date part
		final int days = (int) value;	// This rounds down, as we need it to
		value = value - days;

		// Deal with the time part
		value = value * 24;
		final int hours = (int) value;
		value = value - hours;

		value = value * 60;
		final int mins = (int) value;
		value = value - mins;

		value = value * 60;
		final int secs = (int) value;
		value = value - secs;

		final long millis = Math.round (value * 1000);

		// Build the calendar object
		final GregorianCalendar calendar = new GregorianCalendar (1900, 0, days - 1, hours, mins, secs);
		calendar.set (Calendar.MILLISECOND, (int) millis);

		return calendar.getTime ();
	}

	/**
	 * Writes a Delphi TDateTime value, stored as an 8 byte double of the number of days (and fractions of days) since the start of 30 Dec 1899
	 * @param out The stream to write the date/time to
	 * @param date The date/time to write to the stream
	 * @throws IOException if there is an error writing to the stream
	 */
	public final static void writeDelphiDateTimeToStream (final OutputStream out, final Date date)
		throws IOException
	{
		// Use a calendar object to convert back into components
		final GregorianCalendar calendar = new GregorianCalendar ();
		calendar.setTime (date);

		// Work out the numebr of days since 30 Dec 1899
		int days = calendar.get (Calendar.DAY_OF_YEAR) + 1;
		int year = calendar.get (Calendar.YEAR) - 1;
		while (year >= 1900)
		{
			days = days + 365;
			if (calendar.isLeapYear (year))
				days++;

			year--;
		}

		// Work out the number of millseconds elapsed this day
		final long millis = calendar.get (Calendar.MILLISECOND) +
			(1000 * calendar.get (Calendar.SECOND)) +
			(1000 * 60 * calendar.get (Calendar.MINUTE)) +
			(1000 * 60 * 60 * calendar.get (Calendar.HOUR_OF_DAY));

		// Now can work out the final value and output it
		// 24.0 = force division away from being an integer division
		final double value = days + (millis / (24.0 * 60 * 60 * 1000));
		write8ByteDoubleToStream (out, value);
	}

	// BOOLEANS

	/**
	 * Reads a boolean value from a stream
	 * This uses a whole byte to store the value, with 0 = false, 1 = true, which is compatible with Delphi record structures	 *
	 * @param in InputStream to read the boolean from
	 * @param dataItemName Name of boolean data item, used in exception message
	 * @return Value read from the stream
	 * @throws IOException If error reading stream
	 */
	public static boolean readBooleanFromStream (final InputStream in, final String dataItemName)
		throws IOException
	{
		final int b = in.read ();
		if (b < 0)
			throw new EndOfStreamException ("Ran out of bytes when reading boolean value " + dataItemName);

		return (b == 1);
	}

	/**
	 * Wrtes a boolean value to a stream
	 * This uses a whole byte to store the value, with 0 = false, 1 = true, which is compatible with Delphi record structures	 *
	 * @param out OutputStream to write the boolean to
	 * @param value Boolean value to write to the stream
	 * @throws IOException If error writing to the stream
	 */
	public final static void writeBooleanToStream (final OutputStream out, final boolean value)
		throws IOException
	{
		int b = 0;
		if (value)
			b = 1;

		out.write (b);
	}

	// STRINGS

	/**
	 * Reads a fixed length string from a stream
	 *
	 * @param stream InputStream to read the string from
	 * @param length The length of the string to read (technically, the number of bytes it occupies on the stream, which may not be the same as its length for DBCS)
	 * @param dataItemName Name of string data item, used in exception message
	 * @return String read from the input stream
	 * @throws IOException If error reading stream
	 */
	public static String readFixedLengthStringFromStream (final InputStream stream, final int length, final String dataItemName)
		throws IOException
	{
		final String result;

		if (length == 0)
			result = null;
		else
		{
			final byte [] stringBytes = new byte [length];
			final int bytesRead = blockingReadByteArray (stream, stringBytes);
			if (bytesRead != length)
				throw new EndOfStreamException ("Ran out of bytes when reading String " + dataItemName);

			result = new String (stringBytes);
		}

		return result;
	}

	/**
	 * Reads Delphi-style strings from the stream, consisting of 4 bytes of length, and then length bytes of text
	 *
	 * @param stream InputStream to read the string from
	 * @param dataItemName Name of string data item, used in exception message
	 * @return String read from the input stream
	 * @throws IOException If error reading stream
	 */
	public static String readLengthAndStringFromStream (final InputStream stream, final String dataItemName)
		throws IOException
	{
		final int length = readSigned4ByteIntFromStream (stream, ByteOrder.LITTLE_ENDIAN, "Length of " + dataItemName);

		return readFixedLengthStringFromStream (stream, length, dataItemName);
	}

	/**
	 * Writes Delphi-style strings to the stream, consisting of 4 bytes of length, and then length bytes of text (which may yield less than length characters)
	 *
	 * @param stream The stream to write the string to
	 * @param value String to output
	 * @return Number of bytes written
	 * @throws IOException If error writing to the stream
	 */
	public final static long writeLengthAndStringToStream (final OutputStream stream, final String value)
		throws IOException
	{
		if (value == null)
		{
			writeSigned4ByteIntToStream (stream, ByteOrder.LITTLE_ENDIAN, 0);
			return 4;
		}
		else
		{
			if (value.equals (""))
				throw new IllegalArgumentException ("writeLengthAndStringToStream: Cannot write out empty string, use null instead");

			final byte [] data = value.getBytes ();

			writeSigned4ByteIntToStream (stream, ByteOrder.LITTLE_ENDIAN, data.length);
			stream.write (data);

			return 4 + data.length;
		}
	}

	/**
	 * Reads a null-terminated string that is always padded to a fixed length from a stream
	 *
	 * @param stream InputStream to read the string from
	 * @param length The length of the string to read
	 * @param dataItemName Name of string data item, used in exception message
	 * @return String read from the input stream
	 * @throws IOException If error reading stream
	 */
	public static String readNullTerminatedFixedLengthStringFromStream (final InputStream stream, final int length, final String dataItemName)
		throws IOException
	{
		// First just read the string
		String nullTerminatedString = readFixedLengthStringFromStream (stream, length, dataItemName);

		// The see it it contains a null character
		if (nullTerminatedString != null)
		{
			final int nullPos = nullTerminatedString.indexOf ('\u0000');
			if (nullPos >= 0)
				nullTerminatedString = nullTerminatedString.substring (0, nullPos);
		}

		return nullTerminatedString;
	}

	/**
	 * Writes a null-terminated string that is always padded to a fixed length to a stream
	 *
	 * @param stream OutputStream to write the string to
	 * @param length The length to pad the string out to
	 * @param value The string to write out
	 * @throws IOException If error writing to stream
	 */
	public final static void writeNullTerminatedFixedLengthStringToStream (final OutputStream stream, final int length, final String value)
		throws IOException
	{
		String nullTerminatedString = value;

		if (nullTerminatedString.length () > length)
			nullTerminatedString = nullTerminatedString.substring (0, length);

		else
			while (nullTerminatedString.length () < length)
				nullTerminatedString = nullTerminatedString + '\u0000';

		stream.write (nullTerminatedString.getBytes ());
	}

	// STRING ARRAYS

	/**
	 * Reads a variable length list of strings from a stream
	 * @param stream InputStream to read the string list from
	 * @param dataItemName Name of string list data item, used in exception message
	 * @return Strings read from the input stream
	 * @throws IOException If error reading stream
	 */
	public final static String [] readArrayOfLengthsAndStringsFromStream (final InputStream stream, final String dataItemName)
		throws IOException
	{
		final int count = readSigned4ByteIntFromStream (stream, ByteOrder.LITTLE_ENDIAN, dataItemName + " length");

		final String [] result = new String [count];
		for (int index = 0; index < count; index++)
			result [index] = readLengthAndStringFromStream (stream, dataItemName + " " + index + "/" + count);

		return result;
	}

	/**
	 * Writes a variable length list of strings to a stream
	 * @param stream The stream to write the strings to
	 * @param values Strings to output
	 * @throws IOException If error writing to the stream
	 */
	public final static void writeArrayOfLengthsAndStringsToStream (final OutputStream stream, final String [] values)
		throws IOException
	{
		writeSigned4ByteIntToStream (stream, ByteOrder.LITTLE_ENDIAN, values.length);

		for (final String thisValue : values)
			writeLengthAndStringToStream (stream, thisValue);
	}

	// BYTE ARRAYS

	/**
	 * Reads a fixed byte array from a stream
	 *
	 * @param stream InputStream to read the byte array from
	 * @param length The length of the byte array to read
	 * @param dataItemName Name of byte array data item, used in exception message
	 * @return Byte array read from the input stream
	 * @throws IOException If error reading stream
	 */
	public static byte [] readByteArrayFromStream (final InputStream stream, final int length, final String dataItemName)
		throws IOException
	{
		final byte [] byteArray = new byte [length];
		final int bytesRead = blockingReadByteArray (stream, byteArray);
		if (bytesRead != length)
			throw new EndOfStreamException ("Ran out of bytes when reading Byte Array " + dataItemName);

		return byteArray;
	}

	/**
	 * Writes a fixed byte array to a stream - although there's already a method to do this, define it here for consistency
	 * @param stream OutputStream to write long to
	 * @param bytes The array of bytes to output
	 * @throws IOException If error writing to stream
	 */
	public final static void writeByteArrayToStream (final OutputStream stream, final byte [] bytes)
		throws IOException
	{
		stream.write (bytes);
	}

	/**
	 * Reads a variable byte array from a stream
	 *
	 * @param stream InputStream to read the byte array from
	 * @param dataItemName Name of byte array data item, used in exception message
	 * @return Byte array read from the input stream
	 * @throws IOException If error reading stream
	 */
	public static byte [] readLengthAndByteArrayFromStream (final InputStream stream, final String dataItemName)
		throws IOException
	{
		final int length = readSigned4ByteIntFromStream (stream, ByteOrder.LITTLE_ENDIAN, "Length of " + dataItemName);

		return readByteArrayFromStream (stream, length, dataItemName);
	}

	/**
	 * Writes a variable length byte array to a stream
	 * @param stream OutputStream to write long to
	 * @param bytes The array of bytes to output
	 * @throws IOException If error writing to stream
	 */
	public final static void writeLengthAndByteArrayToStream (final OutputStream stream, final byte [] bytes)
		throws IOException
	{
		writeSigned4ByteIntToStream (stream, ByteOrder.LITTLE_ENDIAN, bytes.length);
		stream.write (bytes);
	}

	/**
	 * Prevent instatiation of this class
	 */
	private StreamUtils ()
	{
	}
}