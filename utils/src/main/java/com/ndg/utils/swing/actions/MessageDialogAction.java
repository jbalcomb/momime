package com.ndg.utils.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of AbstractAction designed to allow lambda expressions to be passed to the constructor
 * and also catch and show checked exceptions in a pop up message box as well as logging them.
 * 
 * e.g. new JButton (new LoggingAction ((ev) -> System.out.println ("Action triggered")));
 */
public final class MessageDialogAction extends AbstractAction
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MessageDialogAction.class);

	/** The action to trigger */
	private final CheckedActionListener action;
	
	/**
	 * @param ac The action to trigger
	 */
	public MessageDialogAction (final CheckedActionListener ac)
	{
		super ();
		action = ac;
	}

	/**
	 * @param name Initial text to show for action
	 * @param ac The action to trigger
	 */
	public MessageDialogAction (final String name, final CheckedActionListener ac)
	{
		super (name);
		action = ac;
	}

	/**
	 * @param name Initial text to show for action
	 * @param icon Initial icon to show for action
	 * @param ac The action to trigger
	 */
	public MessageDialogAction (final String name, final Icon icon, final CheckedActionListener ac)
	{
		super (name, icon);
		action = ac;
	}

	/**
	 * @param ev Swing event that triggered this action
	 */
	@Override
	public final void actionPerformed (final ActionEvent ev)
	{
		try
		{
			action.actionPerformed (ev);
		}
		catch (final Exception e)
		{
			log.error (e, e);
			JOptionPane.showMessageDialog (null, e.toString (), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}