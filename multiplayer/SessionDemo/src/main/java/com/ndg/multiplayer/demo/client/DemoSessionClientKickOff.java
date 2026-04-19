package com.ndg.multiplayer.demo.client;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demo showing how to connect to a session server
 */
public final class DemoSessionClientKickOff
{
	/**
	 * @param args Command line arguments, ignored
	 */
	@SuppressWarnings ("resource")
	public final static void main (final String [] args)
	{
		System.setProperty ("log4j.configurationFile", "MultiplayerSessionDemoLogging.xml");

		try
		{
			new ClassPathXmlApplicationContext ("/multiplayer.demo.spring/client-beans.xml");
		}
		catch (final BeansException e)
		{
			e.printStackTrace ();
		}
	}
}