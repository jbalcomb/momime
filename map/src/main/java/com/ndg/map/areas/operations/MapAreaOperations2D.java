package com.ndg.map.areas.operations;

import java.util.List;

import com.ndg.map.areas.storage.MapArea2D;
import com.ndg.map.coordinates.MapCoordinates2DEx;

/**
 * Interface describing map operations applicable to 2D coordinate systems regardless of the type of value held at each location
 *
 * @param <E> Type of object held at each cell
 */
public interface MapAreaOperations2D<E> extends MapAreaOperations<E, MapCoordinates2DEx>
{
	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param callback Operation to call against every set of coordinates within the polygon
	 */
	public void processCoordinatesWithinPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final ProcessMapCoordinates<MapCoordinates2DEx> callback);
	
	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param callback Operation to call against every cell within the polygon
	 */
	public void processCellsWithinPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final ProcessMapCell<E> callback);
	
	/**
	 * @param area Map area to write to
	 * @param coords Vertices of the polygon
	 * @param value Value to set all cells inside the polygon equal to
	 */
	public void setPolygon (final MapArea2D<E> area, final List<MapCoordinates2DEx> coords, final E value);
	
	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param callback Operation to call against every set of coordinates along the line
	 */
	public void processCoordinatesAlongLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end,
		final int width, final ProcessMapCoordinates<MapCoordinates2DEx> callback);

	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param callback Operation to call against every cell along the line
	 */
	public void processCellsAlongLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end,
		final int width, final ProcessMapCell<E> callback);

	/**
	 * @param area Map area to write to
	 * @param start Start coordinates of the line
	 * @param end End coordinates of the line
	 * @param width Additional width to give the line, 0=normal, 1=extra pixel either side, and so on
	 * @param value Value to set along the line
	 */
	public void setLine (final MapArea2D<E> area, final MapCoordinates2DEx start, final MapCoordinates2DEx end, final int width, final E value);
	
	/**
	 * @param area Area to search over
	 * @param coords Coords of centre to scan out from
	 * @param value Value to look for
	 * @return Coordinates of cell closest to specified location that matches specified value, or null if no cell in the entire area matches the specified value
	 */
	public MapCoordinates2DEx findClosestMatchingCell (final MapArea2D<E> area, final MapCoordinates2DEx coords, final E value);
}