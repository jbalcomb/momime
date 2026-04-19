package com.ndg.multiplayer.client.messages.process;

import jakarta.xml.bind.annotation.XmlRegistry;

import com.ndg.multiplayer.sessionbase.AdditionalPlayerJoined;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsFailed;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsSuccessful;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesFailed;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesSuccessful;
import com.ndg.multiplayer.sessionbase.CreateAccountFailed;
import com.ndg.multiplayer.sessionbase.CreateAccountSuccessful;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameFailed;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameSuccessful;
import com.ndg.multiplayer.sessionbase.JoinFailed;
import com.ndg.multiplayer.sessionbase.JoinSuccessful;
import com.ndg.multiplayer.sessionbase.KickedByAnotherLogin;
import com.ndg.multiplayer.sessionbase.LeaveSessionFailed;
import com.ndg.multiplayer.sessionbase.LoadGameFailed;
import com.ndg.multiplayer.sessionbase.LoginFailed;
import com.ndg.multiplayer.sessionbase.LoginSuccessful;
import com.ndg.multiplayer.sessionbase.LogoutFailed;
import com.ndg.multiplayer.sessionbase.LogoutSuccessful;
import com.ndg.multiplayer.sessionbase.ObjectFactory;
import com.ndg.multiplayer.sessionbase.PlayerLeft;
import com.ndg.multiplayer.sessionbase.RequestSessionListFailed;
import com.ndg.multiplayer.sessionbase.RequestSessionListSuccessful;
import com.ndg.multiplayer.sessionbase.SessionEnding;
import com.ndg.multiplayer.sessionbase.UpdateHumanPlayerToAI;

/**
 * Creates message implementations with Spring injections
 */
@XmlRegistry
public final class MultiplayerSessionClientServerToClientObjectFactory extends ObjectFactory
{
	/** Factory used to create message implementations */
	private MultiplayerSessionClientServerToClientMessageFactory messageFactory;
	
	/**
	 * @return Newly created Player Left message
	 */
	@Override
	public final PlayerLeft createPlayerLeft ()
	{
		return getMessageFactory ().createPlayerLeftMessage ();
	}

	/**
	 * @return Newly created Logout Successful message
	 */
	@Override
	public final LogoutSuccessful createLogoutSuccessful ()
	{
		return getMessageFactory ().createLogoutSuccessfulMessage ();
	}

	/**
	 * @return Newly created Kicked by Another Login message
	 */
	@Override
	public final KickedByAnotherLogin createKickedByAnotherLogin ()
	{
		return getMessageFactory ().createKickedByAnotherLoginMessage ();
	}

	/**
	 * @return Newly created Request Session List Successful message
	 */
	@Override
	public final RequestSessionListSuccessful createRequestSessionListSuccessful ()
	{
		return getMessageFactory ().createRequestSessionListSuccessfulMessage ();
	}

	/**
	 * @return Newly created Session Ending message
	 */
	@Override
	public final SessionEnding createSessionEnding ()
	{
		return getMessageFactory ().createSessionEndingMessage ();
	}

	/**
	 * @return Newly created Create Account Successful message
	 */
	@Override
	public final CreateAccountSuccessful createCreateAccountSuccessful ()
	{
		return getMessageFactory ().createCreateAccountSuccessfulMessage ();
	}

	/**
	 * @return Newly created Additional Player Joined message
	 */
	@Override
	public final AdditionalPlayerJoined createAdditionalPlayerJoined ()
	{
		return getMessageFactory ().createAdditionalPlayerJoinedMessage ();
	}

	/**
	 * @return Newly created Join Successful message
	 */
	@Override
	public final JoinSuccessful createJoinSuccessful ()
	{
		return getMessageFactory ().createJoinSuccessfulMessage ();
	}

	/**
	 * @return Newly created Login Successful message
	 */
	@Override
	public final LoginSuccessful createLoginSuccessful ()
	{
		return getMessageFactory ().createLoginSuccessfulMessage ();
	}

	/**
	 * @return Newly created Leave Session Failed message
	 */
	@Override
	public final LeaveSessionFailed createLeaveSessionFailed ()
	{
		return getMessageFactory ().createLeaveSessionFailedMessage ();
	}

	/**
	 * @return Newly created Request Session List Failed message
	 */
	@Override
	public final RequestSessionListFailed createRequestSessionListFailed ()
	{
		return getMessageFactory ().createRequestSessionListFailedMessage ();
	}

	/**
	 * @return Newly created Create Account Failed message
	 */
	@Override
	public final CreateAccountFailed createCreateAccountFailed ()
	{
		return getMessageFactory ().createCreateAccountFailedMessage ();
	}

	/**
	 * @return Newly created Login Failed message
	 */
	@Override
	public final LoginFailed createLoginFailed ()
	{
		return getMessageFactory ().createLoginFailedMessage ();
	}

	/**
	 * @return Newly created Join Failed message
	 */
	@Override
	public final JoinFailed createJoinFailed ()
	{
		return getMessageFactory ().createJoinFailedMessage ();
	}

	/**
	 * @return Newly created Logout Failed message
	 */
	@Override
	public final LogoutFailed createLogoutFailed ()
	{
		return getMessageFactory ().createLogoutFailedMessage ();
	}

	/**
	 * @return Newly created Browse Save Games Failed message
	 */
	@Override
	public final BrowseSavedGamesFailed createBrowseSavedGamesFailed ()
	{
		return getMessageFactory ().createBrowseSavedGamesFailedMessage ();
	}

	/**
	 * @return Newly created Browse Save Points Failed message
	 */
	@Override
	public final BrowseSavePointsFailed createBrowseSavePointsFailed ()
	{
		return getMessageFactory ().createBrowseSavePointsFailedMessage ();
	}

	/**
	 * @return Newly created Load Game Failed message
	 */
	@Override
	public final LoadGameFailed createLoadGameFailed ()
	{
		return getMessageFactory ().createLoadGameFailedMessage ();
	}

	/**
	 * @return Newly created Browse Save Games Successful message
	 */
	@Override
	public final BrowseSavedGamesSuccessful createBrowseSavedGamesSuccessful ()
	{
		return getMessageFactory ().createBrowseSavedGamesSuccessfulMessage ();
	}

	/**
	 * @return Newly created Browse Save Points Successful message
	 */
	@Override
	public final BrowseSavePointsSuccessful createBrowseSavePointsSuccessful ()
	{
		return getMessageFactory ().createBrowseSavePointsSuccessfulMessage ();
	}

	/**
	 * @return Newly created Delete Saved Game Successful message
	 */
	@Override
	public final DeleteSavedGameSuccessful createDeleteSavedGameSuccessful ()
	{
		return getMessageFactory ().createDeleteSavedGameSuccessfulMessage ();
	}

	/**
	 * @return Newly created Delete Saved Game Failed message
	 */
	@Override
	public final DeleteSavedGameFailed createDeleteSavedGameFailed ()
	{
		return getMessageFactory ().createDeleteSavedGameFailedMessage ();
	}
	
	/**
	 * @return Newly created Update human to AI message
	 */
	@Override
	public final UpdateHumanPlayerToAI createUpdateHumanPlayerToAI ()
	{
		return getMessageFactory ().createUpdateHumanPlayerToAiMessage ();
	}

	/**
	 * @return Factory used to create message implementations
	 */
	public final MultiplayerSessionClientServerToClientMessageFactory getMessageFactory ()
	{
		return messageFactory;
	}

	/**
	 * @param fac Factory used to create message implementations
	 */
	public final void setMessageFactory (final MultiplayerSessionClientServerToClientMessageFactory fac)
	{
		messageFactory = fac;
	}
}