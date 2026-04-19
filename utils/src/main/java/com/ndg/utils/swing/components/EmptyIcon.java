package com.ndg.utils.swing.components;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Empty icon with no size that draws nothing (used just when we need to satistify the requirement to have an icon, without wanting to actually draw anything)
 */
public final class EmptyIcon implements Icon
{
	/**
	 * Icon does not need to be drawn
	 */
	@SuppressWarnings ("unused")
	@Override
	public final void paintIcon (final Component c, final Graphics g, final int x, final int y)
	{
	}

	/**
	 * @return Icon has zero size
	 */
	@Override
	public final int getIconWidth ()
	{
		return 0;
	}

	/**
	 * @return Icon has zero size
	 */
	@Override
	public final int getIconHeight ()
	{
		return 0;
	}
}