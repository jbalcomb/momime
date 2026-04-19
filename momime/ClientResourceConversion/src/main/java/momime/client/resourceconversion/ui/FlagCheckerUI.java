package momime.client.resourceconversion.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.JPanelWithConstantRepaints;
import com.ndg.utils.swing.ModifiedImageCache;
import com.ndg.utils.swing.ModifiedImageCacheImpl;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.NdgUIUtilsImpl;
import com.ndg.utils.swing.actions.MessageDialogAction;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import momime.common.database.Animation;
import momime.common.database.AnimationFrame;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.CommonXsdResourceResolver;
import momime.common.database.MomDatabase;
import momime.common.database.Unit;
import momime.common.database.UnitCombatAction;

/**
 * UI to display combat figures and their flags to prove the flag images recolour the correct portion of the unit
 */
public final class FlagCheckerUI
{
	/** Location of XML to read/update */
	private final static File XML_LOCATION = new File ("W:\\EclipseHome\\SF\\MoMIME\\Server\\src\\external\\resources\\momime.server.database\\Original Master of Magic 1.31 rules.momime.xml");
	
	/** Sample flag colours to show */
	private final static List<String> FLAG_COLOURS= Arrays.asList ("FF0000", "FF00FF", "FFFF00", "0000FF");
	
	/** Space between components */
	private final static int INSET = 2;
	
	/** Helper methods and constants for creating and laying out Swing components */
	private NdgUIUtils utils;
	
	/** For creating recoloured images */
	private ModifiedImageCache modifiedImageCache;
	
	/** Parsed database XML */
	private MomDatabase db;
	
	/** Map of animations so they can be found faster */
	private Map<String, Animation> animationsMap;
	
	/** Index into unit list of unit to display */
	private int unitIndex;
	
	/** Unit to display */
	private Unit currentUnit;
	
	/** Unit's walk action */
	private UnitCombatAction walkAction;
	
	/** Unit's melee attack action */
	private UnitCombatAction meleeAction;
	
	/** Unit's walk animations in each direction */
	private List<Animation> walkAnimations;

	/** Unit's melee animations in each direction */
	private List<Animation> meleeAnimations;
	
	/** Switch to previous unit */
	private Action previousUnit;
	
	/** Switch to next unit */
	private Action nextUnit;
	
	/** Checkbox to show upscaled units */
	private JCheckBox upscaled;
	
	/**
	 * Sets up UI
	 * @throws Exception If there is a problem
	 */
	public final void init () throws Exception
	{
		getUtils ().useNimbusLookAndFeel ();
		
		// Read XML
		final URL xsdResource = getClass ().getResource (CommonDatabaseConstants.COMMON_XSD_LOCATION);

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (new CommonXsdResourceResolver (DOMImplementationRegistry.newInstance ()));
		
		final Schema schema = schemaFactory.newSchema (xsdResource);

		final Unmarshaller unmarshaller = JAXBContext.newInstance (MomDatabase.class).createUnmarshaller ();		
		unmarshaller.setSchema (schema);

		db = (MomDatabase) unmarshaller.unmarshal (XML_LOCATION);
		animationsMap = db.getAnimation ().stream ().collect (Collectors.toMap (a -> a.getAnimationID (), a -> a));
		
		// Checkbox to show upscaled units
		upscaled = new JCheckBox ("Upscaled");		
		
		// Unit to display
		selectUnitIndex (0);
		
		// Actions
		previousUnit = new MessageDialogAction ("Prev", (ev) ->
		{
			selectUnitIndex (unitIndex - 1);

			// Floating island
			if (currentUnit.getUnitID ().equals ("UN191"))
				selectUnitIndex (unitIndex - 1);
			
			previousUnit.setEnabled (unitIndex > 0);
			nextUnit.setEnabled (true);
		});
		previousUnit.setEnabled (false);

		nextUnit = new MessageDialogAction ("Next", (ev) ->
		{
			selectUnitIndex (unitIndex + 1);

			// Floating island
			if (currentUnit.getUnitID ().equals ("UN191"))
				selectUnitIndex (unitIndex + 1);
			
			previousUnit.setEnabled (true);
			nextUnit.setEnabled ((unitIndex + 1) < db.getUnit ().size ());
		});
		
		upscaled.addActionListener ((ev) -> selectUnitIndex (unitIndex));
		
		// Panel
		final JPanel buttonsPanel = new JPanel (new GridBagLayout ());
		buttonsPanel.add (new JButton (previousUnit), getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		buttonsPanel.add (new JButton (nextUnit), getUtils ().createConstraintsNoFill (1, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		buttonsPanel.add (upscaled, getUtils ().createConstraintsNoFill (2, 1, 1, 1, INSET, GridBagConstraintsNoFill.CENTRE));
		
		final JPanelWithConstantRepaints imagesPanel = new JPanelWithConstantRepaints ()
		{
			@Override
			public final void paintComponent (final Graphics g)
			{
				super.paintComponent (g);
				g.setColor (Color.WHITE);
				
				int x = 0;
				for (int d = 0; d < 8; d++)
				{
					final Animation walkAnimation = walkAnimations.get (d);
					final Animation meleeAnimation = meleeAnimations.get (d);
					
					for (int y = 0; y < 6; y++)
						try
						{
							final AnimationFrame frame;
							switch (y)
							{
								// Walking animation
								case 3:
								{
									final double absoluteFrameNumber = System.nanoTime () / (1000000000d / walkAnimation.getAnimationSpeed ());
									final int frameNumber = ((int) absoluteFrameNumber) % walkAnimation.getFrame ().size ();
									
									frame = walkAnimation.getFrame ().get (frameNumber);
									break;
								}
									
								// Static attack image
								case 4:
									frame = meleeAnimation.getFrame ().get (1);
									break;
									
								// Animated attack
								case 5:
									final double absoluteFrameNumber = System.nanoTime () / (1000000000d / meleeAnimation.getAnimationSpeed ());
									final int frameNumber = ((int) absoluteFrameNumber) % meleeAnimation.getFrame ().size ();
									
									frame = meleeAnimation.getFrame ().get (frameNumber);
									break;
								
								// Stacking walking frames
								default:
									frame = walkAnimation.getFrame ().get (y);
							}

							// Use getModifiedImage to colour the flag and superimpose it onto the unit
							final int baseY = y * 100;

							final List<String> shadingColours = new ArrayList<String> ();
							if (frame.getImageFlag () != null)
							{
								final double absoluteFlagNumber = System.nanoTime () / 4000000000d;
								final int flagNumber = ((int) absoluteFlagNumber) % FLAG_COLOURS.size ();
								final String flagColour = FLAG_COLOURS.get (flagNumber);
								shadingColours.add (flagColour);
							}
							
							final Image image = getModifiedImageCache ().getModifiedImage (frame.getImageFile (), frame.getImageFlag (), true, frame.getFlagOffsetX (), frame.getFlagOffsetY (),
								shadingColours, upscaled.isSelected () ? 1d : 2d, false);
							
							g.drawImage (image, x * 100, baseY, null);
						}
						catch (final IOException e)
						{
							// Likely its a missing image, don't print entire stack trace
							System.out.println (e.getMessage ());
						}
					
					x++;
				}
			}
		};
		
		imagesPanel.setBackground (new Color (16, 90, 57));
		imagesPanel.setMinimumSize (new Dimension (800, 600));
		imagesPanel.setMaximumSize (new Dimension (800, 600));
		imagesPanel.setPreferredSize (new Dimension (800, 600));
		
		final JPanel contentPane = new JPanel (new BorderLayout ());
		contentPane.add (buttonsPanel, BorderLayout.NORTH);
		contentPane.add (imagesPanel, BorderLayout.CENTER);
		
		// Frame
		final JFrame frame = new JFrame ("Flag checker UI");
		frame.setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		frame.setContentPane (contentPane);
		frame.pack ();
		frame.setLocationRelativeTo (null);
		frame.setVisible (true);
	
		imagesPanel.init ("Flag checker UI");
	}
	
	/**
	 * @param newIndex New unit index to select, so 0 = first unit
	 */
	private final void selectUnitIndex (final int newIndex)
	{
		unitIndex = newIndex;
		currentUnit = db.getUnit ().get (unitIndex);
		walkAction = currentUnit.getUnitCombatAction ().stream ().filter (a -> a.getCombatActionID ().equals ("WALK")).findAny ().orElse (null);
		
		walkAnimations = walkAction.getUnitCombatImage ().stream ().map (i -> animationsMap.get
			(upscaled.isSelected () ? i.getUnitCombatAnimationUP () : i.getUnitCombatAnimation ())).collect (Collectors.toList ());
		
		meleeAction = currentUnit.getUnitCombatAction ().stream ().filter (a -> a.getCombatActionID ().equals ("MELEE")).findAny ().orElse (null);
		
		meleeAnimations = (meleeAction == null) ? null : meleeAction.getUnitCombatImage ().stream ().map (i -> animationsMap.get
			(upscaled.isSelected () ? i.getUnitCombatAnimationUP () : i.getUnitCombatAnimation ())).collect (Collectors.toList ());
	}

	/**
	 * @return Helper methods and constants for creating and laying out Swing components
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param util Helper methods and constants for creating and laying out Swing components
	 */
	public final void setUtils (final NdgUIUtils util)
	{
		utils = util;
	}
	
	/**
	 * @return For creating resized images
	 */
	public final ModifiedImageCache getModifiedImageCache ()
	{
		return modifiedImageCache;
	}

	/**
	 * @param m For creating resized images
	 */
	public final void setModifiedImageCache (final ModifiedImageCache m)
	{
		modifiedImageCache = m;
	}
	
	/**
	 * @param args Command line arguments, ignored
	 */
	public final static void main (final String [] args)
	{
		try
		{
			final NdgUIUtilsImpl utils = new NdgUIUtilsImpl ();
			
			final ModifiedImageCacheImpl cache = new ModifiedImageCacheImpl ();
			cache.setUtils (utils);
			
			final FlagCheckerUI ui = new FlagCheckerUI ();
			ui.setUtils (utils);
			ui.setModifiedImageCache (cache);
			ui.init ();
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
		}
	}
}