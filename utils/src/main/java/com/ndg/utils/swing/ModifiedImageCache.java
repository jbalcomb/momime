package com.ndg.utils.swing;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * NdgUIUtilsImpl has a cache for loading images from resources, but this only stores them as-is.
 * This adds the capability to multiple images by colours and/or alpha shading, and resize images,
 * and cache the modified images.
 */
public interface ModifiedImageCache
{
	/**
	 * @param imageName Filename of the base colour image
	 * @return Image pixel resized to double size
	 * @throws IOException If there is a problem loading the image
	 */
	public Image doubleSize (final String imageName) throws IOException;

	/**
	 * @param imageName Filename of the base colour image
	 * @return Image pixel resized to double size
	 * @throws IOException If there is a problem loading the image
	 */
	public Image doubleSize (final File imageName) throws IOException;
	
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
	public Image getModifiedImage (final String imageName, final String partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Double resizeFactor, final boolean smoothResize) throws IOException;

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
	public Image getModifiedImage (final File imageName, final File partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Double resizeFactor, final boolean smoothResize) throws IOException;
	
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
	public Image getModifiedImage (final String imageName, final String partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Integer resizeWidth, final Integer resizeHeight, final boolean smoothResize) throws IOException;

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
	public Image getModifiedImage (final File imageName, final File partialImageName,
		final boolean firstShadingColourAppliesToPartialImage, final Integer partialImageOffsetX, final Integer partialImageOffsetY,
		final List<String> shadingColours, final Integer resizeWidth, final Integer resizeHeight, final boolean smoothResize) throws IOException;
}