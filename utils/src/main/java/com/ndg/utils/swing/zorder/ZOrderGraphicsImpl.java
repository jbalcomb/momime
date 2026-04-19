package com.ndg.utils.swing.zorder;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation that defers drawing operations until the 'draw' method is called, and draws according to the ZOrder of each operation.
 * 
 * Higher values make controls appear in front/drawn later;
 * Lower values make controls appear behind/drawn earlier.
 */
public final class ZOrderGraphicsImpl implements ZOrderGraphics
{
	/** List of all operations to render */
	private final List<ZOrderGraphicsOperation> operations = new ArrayList<ZOrderGraphicsOperation> ();
	
	/**
	 * Draws an image at its natural size.
	 * 
	 * @param img Image to draw
	 * @param x X position to draw image at
	 * @param y Y position to draw image at
	 * @param zOrder Z order to place image in front of or behind others
	 */
	@Override
	public final void drawImage (final Image img, final int x, final int y, final int zOrder)
	{
		final ZOrderGraphicsOperationDrawImage op = new ZOrderGraphicsOperationDrawImage ();
		
		op.setImage (img);
		op.setX (x);
		op.setY (y);
		op.setZOrder (zOrder);
		
		operations.add (op);
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
	public final void drawStretchedImage (final Image img, final int x, final int y, final int width, final int height, final int zOrder)
	{
		final ZOrderGraphicsOperationDrawStretchedImage op = new ZOrderGraphicsOperationDrawStretchedImage ();
		
		op.setImage (img);
		op.setX (x);
		op.setY (y);
		op.setWidth (width);
		op.setHeight (height);
		op.setZOrder (zOrder);
		
		operations.add (op);
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
		final int destinationX, final int destinationY, final int destinationWidth, final int destinationHeight, final int zOrder)
	{
		final ZOrderGraphicsOperationDrawStretchedClippedImage op = new ZOrderGraphicsOperationDrawStretchedClippedImage ();
		
		op.setImage (img);
		op.setSourceX (sourceX);
		op.setSourceY (sourceY);
		op.setSourceWidth (sourceWidth);
		op.setSourceHeight (sourceHeight);
		op.setDestinationX (destinationX);
		op.setDestinationY (destinationY);
		op.setDestinationWidth (destinationWidth);
		op.setDestinationHeight (destinationHeight);
		op.setZOrder (zOrder);
		
		operations.add (op);
	}
	
	/**
	 * Clears the list of operations
	 */
	@Override
	public final void clear ()
	{
		operations.clear ();
	}
	
	/**
	 * @param g Graphics context to render all drawing operation onto 
	 */
	@Override
	public final void draw (final Graphics g)
	{
		operations.stream ()
			.sorted ((o1, o2) -> o1.getZOrder () - o2.getZOrder ())
			.forEach (o -> o.draw (g));
	}

	/**
	 * @return Graphics context to render all drawing operation onto
	 */
	@Override
	public final Graphics getGraphics ()
	{
		throw new UnsupportedOperationException ("ZOrderGraphicsImpl.getGraphics");
	}

	/**
	 * @param g Graphics context to render all drawing operation onto
	 */
	@Override
	public final void setGraphics (@SuppressWarnings ("unused") final Graphics g)
	{
		throw new UnsupportedOperationException ("ZOrderGraphicsImpl.setGraphics");
	}
}