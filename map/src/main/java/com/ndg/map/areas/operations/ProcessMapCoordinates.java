package com.ndg.map.areas.operations;

/**
 * Callback that performs some operation on a particular map cell
 *
 * @param <C> Coordinates storage object
 */
@FunctionalInterface
public interface ProcessMapCoordinates<C>
{
	/**
	 * @param coords Coordinates of map cell to perform some operation on
	 * @return Whether to keep going processing further cells
	 */
	public boolean process (final C coords);
}