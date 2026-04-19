package com.ndg.utils.swing.zorder;

import java.awt.Graphics;
import java.awt.Image;

/**
 * Draws an image at its natural size
 */
final class ZOrderGraphicsOperationDrawImage implements ZOrderGraphicsOperation
{
	/** Image to draw */
	private Image image;
	
	/** X position to draw image at */
	private int x;
	
	/** Y position to draw image at */
	private int y;
	
	/** Z order to place image in front of or behind others */
	private int zOrder;

	/**
	 * @param g Graphics context to render this drawing operation onto 
	 */
	@Override
	public final void draw (final Graphics g)
	{
		g.drawImage (getImage (), getX (), getY (), null);
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
	 * @return X position to draw image at
	 */
	public final int getX ()
	{
		return x;
	}

	/**
	 * @param pos X position to draw image at
	 */
	public final void setX (final int pos)
	{
		x = pos;
	}
	
	/**
	 * @return Y position to draw image at
	 */
	public final int getY ()
	{
		return y;
	}
	
	/**
	 * @param pos Y position to draw image at
	 */
	public final void setY (final int pos)
	{
		y = pos;
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