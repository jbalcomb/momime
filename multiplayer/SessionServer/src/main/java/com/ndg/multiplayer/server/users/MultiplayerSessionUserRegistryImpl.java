package com.ndg.multiplayer.server.users;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.ndg.multiplayer.userregistry.Player;
import com.ndg.multiplayer.userregistry.UserRegistry;

/**
 * Default user registry implementation that stores all the users in an XML file
 * Reads the XML in once when server starts, re-saves it every time a user is added
 */
@ManagedResource
public final class MultiplayerSessionUserRegistryImpl implements MultiplayerSessionUserRegistry
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerSessionUserRegistryImpl.class);
	
	/** Creates objects that get used in the user registry */
	private UserRegistryObjectFactory userRegistryObjectFactory;

	/** Path to user registry XML file */
	private File xmlFilename;

	/** Memory copy of user registry */
	private UserRegistry userRegistry;
	
	/** The userRegistry itself can be recreated or reloaded, so to make synchronization safe, locks are obtained against this object which never changes */
	private Object userRegistryLock = new Object ();
	
	/** Marshaller for saving user registry files */
	private Marshaller userRegistryMarshaller;

	/** Unmarshaller for reading user registry files */
	private Unmarshaller userRegistryUnmarshaller;
	
	/**
	 * Sets up a new, empty user registry
	 */
	@Override
	public final void newUserRegistry ()
	{
		synchronized (userRegistryLock)
		{
			userRegistry = new UserRegistry ();
			userRegistry.setNextAvailablePlayerID (1);
		}
	}
	
	/**
	 * Loads the user registry from disk
	 * @throws IOException If there is any misconfiguration
	 * @throws JAXBException If there is a problem parsing the XML to load the user registry
	 */
	@Override
	public final void loadUserRegistry () throws IOException, JAXBException
	{
		if (getXmlFilename () == null)
			throw new IOException ("Tried to call loadUserRegistry without first setting a filename to load it from");
		
		if (!getXmlFilename ().exists ())
			throw new IOException ("Tried to call loadUserRegistry but file \"" + xmlFilename + "\" doesn't exist");

		if (getUserRegistryUnmarshaller () == null)
			throw new IOException ("Tried to call loadUserRegistry without first calling setUserRegistryUnmarshaller");
		
		log.info ("Reading in user registry from \"" + getXmlFilename () + "\"...");

		synchronized (userRegistryLock)
		{
			userRegistry = (UserRegistry) getUserRegistryUnmarshaller ().unmarshal (getXmlFilename ());
		}
	}

	/**
	 * Saves the user registry XML to disk
	 * @throws IOException If there is any misconfiguration
	 * @throws JAXBException If there is a problem generating the XML to save the user registry
	 */
	@Override
	public final void saveUserRegistry () throws IOException, JAXBException
	{
		if (userRegistry == null)
			throw new IOException ("Tried to call saveUserRegistry without first calling newUserRegistry or loadUserRegistry");
		
		if (getXmlFilename () == null)
			throw new IOException ("Tried to call saveUserRegistry without first setting a filename to save it as");

		if (getUserRegistryMarshaller () == null)
			throw new IOException ("Tried to call saveUserRegistry without first calling setUserRegistryMarshaller");
		
		synchronized (userRegistryLock)
		{
			getUserRegistryMarshaller ().marshal (userRegistry, getXmlFilename ());
		}
	}

	/**
	 * @param playerID Player ID to search for
	 * @return User registry entry if player found, null if not found
	 */
	@Override
	public final Player findPlayerByID (final int playerID)
	{
		Player found = null;

		synchronized (userRegistryLock)
		{
			final Iterator<Player> iter = userRegistry.getPlayer ().iterator ();
			while ((found == null) && (iter.hasNext ()))
			{
				final Player thisUser = iter.next ();
				if (thisUser.getPlayerID () == playerID)
					found = thisUser;
			}
		}

		return found;
	}
	
	/**
	 * @param playerName Player name to search for
	 * @return User registry entry if player found, null if not found
	 */
	@Override
	public final Player findPlayerByName (final String playerName)
	{
		Player found = null;

		synchronized (userRegistryLock)
		{
			final Iterator<Player> iter = userRegistry.getPlayer ().iterator ();
			while ((found == null) && (iter.hasNext ()))
			{
				final Player thisUser = iter.next ();
				if ((thisUser.getPlayerName () != null) && (thisUser.getPlayerName ().equals (playerName)))
					found = thisUser;
			}
		}

		return found;
	}

	/**
	 * @param playerName Requested player name
	 * @param playerPassword Password that player supplied
	 * @return playerID allocated to this new player account, or <=0 if player name already exists
	 */
	@Override
	public final int createAccount (final String playerName, final String playerPassword)
	{
		final int playerID;

		synchronized (userRegistryLock)
		{
			// First we have to check that user isn't already in registry
			if (findPlayerByName (playerName) != null)
				playerID = -1;
			else
			{
				// Add user
				playerID = userRegistry.getNextAvailablePlayerID ();

				final Player newUser = getUserRegistryObjectFactory ().createPlayer ();
				newUser.setPlayerID (playerID);
				newUser.setPlayerName (playerName);
				newUser.setPlayerPassword (playerPassword);
				newUser.setNextAvailableSavedGameID (1);
				userRegistry.getPlayer ().add (newUser);

				userRegistry.setNextAvailablePlayerID (playerID + 1);
			}
		}

		return playerID;
	}

	/**
	 * @param playerName Requested player name
	 * @param playerPassword Password that player supplied
	 * @return playerID for this player, or <=0 if name or password is incorrect
	 */
	@Override
	public final int authenticateUser (final String playerName, final String playerPassword)
	{
		final int playerID;
		final Player user = findPlayerByName (playerName);
		if (user == null)
			playerID = -1;
		
		else if (!user.getPlayerPassword ().equals (playerPassword))
			playerID = -1;

		else
			playerID = user.getPlayerID ();

		return playerID;
	}

	/**
	 * @return Creates objects that get used in the user registry
	 */
	public final UserRegistryObjectFactory getUserRegistryObjectFactory ()
	{
		return userRegistryObjectFactory;
	}

	/**
	 * @param fac Creates objects that get used in the user registry
	 */
	public final void setUserRegistryObjectFactory (final UserRegistryObjectFactory fac)
	{
		userRegistryObjectFactory = fac;
	}
	
	/**
	 * @return Marshaller for saving user registry files
	 */
	public final Marshaller getUserRegistryMarshaller ()
	{
		return userRegistryMarshaller;
	}

	/**
	 * @param marshaller Marshaller for saving user registry files
	 */
	public final void setUserRegistryMarshaller (final Marshaller marshaller)
	{
		userRegistryMarshaller = marshaller;
	}

	/**
	 * @return Unmarshaller for reading user registry files
	 */
	public final Unmarshaller getUserRegistryUnmarshaller ()
	{
		return userRegistryUnmarshaller;
	}

	/**
	 * @param unmarshaller Unmarshaller for reading user registry files
	 */
	public final void setUserRegistryUnmarshaller (final Unmarshaller unmarshaller)
	{
		userRegistryUnmarshaller = unmarshaller;
	}

	/**
	 * @return Path to user registry XML file
	 */
	@ManagedAttribute
	public final File getXmlFilename ()
	{
		return xmlFilename;
	}

	/**
	 * @param path Path to user registry XML file
	 */
	public final void setXmlFilename (final File path)
	{
		xmlFilename = path;
	}
}