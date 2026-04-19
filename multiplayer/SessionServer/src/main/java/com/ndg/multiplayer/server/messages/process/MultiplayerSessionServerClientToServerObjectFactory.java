package com.ndg.multiplayer.server.messages.process;

import jakarta.xml.bind.annotation.XmlRegistry;

import com.ndg.multiplayer.sessionbase.BrowseSavePoints;
import com.ndg.multiplayer.sessionbase.BrowseSavedGames;
import com.ndg.multiplayer.sessionbase.CreateAccount;
import com.ndg.multiplayer.sessionbase.DeleteSavedGame;
import com.ndg.multiplayer.sessionbase.JoinSession;
import com.ndg.multiplayer.sessionbase.LeaveSession;
import com.ndg.multiplayer.sessionbase.LoadGame;
import com.ndg.multiplayer.sessionbase.Login;
import com.ndg.multiplayer.sessionbase.Logout;
import com.ndg.multiplayer.sessionbase.NewSession;
import com.ndg.multiplayer.sessionbase.ObjectFactory;
import com.ndg.multiplayer.sessionbase.RequestSessionList;

/**
 * On the server, make sure we create the special versions of each message that include the processing interface
 */
@XmlRegistry
public final class MultiplayerSessionServerClientToServerObjectFactory extends ObjectFactory
{
	/** Message factory */
	private MultiplayerSessionServerClientToServerMessageFactory messageFactory;
	
	/**
	 * @return Newly created CreateAccount message
	 */
	@Override
	public final CreateAccount createCreateAccount ()
	{
		return new CreateAccountImpl ();
	}

	/**
	 * @return Newly created Login message
	 */
	@Override
	public final Login createLogin ()
	{
		return getMessageFactory ().createLoginMessage ();
	}
	
	/**
	 * @return Newly created Logout message
	 */
	@Override
	public final Logout createLogout ()
	{
		return new LogoutImpl ();
	}

	/**
	 * @return Newly created NewSession message
	 */
	@Override
	public final NewSession createNewSession ()
	{
		return new NewSessionImpl ();
	}
	
	/**
	 * @return Newly created JoinSession message
	 */
	@Override
	public final JoinSession createJoinSession ()
	{
		return getMessageFactory ().createJoinSessionMessage ();
	}

	/**
	 * @return Newly created RequestSessionList message
	 */
	@Override
	public final RequestSessionList createRequestSessionList ()
	{
		return getMessageFactory ().createRequestSessionListMessage ();
	}

	/**
	 * @return Newly created LeaveSession message
	 */
	@Override
	public final LeaveSession createLeaveSession ()
	{
		return new LeaveSessionImpl ();
	}

	/**
	 * @return Newly created BrowseSavedGames message
	 */
	@Override
	public final BrowseSavedGames createBrowseSavedGames ()
	{
		return new BrowseSavedGamesImpl ();
	}

	/**
	 * @return Newly created BrowseSavePoints message
	 */
	@Override
	public final BrowseSavePoints createBrowseSavePoints ()
	{
		return new BrowseSavePointsImpl ();
	}
	
	/**
	 * @return Newly created LoadGame message
	 */
	@Override
	public final LoadGame createLoadGame ()
	{
		return new LoadGameImpl ();
	}
	
	/**
	 * @return Newly created DeleteSavedGame message
	 */
	@Override
	public final DeleteSavedGame createDeleteSavedGame ()
	{
		return new DeleteSavedGameImpl ();
	}

	/**
	 * @return Message factory
	 */
	public final MultiplayerSessionServerClientToServerMessageFactory getMessageFactory ()
	{
		return messageFactory;
	}

	/**
	 * @param fac Message factory
	 */
	public final void setMessageFactory (final MultiplayerSessionServerClientToServerMessageFactory fac)
	{
		messageFactory = fac;
	}
}