package com.ndg.multiplayer.client.ui.frames;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.swing.JFrame;

import com.ndg.multiplayer.client.ui.MultiplayerUI;

/**
 * Ancestor used by all of the UI frames.
 */
public abstract class MultiplayerFrameUI extends MultiplayerUI
{
	/** The actual frame */
	private JFrame frame;

	/**
	 * @return The actual frame
	 */
	public final JFrame getFrame ()
	{
		return frame;
	}

	/**
	 * @return Whether this screen is currently displayed or not
	 */
	@Override
	public final boolean isVisible ()
	{
		// If haven't done init yet, then obviously it isn't visible
		return (frame == null) ? false : frame.isVisible ();
	}

	/**
	 * @param v Whether to display or hide this screen
	 * @throws IOException If a resource cannot be found
	 */
	@Override
	public final void setVisible (final boolean v) throws IOException
	{
		GraphicsConfiguration gc = null;

		if (frame == null)
		{
			if (getMonitorNumber () == null)
				frame = new JFrame ();
			else
			{
				gc = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices () [getMonitorNumber ()].getDefaultConfiguration ();
				frame = new JFrame (gc);
			}			

			init ();

			// Build and position the frame
			if (getIconFilename () != null)
				getFrame ().setIconImage (getUtils ().loadImage (getIconFilename ()));
			
			getFrame ().pack ();
			getFrame ().setLocationRelativeTo (null);
		}
		frame.setVisible (v);
		
		if (gc != null)
			frame.setLocation (gc.getBounds ().x + ((gc.getBounds ().width - frame.getWidth ()) / 2), gc.getBounds ().y + ((gc.getBounds ().height - frame.getHeight ()) / 2));
	}
}