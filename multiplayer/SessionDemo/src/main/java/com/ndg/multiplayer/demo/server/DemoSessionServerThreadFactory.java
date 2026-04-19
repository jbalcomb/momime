package com.ndg.multiplayer.demo.server;

/**
 * Factory interface for creating DemoSessionServerThreads
 */
public interface DemoSessionServerThreadFactory
{
	/**
	 * @return Newly created thread
	 */
	public DemoSessionServerThread createThread ();
}
