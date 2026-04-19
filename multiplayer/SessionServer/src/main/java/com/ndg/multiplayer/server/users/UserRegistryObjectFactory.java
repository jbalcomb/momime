package com.ndg.multiplayer.server.users;

import com.ndg.multiplayer.userregistry.Player;

/**
 * Interface for creating objects that get used in the user registry
 */
public interface UserRegistryObjectFactory
{
	/**
	 * @return Newly created player object
	 */
	public Player createPlayer ();
}