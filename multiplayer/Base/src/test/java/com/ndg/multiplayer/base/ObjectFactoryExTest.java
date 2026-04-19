package com.ndg.multiplayer.base;

import com.ndg.multiplayer.baseunittest.extension.MessageAEx;
import com.ndg.multiplayer.baseunittest.extension.ObjectFactoryEx;

/**
 * Tests overriding the object factory in order to get descendant objects to be created
 */
public final class ObjectFactoryExTest extends MultipleMarshallUnmarshallTest
{
	/**
	 * @return Plug in using our ObjectFactoryEx
	 */
	@Override
	protected final Object [] getObjectFactoryArray ()
	{
		return new Object [] {new ObjectFactoryEx ()};
	}

	/**
	 * @return Prove that we actually get the right type of MessageA created
	 */
	@Override
	protected final String getMessageAExpectedClassName ()
	{
		return MessageAEx.class.getName ();
	}
}
