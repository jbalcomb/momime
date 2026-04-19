package com.ndg.utils.swing;

import java.awt.GridBagConstraints;

/**
 * When a component is stretched vertically to fill its cell, whether it is positioned north/centre/south becomes irrlevant,
 * so this gives a type safe implementation for only the west/centre/east positioning
 */
public enum GridBagConstraintsVerticalFill
{
	/** Put the component on the left side of its display area */
	WEST (GridBagConstraints.WEST),
	
	/** Put the component at the centre of its display area */
	CENTRE (GridBagConstraints.CENTER),
	
	/** Put the component on the right side of its display area */
	EAST (GridBagConstraints.EAST);
	
	/** Type unsafe int constant from GridBagConstraints */
	private final int value;
	
	/**
	 * @param aValue Type unsafe int constant from GridBagConstraints
	 */
	private GridBagConstraintsVerticalFill (final int aValue)
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
