package com.ndg.utils.swing.components;

import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthButtonUI;
import javax.swing.plaf.synth.SynthContext;

/**
 * Button UI that draws the text twice to give a shadow effect
 * Also offsets the text down-right 1 pixel when the button is pressed
 * (but without also offsetting the image, which happens by default)
 */
public final class OffsetShadowTextButtonUI extends SynthButtonUI
{
	/** Hack since iconTextGap is ignored if there is no icon, but in this case the icon is the button background, so we need a different zero size icon just to position the text against */
	private boolean includeEmptyIcon;
	
	/** Empty icon, if we've had to create one */
	private Icon emptyIcon;
	
	/**
	 * @param context Context for the component being painted
	 * @param g The graphics object used for painting
	 */
	@Override
	protected final void paint (final SynthContext context, final Graphics g)
	{
        final AbstractButton b = (AbstractButton) context.getComponent ();

        // Create icon if needed
        if ((isIncludeEmptyIcon ()) && (emptyIcon == null))
        	emptyIcon = new EmptyIcon ();
        
        // Button image
        context.getStyle ().getGraphicsUtils (context).paintText (context, g, null, getIcon (b),
            b.getHorizontalAlignment (), b.getVerticalAlignment (),
            b.getHorizontalTextPosition (), b.getVerticalTextPosition (),
            b.getIconTextGap (), b.getDisplayedMnemonicIndex (), 0);
        
        // Back text
        g.setFont (context.getStyle ().getFont (context));
        final int offset = (b.getModel ().isPressed () && b.getModel ().isArmed ()) ? 1 : 0;
        
        g.setColor (context.getStyle ().getColor (context, ColorType.BACKGROUND));
        context.getStyle ().getGraphicsUtils (context).paintText (context, g, b.getText (), isIncludeEmptyIcon () ? emptyIcon : null,
            b.getHorizontalAlignment (), b.getVerticalAlignment (),
            b.getHorizontalTextPosition (), b.getVerticalTextPosition (),
            b.getIconTextGap (), b.getDisplayedMnemonicIndex (), 1 + offset);
        
        // Front text
        g.setColor (context.getStyle ().getColor (context, ColorType.TEXT_FOREGROUND));
        context.getStyle ().getGraphicsUtils (context).paintText (context, g, b.getText (), isIncludeEmptyIcon () ? emptyIcon : null,
            b.getHorizontalAlignment (), b.getVerticalAlignment (),
            b.getHorizontalTextPosition (), b.getVerticalTextPosition (),
            b.getIconTextGap (), b.getDisplayedMnemonicIndex (), 0 + offset);
	}

	/**
	 * @return Hack since iconTextGap is ignored if there is no icon
	 */
	public final boolean isIncludeEmptyIcon ()
	{
		return includeEmptyIcon;
	}

	/**
	 * @param i Hack since iconTextGap is ignored if there is no icon
	 */
	public final void setIncludeEmptyIcon (final boolean i)
	{
		includeEmptyIcon = i;
	}
}