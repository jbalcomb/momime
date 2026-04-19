package com.ndg.multiplayer.client;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.sessionbase.BrowseSavePointsFailedReason;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesFailedReason;
import com.ndg.multiplayer.sessionbase.CreateAccountFailedReason;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameFailedReason;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.LeaveSessionFailedReason;
import com.ndg.multiplayer.sessionbase.LoadGameFailedReason;
import com.ndg.multiplayer.sessionbase.LoginFailedReason;
import com.ndg.multiplayer.sessionbase.LogoutFailedReason;
import com.ndg.multiplayer.sessionbase.RequestSessionListFailedReason;
import com.ndg.multiplayer.sessionbase.SavedGamePoint;
import com.ndg.multiplayer.sessionbase.SavedGameSession;
import com.ndg.multiplayer.sessionbase.SessionAndPlayerDescriptions;

/**
 * Different events triggered in response to messages arriving from the server, that the client might want to take action on.
 * Note the methods don't provide info that can be obtained via sender.getClient ().getXXX () methods, e.g. loginLogout doesn't
 * tell us the playerID that was logged in or out because it can be retrieved with sender.getClient ().getOurPlayerID ().
 */
public interface MultiplayerSessionClientEvent extends EventListener
{
	/**
	 * Event triggered when we server successfully creates an account for us
	 * 
	 * @param playerID Player ID allocated to our new account
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void accountCreated (final int playerID) throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered after we successfully log in.
	 * 
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void loggedIn () throws JAXBException, XMLStreamException, IOException;
	
	/**
	 * Event triggered as we successfully log out - event is
	 * triggered just before playerID and session variables are cleared.
	 * 
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void loggedOut () throws JAXBException, XMLStreamException, IOException;
	
	/**
	 * Forcibly logged out by another client logging in with our account.  This event is triggered just before
	 * the playerID and session variables are cleared.
	 * 
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void kickedByAnotherLogin () throws JAXBException, XMLStreamException, IOException;
	
	/**
	 * Event triggered when server tells us which sessions we can join
	 * 
	 * @param sessions List of sessions the server is telling us we can join
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void receivedSessionList (final List<SessionAndPlayerDescriptions> sessions)
		throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when we successfully join a session
	 * 
	 * @param reason The type of session we've joined into
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void joinedSession (final JoinSuccessfulReason reason) throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when somebody else joins the session we're already in.  This only gets sent if the player is genuinely new
	 * to the session - if they disconnect and rejoin back into their original slot, this message will not be sent.
	 * 
	 * @param playerID Player who joined
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void additionalPlayerJoined (final int playerID) throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when somebody leaves the session we're in, note it could be us leaving.
	 * Event is triggered before player is removed from the list, so if it is us leaving we have time to tidy up the session while we can still access it.
	 * 
	 * @param playerID Player who left
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void beforePlayerLeft (final int playerID) throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when somebody leaves the session we're in, note it could be us leaving.
	 * Event is triggered after player is removed from the list, and if us leaving, session description and others are already blanked out.
	 * 
	 * @param playerID Player who left
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void afterPlayerLeft (final int playerID) throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when the server closes down a session when we're still in it.  This is triggered just before all the
	 * session variables are nulled out.
	 * 
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void sessionEnding () throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when server tells us what saved games we have stored
	 * 
	 * @param savedGames List of saved games the server is telling us we can reload
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void receivedSavedGamesList (final List<SavedGameSession> savedGames)
		throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when server tells us what save points one of our saved games has stored
	 * 
	 * @param savePoints List of save points within a saved game that the server is telling us we can join
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void receivedSavePointsList (final List<SavedGamePoint> savePoints)
		throws JAXBException, XMLStreamException, IOException;

	/**
	 * Event triggered when the server successfully deleted a saved game that we requested.
	 * 
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void savedGameDeleted () throws JAXBException, XMLStreamException, IOException;
	
	/**
	 * Event triggered when the server rejects a request.  Exactly one of the failure codes will be non-null
	 * which indicates which kind of request got rejected.
	 * 
	 * @param createAccountFailed If not null, indicates why our createAccount request was rejected
	 * @param loginFailed If not null, indicates why our login request was rejected
	 * @param logoutFailed If not null, indicates why our logout request was rejected
	 * @param requestSessionListFailed If not null, indicates why our session list request was rejected
	 * @param joinFailed If not null, indicates why our join request was rejected
	 * @param leaveSessionFailed If not null, indicates why our leave request was rejected
	 * @param browseSavedGamesFailed If not null, indicates why our browse saved games request was rejected
	 * @param browseSavePointsFailed If not null, indicates why our browse save points request was rejected
	 * @param deleteSavedGameFailed If not null, indicates why our delete saved game request was rejected
	 * @param loadGameFailed If not null, indicates why our load game request was rejected
	 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
	 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
	 * @throws IOException Can be used for more general types of processing failure
	 */
	public void failed (final CreateAccountFailedReason createAccountFailed,
		final LoginFailedReason loginFailed, final LogoutFailedReason logoutFailed, final RequestSessionListFailedReason requestSessionListFailed,
		final JoinFailedReason joinFailed, final LeaveSessionFailedReason leaveSessionFailed, final BrowseSavedGamesFailedReason browseSavedGamesFailed,
		final BrowseSavePointsFailedReason browseSavePointsFailed, final DeleteSavedGameFailedReason deleteSavedGameFailed, final LoadGameFailedReason loadGameFailed)
		throws JAXBException, XMLStreamException, IOException;
	
	/**
	 * Event triggered when client disconnects, or fails to connect in the first place.
	 * 
	 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
	 */
	public void disconnected (final Exception e);
}