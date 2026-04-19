package com.ndg.utils.swing;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Helper methods for laying out swing frames
 * 
 * Deprecated - if this is still needed anywhere, it should be part of the new merged UIUtils interface 
 */
@Deprecated
public final class SwingLayoutUtils
{
	/**
	 * @param panel Panel to add a standard invisible gap border to
	 */
	public final static void addGapBorder (final JPanel panel)
	{
		panel.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder
			(SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE, SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE,
			 SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE, SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE), panel.getBorder ()));
	}

	/**
	 * @param panel Panel to add a visible frame around, like a TPanel in Delphi
	 * @param text Text to write onto the frame border
	 */
	public final static void addTextBorder (final JPanel panel, final String text)
	{
		panel.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createTitledBorder
			(BorderFactory.createEtchedBorder (EtchedBorder.LOWERED), text,
			 TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, panel.getFont ().deriveFont (Font.BOLD)),
			 BorderFactory.createEmptyBorder (SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE, SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE,
					 											SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE, SwingLayoutConstants.CONTENT_PANE_BORDER_SIZE)));
	}

	/**
	 * Prevent instantiation
	 */
	private SwingLayoutUtils ()
	{
	}
}
