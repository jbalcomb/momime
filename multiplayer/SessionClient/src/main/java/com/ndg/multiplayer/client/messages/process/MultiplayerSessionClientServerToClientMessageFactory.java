package com.ndg.multiplayer.client.messages.process;

/**
 * Creates message implementations with Spring injections
 */
public interface MultiplayerSessionClientServerToClientMessageFactory
{
	/**
	 * @return Message implementation with Spring injections
	 */
	public AdditionalPlayerJoinedImpl createAdditionalPlayerJoinedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public CreateAccountFailedImpl createCreateAccountFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public CreateAccountSuccessfulImpl createCreateAccountSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public JoinFailedImpl createJoinFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public JoinSuccessfulImpl createJoinSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public KickedByAnotherLoginImpl createKickedByAnotherLoginMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LeaveSessionFailedImpl createLeaveSessionFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LoginFailedImpl createLoginFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LoginSuccessfulImpl createLoginSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LogoutFailedImpl createLogoutFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LogoutSuccessfulImpl createLogoutSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public PlayerLeftImpl createPlayerLeftMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public RequestSessionListFailedImpl createRequestSessionListFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public RequestSessionListSuccessfulImpl createRequestSessionListSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public SessionEndingImpl createSessionEndingMessage ();
	
	/**
	 * @return Message implementation with Spring injections
	 */
	public BrowseSavedGamesFailedImpl createBrowseSavedGamesFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public BrowseSavePointsFailedImpl createBrowseSavePointsFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public LoadGameFailedImpl createLoadGameFailedMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public BrowseSavedGamesSuccessfulImpl createBrowseSavedGamesSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public BrowseSavePointsSuccessfulImpl createBrowseSavePointsSuccessfulMessage ();
	
	/**
	 * @return Message implementation with Spring injections
	 */
	public DeleteSavedGameSuccessfulImpl createDeleteSavedGameSuccessfulMessage ();

	/**
	 * @return Message implementation with Spring injections
	 */
	public DeleteSavedGameFailedImpl createDeleteSavedGameFailedMessage ();
	
	/**
	 * @return Message implementation with Spring injections
	 */
	public UpdateHumanPlayerToAiImpl createUpdateHumanPlayerToAiMessage ();
}