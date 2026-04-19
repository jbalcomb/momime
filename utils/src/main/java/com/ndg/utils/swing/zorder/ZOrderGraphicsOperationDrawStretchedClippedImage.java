package com.ndg.utils.swing.zorder;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Draws only a portion of an image, stretched to a specified size
 */
final class ZOrderGraphicsOperationDrawStretchedClippedImage implements ZOrderGraphicsOperation
{
	/** Image to draw */
	private Image image;

	/** X position of portion of image to draw */
	private int sourceX;
	
	/** Y position of portion of image to draw */
	private int sourceY;

	/** Width of portion of image to draw */
	private int sourceWidth;
	
	/** Height of portion of image to draw */
	private int sourceHeight;
	
	/** X position to draw image at */
	private int destinationX;
	
	/** Y position to draw image at */
	private int destinationY;

	/** Width to stretch the image to */
	private int destinationWidth;
	
	/** Height to stretch the image to */
	private int destinationHeight;
	
	/** Z order to place image in front of or behind others */
	private int zOrder;

	/**
	 * @param g Graphics context to render this drawing operation onto 
	 */
	@Override
	public final void draw (final Graphics g)
	{
		g.drawImage (getImage (),
			getDestinationX (), getDestinationY (), getDestinationX () + getDestinationWidth (), getDestinationY () + getDestinationHeight (),
			getSourceX (), getSourceY (), getSourceX () + getSourceWidth (), getSourceY () + getSourceHeight (), null);
	}
	
	/**
	 * @return Image to draw
	 */
	public final Image getImage ()
	{
		return image;
	}

	/**
	 * @param img Image to draw
	 */
	public final void setImage (final Image img)
	{
		image = img;
	}
	
	/**
	 * @return X position of portion of image to draw
	 */
	public final int getSourceX ()
	{
		return sourceX;
	}

	/**
	 * @param x X position of portion of image to draw
	 */
	public final void setSourceX (final int x)
	{
		sourceX = x;
	}
	
	/**
	 * @return Y position of portion of image to draw
	 */
	public final int getSourceY ()
	{
		return sourceY;
	}

	/**
	 * @param y Y position of portion of image to draw
	 */
	public final void setSourceY (final int y)
	{
		sourceY = y;
	}

	/**
	 * @return Width of portion of image to draw
	 */
	public final int getSourceWidth ()
	{
		return sourceWidth;
	}

	/**
	 * @param w Width of portion of image to draw
	 */
	public final void setSourceWidth (final int w)
	{
		sourceWidth = w;
	}
	
	/**
	 * @return Height of portion of image to draw
	 */
	public final int getSourceHeight ()
	{
		return sourceHeight;
	}
	
	/**
	 * @param h Height of portion of image to draw
	 */
	public final void setSourceHeight (final int h)
	{
		sourceHeight = h;
	}
	
	/**
	 * @return X position to draw image at
	 */
	public final int getDestinationX ()
	{
		return destinationX;
	}

	/**
	 * @param x X position to draw image at
	 */
	public final void setDestinationX (final int x)
	{
		destinationX = x;
	}
	
	/**
	 * @return Y position to draw image at
	 */
	public final int getDestinationY ()
	{
		return destinationY;
	}

	/**
	 * @param y Y position to draw image at
	 */
	public final void setDestinationY (final int y)
	{
		destinationY = y;
	}

	/**
	 * @return Width to stretch the image to
	 */
	public final int getDestinationWidth ()
	{
		return destinationWidth;
	}

	/**
	 * @param w Width to stretch the image to
	 */
	public final void setDestinationWidth (final int w)
	{
		destinationWidth = w;
	}
	
	/**
	 * @return Height to stretch the image to
	 */
	public final int getDestinationHeight ()
	{
		return destinationHeight;
	}
	
	/**
	 * @param h Height to stretch the image to
	 */
	public final void setDestinationHeight (final int h)
	{
		destinationHeight = h;
	}
	
	/**
	 * @return Z order to place image in front of or behind others
	 */
	@Override
	public final int getZOrder ()
	{
		return zOrder;
	}
	
	/**
	 * @param z Z order to place image in front of or behind others
	 */
	@Override
	public final void setZOrder (final int z)
	{
		zOrder = z;
	}
}