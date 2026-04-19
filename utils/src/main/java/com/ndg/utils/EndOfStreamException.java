package com.ndg.utils;

import java.io.IOException;

/**
 * Reaching the end of a stream normally does not really constitue a failure, so the various read methods on StreamUtils throw this type of exception 
 * so the calling routines can treat this kind of exception differently if they wish to
 */
public class EndOfStreamException extends IOException
{
	/**
	 * @param message Text describing the exception
	 */
	public EndOfStreamException (String message)
	{
		super (message);
	}
}