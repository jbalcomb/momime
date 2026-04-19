package com.ndg.utils.swing;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the MouseClickListener class
 */
@ExtendWith(MockitoExtension.class)
public final class TestMouseClickListener
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (TestMouseClickListener.class);
	
	/**
	 * Tests the MouseClickListener class
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testMouseClickListener () throws Exception
	{
		// Set look and feel
		final NdgUIUtils utils = new NdgUIUtilsImpl ();
		utils.useNimbusLookAndFeel ();

		// Set up a panel
		final JPanel panel = new JPanel (new GridBagLayout ());
		
		final Dimension panelSize = new Dimension (400, 300);
		panel.setMinimumSize (panelSize);
		panel.setMaximumSize (panelSize);
		panel.setPreferredSize (panelSize);
		
		// Set up listeners
		final DummyMouseClickListener mouseListener = new DummyMouseClickListener ();
		
		// Set up some buttons
		int x = 0;
		for (final String name : new String [] {"One", "Two", "Three"})
		{
			final JButton button = new JButton (name);
			panel.add (button, utils.createConstraintsNoFill (x, 0, 1, 1, 2, GridBagConstraintsNoFill.CENTRE));
			button.addMouseListener (mouseListener);
			button.addActionListener ((ev) -> log.info ("actionPerformed on " + ev.getSource ()));
			button.setName (name);
			
			x++;
		}
		
		// Set up dummy frame to display the panel
		final JFrame frame = new JFrame ("testMouseClickListener");
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane (panel);
		frame.pack ();
		frame.setLocationRelativeTo (null);

		frame.setVisible (true);
		Thread.sleep (5000);
		frame.setVisible (false);		
	}
	
	/**
	 * Dummy implementation that outputs when each event is triggered
	 */
	private final class DummyMouseClickListener extends MouseClickListener
	{
		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void fixMouseClicked (final MouseEvent ev)
		{
			log.info ("fixMouseClicked on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseClicked (final MouseEvent ev)
		{
			log.info ("unfixedMouseClicked on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseClicked (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mousePressed (final MouseEvent ev)
		{
			log.info ("mousePressed on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mousePressed (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseReleased (final MouseEvent ev)
		{
			log.info ("mouseReleased on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseReleased (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseEntered (final MouseEvent ev)
		{
			log.info ("mouseEntered on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseEntered (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseExited (final MouseEvent ev)
		{
			log.info ("mouseExited on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseExited (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseWheelMoved (MouseWheelEvent ev)
		{
			log.info ("mouseWheelMoved on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseWheelMoved (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseDragged (final MouseEvent ev)
		{
			log.info ("mouseDragged on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseDragged (ev);
		}

		/**
		 * Output text when event is triggered
		 */
		@Override
		public final void mouseMoved (final MouseEvent ev)
		{
			log.info ("mouseMoved on " + ev.getComponent ().getName () + ", right = " + SwingUtilities.isRightMouseButton (ev));
			super.mouseMoved (ev);
		}
	}
}