package com.ndg.multiplayer.basedemo.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates subclassing the server so that we can create subclasses of client connection threads
 */
public final class MultiplayerDemoServerKickOff
{
	/**
	 * @param args Command line arguments, ignored
	 */
	@SuppressWarnings ("resource")
	public final static void main (final String [] args)
	{
		System.setProperty ("log4j.configurationFile", "MultiplayerBaseDemoLogging.xml");
		
		// Starts up the server via Spring
		new ClassPathXmlApplicationContext ("/spring/server-beans.xml");
	}
}