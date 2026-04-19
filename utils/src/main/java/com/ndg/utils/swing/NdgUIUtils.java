package com.ndg.utils.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Action;
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

/**
 * Helper methods for dealing with UIManager
 */
public interface NdgUIUtils
{
	/**
	 * Switches to Nimbus look and feel if available
	 */
	public void useNimbusLookAndFeel ();

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
	public GridBagConstraints createConstraintsHorizontalFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsHorizontalFill anchor);

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
	public GridBagConstraints createConstraintsVerticalFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsVerticalFill anchor);

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
	public GridBagConstraints createConstraintsNoFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets, final GridBagConstraintsNoFill anchor);
	
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
	public GridBagConstraints createConstraintsBothFill (final int gridx, final int gridy, final int spanx, final int spany, final int insets);

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
	public GridBagConstraints createConstraintsHorizontalFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsHorizontalFill anchor);

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
	public GridBagConstraints createConstraintsVerticalFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsVerticalFill anchor);

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
	public GridBagConstraints createConstraintsNoFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets, final GridBagConstraintsNoFill anchor);
	
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
	public GridBagConstraints createConstraintsBothFill (final int gridx, final int gridy, final int spanx, final int spany, final Insets insets);

	/**
	 * @param resourceName Name of image resource on classpath, e.g. /momime.client.graphics/something.png 
	 * @return Image
	 * @throws IOException If there is a problem loading the image
	 */
	public BufferedImage loadImage (final String resourceName) throws IOException;

	/**
	 * @param imageFile External file containing image 
	 * @return Image
	 * @throws IOException If there is a problem loading the image
	 */
	public BufferedImage loadImage (final File imageFile) throws IOException;
	
	/**
	 * Creates a label with no text - typically because the text is going to be read from the language XML file later
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	public JLabel createLabel (final Color colour, final Font font);

	/**
	 * Creates a label (actually text area made to look like a label) with wrapping text
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	public JTextArea createWrappingLabel (final Color colour, final Font font);
	
	/**
	 * Creates a label with some text
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param text Text for the label
	 * @return New label
	 */
	public JLabel createLabel (final Color colour, final Font font, final String text);

	/**
	 * Creates a label which draws its text twice to give a shadow effect
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	public JLabel createShadowedLabel (final Color backgroundColour, final Color foregroundColour, final Font font);

	/**
	 * Creates a label which draws its text twice to give a shadow effect, but the background is down-right instead of being up-left
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	public JLabel createBackwardsShadowedLabel (final Color backgroundColour, final Color foregroundColour, final Font font);
	
	/**
	 * Creates a label which draws its text five times to give a border effect
	 * 
	 * @param backgroundColour Colour to set the background text in
	 * @param foregroundColour Colour to set the foreground text in
	 * @param font Font to set the text in
	 * @return New label
	 */
	public JLabel createBorderedLabel (final Color backgroundColour, final Color foregroundColour, final Font font);
	
	/**
	 * Creates a text area with no text - typically because the text is going to be read from the language XML file later
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New text area
	 */
	public JTextArea createTextArea (final Color colour, final Font font);
	
	/**
	 * Creates a label of an image
	 * 
	 * @param image Image to create a label for
	 * @return New label
	 */
	public JLabel createImage (final Image image);
	
	/**
	 * Creates a button comprising only of text, with no actual button appearance; assumed actual text will come from the action
	 * 
	 * @param action Action triggered by this button
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @return New button
	 */
	public JButton createTextOnlyButton (final Action action, final Color colour, final Font font);

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
	public JButton createImageButton (final Action action, final Color backgroundColour, final Color foregroundColour, final Font font,
		final Image normalImage, final Image pressedImage, final Image disabledImage);

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
	public JToggleButton createImageToggleButton (final Action action, final Color backgroundColour, final Color foregroundColour, final Font font,
		final Image normalImage, final Image pressedImage, final Image disabledImage);
	
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
	public JCheckBox createImageCheckBox (final Color colour, final Font font, final BufferedImage untickedImage, final BufferedImage tickedImage);
	
	/**
	 * Creates a text field that uses an image for its background rather than the standard drawing.
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param backgroundImage Background image of the text field
	 * @return New text field
	 */
	public JTextField createTextFieldWithBackgroundImage (final Color colour, final Font font, final BufferedImage backgroundImage);
	
	/**
	 * Creates a transparent text field
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param size Fixed size for the text field
	 * @return New text field
	 */
	public JTextField createTransparentTextField (final Color colour, final Font font, final Dimension size);
	
	/**
	 * Creates a password field that uses an image for its background rather than the standard drawing.
	 * 
	 * @param colour Colour to set the text in
	 * @param font Font to set the text in
	 * @param backgroundImage Background image of the text field
	 * @return New text field
	 */
	public JTextField createPasswordFieldWithBackgroundImage (final Color colour, final Font font, final BufferedImage backgroundImage);

	/**
	 * @param view The component being scrolled
	 * @return Scroll pane with no border or background
	 */
	public JScrollPane createTransparentScrollPane (final Component view);

	/**
	 * @param view The component being scrolled
	 * @param background Background image of the scroll bar 
	 * @return Scroll pane specified background image
	 */
	public JScrollPane createScrollPaneWithBackgroundImage (final Component view, final BufferedImage background);

	/**
	 * @param background Background image of the panel 
	 * @return Panel with specified background image
	 */
	public JPanel createPanelWithBackgroundImage (final BufferedImage background);
	
	/**
	 * @param background Main background to use for popup menu
	 * @param left Image to use as left strip down the popup menu (optional)
	 * @param right Image to use as right strip down the popup menu (optional)
	 * @return Popup menu with specified background image
	 */
	public JPopupMenu createPopupMenuWithBackgroundImage (final BufferedImage background, final BufferedImage left, final BufferedImage right);
	
	/**
	 * @param action Action to trigger when the menu is clicked, and to get the action text from
	 * @param background Background image to draw behind the text
	 * @param font Font to display the menu text in
	 * @return Menu with specified background image
	 */
	public JMenu createMenuWithBackgroundImage (final Action action, final BufferedImage background, final Font font);
	
	/**
	 * A top level menu opens out a popup menu when it is clicked.  However the popup menu it creates is created by a private method we cannot
	 * override and stored in a private field, so have to use reflection to override it.  Also for this to work you must add this JVM param:
	 * --add-opens java.desktop/javax.swing=ALL-UNNAMED
	 * 
	 * @param menu Menu to override the popup for
	 * @param popupMenu Replacement popup menu
	 */
	public void overrideMenuPopupMenu (final JMenu menu, final JPopupMenu popupMenu);
	
	/**
	 * @param src Source white image
	 * @param multRGB Colour to multiply the source image by
	 * @return New image created by multiplying the RGB components of the source image against the RGB components of the colour, and preserving the image alpha
	 */
	public BufferedImage multiplyImageByColour (final BufferedImage src, final int multRGB);

	/**
	 * @param src Source white image
	 * @param multRGBA Colour to multiply the source image by; alpha of 255 keeps original alpha the same, alpha of 0 makes image fully transparent
	 * @return New image created by multiplying the RGB components of the source image against the RGB components of the colour, and preserving the image alpha
	 */
	public BufferedImage multiplyImageByColourAndAlpha (final BufferedImage src, final int multRGBA);

	/**
	 * @param font Font to generate labels in
	 * @param colour Colour to generate labels in
	 * @param text Text to generate labels for
	 * @param maxWidth Max pixels wide any single label is allowed to be
	 * @return List of labels to render the text
	 */
	public List<JLabel> wrapLabels (final Font font, final Color colour, final String text, final int maxWidth);
}