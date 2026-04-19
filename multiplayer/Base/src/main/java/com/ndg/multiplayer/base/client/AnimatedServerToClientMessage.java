package com.ndg.multiplayer.base.client;

/**
 * Interface for messages which display some kind of animation with a number of "ticks" between
 * starting and finishing.  So for example a
 * message with a duration of 1 second and a tickCount of 4 would execute:
 * 
 * start > wait 1/4 second > tick > wait 1/4 second > tick > wait 1/4 second > tick > wait 1/4 second > tick > finish
 */
public interface AnimatedServerToClientMessage extends CustomDurationServerToClientMessage
{
	/**
	 * @return Number of seconds that the animation takes to display
	 */
	public double getDuration ();
	
	/**
	 * @return Number of ticks that the duration is divided into
	 */
	public int getTickCount ();
	
	/**
	 * @param tickNumber How many ticks have occurred, from 1..tickCount
	 */
	public void tick (final int tickNumber);
	
	/**
	 * Controls whether the animation finishes automatically after duration (tickCount calls to the tick method) have executed, or has
	 * custom finishing conditions, in which case the application must take care of finishing the animation in the
	 * manner described on CustomDurationServerToClientMessage.
	 * 
	 * @return True if the multiplayer layer finishes the animation automatically after duration has elaped; False if the application takes care of finishing the animation
	 */
	public boolean isFinishAfterDuration ();
}