package com.ndg.multiplayer.client.ui.frames;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.client.MultiplayerSessionClient;
import com.ndg.multiplayer.client.ui.MultiplayerUI;
import com.ndg.multiplayer.sessionbase.CreateAccount;
import com.ndg.multiplayer.sessionbase.Login;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.actions.MessageDialogAction;

import jakarta.xml.bind.JAXBException;

/**
 * Simple main menu with options to connect to a server and create or join games
 * that can be used as-is for simple games with no special UI requirements, or customized as required
 */
public class SimpleMainMenuUI extends MultiplayerFrameUI
{
	/** Spacing left between the edges of controls and their grid cells */
	protected final static int INSET = 2;

	/** Spacing left inside the panel */
	private final static int BORDER = 6;
	
	/** Multiplayer client */
	private MultiplayerSessionClient client;
	
	/** New game UI; null if it is a very simple game where no information is needed to start up a new game */
	private MultiplayerUI newGameUI;
	
	/** Join game UI; null if it is a game where only a single human player can play with AI players, so no join game UI is needed */
	private MultiplayerUI joinGameUI;
	
	/** Title to give the frame */
	private String title;
	
	/** Connect (and create account / log in) action */
	private Action connectAction;
	
	/** New game action */
	private Action newGameAction;
	
	/** Join game action */
	private Action joinGameAction;
	
	/** Content pane */
	private JPanel contentPane;
	
	/** Server IP address */
	private JTextField ipAddress;
	
	/** Player name */
	private JTextField playerName;
	
	/** Password */
	private JPasswordField password;

	/** Kick existing connection? */
	private JCheckBox kickAccount;
	
	/** New account? */
	private JCheckBox newAccount;
	
	/**
	 * Sets up the frame once all values have been injected
	 * @throws IOException If a resource cannot be found
	 */
	@Override
	protected final void init () throws IOException
	{
		// Controls
		final Dimension textFieldSize = new Dimension (280, 28);
		
		ipAddress = new JTextField ("localhost");
		ipAddress.setMinimumSize (textFieldSize);
		ipAddress.setMaximumSize (textFieldSize);
		ipAddress.setPreferredSize (textFieldSize);
		
		playerName = new JTextField ();
		playerName.setMinimumSize (textFieldSize);
		playerName.setMaximumSize (textFieldSize);
		playerName.setPreferredSize (textFieldSize);
		
		password = new JPasswordField ();
		password.setMinimumSize (textFieldSize);
		password.setMaximumSize (textFieldSize);
		password.setPreferredSize (textFieldSize);
		
		newAccount = new JCheckBox ("This is a new account");
		kickAccount = new JCheckBox ("Kick existing connection");
		
		// Actions
		connectAction = new MessageDialogAction ("Connect", (ev) ->
		{
			getClient ().setServerAddress (ipAddress.getText ());
			getClient ().connect ();
			
			// Then afterConnected () below gets called by the ServerConnection class
		});
		
		newGameAction = new MessageDialogAction ("New Game", (ev) ->
		{
			if (getNewGameUI () != null)
				getNewGameUI ().setVisible (true);
			else
				createNewGame ();
		});
	
		if (getJoinGameUI () != null)
			joinGameAction = new MessageDialogAction ("Join Game", (ev) -> {getJoinGameUI ().setVisible (true);});
		
		// Initialize the frame
		getFrame ().setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		getFrame ().setTitle (getTitle ());

		// Initialize the content pane
		contentPane = new JPanel ();
		getContentPane ().setBorder (BorderFactory.createEmptyBorder (BORDER, BORDER, BORDER, BORDER));

		// Set up layout
		getContentPane ().setLayout (new GridBagLayout ());
		getContentPane ().add (new JLabel ("Server:"), getUtils ().createConstraintsNoFill (0, 0, 2, 1, INSET, GridBagConstraintsNoFill.WEST));
		getContentPane ().add (new JLabel ("Player name:"), getUtils ().createConstraintsNoFill (0, 1, 2, 1, INSET, GridBagConstraintsNoFill.WEST));
		getContentPane ().add (new JLabel ("Password:"), getUtils ().createConstraintsNoFill (0, 2, 2, 1, INSET, GridBagConstraintsNoFill.WEST));

		getContentPane ().add (ipAddress, getUtils ().createConstraintsBothFill (1, 0, 2, 1, INSET));
		getContentPane ().add (playerName, getUtils ().createConstraintsBothFill (1, 1, 2, 1, INSET));
		getContentPane ().add (password, getUtils ().createConstraintsBothFill (1, 2, 2, 1, INSET));

		getContentPane ().add (newAccount, getUtils ().createConstraintsNoFill (1, 3, 2, 1, INSET, GridBagConstraintsNoFill.WEST));
		getContentPane ().add (kickAccount, getUtils ().createConstraintsNoFill (1, 4, 2, 1, INSET, GridBagConstraintsNoFill.WEST));
		
		getContentPane ().add (new JButton (connectAction), getUtils ().createConstraintsNoFill (0, 5, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		getContentPane ().add (new JButton (newGameAction), getUtils ().createConstraintsNoFill (1, 5, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		
		if (joinGameAction != null)
			getContentPane ().add (new JButton (joinGameAction), getUtils ().createConstraintsNoFill (2, 5, 1, 1, INSET, GridBagConstraintsNoFill.WEST));
		
		createAdditionalControls ();
		
		// Ok button should only be enabled once we have enough info
		final DocumentListener documentListener = new DocumentListener ()
		{
			@Override
			public final void insertUpdate (@SuppressWarnings ("unused") final DocumentEvent ev)
			{
				enableActions ();
			}

			@Override
			public final void removeUpdate (@SuppressWarnings ("unused") final DocumentEvent ev)
			{
				enableActions ();
			}

			@Override
			public final void changedUpdate (@SuppressWarnings ("unused") final DocumentEvent ev)
			{
				enableActions ();
			}
		};
		
		ipAddress.getDocument ().addDocumentListener (documentListener);
		playerName.getDocument ().addDocumentListener (documentListener);
		password.getDocument ().addDocumentListener (documentListener);
		
		// All done
		getFrame ().setContentPane (contentPane);
		enableActions ();
	}
	
	/**
	 * Descendant classes can override this to add additional controls to the frame
	 */
	protected void createAdditionalControls ()
	{
	}
	
	/**
	 * Descendant classes must override this if there is no new game form and the new game button creates the game directly
	 * 
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	protected void createNewGame () throws JAXBException, XMLStreamException
	{
	}

	/**
	 * Updates enable/disable status of actions
	 */
	public void enableActions ()
	{
		connectAction.setEnabled ((getClient ().getOurPlayerID () == null) &&
			(!ipAddress.getText ().isEmpty ()) && (!playerName.getText ().isEmpty ()) && (password.getPassword ().length > 0));
		newGameAction.setEnabled (getClient ().getOurPlayerID () != null);
		
		if (joinGameAction != null)
			joinGameAction.setEnabled (getClient ().getOurPlayerID () != null);
	}
	
	/**
	 * Triggered after we successfully make connection to the server, so now need to send credentials
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public final void afterConnected () throws JAXBException, XMLStreamException
	{
		if (newAccount.isSelected ())
		{
			final CreateAccount msg = new CreateAccount ();
			msg.setPlayerName (playerName.getText ());
			msg.setPlayerPassword (new String (password.getPassword ()));
			getClient ().getServerConnection ().sendMessageToServer (msg);
		}
		else
			afterAccountCreated ();
	}
	
	/**
	 * Triggered after we connect and create an account above
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 */
	public final void afterAccountCreated () throws JAXBException, XMLStreamException
	{
		final Login msg = new Login ();
		msg.setPlayerName (playerName.getText ());
		msg.setPlayerPassword (new String (password.getPassword ()));
		msg.setKickExistingConnection (kickAccount.isSelected ());
		getClient ().getServerConnection ().sendMessageToServer (msg);
	}
	
	/**
	 * @return Content pane
	 */
	public final JPanel getContentPane ()
	{
		return contentPane;
	}
	
	/**
	 * @return Multiplayer client
	 */
	public MultiplayerSessionClient getClient ()
	{
		return client;
	}

	/**
	 * @param c Multiplayer client
	 */
	public void setClient (final MultiplayerSessionClient c)
	{
		client = c;
	}

	/**
	 * @return New game UI; null if it is a very simple game where no information is needed to start up a new game
	 */
	public final MultiplayerUI getNewGameUI ()
	{
		return newGameUI;
	}

	/**
	 * @param ui New game UI; null if it is a very simple game where no information is needed to start up a new game
	 */
	public final void setNewGameUI (final MultiplayerUI ui)
	{
		newGameUI = ui;
	}
	
	/**
	 * @return Join game UI; null if it is a game where only a single human player can play with AI players, so no join game UI is needed
	 */
	public final MultiplayerUI getJoinGameUI ()
	{
		return joinGameUI;
	}
	
	/**
	 * @param ui Join game UI; null if it is a game where only a single human player can play with AI players, so no join game UI is needed
	 */
	public final void setJoinGameUI (final MultiplayerUI ui)
	{
		joinGameUI = ui;
	}

	/**
	 * @return Title to give the frame
	 */
	public final String getTitle ()
	{
		return title;
	}

	/**
	 * @param t Title to give the frame
	 */
	public final void setTitle (final String t)
	{
		title = t;
	}
}