package momime.common.database;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.ndg.utils.swing.NdgUIUtils;

import momime.common.MomException;

/**
 * Provides a consistency check on animations, to ensure all the images are the same size
 */
public final class AnimationEx extends Animation
{
	/** All frames of an animation must share the same width; null indicates that we haven't derived this value yet */
	private Integer animationWidth;
	
	/** All frames of an animation must share the same height; null indicates that we haven't derived this value yet */
	private Integer animationHeight;

	/** Helper methods and constants for creating and laying out Swing components */
	private NdgUIUtils utils;
	
	/**
	 * Finds the width and height of all the frames in this animation
	 * @throws IOException If there is a problem loading any of the animation frames, or we fail the consistency checks
	 */
	public final void deriveAnimationWidthAndHeight () throws IOException
	{
		// Animations must be non-empty
		if (getFrame ().size () == 0)
			throw new MomException ("Animation " + getAnimationID () + " has 0 frames");
		
		// Check all the images
		boolean first = true;
		for (final AnimationFrame thisFrame : getFrame ())
		{
			final BufferedImage image = getUtils ().loadImage (thisFrame.getImageFile ());
			if (first)
			{
				setAnimationWidth (image.getWidth ());
				setAnimationHeight (image.getHeight ());
				first = false;
			}
			else if ((getAnimationWidth () != image.getWidth ()) || (getAnimationHeight () != image.getHeight ()))
				throw new MomException ("Frames of animation " + getAnimationID () + " are not consistent sizes (some are " +
					getAnimationWidth () + "x" + getAnimationHeight () + " and some are " + image.getWidth () + "x" + image.getHeight () + ")");
		}
	}
	
	/**
	 * @return All frames of an animation must share the same width
	 * @throws IOException If there is a problem loading any of the animation frames, or we fail the consistency checks
	 */
	public final int getAnimationWidth () throws IOException
	{
		if (animationWidth == null)
			deriveAnimationWidthAndHeight ();
		
		return animationWidth;
	}

	/**
	 * @param w All frames of an animation must share the same width
	 */
	public final void setAnimationWidth (final int w)
	{
		animationWidth = w;
	}
	
	/**
	 * @return All frames of an animation must share the same height
	 * @throws IOException If there is a problem loading any of the animation frames, or we fail the consistency checks
	 */
	public final int getAnimationHeight () throws IOException
	{
		if (animationHeight == null)
			deriveAnimationWidthAndHeight ();

		return animationHeight;
	}

	/**
	 * @param h All frames of an animation must share the same height
	 */
	public final void setAnimationHeight (final int h)
	{
		animationHeight = h;
	}
	
	/**
	 * @return Helper methods and constants for creating and laying out Swing components
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param util Helper methods and constants for creating and laying out Swing components
	 */
	public final void setUtils (final NdgUIUtils util)
	{
		utils = util;
	}
}