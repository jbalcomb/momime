package com.ndg.multiplayer.demo.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.client.MultiplayerSessionClientEvent;
import com.ndg.multiplayer.client.MultiplayerSessionClientImpl;
import com.ndg.multiplayer.demo.CtosChat;
import com.ndg.multiplayer.demo.DemoPlayerDescription;
import com.ndg.multiplayer.demo.DemoSessionDescription;
import com.ndg.multiplayer.demo.RequestUpdateHumanPlayerToAI;
import com.ndg.multiplayer.session.MultiplayerSessionUtils;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.multiplayer.sessionbase.BrowseSavePoints;
import com.ndg.multiplayer.sessionbase.BrowseSavePointsFailedReason;
import com.ndg.multiplayer.sessionbase.BrowseSavedGames;
import com.ndg.multiplayer.sessionbase.BrowseSavedGamesFailedReason;
import com.ndg.multiplayer.sessionbase.CreateAccount;
import com.ndg.multiplayer.sessionbase.CreateAccountFailedReason;
import com.ndg.multiplayer.sessionbase.DeleteSavedGame;
import com.ndg.multiplayer.sessionbase.DeleteSavedGameFailedReason;
import com.ndg.multiplayer.sessionbase.JoinFailedReason;
import com.ndg.multiplayer.sessionbase.JoinSession;
import com.ndg.multiplayer.sessionbase.JoinSuccessfulReason;
import com.ndg.multiplayer.sessionbase.LeaveSession;
import com.ndg.multiplayer.sessionbase.LeaveSessionFailedReason;
import com.ndg.multiplayer.sessionbase.LoadGame;
import com.ndg.multiplayer.sessionbase.LoadGameFailedReason;
import com.ndg.multiplayer.sessionbase.Login;
import com.ndg.multiplayer.sessionbase.LoginFailedReason;
import com.ndg.multiplayer.sessionbase.Logout;
import com.ndg.multiplayer.sessionbase.LogoutFailedReason;
import com.ndg.multiplayer.sessionbase.NewSession;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.multiplayer.sessionbase.RequestSessionList;
import com.ndg.multiplayer.sessionbase.RequestSessionListFailedReason;
import com.ndg.multiplayer.sessionbase.SavedGamePoint;
import com.ndg.multiplayer.sessionbase.SavedGameSession;
import com.ndg.multiplayer.sessionbase.SessionAndPlayerDescriptions;
import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.actions.MessageDialogAction;

import jakarta.xml.bind.JAXBException;

/**
 * Demo showing how to connect to a session server
 */
public final class DemoSessionClient extends MultiplayerSessionClientImpl
{
	/** Typical inset used by this layout */
	private final static int INSET = 3;

	/** Special inset used for layout components */
	private final static int NO_INSET = 0;
	
	/** UI manager helper */
	private NdgUIUtils utils;
	
	/** Session utils */
	private MultiplayerSessionUtils multiplayerSessionUtils;
	
	/** Area on demo panel to add messages to */
	private JTextArea textArea;

	/** Hostname/IP address of server */
	private JTextField hostname;

	/** Server port */
	private JTextField port;

	/** Player name */
	private JTextField playerName;

	/** Player password */
	private JTextField playerPassword;

	/** Kick existing connection checkbox */
	private JCheckBox kickExistingConnection;

	/** Is female checkbox */
	private JCheckBox playerFemale;

	/** Session name */
	private JTextField sessionName;

	/** Max players */
	private JTextField maxPlayers;
	
	/** Map width */
	private JTextField mapWidth;

	/** Map height */
	private JTextField mapHeight;

	/** Connect action */
	private Action connectAction;

	/** Disconnect action */
	private Action disconnectAction;

	/** Create account action */
	private Action createAccountAction;

	/** Login action */
	private Action loginAction;

	/** Logout action */
	private Action logoutAction;

	/** Player ID we are logged on with, 0 if not yet logged in */
	private JTextField playerID;

	/** Request session list action */
	private Action requestSessionListAction;

	/** New session action */
	private Action newSessionAction;

	/** Join session action */
	private Action joinSessionAction;

	/** Chat action */
	private Action chatAction;
	
	/** Leave session action */
	private Action leaveAction;
	
	/** Session ID we want to join */
	private JTextField sessionID;

	/** Text of chat message to send */
	private JTextField chatMessage;
	
	/** Browse saved games action */
	private Action browseSavedGamesAction;

	/** Delete saved game action */
	private Action deleteSavedGameAction;
	
	/** Browse save points action */
	private Action browseSavePointsAction;
	
	/** Load game action */
	private Action loadGameAction;
	
	/** Update human player to AI action */
	private Action updateHumanPlayerToAiAction;
	
	/** Chosen saved game ID */
	private JTextField savedGameID;
	
	/** Chosen saved game filename */
	private JTextField savedGameFilename;
	
	/**
	 * Kick off method invoked by spring's init-method; runs in the main app startup thread
	 */
	public final void start ()
	{
		getUtils ().useNimbusLookAndFeel ();
		
		// To be correct, should start up the first Swing frame in the Swing thread
		SwingUtilities.invokeLater (new Runnable ()
		{
			@Override
			public final void run ()
			{
				setupFrame ();
			}
		});
	}
	
	/**
	 * Sets up the Swing form; runs in the Swing event dispatcher thread
	 */
	private final void setupFrame ()
	{
		// Connect action
		connectAction = new MessageDialogAction ("Connect", (ev) ->
		{
			addToTextArea ("Connecting to " + hostname.getText () + " port " + port.getText () + "...");
			setServerAddress (hostname.getText ());
			setServerPort (Integer.parseInt (port.getText ()));
			connect ();
			enableOrDisableButtons ();
		});

		// Disconnect action
		disconnectAction = new MessageDialogAction ("Disconnect", (ev) ->
		{
			addToTextArea ("Disconnecting...");
			getServerConnection ().close (null);
			enableOrDisableButtons ();
		});

		// Create account action
		createAccountAction = new MessageDialogAction ("Create Account", (ev) ->
		{
			addToTextArea ("Requesting creating an account for " + playerName.getText () + "...");
			
			final CreateAccount msg = new CreateAccount ();
			msg.setPlayerName (playerName.getText ());
			msg.setPlayerPassword (playerPassword.getText ());
			getServerConnection ().sendMessageToServer (msg);
		});

		// Login action
		loginAction = new MessageDialogAction ("Login", (ev) ->
		{
			addToTextArea ("Logging in " + playerName.getText () + "...");

			final Login msg = new Login ();
			msg.setPlayerName (playerName.getText ());
			msg.setPlayerPassword (playerPassword.getText ());
			msg.setKickExistingConnection (kickExistingConnection.isSelected ());
			getServerConnection ().sendMessageToServer (msg);
		});

		// Logout action
		logoutAction = new MessageDialogAction ("Logout", (ev) ->
		{
			addToTextArea ("Logging out...");
			getServerConnection ().sendMessageToServer (new Logout ());
		});

		// Request session list action
		requestSessionListAction = new MessageDialogAction ("Req. session list", (ev) ->
		{
			addToTextArea ("Requesting session list...");
			getServerConnection ().sendMessageToServer (new RequestSessionList ());
		});

		// New session action
		newSessionAction = new MessageDialogAction ("New Session", (ev) ->
		{
			addToTextArea ("Requesting new session for " + playerName.getText () + "...");

			final DemoSessionDescription sd = new DemoSessionDescription ();
			sd.setSessionName (sessionName.getText ());
			sd.setMapWidth (Integer.parseInt (mapWidth.getText ()));
			sd.setMapHeight (Integer.parseInt (mapHeight.getText ()));
			
			if (!maxPlayers.getText ().isBlank ())
				sd.setMaxPlayers (Integer.parseInt (maxPlayers.getText ()));

			final DemoPlayerDescription pd = new DemoPlayerDescription ();
			pd.setPlayerID (Integer.parseInt (playerID.getText ()));
			pd.setPlayerName (playerName.getText ());
			pd.setIsFemale (playerFemale.isSelected ());
			pd.setPlayerType (PlayerType.HUMAN);

			final NewSession msg = new NewSession ();
			msg.setSessionDescription (sd);
			msg.setPlayerDescription (pd);
			getServerConnection ().sendMessageToServer (msg);
		});

		// Join session action
		joinSessionAction = new MessageDialogAction ("Join Session", (ev) ->
		{
			addToTextArea ("Requesting player " + playerName.getText () + " to join session " + sessionID.getText () + "...");

			final DemoPlayerDescription pd = new DemoPlayerDescription ();
			pd.setPlayerID (Integer.parseInt (playerID.getText ()));
			pd.setPlayerName (playerName.getText ());
			pd.setIsFemale (playerFemale.isSelected ());
			pd.setPlayerType (PlayerType.HUMAN);

			final JoinSession msg = new JoinSession ();
			msg.setSessionID (Integer.parseInt (sessionID.getText ()));
			msg.setPlayerDescription (pd);
			getServerConnection ().sendMessageToServer (msg);
		});

		// Leave session action
		leaveAction = new MessageDialogAction ("Leave Session", (ev) ->
		{
			addToTextArea ("Requesting player " + playerName.getText () + " to leave session...");
			getServerConnection ().sendMessageToServer (new LeaveSession ());
		});
		
		// Join session action
		chatAction = new MessageDialogAction ("Chat", (ev) ->
		{
			final CtosChat chat = new CtosChat ();
			chat.setChat (chatMessage.getText ());
			getServerConnection ().sendMessageToServer (chat);
		});
		
		// Browse saved games action
		browseSavedGamesAction = new MessageDialogAction ("Browse Saved Games", (ev) ->
		{
			addToTextArea ("Requesting saved games list...");
			getServerConnection ().sendMessageToServer (new BrowseSavedGames ());
		});
		
		// Delete saved game action
		deleteSavedGameAction = new MessageDialogAction ("Delete Saved Game", (ev) ->
		{
			addToTextArea ("Requesting deletion of saved game...");
			
			final DeleteSavedGame msg = new DeleteSavedGame ();
			msg.setSavedGameID (Integer.parseInt (savedGameID.getText ()));
			getServerConnection ().sendMessageToServer (msg);
		});
		
		// Browse save points action
		browseSavePointsAction = new MessageDialogAction ("Browse Save Points", (ev) ->
		{
			addToTextArea ("Requesting save points list...");
			
			final BrowseSavePoints msg = new BrowseSavePoints ();
			msg.setSavedGameID (Integer.parseInt (savedGameID.getText ()));
			getServerConnection ().sendMessageToServer (msg);
		});
		
		// Load game action
		loadGameAction = new MessageDialogAction ("Load Game", (ev) ->
		{
			addToTextArea ("Requesting load game...");

			final LoadGame msg = new LoadGame ();
			msg.setSavedGameID (Integer.parseInt (savedGameID.getText ()));
			msg.setSavedGameFilename (savedGameFilename.getText ());
			getServerConnection ().sendMessageToServer (msg);
		});
		
		// Update human player to AI action
		updateHumanPlayerToAiAction = new MessageDialogAction ("Convert to AI", (ev) ->
		{
			addToTextArea ("Telling server to kick us and convert us to an AI player...");
			getServerConnection ().sendMessageToServer (new RequestUpdateHumanPlayerToAI ());
		});
		
		// Create frame
		final JFrame frame = new JFrame ();
		// frame.setSize (1200, 800);
		frame.setTitle ("DemoSessionClient");
		frame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);

		// Initialize the content pane
		final JPanel contentPane = new JPanel ();
		contentPane.setLayout (new GridBagLayout ());

		// Create main output text area
		final JScrollPane scrollPane = new JScrollPane ();

		textArea = new JTextArea ();
		scrollPane.setViewportView (textArea);
		contentPane.add (scrollPane, getUtils ().createConstraintsBothFill (0, 2, 5, 1, INSET));
		
		scrollPane.setPreferredSize (new Dimension (0, 300));		// Make it tall enough

		// Connection box
		final JPanel connectSection = new JPanel ();
		addTextBorder (connectSection, "Connect");
		connectSection.setLayout (new GridBagLayout ());
		contentPane.add (connectSection, getUtils ().createConstraintsNoFill (0, 0, 1, 2, INSET, GridBagConstraintsNoFill.NORTHWEST));

		hostname = new JTextField ("localhost");
		port = new JTextField ("12345");

		connectSection.add (new JLabel ("Host name/IP:"),				getUtils ().createConstraintsNoFill (0, 0, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		connectSection.add (hostname,											getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		connectSection.add (new JLabel ("Port:"),							getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		connectSection.add (port,													getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		connectSection.add (new JButton (requestSessionListAction),	getUtils ().createConstraintsHorizontalFill (0, 2, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		connectSection.add (new JButton (connectAction),				getUtils ().createConstraintsHorizontalFill (1, 2, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		connectSection.add (new JButton (leaveAction),					getUtils ().createConstraintsHorizontalFill (0, 3, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		connectSection.add (new JButton (disconnectAction),			getUtils ().createConstraintsHorizontalFill (1, 3, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));

		// Player info box
		final JPanel playerInfoSection = new JPanel ();
		addTextBorder (playerInfoSection, "New Player");
		playerInfoSection.setLayout (new GridBagLayout ());
		contentPane.add (playerInfoSection, getUtils ().createConstraintsNoFill (1, 0, 1, 2, INSET, GridBagConstraintsNoFill.NORTHWEST));

		playerName = new JTextField ("Nigel");
		playerPassword = new JTextField ("My password");
		kickExistingConnection = new JCheckBox ();
		playerID = new JTextField ("0");
		playerFemale = new JCheckBox ();

		playerID.setEnabled (false);

		playerInfoSection.add (new JLabel ("Player name:"),				getUtils ().createConstraintsNoFill (0, 0, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (playerName,										getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		playerInfoSection.add (new JLabel ("Password:"),					getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (playerPassword,								getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		playerInfoSection.add (new JLabel ("Kick existing:"),				getUtils ().createConstraintsNoFill (0, 2, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (kickExistingConnection,						getUtils ().createConstraintsNoFill (1, 2, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (new JLabel ("Player ID:"),					getUtils ().createConstraintsNoFill (0, 3, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (playerID,											getUtils ().createConstraintsHorizontalFill (1, 3, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		playerInfoSection.add (new JLabel ("Female?"),					getUtils ().createConstraintsNoFill (0, 4, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (playerFemale,									getUtils ().createConstraintsNoFill (1, 4, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		playerInfoSection.add (new JButton (createAccountAction),	getUtils ().createConstraintsHorizontalFill (0, 5, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		playerInfoSection.add (new JButton (loginAction),					getUtils ().createConstraintsHorizontalFill (1, 5, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		playerInfoSection.add (new JButton (logoutAction),				getUtils ().createConstraintsHorizontalFill (1, 6, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));

		// New session box
		final JPanel newSessionSection = new JPanel ();
		addTextBorder (newSessionSection, "New Session");
		newSessionSection.setLayout (new GridBagLayout ());
		contentPane.add (newSessionSection, getUtils ().createConstraintsNoFill (2, 0, 1, 2, INSET, GridBagConstraintsNoFill.NORTHWEST));

		sessionName = new JTextField ("Java test client");
		maxPlayers = new JTextField ("0");
		mapWidth = new JTextField ("1234");
		mapHeight = new JTextField ("5678");

		newSessionSection.add (new JLabel ("Session name:"),		getUtils ().createConstraintsNoFill (0, 0, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		newSessionSection.add (sessionName,									getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		newSessionSection.add (new JLabel ("Max players:"),			getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		newSessionSection.add (maxPlayers,									getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		newSessionSection.add (new JLabel ("Map width:"),				getUtils ().createConstraintsNoFill (0, 2, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		newSessionSection.add (mapWidth,										getUtils ().createConstraintsHorizontalFill (1, 2, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		newSessionSection.add (new JLabel ("Map height:"),			getUtils ().createConstraintsNoFill (0, 3, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		newSessionSection.add (mapHeight,									getUtils ().createConstraintsHorizontalFill (1, 3, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		newSessionSection.add (new JButton (newSessionAction),	getUtils ().createConstraintsNoFill (1, 4, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		newSessionSection.add (new JButton (updateHumanPlayerToAiAction), getUtils ().createConstraintsNoFill (1, 5, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));

		// Join session box
		final JPanel joinSessionSection = new JPanel ();
		addTextBorder (joinSessionSection, "Join Session");
		joinSessionSection.setLayout (new GridBagLayout ());
		contentPane.add (joinSessionSection, getUtils ().createConstraintsNoFill (3, 0, 1, 1, INSET, GridBagConstraintsNoFill.NORTHWEST));

		sessionID = new JTextField ("1");

		joinSessionSection.add (new JLabel ("Session ID:"),			getUtils ().createConstraintsNoFill (0, 0, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		joinSessionSection.add (sessionID,									getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		joinSessionSection.add (new JButton (joinSessionAction),	getUtils ().createConstraintsNoFill (1, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		
		// Chat box
		final JPanel chatSection = new JPanel ();
		addTextBorder (chatSection, "Chat");
		chatSection.setLayout (new GridBagLayout ());
		contentPane.add (chatSection, getUtils ().createConstraintsNoFill (3, 1, 1, 1, INSET, GridBagConstraintsNoFill.NORTHWEST));

		chatMessage = new JTextField ("Hi!");

		// Put a filler at the top to make the text field big enough
		chatSection.add (Box.createRigidArea (new Dimension (160, 0)),	getUtils ().createConstraintsNoFill (0, 0, 1, 1, NO_INSET, GridBagConstraintsNoFill.CENTRE));
		chatSection.add (chatMessage,													getUtils ().createConstraintsHorizontalFill (0, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		chatSection.add (new JButton (chatAction),									getUtils ().createConstraintsNoFill (0, 2, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		
		// Load game box
		final JPanel loadGameSection = new JPanel ();
		addTextBorder (loadGameSection, "Load Game");
		loadGameSection.setLayout (new GridBagLayout ());
		contentPane.add (loadGameSection, getUtils ().createConstraintsNoFill (4, 0, 1, 2, INSET, GridBagConstraintsNoFill.NORTHWEST));

		savedGameID = new JTextField ("1");
		savedGameFilename = new JTextField ();
		
		loadGameSection.add (new JButton (browseSavedGamesAction),	getUtils ().createConstraintsHorizontalFill (1, 0, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		loadGameSection.add (new JLabel ("Saved game ID:"),					getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		loadGameSection.add (savedGameID,											getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		loadGameSection.add (new JButton (deleteSavedGameAction),		getUtils ().createConstraintsHorizontalFill (1, 2, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		loadGameSection.add (new JButton (browseSavePointsAction),		getUtils ().createConstraintsHorizontalFill (1, 3, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		loadGameSection.add (new JLabel ("Filename:"),							getUtils ().createConstraintsNoFill (0, 4, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		loadGameSection.add (savedGameFilename,									getUtils ().createConstraintsHorizontalFill (1, 4, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		loadGameSection.add (new JButton (loadGameAction),					getUtils ().createConstraintsHorizontalFill (1, 5, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		
		// Set initial state of form
		enableOrDisableButtons ();
		
		// Event handlers
		getEventListeners ().add (new MultiplayerSessionClientEvent ()
		{
			/**
			 * Event triggered when we server successfully creates an account for us
			 * 
			 * @param aPlayerID Player ID allocated to our new account
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void accountCreated (final int aPlayerID) throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Account created, you were allocated player ID " + aPlayerID + ", Swing thread = " + SwingUtilities.isEventDispatchThread ());
			}

			/**
			 * Event triggered after we successfully log in.
			 * 
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void loggedIn () throws JAXBException, XMLStreamException, IOException
			{
				playerID.setText (Integer.valueOf (getOurPlayerID ()).toString ());
				addToTextArea ("Logged in successfully as player ID " + getOurPlayerID () + ", Swing thread = " + SwingUtilities.isEventDispatchThread ());
				enableOrDisableButtons ();
			}
			
			/**
			 * Event triggered as we successfully log out - event is
			 * triggered just before playerID and session variables are cleared.
			 * 
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void loggedOut () throws JAXBException, XMLStreamException, IOException
			{
				playerID.setText ("0");
				addToTextArea ("Logged out successfully");
				enableOrDisableButtons ();
			}
			
			/**
			 * Forcibly logged out by another client logging in with our account.  This event is triggered just before
			 * the playerID and session variables are cleared.
			 * 
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void kickedByAnotherLogin () throws JAXBException, XMLStreamException, IOException
			{
				playerID.setText ("0");
				addToTextArea ("We were kicked because another connection logged on with our player ID");
				enableOrDisableButtons ();
			}
			
			/**
			 * Event triggered when server tells us which sessions we can join
			 * 
			 * @param sessions List of sessions the server is telling us we can join
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void receivedSessionList (final List<SessionAndPlayerDescriptions> sessions)
				throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Received session list:");

				for (final SessionAndPlayerDescriptions spd : sessions)
				{
					String playerList = null;
					for (final PlayerDescription pd : spd.getPlayer ())
					{
						if (playerList == null)
							playerList = pd.getPlayerName ();
						else
							playerList = playerList + ", " + pd.getPlayerName ();
						
						// Prove we get proper class of player description
						final DemoPlayerDescription dpd = (DemoPlayerDescription) pd;
						playerList = playerList + (dpd.isIsFemale () ? "(F)" : "(M)");
						
						if (pd.getPlayerType () != PlayerType.HUMAN)
							playerList = playerList + "(" + pd.getPlayerType () + ")";
					}

					// Prove we get proper class of session description
					final DemoSessionDescription dsd = (DemoSessionDescription) spd.getSessionDescription ();
					String text;
					if (playerList == null)
						text = dsd.getSessionID () + ": \"" + dsd.getSessionName () + "\" containing no players";
					else
						text = dsd.getSessionID () + ": \"" + dsd.getSessionName () + "\" containing: " + playerList;
					
					text = text + "(" + dsd.getMapWidth () + "x" + dsd.getMapHeight () + ")";
						
					addToTextArea ("  " + text);
				}
				
				addToTextArea ("End of session list");
			}

			/**
			 * Event triggered when we successfully join a session
			 * 
			 * @param reason The type of session we've joined into
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void joinedSession (final JoinSuccessfulReason reason) throws JAXBException, XMLStreamException, IOException
			{
				// Prove we get proper class of player description
				String playerList = null;
				for (final PlayerPublicDetails player : getPlayers ())
				{
					if (playerList == null)
						playerList = "";
					else
						playerList = playerList + ", ";

					final DemoPlayerDescription dpd = (DemoPlayerDescription) player.getPlayerDescription ();
					playerList = playerList + dpd.getPlayerName () + (dpd.isIsFemale () ? "(F)" : "(M)") +
						" (ID " + dpd.getPlayerID () + ", " +
						player.getPersistentPlayerPublicKnowledge () + ", " + player.getTransientPlayerPublicKnowledge () + ")";
				}

				// Prove we get proper class of session description
				final DemoSessionDescription dsd = (DemoSessionDescription) getSessionDescription ();
				addToTextArea ("Successfully joined session " + reason + " " + dsd.getSessionID () + ": \"" + dsd.getSessionName () + "\" (" + dsd.getMapWidth () + "x" + dsd.getMapHeight () + ")");
				addToTextArea ("  General public knowledge: " + getGeneralPublicKnowledge ());
				addToTextArea ("  Our persistent player private knowledge: " + getOurPersistentPlayerPrivateKnowledge ());
				addToTextArea ("  Our transient player private knowledge: " + getOurTransientPlayerPrivateKnowledge ());
				addToTextArea ("  Players now in session: " + playerList);

				enableOrDisableButtons ();
			}

			/**
			 * Event triggered when somebody else joins the session we're already in.  This only gets sent if the player is genuinely new
			 * to the session - if they disconnect and rejoin back into their original slot, this message will not be sent.
			 * 
			 * @param aPlayerID Player who joined
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void additionalPlayerJoined (final int aPlayerID) throws JAXBException, XMLStreamException, IOException
			{
				final PlayerPublicDetails player = getMultiplayerSessionUtils ().findPlayerWithID (getPlayers (), aPlayerID);
				
				final DemoPlayerDescription dpd = (DemoPlayerDescription) player.getPlayerDescription ();
				addToTextArea ("Additional player joined: " + dpd.getPlayerName () + (dpd.isIsFemale () ? "(F)" : "(M)") +
					" (ID " + dpd.getPlayerID () + ", " +
					player.getPersistentPlayerPublicKnowledge () + ", " + player.getTransientPlayerPublicKnowledge () + ")");
			}

			/**
			 * Event triggered when somebody leaves the session we're in, note it could be us leaving.
			 * Event is triggered before player is removed from the list, so if it is us leaving we have time to tidy up the session while we can still access it.
			 * 
			 * @param aPlayerID Player who left
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void beforePlayerLeft (@SuppressWarnings ("unused") final int aPlayerID) throws JAXBException, XMLStreamException, IOException
			{
			}

			/**
			 * Event triggered when somebody leaves the session we're in, note it could be us leaving.
			 * Event is triggered after player is removed from the list, and if us leaving, session description and others are already blanked out.
			 * 
			 * @param aPlayerID Player who left
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void afterPlayerLeft (final int aPlayerID) throws JAXBException, XMLStreamException, IOException
			{
				final PlayerPublicDetails player = getMultiplayerSessionUtils ().findPlayerWithID (getPlayers (), aPlayerID);
				
				addToTextArea (player.getPlayerDescription ().getPlayerName () + " left our session");
				enableOrDisableButtons ();
			}

			/**
			 * Event triggered when the server closes down a session when we're still in it.  This is triggered just before all the
			 * session variables are nulled out.
			 * 
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void sessionEnding () throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Server ended our session");
				enableOrDisableButtons ();
			}

			/**
			 * Event triggered when server tells us what saved games we have stored
			 * 
			 * @param savedGames List of saved games the server is telling us we can reload
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void receivedSavedGamesList (final List<SavedGameSession> savedGames)
				throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Received saved games list:");
				
				for (final SavedGameSession savedGame : savedGames)
				{
					final DemoSessionDescription dsd = (DemoSessionDescription) savedGame.getSessionDescription ();
					addToTextArea (savedGame.getSessionDescription ().getSavedGameID () + ", last saved at " +
						savedGame.getLatestSavedAt () + " with identifier " + savedGame.getLatestSavedGameIdentifier () + ": " + dsd);
				}
				
				if (savedGames.size () > 0)
					savedGameID.setText (savedGames.get (0).getSessionDescription ().getSavedGameID ().toString ());
				
				addToTextArea ("End of saved games list");
			}

			/**
			 * Event triggered when server tells us what save points one of our saved games has stored
			 * 
			 * @param savePoints List of save points within a saved game that the server is telling us we can join
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void receivedSavePointsList (final List<SavedGamePoint> savePoints)
				throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Received save points list:");

				for (final SavedGamePoint savePoint : savePoints)
					addToTextArea ("Filename \"" + savePoint.getSavedGameFilename () + "\", saved at " +
						savePoint.getSavedAt () + " with identifier " + savePoint.getSavedGameIdentifier ());

				if (savePoints.size () > 0)
					savedGameFilename.setText (savePoints.get (0).getSavedGameFilename ());
				
				addToTextArea ("End of save points list");
			}
			
			/**
			 * Event triggered when the server successfully deleted a saved game that we requested.
			 * 
			 * @throws JAXBException Typically used if there is a problem sending a reply back to the server
			 * @throws XMLStreamException Typically used if there is a problem sending a reply back to the server
			 * @throws IOException Can be used for more general types of processing failure
			 */
			@Override
			public final void savedGameDeleted () throws JAXBException, XMLStreamException, IOException
			{
				addToTextArea ("Saved game deleted");
			}
			
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
			@Override
			public final void failed (final CreateAccountFailedReason createAccountFailed,
				final LoginFailedReason loginFailed, final LogoutFailedReason logoutFailed, final RequestSessionListFailedReason requestSessionListFailed,
				final JoinFailedReason joinFailed, final LeaveSessionFailedReason leaveSessionFailed, final BrowseSavedGamesFailedReason browseSavedGamesFailed,
				final BrowseSavePointsFailedReason browseSavePointsFailed, final DeleteSavedGameFailedReason deleteSavedGameFailed, final LoadGameFailedReason loadGameFailed)
				throws JAXBException, XMLStreamException, IOException
			{
				if (createAccountFailed != null)
					addToTextArea ("Account creation failed for reason " + createAccountFailed);
				else if (loginFailed != null)
					addToTextArea ("Login failed for reason " + loginFailed);
				else if (logoutFailed != null)
					addToTextArea ("Logout failed for reason " + logoutFailed);
				else if (requestSessionListFailed != null)
					addToTextArea ("Request session list failed for reason " + requestSessionListFailed);
				else if (joinFailed != null)
					addToTextArea ("Join failed for reason " + joinFailed);
				else if (leaveSessionFailed != null)
					addToTextArea ("Leave session failed for reason " + leaveSessionFailed);
				else if (browseSavedGamesFailed != null)
					addToTextArea ("Browse saved games failed for reason " + browseSavedGamesFailed);
				else if (browseSavePointsFailed != null)
					addToTextArea ("Browse save points failed for reason " + browseSavePointsFailed);
				else if (deleteSavedGameFailed != null)
					addToTextArea ("Delete saved game failed for reason " + deleteSavedGameFailed);
				else if (loadGameFailed != null)
					addToTextArea ("Load game failed for reason " + loadGameFailed);
			}
			
			/**
			 * Event triggered when client disconnects, or fails to connect in the first place.
			 * 
			 * @param e Exception that caused the disconnection, or null if the disconnection was controlled (sent closing conversation tag)
			 */
			@Override
			public final void disconnected (final Exception e)
			{
				addToTextArea ("Disconnected with exception: " + e);
			}
		});

		// Show frame
		frame.setContentPane (contentPane);
		frame.pack ();
		frame.setVisible (true);
	}

	/**
	 * Enables or disables buttons depending on whether we are connected and whether we are in a session
	 */
	final void enableOrDisableButtons ()
	{
		connectAction.setEnabled (getServerConnection () == null);
		disconnectAction.setEnabled (getServerConnection () != null);
		createAccountAction.setEnabled (getServerConnection () != null);

		loginAction.setEnabled ((getServerConnection () != null) && (playerID.getText ().equals ("0")));
		logoutAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		requestSessionListAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		newSessionAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		joinSessionAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		browseSavedGamesAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		browseSavePointsAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		deleteSavedGameAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));
		loadGameAction.setEnabled ((getServerConnection () != null) && (!playerID.getText ().equals ("0")));

		leaveAction.setEnabled (getSessionDescription () != null);
		chatAction.setEnabled (getSessionDescription () != null);
		updateHumanPlayerToAiAction.setEnabled (getSessionDescription () != null);
	}

	/**
	 * @param text Text to add to the output area on the panel
	 */
	final void addToTextArea (final String text)
	{
		textArea.setText (textArea.getText () + System.lineSeparator () + text);
	}

	/**
	 * @param panel Panel to add a visible frame around, like a TPanel in Delphi
	 * @param text Text to write onto the frame border
	 */
	private final void addTextBorder (final JPanel panel, final String text)
	{
		panel.setBorder (BorderFactory.createTitledBorder
			(BorderFactory.createEtchedBorder (EtchedBorder.LOWERED), text,
			 TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, panel.getFont ().deriveFont (Font.BOLD)));
	}
	
	/**
	 * @return UI manager helper
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param helper UI manager helper
	 */
	public final void setUtils (final NdgUIUtils helper)
	{
		utils = helper;
	}

	/**
	 * @return Session utils
	 */
	public final MultiplayerSessionUtils getMultiplayerSessionUtils ()
	{
		return multiplayerSessionUtils;
	}

	/**
	 * @param util Session utils
	 */
	public final void setMultiplayerSessionUtils (final MultiplayerSessionUtils util)
	{
		multiplayerSessionUtils = util;
	}
}