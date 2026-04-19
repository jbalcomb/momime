package com.ndg.map.areas.operations;

import com.ndg.map.coordinates.MapCoordinates3DEx;

/**
 * Interface describing map operations applicable to 3D coordinate systems regardless of the type of value held at each location
 *
 * @param <E> Type of object held at each cell
 */
public interface MapAreaOperations3D<E> extends MapAreaOperations<E, MapCoordinates3DEx>
{
}