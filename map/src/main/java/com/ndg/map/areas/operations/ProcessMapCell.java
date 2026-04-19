package com.ndg.map.areas.operations;

/**
 * Callback that performs some operation on a particular map cell
 *
 * @param <E> Type of object held at each cell
 */
@FunctionalInterface
public interface ProcessMapCell<E>
{
	/**
	 * @param cell Map cell to perform some operation on
	 * @return Whether to keep going processing further cells
	 */
	public boolean process (final E cell);
}