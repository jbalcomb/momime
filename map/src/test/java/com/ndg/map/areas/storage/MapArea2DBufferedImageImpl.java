package com.ndg.map.areas.storage;

import java.awt.image.BufferedImage;

import com.ndg.map.CoordinateSystem;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Implementation of 2D map area storage that reads and writes RGB pixel colours to a BufferedImage
 */
public final class MapArea2DBufferedImageImpl implements MapArea2D<Integer>
{
	/** Coordinate system of the map area */
	private CoordinateSystem coordinateSystem;
	
	/** Storage image */
	private BufferedImage image;

	/**
	 * @return Storage image
	 */
	public final BufferedImage getImage ()
	{
		return image;
	}
	
	/**
	 * @return Coordinate system of the map area
	 */
	@Override
	public final CoordinateSystem getCoordinateSystem ()
	{
		return coordinateSystem;
	}

	/**
	 * Note altering the coordinate system loses an existing values
	 * @param sys Coordinate system of the map area
	 */
	@Override
	public final void setCoordinateSystem (final CoordinateSystem sys)
	{
		coordinateSystem = sys;
		
		// Create bitmap
		image = new BufferedImage (sys.getWidth (), sys.getHeight (), BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * @param coords Coordinates of cell to read
	 * @return Colour held at this cell
	 */
	@Override
	public final Integer get (final MapCoordinates2DEx coords)
	{
		return get (coords.getX (), coords.getY ());
	}

	/**
	 * @param coords Coordinates of cell to set
	 * @param value Colour to write to this cell
	 */
	@Override
	public final void set (final MapCoordinates2DEx coords, final Integer value)
	{
		set (coords.getX (), coords.getY (), value);
	}

	/**
	 * @param x X coordinate of cell to read
	 * @param y Y coordinate of cell to read
	 * @return Colour held at this cell
	 */
	@Override
	public final Integer get (final int x, final int y)
	{
		return image.getRGB (x, y);
	}

	/**
	 * @param x X coordinate of cell to set
	 * @param y Y coordinate of cell to set
	 * @param value Colour to write to this cell
	 */
	@Override
	public final void set (final int x, final int y, final Integer value)
	{
		image.setRGB (x, y, value);
	}
}