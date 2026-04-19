package com.ndg.map;

/**
 * Storage for radial map coordinates; currently just used in some unit tests
 */
public final class RadialMapCoordinates
{
	/** X coordinate of map cell being traversed */
	private int x;

	/** Y coordinate of map cell being traversed */
	private int y;
	
	/** Radius or step distance from the start point of this map cell */
	private int r;
	
	/** Direction being traversed to arrive at this cell (exact sequence of directions and so the meaning of this value depends on the CoordinateSystemType) */
	private int d;
	
	/** Number of cells travelled in direction d; for hex maps this ranges from 1..r, for square and diamond maps this ranges from 1..2r */
	private int n;

	/**
	 * @return X coordinate of map cell being traversed
	 */
	public final int getX ()
	{
		return x;
	}

	/**
	 * @param v X coordinate of map cell being traversed
	 */
	public final void setX (final int v)
	{
		x = v;
	}

	/**
	 * @return Y coordinate of map cell being traversed
	 */
	public final int getY ()
	{
		return y;
	}
	
	/**
	 * @param v Y coordinate of map cell being traversed
	 */
	public final void setY (final int v)
	{
		y = v;
	}

	/**
	 * @return Radius or step distance from the start point of this map cell
	 */
	public final int getR ()
	{
		return r;
	}

	/**
	 * @param v Radius or step distance from the start point of this map cell
	 */
	public final void setR (final int v)
	{
		r = v;
	}
	
	/**
	 * @return Direction being traversed to arrive at this cell (exact sequence of directions and so the meaning of this value depends on the CoordinateSystemType)
	 */
	public final int getD ()
	{
		return d;
	}
	
	/**
	 * @param v Direction being traversed to arrive at this cell (exact sequence of directions and so the meaning of this value depends on the CoordinateSystemType)
	 */
	public final void setD (final int v)
	{
		d = v;
	}

	/**
	 * @return Number of cells travelled in direction d; for hex maps this ranges from 1..r, for square and diamond maps this ranges from 1..2r
	 */
	public final int getN ()
	{
		return n;
	}

	/**
	 * @param v Number of cells travelled in direction d; for hex maps this ranges from 1..r, for square and diamond maps this ranges from 1..2r
	 */
	public final void setN (final int v)
	{
		n = v;
	}
}