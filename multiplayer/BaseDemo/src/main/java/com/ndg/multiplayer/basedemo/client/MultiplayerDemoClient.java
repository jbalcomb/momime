package com.ndg.multiplayer.basedemo.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.ndg.multiplayer.base.client.CustomDurationServerToClientMessage;
import com.ndg.multiplayer.base.client.MultiplayerBaseClientImpl;
import com.ndg.multiplayer.basedemo.AddToTotal;
import com.ndg.multiplayer.basedemo.CounterRequest;
import com.ndg.multiplayer.basedemo.DateTimeRequest;
import com.ndg.multiplayer.basedemo.PauseRequest;
import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.actions.MessageDialogAction;

/**
 * Demonstrates interacting via Swing form
 */
public final class MultiplayerDemoClient extends MultiplayerBaseClientImpl
{
	/** Typical inset used for this form layout */
	private final static int INSET = 3;
	
	/** UI manager helper */
	private NdgUIUtils utils;
	
	/** Text area for output type messages */
	private JTextArea textArea;
	
	/** Message that paused the form */
	private CustomDurationServerToClientMessage pausedBy;
	
	/** Unpause action */
	private Action unpauseAction;
	
	/**
	 * Kick off method invoked by spring's init-method; runs in the main app startup thread
	 */
	public final void start ()
	{
		getUtils ().useNimbusLookAndFeel ();
		
		// To be correct, should start up the first Swing frame in the Swing thread
		SwingUtilities.invokeLater (() -> setupFrame ());
	}
	
	/**
	 * Sets up the Swing form; runs in the Swing event dispatcher thread
	 */
	private final void setupFrame ()
	{
		// Actions
		final Action connectAction = new MessageDialogAction ("Connect", (ev) -> connect ());
		
		final Action dateTimeRequestAction = new MessageDialogAction ("Request Date+Time",
			(ev) -> getServerConnection ().sendMessageToServer (new DateTimeRequest ()));
		
		final Action pauseAction = new MessageDialogAction ("Pause",
			(ev) -> getServerConnection ().sendMessageToServer (new PauseRequest ()));
		
		unpauseAction = new MessageDialogAction ("Unpause", (ev) -> finishCustomDurationMessage (pausedBy));
		unpauseAction.setEnabled (false);
		
		final JTextField valueField = new JTextField ("5");
		final Action addToTotalAction = new MessageDialogAction ("Add value to running total", (ev) ->
		{
			final AddToTotal msg = new AddToTotal ();
			msg.setValue (Integer.parseInt (valueField.getText ()));
			getServerConnection ().sendMessageToServer (msg);
		});

		final JCheckBox checkBox = new JCheckBox ();
		
		final Action animAction = new MessageDialogAction ("Anim", (ev) ->
		{
			final CounterRequest msg = new CounterRequest ();
			msg.setNumberOfSeconds (Integer.parseInt (valueField.getText ()));
			msg.setPauseAfterwards (checkBox.isSelected ());
			getServerConnection ().sendMessageToServer (msg);
		});
		
		// Frame
		final JFrame frame = new JFrame ("NdgMultiplayerBaseDemo client");
		frame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		
		final JPanel contentPane = new JPanel ();
		contentPane.setPreferredSize (new Dimension (800, 480));
		contentPane.setLayout (new GridBagLayout ());
		
		textArea = new JTextArea ();
		final GridBagConstraints textAreaC = getUtils ().createConstraintsBothFill (0, 0, 8, 1, INSET);
		textAreaC.weightx = 1;
		textAreaC.weighty = 1;
		contentPane.add (textArea, textAreaC);
		
		contentPane.add (new JButton (connectAction), getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		contentPane.add (new JButton (dateTimeRequestAction), getUtils ().createConstraintsNoFill (1, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		contentPane.add (new JButton (pauseAction), getUtils ().createConstraintsNoFill (2, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		contentPane.add (new JButton (unpauseAction), getUtils ().createConstraintsNoFill (3, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		
		final GridBagConstraints valueFieldC = getUtils ().createConstraintsHorizontalFill (4, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE);
		valueFieldC.weightx = 1;
		contentPane.add (valueField, valueFieldC);
		
		contentPane.add (checkBox, getUtils ().createConstraintsNoFill (5, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		contentPane.add (new JButton (addToTotalAction), getUtils ().createConstraintsNoFill (6, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		contentPane.add (new JButton (animAction), getUtils ().createConstraintsNoFill (7, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		
		frame.setContentPane (contentPane);
		frame.pack ();
		frame.setVisible (true);
	} 
	
	/**
	 * @param text Text to add to the output area on the panel
	 */
	final void addToTextArea (final String text)
	{
		textArea.setText (textArea.getText () + System.lineSeparator () + text); // + System.lineSeparator () + "Swing thread: " + SwingUtilities.isEventDispatchThread ());
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
	 * @return Message that paused the form
	 */
	public final CustomDurationServerToClientMessage getPausedBy ()
	{
		return pausedBy;
	}

	/**
	 * @param paused Message that paused the form
	 */
	public final void setPausedBy (final CustomDurationServerToClientMessage paused)
	{
		pausedBy = paused;
		unpauseAction.setEnabled (paused != null);
	}
}