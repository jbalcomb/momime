package momime.client.ui.frames;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.xml.bind.Marshaller;
import momime.client.config.MomImeClientConfig;
import momime.client.config.WindowID;
import momime.client.config.WindowPosition;
import momime.client.language.LanguageVariableUIImpl;

/**
 * Ancestor used by all of the UI frames.
 * 
 * The way this basically works is that, we aren't allowed to create/configure Swing components outside of the event dispatcher thread.
 * Spring will start up in the JVM's initial thread, so we can't create Swing components from Spring.  So instead Spring creates these
 * "container" objects and injects any resources like fonts that we might need, and injects the Language XML holder.
 * 
 * At some later point, the app then calls setVisible (true) for the first time, which must be done from the event dispatcher thread, at which
 * point init () gets called to create all the Swing components.  setVisible is done in such a way that the screen can be subsequently hidden
 * and then redisplayed, and the Swing components won't need to be recreated.
 */
public abstract class MomClientFrameUI extends LanguageVariableUIImpl
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MomClientFrameUI.class);
	
	/** The actual frame */
	private JFrame frame;

	/** Whether clicks close the form (can't use this on prototype forms, since they'll only close with setVisible (false) and won't be tidied up properly) */
	private boolean closeOnClick;
	
	/** Enum value representing this window; null if its position isn't recorded in config file (for example scope=prototype windows) */
	private WindowID windowID;
	
	/** Whether the window being shown and hidden is recorded in the config file; only relevant if getWindowID does not return null */
	private boolean persistVisibility = true;

	/** Used when game is closing down and all windows are being hidden, so we don't record that hide into the config file */
	private boolean ignoreNextHide;
	
	/** Client config, containing various overland map settings */
	private MomImeClientConfig clientConfig;
	
	/** Marshaller for saving client config */
	private Marshaller clientConfigMarshaller;
	
	/** Location to save updated client config */
	private String clientConfigLocation;

	/**
	 * This is package-private so it can be accessed for setLocationRelativeTo () methods
	 * @return The actual frame
	 */
	final JFrame getFrame ()
	{
		return frame;
	}

	/**
	 * @return Whether this screen is currently displayed or not
	 */
	public final boolean isVisible ()
	{
		// If haven't done init yet, then obviously it isn't visible
		return (frame == null) ? false : frame.isVisible ();
	}
	
	/**
	 * @param v Whether to display or hide this screen
	 * @throws IOException If a resource cannot be found
	 */
	public final void setVisible (final boolean v) throws IOException
	{
		if (frame == null)
		{
			frame = new JFrame ();
			init ();
			languageChanged ();
			getLanguageChangeMaster ().addLanguageChangeListener (this);

			// If the preferred content size won't fit on the current screen (e.g. with -Dsun.java2d.uiScale>1
			// on a small display), force the frame to be decorated and resizable, and cap the content pane
			// size. Otherwise the title bar + window controls end up off-screen and the user has no way to
			// move or close the window.
			fitFrameToScreen ();

			// If the window isn't resizeable, then hide the window border
			// but this means it has no title bar to enable it to be moved, so also set up dragging the background for it
			if (!frame.isResizable ())
			{
				frame.setUndecorated (true);
				
				final MouseAdapter undecoratedFrameMover = new MouseAdapter ()
				{
					/** The location the mouse was at when it was initially clicked */
					private Point mouseStart;

					/** The location the frame was at when the mouse was initially clicked */
					private Point frameStart;
					
					/**
					 * Record the current position of the mouse and frame, so we know where to drag from
					 */
					@Override
					public final void mousePressed (final MouseEvent e)
					{
						mouseStart = e.getLocationOnScreen ();
						frameStart = frame.getLocation ();
					}

					/**
					 * Update the position of the frame based on how far the mouse has moved
					 */
					@Override
					public final void mouseDragged (final MouseEvent e)
					{
						if (frameStart != null)
						{
							final Point mouseNow = e.getLocationOnScreen ();
							final Point frameNow = new Point (frameStart.x + mouseNow.x - mouseStart.x, frameStart.y + mouseNow.y - mouseStart.y);
							frame.setLocation (frameNow);
						}
					}
					
					/**
					 * Maybe close the form with a mouse click
					 */
					@Override
					public final void mouseClicked (@SuppressWarnings ("unused") final MouseEvent ev)
					{
						if (isCloseOnClick ())
							try
							{
								setVisible (false);
							}
							catch (final IOException e)
							{
								log.error (e, e);
							}
					}
				};
				
				frame.addMouseListener (undecoratedFrameMover);
				frame.addMouseMotionListener (undecoratedFrameMover);
			}

			// Build and position the frame
			getFrame ().setIconImage (getUtils ().loadImage ("/momime.client.graphics/ui/backgrounds/frameIcon.png"));
			getFrame ().pack ();
			getFrame ().setLocationRelativeTo (null);
			
			if ((getWindowID () != null) && (getClientConfig () != null))
			{
				// Restore window back to its saved position
				final WindowPosition windowPos = findWindowPosition ();
				if (windowPos != null)
				{
					getFrame ().setLocation (windowPos.getLeft (), windowPos.getTop ());
					
					if ((getFrame ().isResizable ()) && (windowPos.getWidth () != null) && (windowPos.getHeight () != null))
						getFrame ().setSize (windowPos.getWidth (), windowPos.getHeight ());
				}
				
				// Record changes to the frame in the config file
				getFrame ().addComponentListener (new ComponentListener ()
				{
					@Override
					public final void componentResized (@SuppressWarnings ("unused") final ComponentEvent e)
					{
						if (getFrame ().isResizable ())
						{
							final WindowPosition pos = findOrAddWindowPosition ();
							pos.setWidth (getFrame ().getWidth ());
							pos.setHeight (getFrame ().getHeight ());
							saveConfigFile ();
						}
					}

					@Override
					public final void componentMoved (@SuppressWarnings ("unused") final ComponentEvent e)
					{
						final WindowPosition pos = findOrAddWindowPosition ();
						pos.setLeft (getFrame ().getLocation ().x);
						pos.setTop (getFrame ().getLocation ().y);
						saveConfigFile ();
					}

					@Override
					public final void componentShown (@SuppressWarnings ("unused") final ComponentEvent e)
					{
						if (isPersistVisibility ())
						{
							final WindowPosition pos = findOrAddWindowPosition ();
							pos.setVisible (true);
							saveConfigFile ();
						}
					}

					@Override
					public final void componentHidden (@SuppressWarnings ("unused") final ComponentEvent e)
					{
						if (isPersistVisibility ())
						{
							if (ignoreNextHide)
								ignoreNextHide = false;
							else
							{
								final WindowPosition pos = findOrAddWindowPosition ();
								pos.setVisible (false);
								saveConfigFile ();
							}
						}
					}
				});
			}
		}
		frame.setVisible (v);
	}
	
	/**
	 * If the preferred content size won't fit on the primary screen, wrap the content pane in a
	 * scaling JLayer so it shrinks uniformly (preserving aspect) to the limit of the horizontal or
	 * vertical desktop resolution. Mouse/wheel events are re-dispatched through the inverse scale
	 * so hit-testing still hits the original components.
	 */
	private final void fitFrameToScreen ()
	{
		GraphicsConfiguration gc = frame.getGraphicsConfiguration ();
		if (gc == null)
			gc = GraphicsEnvironment.getLocalGraphicsEnvironment ().getDefaultScreenDevice ().getDefaultConfiguration ();

		// Normalize to logical pixels (the same units Swing uses for component sizes). Some JDK
		// versions/platforms return device pixels from getBounds()/getScreenInsets() when
		// sun.java2d.uiScale is set; dividing by the GC's transform scale gives logical pixels in
		// every case. When uiScale is unset the transform scale is 1.0 and this is a no-op.
		final AffineTransform tx = gc.getDefaultTransform ();
		final double sx = tx.getScaleX () > 0 ? tx.getScaleX () : 1.0;
		final double sy = tx.getScaleY () > 0 ? tx.getScaleY () : 1.0;

		final Rectangle screenBounds = gc.getBounds ();
		final Insets screenInsets = Toolkit.getDefaultToolkit ().getScreenInsets (gc);

		// If getBounds() already returns logical pixels (Linux/Mac, some JDK versions on Windows),
		// the screen bounds will be much smaller than the toolkit screen size — heuristically
		// detect this by comparing against Toolkit.getScreenSize.
		final Dimension toolkitScreen = Toolkit.getDefaultToolkit ().getScreenSize ();
		final boolean boundsAreLogical = (Math.abs (screenBounds.width - toolkitScreen.width) < 4)
			&& (Math.abs (screenBounds.height - toolkitScreen.height) < 4)
			&& (sx > 1.0);
		final double divX = boundsAreLogical ? 1.0 : sx;
		final double divY = boundsAreLogical ? 1.0 : sy;

		final int logicalScreenW = (int) (screenBounds.width  / divX);
		final int logicalScreenH = (int) (screenBounds.height / divY);
		final int logicalInsetL  = (int) (screenInsets.left   / divX);
		final int logicalInsetR  = (int) (screenInsets.right  / divX);
		final int logicalInsetT  = (int) (screenInsets.top    / divY);
		final int logicalInsetB  = (int) (screenInsets.bottom / divY);
		final int availW = logicalScreenW - logicalInsetL - logicalInsetR;
		final int availH = logicalScreenH - logicalInsetT - logicalInsetB;

		// Reserve room for a title bar (~32px) and side/bottom borders in case the frame is decorated
		final int decorationH = 40;
		final int decorationW = 16;
		final int maxContentW = availW - decorationW;
		final int maxContentH = availH - decorationH;

		final Container content = frame.getContentPane ();
		final Dimension pref = content.getPreferredSize ();
		final int prefW = (pref != null) ? pref.width : 0;
		final int prefH = (pref != null) ? pref.height : 0;

		log.info ("fitFrameToScreen " + getClass ().getSimpleName ()
			+ ": pref=" + prefW + "x" + prefH
			+ " gcBounds=" + screenBounds.width + "x" + screenBounds.height
			+ " insets=L" + screenInsets.left + "/R" + screenInsets.right + "/T" + screenInsets.top + "/B" + screenInsets.bottom
			+ " gcScale=" + sx + "x" + sy
			+ " toolkitScreen=" + toolkitScreen.width + "x" + toolkitScreen.height
			+ " boundsAreLogical=" + boundsAreLogical
			+ " avail=" + availW + "x" + availH
			+ " maxContent=" + maxContentW + "x" + maxContentH);

		if ((prefW <= 0) || (prefH <= 0))
			return;

		if ((prefW <= maxContentW) && (prefH <= maxContentH))
			return;

		final double scale = Math.min ((double) maxContentW / prefW, (double) maxContentH / prefH);
		if (scale >= 1.0)
			return;

		if (!(content instanceof JComponent))
		{
			log.warn ("Cannot scale frame " + getClass ().getSimpleName () + ": content pane is not a JComponent");
			return;
		}

		final JComponent view = (JComponent) content;
		view.setPreferredSize (new Dimension (prefW, prefH));
		frame.setContentPane (new ScalingHostPanel (view, scale));

		log.info ("Scaling frame " + getClass ().getSimpleName () + " by " + String.format ("%.3f", scale)
			+ " to fit content " + prefW + "x" + prefH + " into " + maxContentW + "x" + maxContentH);
	}

	/**
	 * Content-pane wrapper that hosts a {@link JLayer} sized at its view's natural (unscaled)
	 * dimensions, while reporting a scaled preferred size to its parent and applying the uniform
	 * scale during paint. Mouse coordinate translation is handled by {@link ScalingLayerUI}.
	 */
	private static final class ScalingHostPanel extends javax.swing.JPanel
	{
		private static final long serialVersionUID = 1L;

		private final double scale;
		private final JComponent view;
		private final JLayer<JComponent> layer;

		ScalingHostPanel (final JComponent v, final double s)
		{
			super (null);
			this.scale = s;
			this.view = v;
			this.layer = new JLayer<> (v, new ScalingLayerUI (s));
			setOpaque (false);
			add (layer);
		}

		private Dimension scaled (final Dimension d)
		{
			if (d == null)
				return null;
			return new Dimension ((int) Math.ceil (d.width * scale), (int) Math.ceil (d.height * scale));
		}

		@Override
		public Dimension getPreferredSize ()
		{
			return scaled (view.getPreferredSize ());
		}

		@Override
		public Dimension getMinimumSize ()
		{
			return scaled (view.getMinimumSize ());
		}

		@Override
		public void doLayout ()
		{
			// JLayer (and the view inside it) is laid out at its natural unscaled size; the scale
			// transform in paint() shrinks it visually to fit our (smaller) bounds.
			final Dimension p = view.getPreferredSize ();
			layer.setBounds (0, 0, p.width, p.height);
		}

		@Override
		public void paint (final Graphics g)
		{
			final Graphics2D g2 = (Graphics2D) g.create ();
			g2.scale (scale, scale);
			super.paint (g2);
			g2.dispose ();
		}
	}

	/**
	 * LayerUI used purely for mouse event interception so children receive coordinates in their
	 * natural (unscaled) space. The visual scale itself is applied by {@link ScalingHostPanel};
	 * we don't apply it here as well or content would be scaled twice.
	 */
	private static final class ScalingLayerUI extends LayerUI<JComponent>
	{
		private static final long serialVersionUID = 1L;

		private final double scale;

		/** Re-entry guard: when we re-dispatch an event with translated coordinates, JLayer's
		 * AWTEventListener fires again on the new event. Without this, every mouse event
		 * recurses until the stack overflows. */
		private boolean redispatching;

		ScalingLayerUI (final double s)
		{
			this.scale = s;
		}

		@Override
		public void installUI (final JComponent c)
		{
			super.installUI (c);
			((JLayer<?>) c).setLayerEventMask (
				AWTEvent.MOUSE_EVENT_MASK |
				AWTEvent.MOUSE_MOTION_EVENT_MASK |
				AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		}

		@Override
		public void uninstallUI (final JComponent c)
		{
			((JLayer<?>) c).setLayerEventMask (0);
			super.uninstallUI (c);
		}

		@Override
		protected void processMouseEvent (final MouseEvent e, final JLayer<? extends JComponent> l)
		{
			redispatch (e, l);
		}

		@Override
		protected void processMouseMotionEvent (final MouseEvent e, final JLayer<? extends JComponent> l)
		{
			redispatch (e, l);
		}

		@Override
		protected void processMouseWheelEvent (final MouseWheelEvent e, final JLayer<? extends JComponent> l)
		{
			redispatch (e, l);
		}

		private void redispatch (final MouseEvent e, final JLayer<? extends JComponent> l)
		{
			if (redispatching || e.isConsumed ())
				return;

			final Component view = l.getView ();
			if (view == null)
				return;

			// Translate coordinates from the event source into the JLayer's space, then divide by
			// scale to get the position in the unscaled view, then convert to the deepest target.
			final Point inLayer = SwingUtilities.convertPoint (e.getComponent (), e.getPoint (), l);
			final int viewX = (int) Math.round (inLayer.x / scale);
			final int viewY = (int) Math.round (inLayer.y / scale);

			Component target = SwingUtilities.getDeepestComponentAt (view, viewX, viewY);
			if (target == null)
				target = view;

			final Point targetPt = SwingUtilities.convertPoint (view, viewX, viewY, target);

			final MouseEvent newEvent;
			if (e instanceof MouseWheelEvent we)
				newEvent = new MouseWheelEvent (target, we.getID (), we.getWhen (), we.getModifiersEx (),
					targetPt.x, targetPt.y, we.getXOnScreen (), we.getYOnScreen (),
					we.getClickCount (), we.isPopupTrigger (),
					we.getScrollType (), we.getScrollAmount (), we.getWheelRotation ());
			else
				newEvent = new MouseEvent (target, e.getID (), e.getWhen (), e.getModifiersEx (),
					targetPt.x, targetPt.y, e.getXOnScreen (), e.getYOnScreen (),
					e.getClickCount (), e.isPopupTrigger (), e.getButton ());

			redispatching = true;
			try
			{
				target.dispatchEvent (newEvent);
			}
			finally
			{
				redispatching = false;
			}
			e.consume ();
		}
	}

	/**
	 * @return Window position entry from config file for this frame, or null if not found
	 */
	private final WindowPosition findWindowPosition ()
	{
		return getClientConfig ().getWindowPosition ().stream ().filter (p -> p.getWindowID () == getWindowID ()).findAny ().orElse (null);
	}
	
	/**
	 * @return Window position entry from config file for this frame
	 */
	private final WindowPosition findOrAddWindowPosition ()
	{
		WindowPosition pos = getClientConfig ().getWindowPosition ().stream ().filter (p -> p.getWindowID () == getWindowID ()).findAny ().orElse (null);
		if (pos == null)
		{
			pos = new WindowPosition ();
			pos.setWindowID (getWindowID ());
			getClientConfig ().getWindowPosition ().add (pos);
		}
		return pos;
	}

	/**
	 * After any change to the config options, we resave out the config XML immediately
	 */
	final void saveConfigFile ()
	{
		if (getClientConfigLocation () != null)
			try
			{
				getClientConfigMarshaller ().marshal (getClientConfig (), new File (getClientConfigLocation ()));
			}
			catch (final Exception e)
			{
				log.error (e, e);
			}
	}
	
	/**
	 * Called when game is closing down; hides the window, but doesn't record the fact that its hidden in the config file
	 */
	public final void closedown ()
	{
		if ((frame != null) && (frame.isVisible ()))
		{
			if (isPersistVisibility ())
				ignoreNextHide = true;
			
			frame.setVisible (false);
		}
	}
	
	/**
	 * @return Whether clicks close the form
	 */
	final boolean isCloseOnClick ()
	{
		return closeOnClick;
	}

	/**
	 * @param close Whether clicks close the form
	 */
	final void setCloseOnClick (final boolean close)
	{
		closeOnClick = close;
	}
	
	/**
	 * @return Enum value representing this window; null if its position isn't recorded in config file (for example scope=prototype windows)
	 */
	final WindowID getWindowID ()
	{
		return windowID;
	}

	/**
	 * @param w Enum value representing this window
	 */
	final void setWindowID (final WindowID w)
	{
		windowID = w;
	}
	
	/**
	 * @return Whether the window being shown and hidden is recorded in the config file; only relevant if getWindowID does not return null
	 */
	final boolean isPersistVisibility ()
	{
		return persistVisibility;
	}

	/**
	 * @param v Whether the window being shown and hidden is recorded in the config file; only relevant if getWindowID does not return null
	 */
	final void setPersistVisibility (final boolean v)
	{
		persistVisibility = v;
	}

	/**
	 * @return Client config, containing various overland map settings
	 */	
	public final MomImeClientConfig getClientConfig ()
	{
		return clientConfig;
	}

	/**
	 * @param config Client config, containing various overland map settings
	 */
	public final void setClientConfig (final MomImeClientConfig config)
	{
		clientConfig = config;
	}

	/**
	 * @return Marshaller for saving client config
	 */
	public final Marshaller getClientConfigMarshaller ()
	{
		return clientConfigMarshaller;
	}

	/**
	 * @param marsh Marshaller for saving client config
	 */
	public final void setClientConfigMarshaller (final Marshaller marsh)
	{
		clientConfigMarshaller = marsh;
	}

	/**
	 * @return Location to save updated client config
	 */
	public final String getClientConfigLocation ()
	{
		return clientConfigLocation;
	}

	/**
	 * @param loc Location to save updated client config
	 */
	public final void setClientConfigLocation (final String loc)
	{
		clientConfigLocation = loc;
	}
}