package com.ndg.multiplayer.basedemo.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates interacting via Swing form
 */
public final class MultiplayerDemoClientKickOff
{
	/**
	 * @param args Command line arguments, ignored
	 */
	@SuppressWarnings ("resource")
	public final static void main (final String [] args)
	{
		System.setProperty ("log4j.configurationFile", "MultiplayerBaseDemoLogging.xml");
		
		// Starts up the client via Spring
		new ClassPathXmlApplicationContext ("/spring/client-beans.xml");
	}
}