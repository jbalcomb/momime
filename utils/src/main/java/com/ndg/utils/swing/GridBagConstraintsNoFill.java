package com.ndg.utils.swing;

import java.awt.GridBagConstraints;

/**
 * When a component is stretched neither horizontally nor vertically, we need both west/centre/east and north/centre/south positioning,
 * but this at least gives a type safe implementation
 */
public enum GridBagConstraintsNoFill
{
	/** Put the component at the centre of its display area */
	CENTRE (GridBagConstraints.CENTER),
	
	/** Put the component at the top of its display area, centred horizontally */
	NORTH (GridBagConstraints.NORTH),

	/** Put the component at the top-right corner of its display area */
	NORTHEAST (GridBagConstraints.NORTHEAST),
	
	/** Put the component on the right side of its display area, centred vertically */
	EAST (GridBagConstraints.EAST),

	/** Put the component at the bottom-right corner of its display area */
	SOUTHEAST (GridBagConstraints.SOUTHEAST),
	
	/** Put the component at the bottom of its display area, centred horizontally */
	SOUTH (GridBagConstraints.SOUTH),

	/** Put the component at the bottom-left corner of its display area */
	SOUTHWEST (GridBagConstraints.SOUTHWEST),
	
	/** Put the component on the left side of its display area, centred vertically */
	WEST (GridBagConstraints.WEST),

	/** Put the component at the top-left corner of its display area */
	NORTHWEST (GridBagConstraints.NORTHWEST);
	
	/** Type unsafe int constant from GridBagConstraints */
	private final int value;
	
	/**
	 * @param aValue Type unsafe int constant from GridBagConstraints
	 */
	private GridBagConstraintsNoFill (final int aValue)
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
