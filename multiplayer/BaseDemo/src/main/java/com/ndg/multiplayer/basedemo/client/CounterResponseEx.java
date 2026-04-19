package com.ndg.multiplayer.basedemo.client;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.base.client.AnimatedServerToClientMessage;
import com.ndg.multiplayer.basedemo.CounterResponse;

/**
 * Example animation which ticks up and finishes by itself (or not, depending on pauseAfterwards setting)
 */
public final class CounterResponseEx extends CounterResponse implements AnimatedServerToClientMessage
{
	/** Demo client */
	private MultiplayerDemoClient client;
	
	/**
	 * @return Number of seconds that the animation takes to display
	 */
	@Override
	public final double getDuration ()
	{
		return getNumberOfSeconds ();
	}
	
	/**
	 * @return Number of ticks that the duration is divided into
	 */
	@Override
	public final int getTickCount ()
	{
		return getNumberOfSeconds ();
	}
	
	/**
	 * Controls whether the animation finishes automatically after duration (tickCount calls to the tick method) have executed, or has
	 * custom finishing conditions, in which case the application must take care of finishing the animation in the
	 * manner described on CustomDurationServerToClientMessage.
	 * 
	 * @return True if the multiplayer layer finishes the animation automatically after duration has elaped; False if the application takes care of finishing the animation
	 */
	@Override
	public final boolean isFinishAfterDuration ()
	{
		return !isPauseAfterwards ();
	}
	
	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void start () throws JAXBException, XMLStreamException, IOException
	{
		getClient ().addToTextArea ("Anim started");
	}

	/**
	 * @param tickNumber How many ticks have occurred, from 1..tickCount
	 */
	@Override
	public final void tick (final int tickNumber)
	{
		getClient ().addToTextArea ("Tick " + tickNumber + " / " + getTickCount ());
		if ((tickNumber == getTickCount ()) && (isPauseAfterwards ()))
			getClient ().setPausedBy (this);
	}

	/**
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	@Override
	public final void finish () throws JAXBException, XMLStreamException, IOException
	{
		getClient ().addToTextArea ("Anim finished");
	}
	
	/**
	 * @return Demo client
	 */
	public final MultiplayerDemoClient getClient ()
	{
		return client;
	}

	/**
	 * @param obj Demo client
	 */
	public final void setClient (final MultiplayerDemoClient obj)
	{
		client = obj;
	}
}