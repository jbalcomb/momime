package com.ndg.utils.swing;

import java.awt.Dimension;

/**
 * Stores general constants controlling appearance and sizing of the UI
 * Any frame-specific values are declared as constants within that frame
 * 
 * Deprecated, screens should be using GridBagLayout with appropriate constraints and insets, rather than fixing control sizes
 */
@Deprecated
public class SwingLayoutConstants
{
	/**
	 * Space to leave around the outside of the content pane, by:
	 * contentPane.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder (CONTENT_PANE_BORDER_SIZE, CONTENT_PANE_BORDER_SIZE,
	 * CONTENT_PANE_BORDER_SIZE, CONTENT_PANE_BORDER_SIZE), contentPane.getBorder ()));
	 */
	public final static int CONTENT_PANE_BORDER_SIZE = 8;
	
	/**
	 * Space to leave between controls
	 */
	public final static int SPACE_BETWEEN_CONTROLS = 8;
	
	/**
	 * Height to leave between text fields stacked on top of each other
	 */
	public final static int HEIGHT_BETWEEN_CONTROLS = 24;
	
	/**
	 * Standard label height
	 */
	public final static int LABEL_HEIGHT = 16;

	/**
	 * Standard text field height
	 */
	public final static int TEXT_FIELD_HEIGHT = 21;
	
	/**
	 * Width to make a checkbox just include both borders
	 */
	public final static int CHECKBOX_WIDTH = 17;
	
	/**
	 * How many pixels lower to drop a label lower than the text box it refers to
	 */
	public final static int LABEL_LINE_UP_WITH_TEXT_FIELD = 2;
	
	/**
	 * How many pixels to offset the left edge of a checkbox to line it up with other controls
	 */
	public final static int CHECKBOX_LEFT_ADJUSTMENT = -4;
	
	/**
	 * Standard button width
	 */
	public final static int BUTTON_WIDTH = 120;
	
	/**
	 * Standard button height (just large enough for the Delphi button images with a 1 pixel border around the image)
	 */
	public final static int BUTTON_HEIGHT = 22;

	/**
	 * Standard size for full width buttons
	 */
	public final static Dimension BUTTON_SIZE = new Dimension (BUTTON_WIDTH, BUTTON_HEIGHT);

	/**
	 * Mini button width
	 */
	public final static int MINI_BUTTON_WIDTH = 60;
	
	/**
	 * Standard size for mini width buttons
	 */
	public final static Dimension MINI_BUTTON_SIZE = new Dimension (MINI_BUTTON_WIDTH, BUTTON_HEIGHT);
	
	/**
	 * Tinyest button width (just enough for two letters or an icon)
	 */
	public final static int TINYEST_BUTTON_WIDTH = 45;
	
	/**
	 * Standard size for tinyest width buttons (just enough for two letters or an icon)
	 */
	public final static Dimension TINYEST_BUTTON_SIZE = new Dimension (TINYEST_BUTTON_WIDTH, BUTTON_HEIGHT);
	
	/**
	 * Minimum size for a date control (non-nillable) to fit properly
	 */
	public final static int DATE_WIDTH = 120;

	/**
	 * Minimum size for a date control (non-nillable) to fit properly
	 */
	public final static Dimension DATE_SIZE = new Dimension (DATE_WIDTH, TEXT_FIELD_HEIGHT);
	
	/**
	 * Prevent instatiation of this class
	 */
	private SwingLayoutConstants ()
	{
	}
}
