package com.ndg.multiplayer.client;

import java.util.List;

import com.ndg.multiplayer.base.client.MultiplayerBaseClient;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.GeneralPublicKnowledge;
import com.ndg.multiplayer.sessionbase.PersistentPlayerPrivateKnowledge;
import com.ndg.multiplayer.sessionbase.SessionDescription;
import com.ndg.multiplayer.sessionbase.TransientPlayerPrivateKnowledge;

/**
 * Main client class to initiate a connection to a server.
 * A separate thread is kicked off to listen for messages from the server when we make a connection, but state should be
 * held here so that if the connection thread dies, we don't lose the info.
 */
public interface MultiplayerSessionClient extends MultiplayerBaseClient
{
	/**
	 * @return The player ID we're currently logged as; null if not currently logged in
	 */
	public Integer getOurPlayerID ();
	
	/**
	 * @param id The player ID we're currently logged as; null if not currently logged in
	 */
	public void setOurPlayerID (final Integer id);
	
	/**
	 * @return Name that we logged in using
	 */
	public String getOurPlayerName ();

	/**
	 * @param name Name that we logged in using
	 */
	public void setOurPlayerName (final String name);
	
	/**
	 * @return Details of the session we're currently in; null if not currently in a session
	 */
	public SessionDescription getSessionDescription ();
	
	/**
	 * @param sd Details of the session we're currently in; null if not currently in a session
	 */
	public void setSessionDescription (final SessionDescription sd);
	
	/**
	 * @return All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	public GeneralPublicKnowledge getGeneralPublicKnowledge ();

	/**
	 * @param gpk All in progress information that is commonly known to all players and the server in this session, just cannot be seen by anyone outside the session
	 */
	public void setGeneralPublicKnowledge (final GeneralPublicKnowledge gpk);
	
	/**
	 * @return Publicly available information about all players in our current session; this is how we access our own public details too, by searching for our player ID
	 */
	public List<PlayerPublicDetails> getPlayers ();

	/**
	 * @return Private knowledge about our player that is persisted to save game files; we can't access this for any other players
	 */
	public PersistentPlayerPrivateKnowledge getOurPersistentPlayerPrivateKnowledge ();

	/**
	 * @param ppk Private knowledge about our player that is persisted to save game files; we can't access this for any other players
	 */
	public void setOurPersistentPlayerPrivateKnowledge (final PersistentPlayerPrivateKnowledge ppk);

	/**
	 * @return Private knowledge about our player that is not persisted to save game files; we can't access this for any other players
	 */
	public TransientPlayerPrivateKnowledge getOurTransientPlayerPrivateKnowledge ();

	/**
	 * @param tpk Private knowledge about our player that is not persisted to save game files; we can't access this for any other players
	 */
	public void setOurTransientPlayerPrivateKnowledge (final TransientPlayerPrivateKnowledge tpk);
	
	/**
	 * @return Event listeners
	 */
	public List<MultiplayerSessionClientEvent> getEventListeners ();
}