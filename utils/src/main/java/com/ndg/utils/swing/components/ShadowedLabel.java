package com.ndg.utils.swing.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

/**
 * Label that draws the text twice to give a shadow effect.
 * Would have preferred to do this via overriding SynthLabelUI - that would feel less of a hack - but the UI
 * relies on SynthGraphicsUtils.paintText to draw its text, and only allows passing in a single
 * offset, you can't supply different offsetX and offsetY values which is necessary for BorderedLabel.
 */
public final class ShadowedLabel extends JLabel
{
	/** Offset to draw the background text */
	private int backgroundOffset;

	/** Offset to draw the foreground text */
	private int foregroundOffset;
	
	/**
	 * Override default drawing of the label
	 */
	@Override
	protected final void paintComponent (final Graphics g)
	{
		if ((getText () != null) && (!getText ().trim ().equals ("")))
		{
			// Scale the text smoothly
			final Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setFont (getFont ());
	
			// Draw shadow
			g.setColor (getBackground ());
			g.drawString (getText (), getBackgroundOffset (), g.getFontMetrics ().getAscent () + getBackgroundOffset ());
			
			// Draw text
			g.setColor (getForeground ());
			g.drawString (getText (), getForegroundOffset (), g.getFontMetrics ().getAscent () + getForegroundOffset ());
		}
	}
	
	/**
	 * Add space to accomodate the shadow if the size is being derived from the text
	 * If the size has been explicitly set in the code then leave it alone
	 */
	@Override
	public final Dimension getPreferredSize ()
	{
		final Dimension size = super.getPreferredSize ();

		if (!isPreferredSizeSet ())
		{
			size.width++;
			size.height++;
		}
		return size;
	}

	/**
	 * @return Offset to draw the background text
	 */
	public final int getBackgroundOffset ()
	{
		return backgroundOffset;
	}

	/**
	 * @param o Offset to draw the background text
	 */
	public final void setBackgroundOffset (final int o)
	{
		backgroundOffset = o;
	}

	/**
	 * @return Offset to draw the foreground text
	 */
	public final int getForegroundOffset ()
	{
		return foregroundOffset;
	}
	
	/**
	 * @param o Offset to draw the foreground text
	 */
	public final void setForegroundOffset (final int o)
	{
		foregroundOffset = o;
	}
}