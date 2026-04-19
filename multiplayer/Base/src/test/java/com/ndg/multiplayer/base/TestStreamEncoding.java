package com.ndg.multiplayer.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jakarta.xml.bind.JAXBContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.baseunittest.MessageA;

/**
 * Tests that MultipleMarshallStream and MultipleUnmarshallStream agree on character encoding
 */
@ExtendWith(MockitoExtension.class)
public final class TestStreamEncoding
{
	/**
	 * @param text Text to test with
	 * @param compress Whether to compress and decompress data
	 * @throws Exception If there is a problem
	 */
	private final void testText (final String text, final boolean compress) throws Exception
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class, MessageA.class);
		
		// Encode a message containing the text as a byte stream
		final MessageA msgIn = new MessageA ();
		msgIn.setFieldA (text);
		
		final ByteArrayOutputStream outStream = new ByteArrayOutputStream ();
		
		final MultipleMarshallStream marshaller = new MultipleMarshallStream ();
		marshaller.setJaxbContext (jaxbContext);
		marshaller.setStream (outStream);
		marshaller.setCompress (compress);
		marshaller.init ();
		marshaller.writeObject (msgIn);
		marshaller.close ();
		
		final byte [] bytes = outStream.toByteArray ();
		
		System.out.println (new String (bytes));
		
		// Convert byte stream back to a message
		final ByteArrayInputStream inStream = new ByteArrayInputStream (bytes);
		
		final MultipleUnmarshallStream unmarshaller = new MultipleUnmarshallStream ();
		unmarshaller.setJaxbContext (jaxbContext);
		unmarshaller.setStream (inStream);
		unmarshaller.setDecompress (compress);
		unmarshaller.init ();
		
		final Object obj = unmarshaller.readObject ();
		assertEquals (msgIn.getClass ().getName (), obj.getClass ().getName ());
		
		final MessageA msgOut = (MessageA) obj;
		assertEquals (text, msgOut.getFieldA ());
		
		// Now should get end of stream
		assertNull (unmarshaller.readObject ());
	}

	/**
	 * Tests with a simple letter which requires no special encoding
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSimple () throws Exception
	{
		testText ("b", true);
	}
	
	/**
	 * Tests with a ½ symbol which was what originally caused problems here
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testHalf () throws Exception
	{
		testText ("½", true);
	}

	/**
	 * Tests randomly picked unicode symbol that requires a single char to represent in Java
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSingle () throws Exception
	{
		testText ("\u26DF", true);		// https://www.fileformat.info/info/unicode/char/26df/index.htm
	}

	/**
	 * Tests randomly picked unicode symbol that requires two chars to represent in Java
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testDouble () throws Exception
	{
		testText ("\uD834\uDD60", true);		// https://www.fileformat.info/info/unicode/char/1d160/index.htm
	}
}