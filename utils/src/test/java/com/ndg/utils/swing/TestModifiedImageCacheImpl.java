package com.ndg.utils.swing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the ModifiedImageCacheImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestModifiedImageCacheImpl
{
	/**
	 * Tests the getModifiedImage method when we don't ask for any modifications to the base image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_unmodified () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", null, false, null, null, null, null, null, false);
		
		// Check results
		assertSame (src, image);
	}

	/**
	 * Tests the getModifiedImage method when we multiply the entire image by a colour
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourEntireImage () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Multiply by colour
		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		when (utils.multiplyImageByColour (src, 12)).thenReturn (dest);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", null, false, null, null, Arrays.asList ("C"), null, null, false);
		
		// Check results
		assertSame (dest, image);
	}

	/**
	 * Tests the getModifiedImage method when we multiply the entire image by two colours
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourEntireImageTwice () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Multiply by colour
		final BufferedImage middle = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		when (utils.multiplyImageByColour (src, 12)).thenReturn (middle);

		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		when (utils.multiplyImageByColour (middle, 13)).thenReturn (dest);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", null, false, null, null, Arrays.asList ("C", "D"), null, null, false);
		
		// Check results
		assertSame (dest, image);
	}

	/**
	 * Tests the getModifiedImage method when we superimpose a partial image onto the main image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_superimpose () throws Exception
	{
		// Base and partial image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, Color.BLUE.getRGB ());
		src.setRGB (1, 0, Color.BLUE.getRGB ());

		final BufferedImage partial = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		partial.setRGB (0, 0, Color.WHITE.getRGB ());
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		when (utils.loadImage ("partial.png")).thenReturn (partial);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", "partial.png", false, 1, 0, null, null, null, false);
		
		// Check results
		assertEquals (BufferedImage.class, image.getClass ());
		final BufferedImage dest = (BufferedImage) image;
		
		assertEquals (src.getWidth (), dest.getWidth ());
		assertEquals (src.getHeight (), dest.getHeight ());
		
		assertEquals (Color.BLUE.getRGB (), dest.getRGB (0, 0));
		assertEquals (Color.WHITE.getRGB (), dest.getRGB (1, 0));
	}

	/**
	 * Tests the getModifiedImage method when we superimpose a partial image onto the main image, and colour only the partial image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourPartial () throws Exception
	{
		// Base and partial image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, Color.BLUE.getRGB ());
		src.setRGB (1, 0, Color.BLUE.getRGB ());

		final BufferedImage partial = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		partial.setRGB (0, 0, Color.WHITE.getRGB ());
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		when (utils.loadImage ("partial.png")).thenReturn (partial);

		// Multiply by colour
		final BufferedImage coloured = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		coloured.setRGB (0, 0, Color.RED.getRGB ());
		when (utils.multiplyImageByColour (partial, 12)).thenReturn (coloured);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", "partial.png", true, 1, 0, Arrays.asList ("C"), null, null, false);
		
		// Check results
		assertEquals (BufferedImage.class, image.getClass ());
		final BufferedImage dest = (BufferedImage) image;
		
		assertEquals (src.getWidth (), dest.getWidth ());
		assertEquals (src.getHeight (), dest.getHeight ());
		
		assertEquals (Color.BLUE.getRGB (), dest.getRGB (0, 0));
		assertEquals (Color.RED.getRGB (), dest.getRGB (1, 0));
	}

	/**
	 * Tests the getModifiedImage method when we superimpose a partial image onto the main image, and colour the combined image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourWhole () throws Exception
	{
		// Base and partial image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, Color.BLUE.getRGB ());
		src.setRGB (1, 0, Color.BLUE.getRGB ());

		final BufferedImage partial = new BufferedImage (1, 1, BufferedImage.TYPE_INT_ARGB);
		partial.setRGB (0, 0, Color.WHITE.getRGB ());
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		when (utils.loadImage ("partial.png")).thenReturn (partial);

		// Multiply by colour
		final BufferedImage coloured = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		when (utils.multiplyImageByColour (argThat (match -> (match != null) && (match.getWidth () == 2) && (match.getHeight () == 1) &&
			(match.getRGB (0, 0) == Color.BLUE.getRGB ()) && (match.getRGB (1, 0) == Color.WHITE.getRGB ())), eq (12))).thenReturn (coloured);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", "partial.png", false, 1, 0, Arrays.asList ("C"), null, null, false);
		
		// Check results
		assertSame (coloured, image);
	}
	
	/**
	 * Tests the getModifiedImage method when we do a pixel resize
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_pixelResize () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 2, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, Color.BLACK.getRGB ());
		src.setRGB (1, 0, Color.DARK_GRAY.getRGB ());
		src.setRGB (0, 1, Color.LIGHT_GRAY.getRGB ());
		src.setRGB (1, 1, Color.WHITE.getRGB ());
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", null, false, null, null, null, 3, 3, false);
		
		// Check results
		assertEquals (3, image.getWidth (null));
		assertEquals (3, image.getHeight (null));
		
		// To check the colours, have to render it to a BufferedImage
		final BufferedImage dest = new BufferedImage (image.getWidth (null), image.getHeight (null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = dest.createGraphics ();
		try
		{
			g.drawImage (image, 0, 0, null);
		}
		finally
		{
			g.dispose ();
		}
		
		assertEquals (Color.BLACK.getRGB (), dest.getRGB (0, 0));
		assertEquals (Color.DARK_GRAY.getRGB (), dest.getRGB (2, 0));
		assertEquals (Color.LIGHT_GRAY.getRGB (), dest.getRGB (0, 2));
		assertEquals (Color.WHITE.getRGB (), dest.getRGB (2, 2));

		assertEquals (Color.DARK_GRAY.getRGB (), dest.getRGB (1, 0));
		assertEquals (Color.LIGHT_GRAY.getRGB (), dest.getRGB (0, 1));
		assertEquals (Color.WHITE.getRGB (), dest.getRGB (1, 2));
		assertEquals (Color.WHITE.getRGB (), dest.getRGB (2, 1));

		assertEquals (Color.WHITE.getRGB (), dest.getRGB (1, 1));
	}

	/**
	 * Tests the getModifiedImage method when we do a smooth resize
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_smoothResize () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 2, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, Color.BLACK.getRGB ());
		src.setRGB (1, 0, Color.DARK_GRAY.getRGB ());
		src.setRGB (0, 1, Color.LIGHT_GRAY.getRGB ());
		src.setRGB (1, 1, Color.WHITE.getRGB ());
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Set up object to test
		final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
		cache.setUtils (utils);
		
		// Call method
		final Image image = cache.getModifiedImage ("test.png", null, false, null, null, null, 3, 3, true);
		
		// Check results
		assertEquals (3, image.getWidth (null));
		assertEquals (3, image.getHeight (null));
		
		// To check the colours, have to render it to a BufferedImage
		final BufferedImage dest = new BufferedImage (image.getWidth (null), image.getHeight (null), BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = dest.createGraphics ();
		try
		{
			g.drawImage (image, 0, 0, null);
		}
		finally
		{
			g.dispose ();
		}
		
		assertEquals (Color.BLACK.getRGB (), dest.getRGB (0, 0));
		assertEquals (Color.DARK_GRAY.getRGB (), dest.getRGB (2, 0));
		assertEquals (Color.LIGHT_GRAY.getRGB (), dest.getRGB (0, 2));
		assertEquals (Color.WHITE.getRGB (), dest.getRGB (2, 2));

		assertEquals (new Color (32, 32, 32).getRGB (), dest.getRGB (1, 0));
		assertEquals (new Color (96, 96, 96).getRGB (), dest.getRGB (0, 1));
		assertEquals (new Color (224, 224, 224).getRGB (), dest.getRGB (1, 2));
		assertEquals (new Color (160, 160, 160).getRGB (), dest.getRGB (2, 1));

		assertEquals (new Color (128, 128, 128).getRGB (), dest.getRGB (1, 1));
	}
}