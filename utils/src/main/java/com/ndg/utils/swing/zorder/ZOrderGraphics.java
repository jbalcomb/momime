package com.ndg.utils.swing.zorder;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Interface prodiving the same drawing methods as drawing directly onto a "Graphics" object, except that
 * every drawing is recorded with a ZOrder value, describing which drawings should appear in front or behind others.
 * 
 * Higher values make controls appear in front/drawn later;
 * Lower values make controls appear behind/drawn earlier.
 */
public interface ZOrderGraphics
{
	/**
	 * Draws an image at its natural size.
	 * 
	 * @param img Image to draw
	 * @param x X position to draw image at
	 * @param y Y position to draw image at
	 * @param zOrder Z order to place image in front of or behind others
	 */
	public void drawImage (final Image img, final int x, final int y, final int zOrder);

	/**
	 * Draws an image with its size overriden.
	 * 
	 * @param img Image to draw
	 * @param x X position to draw image at
	 * @param y Y position to draw image at
	 * @param width Width to stretch the image to
	 * @param height Height to stretch the image to
	 * @param zOrder Z order to place image in front of or behind others
	 */
	public void drawStretchedImage (final Image img, final int x, final int y, final int width, final int height, final int zOrder);

	/**
	 * Draws only a portion of an image, with its size overriden.
	 * 
	 * @param img Image to draw
	 * @param sourceX X position of portion of image to draw
	 * @param sourceY Y position of portion of image to draw
	 * @param sourceWidth Width of portion of image to draw
	 * @param sourceHeight Height of portion of image to draw
	 * @param destinationX X position to draw image at
	 * @param destinationY Y position to draw image at
	 * @param destinationWidth Width to stretch the image to
	 * @param destinationHeight Height to stretch the image to
	 * @param zOrder Z order to place image in front of or behind others
	 */
	public void drawStretchedClippedImage (final Image img, final int sourceX, final int sourceY, final int sourceWidth, final int sourceHeight,
		final int destinationX, final int destinationY, final int destinationWidth, final int destinationHeight, final int zOrder);
	
	/**
	 * Clears the list of operations
	 */
	public void clear ();
	
	/**
	 * @param g Graphics context to render all drawing operation onto 
	 */
	public void draw (final Graphics g);
	
	/**
	 * @return Graphics context to render all drawing operation onto
	 */
	public Graphics getGraphics ();

	/**
	 * @param g Graphics context to render all drawing operation onto
	 */
	public void setGraphics (final Graphics g);
}