package com.ndg.utils.swing.zorder;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Implementation that ignores ZOrders and draws all operations immediately.
 */
public final class ZOrderGraphicsImmediateImpl implements ZOrderGraphics
{
	/** Graphics context to render all drawing operation onto */
	private Graphics graphics;
	
	/**
	 * Draws an image at its natural size.
	 * 
	 * @param img Image to draw
	 * @param x X position to draw image at
	 * @param y Y position to draw image at
	 * @param zOrder Z order to place image in front of or behind others
	 */
	@Override
	public final void drawImage (final Image img, final int x, final int y, @SuppressWarnings ("unused") final int zOrder)
	{
		getGraphics ().drawImage (img, x, y, null);
	}

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
	@Override
	public final void drawStretchedImage (final Image img, final int x, final int y, final int width, final int height, @SuppressWarnings ("unused") final int zOrder)
	{
		getGraphics ().drawImage (img, x, y, width, height, null);
	}
	
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
	@Override
	public final void drawStretchedClippedImage (final Image img, final int sourceX, final int sourceY, final int sourceWidth, final int sourceHeight,
		final int destinationX, final int destinationY, final int destinationWidth, final int destinationHeight, @SuppressWarnings ("unused") final int zOrder)
	{
		getGraphics ().drawImage (img, destinationX, destinationY, destinationX + destinationWidth, destinationY + destinationHeight,
			sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight, null);
	}
	
	/**
	 * Clears the list of operations
	 */
	@Override
	public final void clear ()
	{
		throw new UnsupportedOperationException ("ZOrderGraphicsImmediateImpl.clear");
	}
	
	/**
	 * @param g Graphics context to render all drawing operation onto 
	 */
	@Override
	public final void draw (@SuppressWarnings ("unused") final Graphics g)
	{
		throw new UnsupportedOperationException ("ZOrderGraphicsImmediateImpl.draw");
	}

	/**
	 * @return Graphics context to render all drawing operation onto
	 */
	@Override
	public final Graphics getGraphics ()
	{
		return graphics;
	}

	/**
	 * @param g Graphics context to render all drawing operation onto
	 */
	@Override
	public final void setGraphics (final Graphics g)
	{
		graphics = g;
	}
}