package com.ndg.multiplayer.demo.server;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demo showing how to subclass MultiplayerSessionServer with as much handled in spring as possible
 */
public final class DemoSessionServerKickOff
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
			new ClassPathXmlApplicationContext ("/multiplayer.demo.spring/server-beans.xml");
		}
		catch (final BeansException e)
		{
			e.printStackTrace ();
		}
	}
}