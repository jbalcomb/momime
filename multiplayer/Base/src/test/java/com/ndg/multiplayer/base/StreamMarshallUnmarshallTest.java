package com.ndg.multiplayer.base;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.baseunittest.MessageA;
import com.ndg.multiplayer.baseunittest.MessageB;

/**
 * Tests using MultipleMarshallStream and MultipleUnmarshallStream on a TCP/IP socket stream
 * Message flow is only one way - client sends packets to server and server recieves them, but sends nothing back
 */
@ExtendWith(MockitoExtension.class)
public final class StreamMarshallUnmarshallTest
{
	/** TCP/IP port the test server should listen on */
	public final static int SERVER_PORT = 9000;

	/** TCP/IP port the test client should connect to (can make it different to go through a proxy so can see the packets being sent) */
	public final static int CLIENT_PORT = SERVER_PORT;

	/** Threads write exceptions into here if they fail */
	private Exception threadException;

	/**
	 * Starts server listening for messages in one thread, then connects to it and sends messages to it in this thread; without compression
	 * @throws InterruptedException If there is a problem with the startMonitor
	 * @throws IOException If there is a problem with the TCP/IP socket
	 * @throws JAXBException If there is a problem marshalling objects to send
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	@Test
	public final void streamMarshallUnmarshallTest_WithoutCompression () throws InterruptedException, IOException, JAXBException, XMLStreamException
	{
		streamMarshallUnmarshallTest (false);
	}
	
	/**
	 * Starts server listening for messages in one thread, then connects to it and sends messages to it in this thread; with compression
	 * @throws InterruptedException If there is a problem with the startMonitor
	 * @throws IOException If there is a problem with the TCP/IP socket
	 * @throws JAXBException If there is a problem marshalling objects to send
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	@Test
	public final void streamMarshallUnmarshallTest_WithCompression () throws InterruptedException, IOException, JAXBException, XMLStreamException
	{
		streamMarshallUnmarshallTest (true);
	}
	
	/**
	 * Starts server listening for messages in one thread, then connects to it and sends messages to it in this thread
	 * @param compress Whether to compress and decompress the data streams
	 * @throws InterruptedException If there is a problem with the startMonitor
	 * @throws IOException If there is a problem with the TCP/IP socket
	 * @throws JAXBException If there is a problem marshalling objects to send
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	private final void streamMarshallUnmarshallTest (final boolean compress) throws InterruptedException, IOException, JAXBException, XMLStreamException
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance (ClientToServerMessage.class, MessageA.class, MessageB.class);

		threadException = null;

		// Server
		System.out.println ("Starting up server...");
		final Object startMonitor = new Object ();
		new DemoServer (startMonitor, jaxbContext, compress).start ();

		synchronized (startMonitor)
		{
			startMonitor.wait ();
		}

		// Client
		System.out.println ("Client requesting connection");
		try (final Socket clientSocket = new Socket ("localhost", CLIENT_PORT))
		{
			System.out.println ("DemoClient sending initial MultipleMarshallStream handshake");
			final MultipleMarshallStream out = new MultipleMarshallStream ();
			out.setJaxbContext (jaxbContext);
			out.setStream (clientSocket.getOutputStream ());
			out.setCompress (compress);
			out.init ();
			
			System.out.println ("DemoClient initial MultipleMarshallStream handshake sent");
			Thread.sleep (3000);

			for (int n = 1; n <= 5; n++)
			{
				System.out.println ("Sending message " + n);
				final MessageA msg = new MessageA ();
				msg.setFieldA ("A" + n);
				out.writeObject (msg);
				Thread.sleep (1000);
			}

			System.out.println ("DemoClient send message, closing MultipleMarshallStream");
			out.close ();
			clientSocket.close ();
		}
		System.out.println ("DemoClient closed MultipleMarshallStream and ending");

		// See if any of the threads had an error
		// Should be a delay/monitor here to actually wait for the other threads to end before we check this
		if (threadException != null)
			fail (threadException.getMessage ());
	}

	/**
	 * Server thread
	 */
	private final class DemoServer extends Thread
	{
		/** Monitor to notify caller when server has started */
		private final Object startMonitor;

		/** Context which knows about messages we expect to receive from client */
		private final JAXBContext jaxbContext;

		/** Whether to decompress the incoming data */
		final boolean decompress;
		
		/**
		 * @param aStartMonitor Monitor to notify caller when server has started
		 * @param aJaxbContext Context which knows about messages we expect to receive from client
		 * @param aDecompress Whether to decompress the incoming data
		 */
		private DemoServer (final Object aStartMonitor, final JAXBContext aJaxbContext, final boolean aDecompress)
		{
			super ("DemoServer");
			startMonitor = aStartMonitor;
			jaxbContext = aJaxbContext;
			decompress = aDecompress;
		}

		/**
		 * Waits listening for client connections
		 */
		@Override
		public final void run ()
		{
			try
			{
				try (final ServerSocket serverSocket = new ServerSocket (SERVER_PORT))
				{
					System.out.println ("DemoServer listening on port " + SERVER_PORT);

					if (startMonitor != null)
						synchronized (startMonitor)
						{
							startMonitor.notifyAll ();
						}

					// Only wait for 1 connection and then let the thread exit, otherwise
					// the test ends and the server continues running, blocking up the port
					// while (true)
					// {
						new DemoServerThread (serverSocket.accept (), jaxbContext, decompress).start ();
						// }

						serverSocket.close ();
				}
			}
			catch (final Exception e)
			{
				e.printStackTrace ();
				threadException = e;
			}
		}
	}

	/**
	 * Thread that runs on server to listen for messages on one client socket
	 */
	private final class DemoServerThread extends Thread
	{
		/** Client socket to listen for connections on */
		final Socket socket;

		/** Context which knows about messages we expect to receive from client */
		final JAXBContext jaxbContext;
		
		/** Whether to decompress the incoming data */
		final boolean decompress;

		/**
		 * @param aSocket Client socket to listen for connections on
		 * @param aJaxbContext Context which knows about messages we expect to receive from client
		 * @param aDecompress Whether to decompress the incoming data
		 */
		private DemoServerThread (final Socket aSocket, final JAXBContext aJaxbContext, final boolean aDecompress)
		{
			super ("DemoServerThread");

			socket = aSocket;
			jaxbContext = aJaxbContext;
			decompress = aDecompress;
		}

		/**
		 * Listens for messages from client
		 */
		@Override
		public final void run ()
		{
			try
			{
				System.out.println ("DemoServer accepted connection from " + socket.getRemoteSocketAddress ());
				final MultipleUnmarshallStream in = new MultipleUnmarshallStream ();
				in.setJaxbContext (jaxbContext);
				in.setStream (socket.getInputStream ());
				in.setDecompress (decompress);
				in.init ();
				
				System.out.println ("DemoServer received initial MultipleUnmarshallStream handshake");

				boolean endOfStream = false;
				while (!endOfStream)
				{
					final Object msg = in.readObject ();
					if (msg == null)
						endOfStream = true;
					else if (!(msg instanceof MessageA))
						throw new IOException ("Got message of unexpected type");
					else
						System.out.println ("DemoServer received message with value " + ((MessageA) msg).getFieldA ());
				}

				System.out.println ("DemoServer received controlled end of stream, ending thread");
				socket.close ();
			}
			catch (final Exception e)
			{
				e.printStackTrace ();
				threadException = e;
			}
		}
	}
}