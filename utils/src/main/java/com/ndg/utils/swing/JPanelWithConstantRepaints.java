package com.ndg.utils.swing;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 * JPanel with a timer that triggers endless repaint events.  Because the timer is running in a different thread than the EDT,
 * it doesn't get clogged up if each paint of the panel starts to take little too long, as happens using Swing timers.
 * However it avoids the headaches of using true active rendering, because the painting is still being done via the EDT,
 * so we have no synchronization to worry about and we can freely mix hand drawn elements with Swing components.
 * 
 * This purely generates the repaint events; the frequency of updates shouldn't be relied up to generate frame numbers,
 * which should be done via reading System.nanoTime ().
 *
 * This is based on the suggestions and example code from:
 * http://jamesgames.org/resources/double_buffer/double_buffering_and_passive_rendering.html
 */
public class JPanelWithConstantRepaints extends JPanel
{
	/** Milliseconds between timer ticks */
	private final static long INTERVAL_MILLISECONDS = 10;
	
	/**
	 * Starts the repaint timer
	 * @param threadName Name to give the timer thread
	 */
	public void init (final String threadName)
	{
		final Timer timer = new Timer (threadName);
		timer.scheduleAtFixedRate (new TimerTask ()
		{
			@Override
			public final void run ()
			{
				repaint ();
			}
		}, 0, INTERVAL_MILLISECONDS);
	}
}