package com.ndg.multiplayer.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.baseunittest.MessageA;
import com.ndg.multiplayer.baseunittest.MessageB;

/**
 * Tests using MultipleMarshallStream and MultipleUnmarshallStream to marshall and unmarshall multiple objects onto the same stream
 */
@ExtendWith(MockitoExtension.class)
public class MultipleMarshallUnmarshallTest
{
	/**
	 * Tests writing a mix of multiple message A and B objects onto a single stream, then reading them back in again, without compression
	 * @throws JAXBException If conversion fails
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void multipleMarshallUnmarshallTest_WithoutCompression () throws Exception
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class, MessageA.class, MessageB.class);

		final byte [] bytesA = convertToXml (jaxbContext, false);
		System.out.println ("Length without compression = " + bytesA.length);
		convertFromXml (jaxbContext, bytesA, false);
	}

	/**
	 * Tests writing a mix of multiple message A and B objects onto a single stream, then reading them back in again, with compression
	 * @throws JAXBException If conversion fails
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void multipleMarshallUnmarshallTest_WithCompression () throws Exception
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class, MessageA.class, MessageB.class);

		final byte [] bytesA = convertToXml (jaxbContext, true);
		System.out.println ("Length with compression = " + bytesA.length);
		convertFromXml (jaxbContext, bytesA, true);
	}

	/**
	 * @param jaxbContext JAXB Context
	 * @param compress Whether to compress data
	 * @return XML byte stream
	 * @throws Exception If there is a problem
	 */
	private final byte [] convertToXml (final JAXBContext jaxbContext, final boolean compress) throws Exception
	{
		final ByteArrayOutputStream stream = new ByteArrayOutputStream ();
		
		final MultipleMarshallStream out = new MultipleMarshallStream ();
		out.setJaxbContext (jaxbContext);
		out.setStream (stream);
		out.setCompress (compress);
		out.init ();

		for (int n = 1; n <= 5; n++)
		{
			final MessageA msgA = new MessageA ();
			msgA.setFieldA ("A" + n);
			out.writeObject (msgA);

			final MessageB msgB = new MessageB ();
			msgB.setFieldB (n);
			out.writeObject (msgB);
		}

		out.close ();
		return stream.toByteArray ();
	}

	/**
	 * @param jaxbContext JAXB Context
	 * @param xml XML byte stream
	 * @param decompress Whether to decompress data
	 * @throws Exception If there is a problem
	 */
	private final void convertFromXml (final JAXBContext jaxbContext, final byte [] xml, final boolean decompress) throws Exception
	{
		final MultipleUnmarshallStream in = new MultipleUnmarshallStream ();
		in.setJaxbContext (jaxbContext);
		in.setObjectFactoryArray (getObjectFactoryArray ());
		in.setStream (new ByteArrayInputStream (xml));
		in.setDecompress (decompress);
		in.init ();

		for (int n = 1; n <= 5; n++)
		{
			final Object objA = in.readObject ();
			assertEquals (getMessageAExpectedClassName (), objA.getClass ().getName (), "objA not of expected type");
			assertEquals ("A" + n, ((MessageA) objA).getFieldA (), "objA doesn't have expected value");

			final Object objB = in.readObject ();
			assertEquals (MessageB.class.getName (), objB.getClass ().getName (), "objB not of expected type");
			assertEquals (n, ((MessageB) objB).getFieldB (), "objB doesn't have expected value");
		}

		final Object objC = in.readObject ();
		assertNull (objC, "Should only be 10 objects on stream");
	}

	/**
	 * @return By default don't use this; but another test overrides this
	 */
	protected Object [] getObjectFactoryArray ()
	{
		return null;
	}

	/**
	 * @return By default expect a MessageA; but another test overrides this
	 */
	protected String getMessageAExpectedClassName ()
	{
		return MessageA.class.getName ();
	}
}