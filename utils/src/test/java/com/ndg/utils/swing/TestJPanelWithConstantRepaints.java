package com.ndg.utils.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the JPanelWithConstantRepaints class
 */
@ExtendWith(MockitoExtension.class)
public final class TestJPanelWithConstantRepaints
{
	/** Number format for outputting FPS */
	private final static NumberFormat formatter = new DecimalFormat ("#0.00");
	
	/**
	 * Tests the JPanelWithConstantRepaints class
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testJPanelWithConstantRepaints () throws Exception
	{
		// Set look and feel
		final NdgUIUtils utils = new NdgUIUtilsImpl ();
		utils.useNimbusLookAndFeel ();
		
		// Set up demo panel
		final List<Long> nanoTimes = new ArrayList<Long> ();
		
		final JPanelWithConstantRepaints panel = new JPanelWithConstantRepaints ()
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				// Draw simple animation
				super.paintComponent (g);
				g.setColor (Color.BLUE);
				
				final long nanos = System.nanoTime ();
				final long animCounter = nanos / 2000000l;
				
				int x = (int) (animCounter % (750l * 2l));
				if (x > 750)
					x = (750 * 2) - x;
				
				int y = (int) (animCounter % (550l * 2l));
				if (y > 550l)
					y = (550 * 2) - y;
				
				g.fillOval (x, y, 50, 50);
				
				// Count FPS
				nanoTimes.add (nanos);
				if (nanoTimes.size () > 11)
					nanoTimes.remove (0);
				
				// Length 11 not 10, because the time is recorded at the same point in each frame,
				// so 10 values only gives us 9 time intervals.
				if (nanoTimes.size () == 11)
				{
					final double tenFramesDuration = nanos - nanoTimes.get (0);
					final double fps = 10000000000d / tenFramesDuration;
					
					g.setColor (Color.WHITE);
					g.drawString (formatter.format (fps), 0, 20);
				}
			}
		};
		
		final Dimension panelSize = new Dimension (800, 600);
		panel.setMinimumSize (panelSize);
		panel.setMaximumSize (panelSize);
		panel.setPreferredSize (panelSize);
		
		panel.setBackground (Color.BLACK);
		
		// Set up dummy frame to display the panel
		final JFrame frame = new JFrame ("testJPanelWithConstantRepaints");
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane (panel);
		frame.pack ();
		frame.setLocationRelativeTo (null);
		
		panel.init ("testJPanelWithConstantRepaints-timer");
		
		frame.setVisible (true);
		Thread.sleep (5000);
		frame.setVisible (false);
	}
}