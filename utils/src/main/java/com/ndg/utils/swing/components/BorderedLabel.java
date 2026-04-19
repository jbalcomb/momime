package com.ndg.utils.swing.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;

/**
 * Label that draws the text five times to give a border/glow effect.
 * Would have preferred to do this via overriding SynthLabelUI - that would feel less of a hack - but the UI
 * relies on SynthGraphicsUtils.paintText to draw its text, and only allows passing in a single
 * offset, you can't supply different offsetX and offsetY values.
 */
public final class BorderedLabel extends JLabel
{
	/**
	 * Override default drawing of the label
	 */
	@Override
	protected final void paintComponent (final Graphics g)
	{
		// Scale the text smoothly
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont (getFont ());

		// Draw shadow
		g.setColor (getBackground ());
		g.drawString (getText (), 0, g.getFontMetrics ().getAscent () + 0);
		g.drawString (getText (), 2, g.getFontMetrics ().getAscent () + 0);
		g.drawString (getText (), 2, g.getFontMetrics ().getAscent () + 2);
		g.drawString (getText (), 0, g.getFontMetrics ().getAscent () + 2);
		
		// Draw text
		g.setColor (getForeground ());
		g.drawString (getText (), 1, g.getFontMetrics ().getAscent () + 1);
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
			size.width = size.width + 2;
			size.height = size.height + 2;
		}
		return size;
	}
}