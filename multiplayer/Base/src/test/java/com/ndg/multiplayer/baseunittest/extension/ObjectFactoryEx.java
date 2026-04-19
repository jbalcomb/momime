package com.ndg.multiplayer.baseunittest.extension;

import jakarta.xml.bind.annotation.XmlRegistry;

import com.ndg.multiplayer.baseunittest.MessageA;
import com.ndg.multiplayer.baseunittest.ObjectFactory;
import com.ndg.multiplayer.baseunittest.Request;
import com.ndg.multiplayer.baseunittest.Response;

/**
 * Demo object factory proving we can extended one of the JAXB generated classes
 */
@XmlRegistry
public final class ObjectFactoryEx extends ObjectFactory
{
	/**
	 * @return Extended message
	 */
	@Override
	public MessageA createMessageA ()
	{
        return new MessageAEx ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final Response createResponse ()
	{
		return new ResponseEx ();
	}

	/**
	 * @return Message extended to include processing interface
	 */
	@Override
	public final Request createRequest ()
	{
		return new RequestEx ();
	}
}
