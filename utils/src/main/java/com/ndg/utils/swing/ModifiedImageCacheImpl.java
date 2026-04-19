package com.ndg.utils.swing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NdgUIUtilsImpl has a cache for loading images from resources, but this only stores them as-is.
 * This adds the capability to multiple images by colours and/or alpha shading, and resize images,
 * and cache the modified images.
 */
public final class ModifiedImageCacheImpl implements ModifiedImageCache
{
	/** Helper methods and constants for creating and laying out Swing components */
	private NdgUIUtils utils;
	
	/**
	 * Images that have been modified by changing their colour or size.  These then get cached
	 * here so we don't have to generate the modified image over and over again.
	 * 
	 * The key to this is the name of the image, followed by any modifications made as follows:
	 * :S indicates the partial image was superimposed
	 * :Wxxxxxx indicates the colour of the whole image was multiplied by the specified RGB value
	 * :Pxxxxxx indicates the partial image was multiplied by the specified RGB value
	 * :R20x20 indicates the image was resized to the specified size using IMAGE_SMOOTH
	 * :D20x20 indicates the image was resized to the specified size using IMAGE_FAST
	 */
	private final Map<String, Image> modifiedImages = new HashMap<String, Image> ();

	/**
	 * @param imageName Filename of the base colour image
	 * @return Image pixel resized to double size
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image doubleSize (final String imageName) throws IOException
	{
		return getModifiedImage (imageName, null, false, null, null, null, 2d, false);
	}
	
	/**
	 * @param imageName Filename of the base colour image
	 * @return Image pixel resized to double size
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image doubleSize (final File imageName) throws IOException
	{
		return getModifiedImage (imageName, null, false, null, null, null, 2d, false);
	}
	
	/**
	 * Calculates an image that may have its colour and/or size modified, and caches it so
	 * we don't have to keep reapplying the same modifications to the same images over and over.
	 * 
	 * @param imageName Filename of the base colour image
	 * @param partialImageName Name of partial image to superimpose onto the main image, if there is one
	 * @param firstShadingColourAppliesToPartialImage If false, the partial image is superimposed onto the main image first and then
	 * 	all shading colours applied after; if true, partial image only is multiplied by the first shading colour prior to being superimposed
	 * 	onto the main image.  Flag only used when a) shadingColours has at least 1 entry and b) we have a partialImageName
	 * @param partialImageOffsetX X offset to draw partial image
	 * @param partialImageOffsetY Y offset to draw partial iamge
	 * @param shadingColours List of shading colours to apply to the image
	 * @param resizeFactor Factor to multiply the size of the image by
	 * @param smoothResize Whether smooth or pixel resize is performed
	 * @return Image with modified colours, or null if we end up resizing an image down to zero width or height
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image getModifiedImage (final String imageName, final String partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Double resizeFactor, final boolean smoothResize) throws IOException
	{
		final BufferedImage image = getUtils ().loadImage (imageName);
		
		return getModifiedImage (imageName, partialImageName, firstShadingColourAppliesToPartialImage, partialImageOffsetX, partialImageOffsetY, shadingColours,
			(resizeFactor != null) ? ((int) (resizeFactor * image.getWidth ())) : null, 
			(resizeFactor != null) ? ((int) (resizeFactor * image.getHeight ())) : null, smoothResize);
	}
	
	/**
	 * Calculates an image that may have its colour and/or size modified, and caches it so
	 * we don't have to keep reapplying the same modifications to the same images over and over.
	 * 
	 * @param imageName Filename of the base colour image
	 * @param partialImageName Name of partial image to superimpose onto the main image, if there is one
	 * @param firstShadingColourAppliesToPartialImage If false, the partial image is superimposed onto the main image first and then
	 * 	all shading colours applied after; if true, partial image only is multiplied by the first shading colour prior to being superimposed
	 * 	onto the main image.  Flag only used when a) shadingColours has at least 1 entry and b) we have a partialImageName
	 * @param partialImageOffsetX X offset to draw partial image
	 * @param partialImageOffsetY Y offset to draw partial iamge
	 * @param shadingColours List of shading colours to apply to the image
	 * @param resizeFactor Factor to multiply the size of the image by
	 * @param smoothResize Whether smooth or pixel resize is performed
	 * @return Image with modified colours, or null if we end up resizing an image down to zero width or height
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image getModifiedImage (final File imageName, final File partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Double resizeFactor, final boolean smoothResize) throws IOException
	{
		final BufferedImage image = getUtils ().loadImage (imageName);
		
		return getModifiedImage (imageName, partialImageName, firstShadingColourAppliesToPartialImage, partialImageOffsetX, partialImageOffsetY, shadingColours,
			(resizeFactor != null) ? ((int) (resizeFactor * image.getWidth ())) : null, 
			(resizeFactor != null) ? ((int) (resizeFactor * image.getHeight ())) : null, smoothResize);
	}
	
	/**
	 * Calculates an image that may have its colour and/or size modified, and caches it so
	 * we don't have to keep reapplying the same modifications to the same images over and over.
	 * 
	 * @param imageName Filename of the base colour image
	 * @param partialImageName Name of partial image to superimpose onto the main image, if there is one
	 * @param firstShadingColourAppliesToPartialImage If false, the partial image is superimposed onto the main image first and then
	 * 	all shading colours applied after; if true, partial image only is multiplied by the first shading colour prior to being superimposed
	 * 	onto the main image.  Flag only used when a) shadingColours has at least 1 entry and b) we have a partialImageName
	 * @param partialImageOffsetX X offset to draw partial image
	 * @param partialImageOffsetY Y offset to draw partial iamge
	 * @param shadingColours List of shading colours to apply to the image
	 * @param resizeWidth Width to resize image to
	 * @param resizeHeight Height to resize image to
	 * @param smoothResize Whether smooth or pixel resize is performed
	 * @return Image with modified colours, or null if we end up resizing an image down to zero width or height
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image getModifiedImage (final String imageName, final String partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Integer resizeWidth, final Integer resizeHeight, final boolean smoothResize) throws IOException
	{
		// Build a list of modifications to make
		final List<String> modifications = new ArrayList<String> ();
		
		// Is there a partial image at all?
		if ((partialImageName != null) && (partialImageOffsetX != null) && (partialImageOffsetY != null))
		{
			// Does the partial image need to be coloured?
			if ((firstShadingColourAppliesToPartialImage) && (shadingColours != null) && (!shadingColours.isEmpty ()))
			{
				modifications.add ("P" + shadingColours.get (0));
				modifications.add ("S");
				
				for (int colourNo = 1; colourNo < shadingColours.size (); colourNo++)
					modifications.add ("W" + shadingColours.get (colourNo));
			}
			else
			{
				modifications.add ("S");
				if (shadingColours != null)
					for (final String shadingColour : shadingColours)
						modifications.add ("W" + shadingColour);
			}
		}
		
		// No partial image, so just apply the colours as they are
		else if (shadingColours != null)
			for (final String shadingColour : shadingColours)
				modifications.add ("W" + shadingColour);
		
		// Reize image at the end
		if ((resizeWidth != null) && (resizeHeight != null))
		{
			if ((resizeWidth <= 0) || (resizeHeight <= 0))
				return null;
			
			if (smoothResize)
				modifications.add ("R" + resizeWidth + "x" + resizeHeight);
			else
				modifications.add ("D" + resizeWidth + "x" + resizeHeight);
		}
		
		// If there are no modifications to make at all, just use the normal image cache
		Image image;
		if (modifications.isEmpty ())
			image = getUtils ().loadImage (imageName);
		else
		{
			// Generate progressively shorter keys until we find one in the cache
			image = null;
			int modificationCount = modifications.size ();
			while ((image == null) && (modificationCount > 0))
			{
				final StringBuilder key = new StringBuilder (imageName);
				for (int modificationNo = 0; modificationNo < modificationCount; modificationNo++)
					key.append (":" + modifications.get (modificationNo));
					
				image = modifiedImages.get (key.toString ());
				if (image == null)
					modificationCount--;
			}
			
			// If no image at all at this point then no version of it exists in the cache
			if (image == null)
				image = getUtils ().loadImage (imageName);
			
			// What modifications are left to apply?
			final StringBuilder key = new StringBuilder (imageName);
			for (int modificationNo = 0; modificationNo < modificationCount; modificationNo++)
				key.append (":" + modifications.get (modificationNo));

			Image partialImage = null;
			for (int modificationNo = modificationCount; modificationNo < modifications.size (); modificationNo++)
			{
				// Apply modification
				String modification = modifications.get (modificationNo);
				String modificationType = modification.substring (0, 1);
				Image modifiedImage;
				switch (modificationType)
				{
					case "S":
						// If we don't already have the partial image then load it; possibly we already have it if it was already loaded and multiplied by a colour
						if (partialImage == null)						
							partialImage = getUtils ().loadImage (partialImageName);
						
						final BufferedImage superimposedImage = new BufferedImage (image.getWidth (null), image.getHeight (null), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D g = superimposedImage.createGraphics ();
						try
						{
							g.drawImage (image, 0, 0, null);
							g.drawImage (partialImage, partialImageOffsetX, partialImageOffsetY, null);
						}
						finally
						{
							g.dispose ();
						}
						modifiedImage = superimposedImage;
						break;
						
					case "P":
						// Recursive call to get the coloured partial image
						final String partialColour = modification.substring (1);
						partialImage = getModifiedImage (partialImageName, null, false, null, null, Arrays.asList (partialColour), null, null, true);
						
						// There will be an "S" after that then superimposes this onto the main image; for now nothing is modified
						modifiedImage = null;
						break;
						
					case "W":
						final String wholeColour = modification.substring (1);
						if (wholeColour.length () == 8)
							modifiedImage = getUtils ().multiplyImageByColourAndAlpha ((BufferedImage) image, (int) Long.parseLong (wholeColour, 16));
						else
							modifiedImage = getUtils ().multiplyImageByColour ((BufferedImage) image, Integer.parseInt (wholeColour, 16));
						break;
						
					case "R":
					case "D":
						modifiedImage = image.getScaledInstance (resizeWidth, resizeHeight, smoothResize ? Image.SCALE_SMOOTH : Image.SCALE_FAST);
						break;
						
					default:
						throw new IOException ("getModifiedImage doesn't know how to process modification: " + modification);
				}
				
				// Put partially modified image into the cache
				key.append (":" + modifications.get (modificationNo));
				if (modifiedImage != null)
				{
					modifiedImages.put (key.toString (), modifiedImage);
					image = modifiedImage;
				}
			}
		}
		
		return image;
	}
	
	/**
	 * Calculates an image that may have its colour and/or size modified, and caches it so
	 * we don't have to keep reapplying the same modifications to the same images over and over.
	 * 
	 * @param imageName Filename of the base colour image
	 * @param partialImageName Name of partial image to superimpose onto the main image, if there is one
	 * @param firstShadingColourAppliesToPartialImage If false, the partial image is superimposed onto the main image first and then
	 * 	all shading colours applied after; if true, partial image only is multiplied by the first shading colour prior to being superimposed
	 * 	onto the main image.  Flag only used when a) shadingColours has at least 1 entry and b) we have a partialImageName
	 * @param partialImageOffsetX X offset to draw partial image
	 * @param partialImageOffsetY Y offset to draw partial iamge
	 * @param shadingColours List of shading colours to apply to the image
	 * @param resizeWidth Width to resize image to
	 * @param resizeHeight Height to resize image to
	 * @param smoothResize Whether smooth or pixel resize is performed
	 * @return Image with modified colours, or null if we end up resizing an image down to zero width or height
	 * @throws IOException If there is a problem loading the image
	 */
	@Override
	public final Image getModifiedImage (final File imageName, final File partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Integer resizeWidth, final Integer resizeHeight, final boolean smoothResize) throws IOException
	{
		// Build a list of modifications to make
		final List<String> modifications = new ArrayList<String> ();
		
		// Is there a partial image at all?
		if ((partialImageName != null) && (partialImageOffsetX != null) && (partialImageOffsetY != null))
		{
			// Does the partial image need to be coloured?
			if ((firstShadingColourAppliesToPartialImage) && (shadingColours != null) && (!shadingColours.isEmpty ()))
			{
				modifications.add ("P" + shadingColours.get (0));
				modifications.add ("S");
				
				for (int colourNo = 1; colourNo < shadingColours.size (); colourNo++)
					modifications.add ("W" + shadingColours.get (colourNo));
			}
			else
			{
				modifications.add ("S");
				if (shadingColours != null)
					for (final String shadingColour : shadingColours)
						modifications.add ("W" + shadingColour);
			}
		}
		
		// No partial image, so just apply the colours as they are
		else if (shadingColours != null)
			for (final String shadingColour : shadingColours)
				modifications.add ("W" + shadingColour);
		
		// Reize image at the end
		if ((resizeWidth != null) && (resizeHeight != null))
		{
			if ((resizeWidth <= 0) || (resizeHeight <= 0))
				return null;
			
			if (smoothResize)
				modifications.add ("R" + resizeWidth + "x" + resizeHeight);
			else
				modifications.add ("D" + resizeWidth + "x" + resizeHeight);
		}
		
		// If there are no modifications to make at all, just use the normal image cache
		Image image;
		if (modifications.isEmpty ())
			image = getUtils ().loadImage (imageName);
		else
		{
			// Generate progressively shorter keys until we find one in the cache
			image = null;
			int modificationCount = modifications.size ();
			while ((image == null) && (modificationCount > 0))
			{
				final StringBuilder key = new StringBuilder (imageName.toString ());
				for (int modificationNo = 0; modificationNo < modificationCount; modificationNo++)
					key.append (":" + modifications.get (modificationNo));
					
				image = modifiedImages.get (key.toString ());
				if (image == null)
					modificationCount--;
			}
			
			// If no image at all at this point then no version of it exists in the cache
			if (image == null)
				image = getUtils ().loadImage (imageName);
			
			// What modifications are left to apply?
			final StringBuilder key = new StringBuilder (imageName.toString ());
			for (int modificationNo = 0; modificationNo < modificationCount; modificationNo++)
				key.append (":" + modifications.get (modificationNo));

			Image partialImage = null;
			for (int modificationNo = modificationCount; modificationNo < modifications.size (); modificationNo++)
			{
				// Apply modification
				String modification = modifications.get (modificationNo);
				String modificationType = modification.substring (0, 1);
				Image modifiedImage;
				switch (modificationType)
				{
					case "S":
						// If we don't already have the partial image then load it; possibly we already have it if it was already loaded and multiplied by a colour
						if (partialImage == null)						
							partialImage = getUtils ().loadImage (partialImageName);
						
						final BufferedImage superimposedImage = new BufferedImage (image.getWidth (null), image.getHeight (null), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D g = superimposedImage.createGraphics ();
						try
						{
							g.drawImage (image, 0, 0, null);
							g.drawImage (partialImage, partialImageOffsetX, partialImageOffsetY, null);
						}
						finally
						{
							g.dispose ();
						}
						modifiedImage = superimposedImage;
						break;
						
					case "P":
						// Recursive call to get the coloured partial image
						final String partialColour = modification.substring (1);
						partialImage = getModifiedImage (partialImageName, null, false, null, null, Arrays.asList (partialColour), null, null, true);
						
						// There will be an "S" after that then superimposes this onto the main image; for now nothing is modified
						modifiedImage = null;
						break;
						
					case "W":
						final String wholeColour = modification.substring (1);
						if (wholeColour.length () == 8)
							modifiedImage = getUtils ().multiplyImageByColourAndAlpha ((BufferedImage) image, Integer.parseInt (wholeColour, 16));
						else
							modifiedImage = getUtils ().multiplyImageByColour ((BufferedImage) image, Integer.parseInt (wholeColour, 16));
						break;
						
					case "R":
					case "D":
						modifiedImage = image.getScaledInstance (resizeWidth, resizeHeight, smoothResize ? Image.SCALE_SMOOTH : Image.SCALE_FAST);
						break;
						
					default:
						throw new IOException ("getModifiedImage doesn't know how to process modification: " + modification);
				}
				
				// Put partially modified image into the cache
				key.append (":" + modifications.get (modificationNo));
				if (modifiedImage != null)
				{
					modifiedImages.put (key.toString (), modifiedImage);
					image = modifiedImage;
				}
			}
		}
		
		return image;
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