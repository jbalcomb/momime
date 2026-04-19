package com.ndg.utils.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.utils.swing.components.BorderedLabel;
import com.ndg.utils.swing.components.JPasswordFieldWithBackgroundImage;
import com.ndg.utils.swing.components.JTextFieldWithBackgroundImage;
import com.ndg.utils.swing.components.OffsetShadowTextButtonUI;
import com.ndg.utils.swing.components.OffsetShadowToggleButtonUI;
import com.ndg.utils.swing.components.ShadowedLabel;

/**
 * Helper methods for dealing with UIManager
 */
public final class NdgUIUtilsImpl implements NdgUIUtils
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (NdgUIUtilsImpl.class);

	/** Image cache */
	private final Map<String, BufferedImage> imageCache = new HashMap<String, BufferedImage> ();
	
	/**
	 * Switches to Nimbus look and feel if available
	 */
	@Override
	public final void useNimbusLookAndFeel ()
	{
		// Use Nimbus look and feel
		try
		{
		    for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels ())
		    {
		        if ("Nimbus".equals (info.getName ()))
		        {
		            UIManager.setLookAndFeel (info.getClassName ());
		            break;
		        }
		    }
		}
		catch (final Exception e)
		{
			// Log error but continue
			log.warn ("Failed to switch to Nimbus look and feel", e);
		}
	}

	/**
	 * Positions a component within a grid cell, stretching it horizontally to the width of the cell
	 * 
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsHorizontalFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsHorizontalFill anchor)
	{
		return createConstraintsHorizontalFill (gridx, gridy, spanx, spany,
			(insets > 0) ? new Insets (insets, insets, insets, insets) : null, anchor);
	}

	/**
	 * Positions a component within a grid cell, stretching it vertically to the height of the cell
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsVerticalFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsVerticalFill anchor)
	{
		return createConstraintsVerticalFill (gridx, gridy, spanx, spany,
			(insets > 0) ? new Insets (insets, insets, insets, insets) : null, anchor);
	}

	/**
	 * Positions a component within a grid cell, stretching it neither horizontally nor vertically
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsNoFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsNoFill anchor)
	{
		return createConstraintsNoFill (gridx, gridy, spanx, spany,
			(insets > 0) ? new Insets (insets, insets, insets, insets) : null, anchor);
	}
	
	/**
	 * Positions a component within a grid cell, stretching it both horizontally and vertically to the full size of the cell
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Gap to leave around component
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsBothFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets)
	{
		return createConstraintsBothFill (gridx, gridy, spanx, spany,
			(insets > 0) ? new Insets (insets, insets, insets, insets) : null);
	}

	/**
	 * Positions a component within a grid cell, stretching it horizontally to the width of the cell
	 * 
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Custom gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsHorizontalFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsHorizontalFill anchor)
	{
		return createConstraints (gridx, gridy, spanx, spany, insets, anchor.getValue (), GridBagConstraints.HORIZONTAL);
	}

	/**
	 * Positions a component within a grid cell, stretching it vertically to the height of the cell
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Custom gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsVerticalFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsVerticalFill anchor)
	{
		return createConstraints (gridx, gridy, spanx, spany, insets, anchor.getValue (), GridBagConstraints.VERTICAL);
	}

	/**
	 * Positions a component within a grid cell, stretching it neither horizontally nor vertically
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Custom gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsNoFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsNoFill anchor)
	{
		return createConstraints (gridx, gridy, spanx, spany, insets, anchor.getValue (), GridBagConstraints.NONE);
	}
	
	/**
	 * Positions a component within a grid cell, stretching it both horizontally and vertically to the full size of the cell
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Custom gap to leave around component
	 * @return Constraints object
	 */
	@Override
	public final GridBagConstraints createConstraintsBothFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets)
	{
		return createConstraints (gridx, gridy, spanx, spany, insets, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}

	/**
	 * Internal type unsafe version, that is called by all the others
	 *
	 * @param gridx X cell we are putting a component into
	 * @param gridy Y cell we are putting a component into
	 * @param spanx Number of cells wide this component is
	 * @param spany Number of cells high this component is
	 * @param insets Custom gap to leave around component
	 * @param anchor Position of the component within the grid cell
	 * @param fill Whether to stretch the component if it is smaller than the grid cell
	 * @return Constraints object
	 */
	private final GridBagConstraints createConstraints (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final int anchor, final int fill)
	{
		final GridBagConstraints c = new GridBagConstraints ();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = spanx;
		c.gridheight = spany;
		c.anchor = anchor;
		c.fill = fill;

		// Insets defaults to "new Insets (0, 0, 0, 0)" and cannot be null, so if we get a null then simply don't overwrite the default
		if (insets != null)
			c.insets = insets;
		
		return c;
	}

	/**
	 * @param resourceName Name of image resource on classpath, e.g. /images/cards/Clubs/5C.png 
	 * @return Image
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final BufferedImage loadImage (final String resourceName) throws IOException
	{
		// Check cache first
		BufferedImage image = imageCache.get (resourceName);
		if (image == null)
		{
			final URL resource = getClass ().getResource (resourceName);
			if (resource == null)
				throw new IOException ("Image \"" + resourceName + "\" not found on classpath");
			
			image = ImageIO.read (resource);
			imageCache.put (resourceName, image);
		}
		return image;
	}
	
	/**
	 * @param imageFile External file containing image 
	 * @return Image
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final BufferedImage loadImage (final File imageFile) throws IOException
	{
		// Check cache first
		BufferedImage image = imageCache.get (imageFile.toString ());
		if (image == null)
		{
			image = ImageIO.read (imageFile);
			imageCache.put (imageFile.toString (), image);
		}
		return image;
	}

	/**
	 * Creates a label with no text - typically because the text is going to be read from the language XML file later
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	@Override
	public final JLabel createLabel (final Color colour, final Font font)
	{
		final JLabel label = new JLabel ();
		label.setForeground (colour);
		label.setFont (font);
		
		return label;
	}

	/**
	 * Creates a label which draws its text twice to give a shadow effect
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	@Override
	public final JLabel createShadowedLabel (final Color backgroundColour, final Color foregroundColour, final Font font)
	{
		final ShadowedLabel label = new ShadowedLabel ();
		label.setForeground (foregroundColour);
		label.setBackground (backgroundColour);
		label.setFont (font);
		
		label.setBackgroundOffset (0);
		label.setForegroundOffset (1);
		
		return label;
	}
	
	/**
	 * Creates a label which draws its text twice to give a shadow effect, but the background is down-right instead of being up-left
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	@Override
	public final JLabel createBackwardsShadowedLabel (final Color backgroundColour, final Color foregroundColour, final Font font)
	{
		final ShadowedLabel label = new ShadowedLabel ();
		label.setForeground (foregroundColour);
		label.setBackground (backgroundColour);
		label.setFont (font);
		
		label.setBackgroundOffset (1);
		label.setForegroundOffset (0);
		
		return label;
	}
	
	/**
	 * Creates a label which draws its text five times to give a border effect
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	@Override
	public final JLabel createBorderedLabel (final Color backgroundColour, final Color foregroundColour, final Font font)
	{
		final JLabel label = new BorderedLabel ();
		label.setForeground (foregroundColour);
		label.setBackground (backgroundColour);
		label.setFont (font);
		
		return label;
	}
	
	/**
	 * Creates a label with some text
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param text Text for the label
	 * @return New label
	 */
	@Override
	public final JLabel createLabel (final Color colour, final Font font, final String text)
	{
		final JLabel label = createLabel (colour, font);
		label.setText (text);
		
		return label;
	}

	/**
	 * Creates a label (actually text area made to look like a label) with wrapping text
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	@Override
	public final JTextArea createWrappingLabel (final Color colour, final Font font)
	{
		final JTextArea messageText = createTextArea (colour, font);
		messageText.setEditable (false);
		messageText.setOpaque (false);
		messageText.setWrapStyleWord (true);		// This is why we have to use a JTextArea, since JLabels don't support wrapping
		messageText.setLineWrap (true);
		messageText.setBorder (new EmptyBorder (0, 0, 0, 0));
		
		// Setting background to null just paints it black - to make it invisible we have to explicitly create a colour with 0 alpha component (4th param)
		messageText.setBackground (new Color (0, 0, 0, 0));
		return messageText;
	}
	
	/**
	 * Creates a text area with no text - typically because the text is going to be read from the language XML file later
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New text area
	 */
	@Override
	public final JTextArea createTextArea (final Color colour, final Font font)
	{
		final JTextArea label = new JTextArea ();
		label.setForeground (colour);
		label.setFont (font);
		
		return label;
	}
	
	/**
	 * Creates a label of an image
	 * 
	 * @param image Image to create a label for
	 * @return New label
	 */
	@Override
	public final JLabel createImage (final Image image)
	{
		return new JLabel (new ImageIcon (image));
	}
	
	/**
	 * Creates a button comprising only of text, with no actual button appearance; assumed actual text will come from the action
	 * 
	 * @param action Action triggered by this button
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New button
	 */
	@Override
	public final JButton createTextOnlyButton (final Action action, final Color colour, final Font font)
	{
		final JButton button = new JButton (action);
		button.setForeground (colour);
		button.setFont (font);
		button.setContentAreaFilled (false);
		button.setMargin (new Insets (0, 0, 0, 0));
		button.setBorder (null);
		
		return button;
	}

	/**
	 * Creates a button with a background image; text will come from the action, so can be included or blank
	 * 
	 * @param action Action triggered by this button
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @param normalImage Image of the button in normal state
	 * @param pressedImage Image of the button when it is pressed
	 * @param disabledImage Image of the button when it is disabled
	 * @return New button
	 */
	@Override
	public final JButton createImageButton (final Action action, final Color backgroundColour, final Color foregroundColour, final Font font,
		final Image normalImage, final Image pressedImage, final Image disabledImage)
	{
		final JButton button = new JButton (action);
		button.setUI (new OffsetShadowTextButtonUI ());

		button.setForeground (foregroundColour);
		button.setBackground (backgroundColour);
		button.setFont (font);
		button.setContentAreaFilled (false);
		button.setMargin (new Insets (0, 0, 0, 0));
		button.setBorder (null);
		
		button.setIcon (new ImageIcon (normalImage));
		button.setPressedIcon (new ImageIcon (pressedImage));
		button.setDisabledIcon (new ImageIcon (disabledImage));
		button.setHorizontalTextPosition (SwingConstants.CENTER);
		button.setRolloverEnabled (false);

		return button;
	}

	/**
	 * Creates a toggle button with a background image; text will come from the action, so can be included or blank
	 * 
	 * @param action Action triggered by this button
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @param normalImage Image of the button in normal state
	 * @param pressedImage Image of the button when it is pressed
	 * @param disabledImage Image of the button when it is disabled
	 * @return New button
	 */
	@Override
	public final JToggleButton createImageToggleButton (final Action action, final Color backgroundColour, final Color foregroundColour, final Font font,
		final Image normalImage, final Image pressedImage, final Image disabledImage)
	{
		final JToggleButton button = new JToggleButton (action);
		button.setUI (new OffsetShadowToggleButtonUI ());

		button.setForeground (foregroundColour);
		button.setBackground (backgroundColour);
		button.setFont (font);
		button.setContentAreaFilled (false);
		button.setMargin (new Insets (0, 0, 0, 0));
		button.setBorder (null);
		
		button.setIcon (new ImageIcon (normalImage));
		button.setPressedIcon (new ImageIcon (pressedImage));
		button.setDisabledIcon (new ImageIcon (disabledImage));
		button.setSelectedIcon (new ImageIcon (pressedImage));
		button.setHorizontalTextPosition (SwingConstants.CENTER);
		button.setRolloverEnabled (false);

		return button;
	}
	
	/**
	 * Creates an image from an unticked image and a ticked image
	 * Text, if any, is assumed that it will be set later from the language XML file
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param untickedImage Image of the checkbox in unticked state
	 * @param tickedImage Image of the checkbox in ticked state
	 * @return New checkbox
	 */
	@Override
	public final JCheckBox createImageCheckBox (final Color colour, final Font font, final BufferedImage untickedImage, final BufferedImage tickedImage)
	{
		final JCheckBox checkbox = new JCheckBox ();
		
		checkbox.setForeground (colour);
		checkbox.setFont (font);
		
		checkbox.setIcon (new ImageIcon (untickedImage));
		checkbox.setRolloverIcon (new ImageIcon (untickedImage));
		checkbox.setSelectedIcon (new ImageIcon (tickedImage));
		
		return checkbox;
	}

	/**
	 * Creates a text field that uses an image for its background rather than the standard drawing.
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param backgroundImage Background image of the text field
	 * @return New text field
	 */
	@Override
	public final JTextField createTextFieldWithBackgroundImage (final Color colour, final Font font, final BufferedImage backgroundImage)
	{
		final JTextFieldWithBackgroundImage tf = new JTextFieldWithBackgroundImage ();

		tf.setBackgroundImage (backgroundImage);
		tf.setFont (font);
		tf.setForeground (colour);
		
		// Setting background to null just paints it black - to make it invisible we have to explicitly create a colour with 0 alpha component (4th param)
		tf.setBackground (new Color (0, 0, 0, 0));
		
		// Leave small gap at left and right so the text doesn't overlap the borders drawn on the image
		tf.setBorder (new EmptyBorder (3, 6, 3, 6));
		
		return tf;
	}

	/**
	 * Creates a transparent text field
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param size Fixed size for the text field
	 * @return New text field
	 */
	@Override
	public final JTextField createTransparentTextField (final Color colour, final Font font, final Dimension size)
	{
		final JTextField tf = new JTextField ();

		tf.setFont (font);
		tf.setForeground (colour);
		tf.setMinimumSize (size);
		tf.setMaximumSize (size);
		tf.setPreferredSize (size);
		
		// Setting background to null just paints it black - to make it invisible we have to explicitly create a colour with 0 alpha component (4th param)
		tf.setBackground (new Color (0, 0, 0, 0));
		
		// Assume since its transparent that we created it exactly the right size, so don't need any border
		tf.setBorder (null);
		
		return tf;
	}
	
	/**
	 * Creates a password field that uses an image for its background rather than the standard drawing.
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param backgroundImage Background image of the text field
	 * @return New text field
	 */
	@Override
	public final JTextField createPasswordFieldWithBackgroundImage (final Color colour, final Font font, final BufferedImage backgroundImage)
	{
		final JPasswordFieldWithBackgroundImage tf = new JPasswordFieldWithBackgroundImage ();

		tf.setBackgroundImage (backgroundImage);
		tf.setFont (font);
		tf.setForeground (colour);
		
		// Setting background to null just paints it black - to make it invisible we have to explicitly create a colour with 0 alpha component (4th param)
		tf.setBackground (new Color (0, 0, 0, 0));
		
		// Leave small gap at left and right so the text doesn't overlap the borders drawn on the image
		tf.setBorder (new EmptyBorder (3, 6, 3, 6));
		
		return tf;
	}
	
	/**
	 * @param view The component being scrolled
	 * @return Scroll pane with no border or background
	 */
	@Override
	public final JScrollPane createTransparentScrollPane (final Component view)
	{
		final JScrollPane scrollPane = new JScrollPane (view);
		scrollPane.setOpaque (false);
		scrollPane.setBorder (null);
		scrollPane.setViewportBorder (null);
		scrollPane.getViewport ().setOpaque (false);
		
		return scrollPane;
	}

	/**
	 * @param view The component being scrolled
	 * @param background Background image of the scroll bar 
	 * @return Scroll pane with specified background image
	 */
	@Override
	public final JScrollPane createScrollPaneWithBackgroundImage (final Component view, final BufferedImage background)
	{
		final JScrollPane scrollPane = new JScrollPane (view)
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				super.paintComponent (g);
				g.drawImage (background, 0, 0, null);
			}
		};

		final Dimension size = new Dimension (background.getWidth (), background.getHeight ());
		scrollPane.setMinimumSize (size);
		scrollPane.setMaximumSize (size);
		scrollPane.setPreferredSize (size);
		
		scrollPane.setOpaque (false);
		scrollPane.setBorder (null);
		scrollPane.setViewportBorder (null);
		scrollPane.getViewport ().setOpaque (false);
		
		return scrollPane;
	}
	
	/**
	 * @param background Background image of the panel 
	 * @return Panel with specified background image
	 */
	@Override
	public final JPanel createPanelWithBackgroundImage (final BufferedImage background)
	{
		final JPanel panel = new JPanel ()
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				super.paintComponent (g);
				g.drawImage (background, 0, 0, null);
			}
		};

		final Dimension size = new Dimension (background.getWidth (), background.getHeight ());
		panel.setMinimumSize (size);
		panel.setMaximumSize (size);
		panel.setPreferredSize (size);
		
		return panel;
	}
	
	/**
	 * @param background Main background to use for popup menu
	 * @param left Image to use as left strip down the popup menu (optional)
	 * @param right Image to use as right strip down the popup menu (optional)
	 * @return Popup menu with specified background image
	 */
	@Override
	public final JPopupMenu createPopupMenuWithBackgroundImage (final BufferedImage background, final BufferedImage left, final BufferedImage right)
	{
		return new JPopupMenu ()
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				// Draw centre
				int y = 0;
				while (y < getHeight ())
				{
					int x = 0;
					while (x < getWidth ())
					{
						g.drawImage (background, x, y, null);
						x = x + background.getWidth ();
					}
					y = y + background.getHeight ();
				}
				
				// Draw left
				if (left != null)
				{
					y = 0;
					while (y < getHeight ())
					{
						g.drawImage (left, 0, y, null);
						y = y + left.getHeight ();
					}
				}
				
				// Draw right
				if (right != null)
				{
					y = 0;
					while (y < getHeight ())
					{
						g.drawImage (right, getWidth () - right.getWidth (), y, null);
						y = y + right.getHeight ();
					}
				}
			}
		};		
	}
	
	/**
	 * @param action Action to trigger when the menu is clicked, and to get the action text from
	 * @param background Background image to draw behind the text
	 * @param font Font to display the menu text in
	 * @return Menu with specified background image
	 */
	@Override
	public final JMenu createMenuWithBackgroundImage (final Action action, final BufferedImage background, final Font font)
	{
		final JMenu menu = new JMenu (action)
		{
			@Override
			protected final void paintComponent (final Graphics gr)
			{
				final Graphics2D g = (Graphics2D) gr;
				g.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint (RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				g.drawImage (background, 0, 0, null);
				g.setFont (font);
				
				final int textWidth = g.getFontMetrics (font).stringWidth (getText ());
				g.drawString (getText (), (background.getWidth () - textWidth) / 2, background.getHeight () - 8);
			}			
		};

		final Dimension backgroundSize = new Dimension (background.getWidth (), background.getHeight ());
		menu.setMinimumSize (backgroundSize);
		menu.setMaximumSize (backgroundSize);
		menu.setPreferredSize (backgroundSize);
		
		return menu;
	}
	
	/**
	 * A top level menu opens out a popup menu when it is clicked.  However the popup menu it creates is created by a private method we cannot
	 * override and stored in a private field, so have to use reflection to override it.  Also for this to work you must add this JVM param:
	 * --add-opens java.desktop/javax.swing=ALL-UNNAMED
	 * 
	 * @param menu Menu to override the popup for
	 * @param popupMenu Replacement popup menu
	 */
	@Override
	public final void overrideMenuPopupMenu (final JMenu menu, final JPopupMenu popupMenu)
	{
		try
		{
			popupMenu.setInvoker (menu);
			
			final Field popupMenuField = JMenu.class.getDeclaredField ("popupMenu");
			popupMenuField.setAccessible (true);
			popupMenuField.set (menu, popupMenu);
		}
		catch (final NoSuchFieldException | IllegalAccessException e)
		{
			log.error ("Error trying to override popup menu", e);
		}
	}
	
	/**
	 * @param src Source white image
	 * @param multRGB Colour to multiply the source image by
	 * @return New image created by multiplying the RGB components of the source image against the RGB components of the colour, and preserving the image alpha
	 */
	@Override
	public final BufferedImage multiplyImageByColour (final BufferedImage src, final int multRGB)
	{
		final int multBlue = (multRGB >>> 16) & 0xFF;
		final int multGreen = (multRGB >>> 8) & 0xFF;
		final int multRed = multRGB & 0xFF;
		
		final BufferedImage dest = new BufferedImage (src.getWidth (), src.getHeight (), src.getType ());
		for (int y = 0; y < src.getHeight (); y++)
			for (int x = 0; x < src.getWidth (); x++)
			{
				final int srcRGBA = src.getRGB (x, y);
				final int srcAlpha = srcRGBA >>> 24;
				final int srcBlue = (srcRGBA >>> 16) & 0xFF;
				final int srcGreen = (srcRGBA >>> 8) & 0xFF;
				final int srcRed = srcRGBA & 0xFF;
				
				final int destAlpha = srcAlpha;
				final int destBlue = (srcBlue * multBlue) / 0xFF;
				final int destGreen = (srcGreen * multGreen) / 0xFF;
				final int destRed = (srcRed * multRed) / 0xFF;
				
				final int destRGBA = (destAlpha << 24) | (destBlue << 16) | (destGreen << 8) | destRed;
				dest.setRGB (x, y, destRGBA);
			}
		
		return dest;
	}

	/**
	 * @param src Source white image
	 * @param multRGBA Colour to multiply the source image by; alpha of 255 keeps original alpha the same, alpha of 0 makes image fully transparent
	 * @return New image created by multiplying the RGB components of the source image against the RGB components of the colour, and preserving the image alpha
	 */
	@Override
	public final BufferedImage multiplyImageByColourAndAlpha (final BufferedImage src, final int multRGBA)
	{
		final int multAlpha = multRGBA >>> 24;
		final int multBlue = (multRGBA >>> 16) & 0xFF;
		final int multGreen = (multRGBA >>> 8) & 0xFF;
		final int multRed = multRGBA & 0xFF;
		
		final BufferedImage dest = new BufferedImage (src.getWidth (), src.getHeight (), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < src.getHeight (); y++)
			for (int x = 0; x < src.getWidth (); x++)
			{
				final int srcRGBA = src.getRGB (x, y);
				final int srcAlpha = srcRGBA >>> 24;
				final int srcBlue = (srcRGBA >>> 16) & 0xFF;
				final int srcGreen = (srcRGBA >>> 8) & 0xFF;
				final int srcRed = srcRGBA & 0xFF;
				
				final int destAlpha = (srcAlpha * multAlpha) / 0xFF;
				final int destBlue = (srcBlue * multBlue) / 0xFF;
				final int destGreen = (srcGreen * multGreen) / 0xFF;
				final int destRed = (srcRed * multRed) / 0xFF;
				
				final int destRGBA = (destAlpha << 24) | (destBlue << 16) | (destGreen << 8) | destRed;
				dest.setRGB (x, y, destRGBA);
			}
		
		return dest;
	}
	
	/**
	 * @param font Font to generate labels in
	 * @param colour Colour to generate labels in
	 * @param text Text to generate labels for
	 * @param maxWidth Max pixels wide any single label is allowed to be
	 * @return List of labels to render the text
	 */
	@Override
	public final List<JLabel> wrapLabels (final Font font, final Color colour, final String text, final int maxWidth)
	{
		final List<JLabel> list = new ArrayList<JLabel> ();
		
		final BufferedImage testImage = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = testImage.createGraphics ();
		try
		{
			final FontMetrics metrics = g.getFontMetrics (font);
			
			String textRemaining = text;
			while (textRemaining.length () > 0)
			{
				String thisLine = textRemaining;
				textRemaining = "";
				
				while (metrics.stringWidth (thisLine) > maxWidth)
				{
					int pos = thisLine.lastIndexOf (" ");
					textRemaining = thisLine.substring (pos + 1) + " " + textRemaining;
					thisLine = thisLine.substring (0, pos);
				}
				
				final JLabel label = new JLabel ();
				label.setFont (font);
				label.setForeground (colour);
				label.setText (thisLine);
				list.add (label);
			}
		}
		finally
		{
			g.dispose ();
		}
		
		return list;
	}
}