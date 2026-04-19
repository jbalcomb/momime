package com.ndg.utils.swing;

import java.awt.GridBagConstraints;

/**
 * When a component is stretched horizontally to fill its cell, whether it is positioned west/centre/east becomes irrlevant,
 * so this gives a type safe implementation for only the north/centre/south positioning
 */
public enum GridBagConstraintsHorizontalFill
{
	/** Put the component at the top of its display area */
	NORTH (GridBagConstraints.NORTH),
	
	/** Put the component at the centre of its display area */
	CENTRE (GridBagConstraints.CENTER),
	
	/** Put the component at the bottom of its display area */
	SOUTH (GridBagConstraints.SOUTH);
	
	/** Type unsafe int constant from GridBagConstraints */
	private final int value;
	
	/**
	 * @param aValue Type unsafe int constant from GridBagConstraints
	 */
	private GridBagConstraintsHorizontalFill (final int aValue)
	{
		value = aValue;
	}
	
	/**
	 * @return Type unsafe int constant from GridBagConstraints
	 */
	public final int getValue ()
	{
		return value;
	}
}
