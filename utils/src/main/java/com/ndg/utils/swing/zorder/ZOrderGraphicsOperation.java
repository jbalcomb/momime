package com.ndg.utils.swing.zorder;

import java.awt.Graphics;

/**
 * Interface that all drawing operations must support
 */
interface ZOrderGraphicsOperation
{
	/**
	 * @param g Graphics context to render this drawing operation onto 
	 */
	public void draw (final Graphics g);
	
	/**
	 * @return Z order to place image in front of or behind others
	 */
	public int getZOrder ();

	/**
	 * @param z Z order to place image in front of or behind others
	 */
	public void setZOrder (final int z);
}