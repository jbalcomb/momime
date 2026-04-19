package com.ndg.map.coordinates;

import com.ndg.map.MapCoordinates2D;
import com.ndg.map.MapCoordinates3D;
import com.ndg.map.ObjectFactory;

/**
 * Create extended versions of coordinates objects
 */
public final class ObjectFactoryMap extends ObjectFactory
{
	/**
	 * @return Newly created MapCoordinates2D object
	 */
	@SuppressWarnings ("deprecation")
	@Override
	public final MapCoordinates2D createMapCoordinates2D ()
	{
		return new MapCoordinates2DEx ();
	}

	/**
	 * @return Newly created MapCoordinates3D object
	 */
	@SuppressWarnings ("deprecation")
	@Override
	public final MapCoordinates3D createMapCoordinates3D ()
	{
		return new MapCoordinates3DEx ();
	}
}
