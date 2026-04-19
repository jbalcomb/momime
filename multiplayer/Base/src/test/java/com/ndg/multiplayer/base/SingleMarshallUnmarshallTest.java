package com.ndg.multiplayer.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Tests using JAXB to marshall and unmarshall single objects into a byte stream
 */
@ExtendWith(MockitoExtension.class)
public final class SingleMarshallUnmarshallTest
{
	/**
	 * Tests creating a single message A object, converting to XML and back again
	 * Then tests creates a single message B object, converting to XML and back again
	 * @throws JAXBException If conversion fails
	 */
	@Test
	public final void singleMarshallUnmarshallTest ()
		throws JAXBException
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class, MessageA.class, MessageB.class);

		// Message A
		final MessageA msgA = new MessageA ();
		msgA.setFieldA ("blah");

		final byte [] bytesA = convertToXml (jaxbContext, msgA);
		final Object objA = convertFromXml (jaxbContext, bytesA);

		assertEquals (msgA.getClass ().getName (), objA.getClass ().getName (), "objA not of expected type");
		assertEquals (msgA.getFieldA (), ((MessageA) objA).getFieldA (), "objA doesn't have expected value");

		// Message B
		final MessageB msgB = new MessageB ();
		msgB.setFieldB (23);

		final byte [] bytesB = convertToXml (jaxbContext, msgB);
		final Object objB = convertFromXml (jaxbContext, bytesB);

		assertEquals (msgB.getClass ().getName (), objB.getClass ().getName (), "objB not of expected type");
		assertEquals (msgB.getFieldB (), ((MessageB) objB).getFieldB (), "objB doesn't have expected value");
	}

	/**
	 * @param jaxbContext JAXB Context
	 * @param msg Message object to convert into XML
	 * @return XML byte stream
	 * @throws JAXBException If conversion fails
	 */
	private final byte [] convertToXml (final JAXBContext jaxbContext, final ClientToServerMessage msg)
		throws JAXBException
	{
		final ByteArrayOutputStream stream = new ByteArrayOutputStream ();
		jaxbContext.createMarshaller ().marshal (msg, stream);
		return stream.toByteArray ();
	}

	/**
	 * @param jaxbContext JAXB Context
	 * @param xml XML byte stream
	 * @return Message object converted from byte stream
	 * @throws JAXBException If conversion fails
	 */
	private final Object convertFromXml (final JAXBContext jaxbContext, final byte [] xml)
		throws JAXBException
	{
		final ByteArrayInputStream stream = new ByteArrayInputStream (xml);
		return jaxbContext.createUnmarshaller ().unmarshal (stream);
	}

	/**
	 * This proves that because we are generating ClientToServerMessage in isolation from descendant classes like MessageA,
	 * and it therefore gets no @XmlSeeAlso tags, that if we don't explicitly list out all descendant types when we create the JAXBContext
	 * then unmarshalling fails
	 * @throws JAXBException Expected outcome
	 */
	@Test
	public final void proveAllMessageTypesRequiredToContext () throws JAXBException
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class);

		// Message A
		final MessageA msgA = new MessageA ();
		msgA.setFieldA ("blah");

		assertThrows (JAXBException.class, () ->
		{
			final byte [] bytesA = convertToXml (jaxbContext, msgA);
			convertFromXml (jaxbContext, bytesA);
		});
	}
}