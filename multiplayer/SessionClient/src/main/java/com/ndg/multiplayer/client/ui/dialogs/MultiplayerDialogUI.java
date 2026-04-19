package com.ndg.multiplayer.client.ui.dialogs;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Dialog.ModalityType;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.ndg.multiplayer.client.ui.MultiplayerUI;

/**
 * Ancestor used by all of the UI dialogs.
 */
public abstract class MultiplayerDialogUI extends MultiplayerUI
{
	/** Parent frame */
	private JFrame parentFrame;

	/** The actual dialog */
	private JDialog dialog;
	
	/** Whether the dialog box should be modal - so that unit tests can set this to false */
	private boolean modal = true;

	/**
	 * @return The actual dialog
	 */
	public final JDialog getDialog ()
	{
		return dialog;
	}

	/**
	 * @return Whether this screen is currently displayed or not
	 */
	@Override
	public final boolean isVisible ()
	{
		// If haven't done init yet, then obviously it isn't visible
		return (dialog == null) ? false : dialog.isVisible ();
	}

	/**
	 * @param v Whether to display or hide this screen
	 * @throws IOException If a resource cannot be found
	 */
	@Override
	public final void setVisible (final boolean v) throws IOException
	{
		GraphicsConfiguration gc = null;
		
		if (dialog == null)
		{
			if (getMonitorNumber () != null)
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices () [getMonitorNumber ()].getDefaultConfiguration ();
			
			dialog = new JDialog (getParentFrame ());
			init ();

			// Build and position the dialog
			if (getIconFilename () != null)
				getDialog ().setIconImage (getUtils ().loadImage (getIconFilename ()));
			
			getDialog ().pack ();
			getDialog ().setLocationRelativeTo (null);
			
			if (isModal ())
				getDialog ().setModalityType (ModalityType.APPLICATION_MODAL);
		}
		
		if (gc != null)
			dialog.setLocation (gc.getBounds ().x + ((gc.getBounds ().width - dialog.getWidth ()) / 2), gc.getBounds ().y + ((gc.getBounds ().height - dialog.getHeight ()) / 2));
		
		dialog.setVisible (v);
	}

	/**
	 * @return Parent frame
	 */
	public final JFrame getParentFrame ()
	{
		return parentFrame;
	}
	
	/**
	 * @param f Parent frame
	 */
	public final void setParentFrame (final JFrame f)
	{
		parentFrame = f;
	}
	
	/**
	 * @return Whether the dialog box should be modal - so that unit tests can set this to false
	 */
	public final boolean isModal ()
	{
		return modal;
	}
	
	/**
	 * @param value Whether the dialog box should be modal - so that unit tests can set this to false
	 */
	public final void setModal (final boolean value)
	{
		modal = value;
	}
}