package com.ndg.multiplayer.demo.server;

import javax.xml.datatype.DatatypeConfigurationException;

import com.ndg.multiplayer.server.MultiplayerSessionServer;
import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.sessionbase.SessionDescription;

/**
 * Demo showing how to subclass MultiplayerSessionServer with as much handled in spring as possible
 */
public final class DemoSessionServer extends MultiplayerSessionServer
{
	/** Session thread factory */
	private DemoSessionServerThreadFactory sessionThreadFactory;
	
	/**
	 * Creates demo server
	 * @throws DatatypeConfigurationException If there is a problem creating the DatatypeFactory
	 */
	public DemoSessionServer () throws DatatypeConfigurationException
	{
		super ("DemoSessionServer");
	}

	/**
	 * Descendant server classes will want to override this to create a thread that knows how to process useful messages
	 * @param sessionDescription Description of the new session
	 * @return Thread object to handle requests for this session
	 */
	@Override
	public final MultiplayerSessionThread createSessionThread (final SessionDescription sessionDescription)
	{
		// Create a new instance of sessionThread prototype bean
		final DemoSessionServerThread newSessionThread = getSessionThreadFactory ().createThread ();
		newSessionThread.setSessionDescription (sessionDescription);
		
		return newSessionThread;
	}

	/**
	 * @return Session thread factory
	 */
	public final DemoSessionServerThreadFactory getSessionThreadFactory ()
	{
		return sessionThreadFactory;
	}

	/**
	 * @param fac Session thread factory
	 */
	public final void setSessionThreadFactory (final DemoSessionServerThreadFactory fac)
	{
		sessionThreadFactory = fac;
	}
}