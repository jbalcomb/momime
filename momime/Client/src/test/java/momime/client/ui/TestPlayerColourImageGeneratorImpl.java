package momime.client.ui;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.session.MultiplayerSessionUtils;
import com.ndg.multiplayer.session.PlayerPublicDetails;
import com.ndg.utils.swing.ModifiedImageCache;
import com.ndg.utils.swing.ModifiedImageCacheImpl;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.NdgUIUtilsImpl;

import momime.client.MomClient;
import momime.common.database.UnitEx;
import momime.common.messages.FogOfWarMemory;
import momime.common.messages.KnownWizardDetails;
import momime.common.messages.MomPersistentPlayerPrivateKnowledge;
import momime.common.messages.MomTransientPlayerPublicKnowledge;
import momime.common.utils.KnownWizardUtils;

/**
 * Tests the PlayerColourImageGeneratorImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestPlayerColourImageGeneratorImpl
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (TestPlayerColourImageGeneratorImpl.class);
	
	/**
	 * Tests all the normal overland unit images
	 * Really to do this we need data from the server XML, which we can't access, so this is a bit of a hack hard coding data from the server XML that we need here
	 * 
	 * @param doubleSize Whether to double the size of all the unit images
	 * @throws Exception If there is a problem
	 */
	private final void testGetOverlandUnitImage_Original (final boolean doubleSize) throws Exception
	{
		// Set up unit data
		final List<UnitEx> units = new ArrayList<UnitEx> ();
		for (int n = 1; n <= 199; n++)
		{
			String s = Integer.toString (n);
			while (s.length () < 3)
				s = "0" + s;
			
			final String unitID = "UN" + s;
			
			final UnitEx unitDef = new UnitEx ();
			unitDef.setUnitOverlandImageFile ("/momime.client.graphics/units/" + unitID + "/overland.png");

			if ((unitID.equals ("UN001")) || (unitID.equals ("UN002")) || (unitID.equals ("UN003")) || (unitID.equals ("UN004")) || (unitID.equals ("UN006")) || (unitID.equals ("UN011")) || (unitID.equals ("UN012")) || (unitID.equals ("UN013")) || (unitID.equals ("UN014")) || (unitID.equals ("UN015")) || (unitID.equals ("UN016")) || (unitID.equals ("UN022")) || (unitID.equals ("UN023")) || (unitID.equals ("UN024")) || (unitID.equals ("UN025")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHeroFlag-1.png");
			else if (unitID.equals ("UN005"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHeroFlag-3.png");
			else if ((unitID.equals ("UN007")) || (unitID.equals ("UN008")) || (unitID.equals ("UN009")) || (unitID.equals ("UN010")) || (unitID.equals ("UN017")) || (unitID.equals ("UN018")) || (unitID.equals ("UN019")) || (unitID.equals ("UN020")) || (unitID.equals ("UN026")) || (unitID.equals ("UN027")) || (unitID.equals ("UN028")) || (unitID.equals ("UN029")) || (unitID.equals ("UN030")) || (unitID.equals ("UN031")) || (unitID.equals ("UN032")) || (unitID.equals ("UN033")) || (unitID.equals ("UN034")) || (unitID.equals ("UN035")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHeroFlag-2.png");
			else if (unitID.equals ("UN021"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHeroFlag-4.png");
			else if (unitID.equals ("UN036"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBoatFlag-1.png");
			else if (unitID.equals ("UN037"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBoatFlag-2.png");
			else if (unitID.equals ("UN038"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandCatapaultFlag.png");
			else if (unitID.equals ("UN039"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBoatFlag-3.png");
			else if ((unitID.equals ("UN045")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandSettlerFlag.png");
			else if ((unitID.equals ("UN047")) || (unitID.equals ("UN048")) || (unitID.equals ("UN061")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBeastmenFlag-1.png");
			else if ((unitID.equals ("UN049")) || (unitID.equals ("UN050")) || (unitID.equals ("UN084")) || (unitID.equals ("UN087")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBeastmenFlag-2.png");
			else if (unitID.equals ("UN051"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBeastmenFlag-3.png");
			else if (unitID.equals ("UN052"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBeastmenFlag-4.png");
			else if (unitID.equals ("UN053"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandBeastmenFlag-5.png");
			else if (unitID.equals ("UN067"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDraconianFlag-1.png");
			else if ((unitID.equals ("UN068")) || (unitID.equals ("UN069")) || (unitID.equals ("UN070")) || (unitID.equals ("UN071")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDraconianFlag-2.png");
			else if ((unitID.equals ("UN072")) || (unitID.equals ("UN073")))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDraconianFlag-3.png");
			else if (unitID.equals ("UN076"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDraconianFlag-4.png");
			else if (unitID.equals ("UN077"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDwarfFlag-1.png");
			else if (unitID.equals ("UN078"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDwarfFlag-2.png");
			else if (unitID.equals ("UN080"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDwarfFlag-3.png");
			else if (unitID.equals ("UN081"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDwarfFlag-4.png");
			else if (unitID.equals ("UN085"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandGnollFlag-1.png");
			else if (unitID.equals ("UN086"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandGnollFlag-2.png");
			else if (unitID.equals ("UN089"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandGnollFlag-3.png");
			else if (unitID.equals ("UN099"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighElfFlag-1.png");
			else if (unitID.equals ("UN100"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighElfFlag-2.png");
			else if (unitID.equals ("UN104"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighElfFlag-3.png");
			else if (unitID.equals ("UN105"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-1.png");
			else if (unitID.equals ("UN106"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-2.png");
			else if (unitID.equals ("UN107"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-3.png");
			else if (unitID.equals ("UN109"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-4.png");
			else if (unitID.equals ("UN110"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-5.png");
			else if (unitID.equals ("UN113"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHighMenFlag-6.png");
			else if (unitID.equals ("UN127"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandLizardmenFlag.png");
			else if (unitID.equals ("UN138"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandOrcFlag-1.png");
			else if (unitID.equals ("UN139"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandOrcFlag-2.png");
			else if (unitID.equals ("UN142"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandOrcFlag-3.png");
			else if (unitID.equals ("UN143"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandOrcFlag-4.png");
			else if (unitID.equals ("UN147"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandOrcFlag-5.png");
			else if (unitID.equals ("UN164"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandHydraFlag.png");
			else if (unitID.equals ("UN172"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandShadowDemonsFlag.png");
			else if (unitID.equals ("UN173"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDeathKnightsFlag.png");
			else if (unitID.equals ("UN174"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandDemonLordFlag.png");
			else if (unitID.equals ("UN184"))
				unitDef.setUnitOverlandImageFlag ("/momime.client.graphics/flags/overlandGiantSpidersFlag.png");

			if ((unitID.equals ("UN127")) || (unitID.equals ("UN164")) || (unitID.equals ("UN184")))
				unitDef.setFlagOffsetX (2);
			else if ((unitID.equals ("UN009")) || (unitID.equals ("UN022")) || (unitID.equals ("UN027")) || (unitID.equals ("UN028")) || (unitID.equals ("UN110")))
				unitDef.setFlagOffsetX (5);
			else if ((unitID.equals ("UN002")) || (unitID.equals ("UN004")) || (unitID.equals ("UN005")) || (unitID.equals ("UN006")) || (unitID.equals ("UN007")) || (unitID.equals ("UN008")) || (unitID.equals ("UN010")) || (unitID.equals ("UN011")) || (unitID.equals ("UN012")) || (unitID.equals ("UN013")) || (unitID.equals ("UN014")) || (unitID.equals ("UN015")) || (unitID.equals ("UN016")) || (unitID.equals ("UN017")) || (unitID.equals ("UN019")) || (unitID.equals ("UN024")) || (unitID.equals ("UN026")) || (unitID.equals ("UN029")) || (unitID.equals ("UN030")) || (unitID.equals ("UN032")) || (unitID.equals ("UN033")) || (unitID.equals ("UN034")) || (unitID.equals ("UN035")) || (unitID.equals ("UN036")) || (unitID.equals ("UN037")) || (unitID.equals ("UN038")) || (unitID.equals ("UN068")) || (unitID.equals ("UN069")) || (unitID.equals ("UN070")) || (unitID.equals ("UN071")) || (unitID.equals ("UN072")) || (unitID.equals ("UN113")) || (unitID.equals ("UN174")))
				unitDef.setFlagOffsetX (6);
			else if ((unitID.equals ("UN001")) || (unitID.equals ("UN003")) || (unitID.equals ("UN021")) || (unitID.equals ("UN023")) || (unitID.equals ("UN025")) || (unitID.equals ("UN067")) || (unitID.equals ("UN073")) || (unitID.equals ("UN076")) || (unitID.equals ("UN099")) || (unitID.equals ("UN106")) || (unitID.equals ("UN107")))
				unitDef.setFlagOffsetX (7);
			else if ((unitID.equals ("UN031")) || (unitID.equals ("UN048")) || (unitID.equals ("UN049")) || (unitID.equals ("UN050")) || (unitID.equals ("UN051")) || (unitID.equals ("UN052")) || (unitID.equals ("UN053")) || (unitID.equals ("UN077")) || (unitID.equals ("UN080")) || (unitID.equals ("UN084")) || (unitID.equals ("UN085")) || (unitID.equals ("UN086")) || (unitID.equals ("UN087")) || (unitID.equals ("UN089")) || (unitID.equals ("UN100")) || (unitID.equals ("UN104")) || (unitID.equals ("UN105")) || (unitID.equals ("UN109")) || (unitID.equals ("UN138")) || (unitID.equals ("UN139")) || (unitID.equals ("UN142")) || (unitID.equals ("UN143")) || (unitID.equals ("UN147")) || (unitID.equals ("UN172")) || (unitID.equals ("UN173")))
				unitDef.setFlagOffsetX (8);
			else if ((unitID.equals ("UN020")) || (unitID.equals ("UN039")) || (unitID.equals ("UN047")) || (unitID.equals ("UN078")))
				unitDef.setFlagOffsetX (9);
			else if (unitID.equals ("UN061"))
				unitDef.setFlagOffsetX (10);
			else if ((unitID.equals ("UN045")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN081")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setFlagOffsetX (11);
			else if (unitID.equals ("UN018"))
				unitDef.setFlagOffsetX (12);

			if ((unitID.equals ("UN018")) || (unitID.equals ("UN021")) || (unitID.equals ("UN031")))
				unitDef.setFlagOffsetY (1);
			else if ((unitID.equals ("UN001")) || (unitID.equals ("UN002")) || (unitID.equals ("UN003")) || (unitID.equals ("UN004")) || (unitID.equals ("UN005")) || (unitID.equals ("UN006")) || (unitID.equals ("UN007")) || (unitID.equals ("UN008")) || (unitID.equals ("UN009")) || (unitID.equals ("UN010")) || (unitID.equals ("UN011")) || (unitID.equals ("UN012")) || (unitID.equals ("UN013")) || (unitID.equals ("UN014")) || (unitID.equals ("UN015")) || (unitID.equals ("UN016")) || (unitID.equals ("UN019")) || (unitID.equals ("UN020")) || (unitID.equals ("UN022")) || (unitID.equals ("UN023")) || (unitID.equals ("UN024")) || (unitID.equals ("UN025")) || (unitID.equals ("UN026")) || (unitID.equals ("UN027")) || (unitID.equals ("UN029")) || (unitID.equals ("UN030")) || (unitID.equals ("UN032")) || (unitID.equals ("UN033")) || (unitID.equals ("UN034")) || (unitID.equals ("UN035")) || (unitID.equals ("UN036")) || (unitID.equals ("UN037")) || (unitID.equals ("UN039")) || (unitID.equals ("UN076")) || (unitID.equals ("UN081")))
				unitDef.setFlagOffsetY (2);
			else if ((unitID.equals ("UN017")) || (unitID.equals ("UN028")) || (unitID.equals ("UN045")) || (unitID.equals ("UN051")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setFlagOffsetY (3);
			else if ((unitID.equals ("UN052")) || (unitID.equals ("UN053")) || (unitID.equals ("UN089")) || (unitID.equals ("UN172")) || (unitID.equals ("UN174")))
				unitDef.setFlagOffsetY (4);
			else if ((unitID.equals ("UN038")) || (unitID.equals ("UN099")) || (unitID.equals ("UN100")))
				unitDef.setFlagOffsetY (5);
			else if ((unitID.equals ("UN061")) || (unitID.equals ("UN104")) || (unitID.equals ("UN106")) || (unitID.equals ("UN107")) || (unitID.equals ("UN110")) || (unitID.equals ("UN142")) || (unitID.equals ("UN147")) || (unitID.equals ("UN164")))
				unitDef.setFlagOffsetY (6);
			else if ((unitID.equals ("UN077")) || (unitID.equals ("UN105")) || (unitID.equals ("UN113")) || (unitID.equals ("UN173")))
				unitDef.setFlagOffsetY (7);
			else if (unitID.equals ("UN080"))
				unitDef.setFlagOffsetY (8);
			else if ((unitID.equals ("UN049")) || (unitID.equals ("UN050")) || (unitID.equals ("UN067")) || (unitID.equals ("UN068")) || (unitID.equals ("UN069")) || (unitID.equals ("UN070")) || (unitID.equals ("UN071")) || (unitID.equals ("UN086")) || (unitID.equals ("UN109")) || (unitID.equals ("UN127")) || (unitID.equals ("UN184")))
				unitDef.setFlagOffsetY (9);
			else if ((unitID.equals ("UN047")) || (unitID.equals ("UN048")) || (unitID.equals ("UN072")) || (unitID.equals ("UN073")) || (unitID.equals ("UN084")) || (unitID.equals ("UN087")) || (unitID.equals ("UN143")))
				unitDef.setFlagOffsetY (10);
			else if ((unitID.equals ("UN078")) || (unitID.equals ("UN085")) || (unitID.equals ("UN138")) || (unitID.equals ("UN139")))
				unitDef.setFlagOffsetY (11);
			
			units.add (unitDef);
		}

		// Set look and feel
		final NdgUIUtils utils = new NdgUIUtilsImpl ();
		utils.useNimbusLookAndFeel ();
		
		// Player details
		final MomClient client = mock (MomClient.class);
		
		final MomTransientPlayerPublicKnowledge trans = new MomTransientPlayerPublicKnowledge ();
		trans.setFlagColour ("E0218A");
		
		final PlayerPublicDetails pd = new PlayerPublicDetails (null, null, trans);
		
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		when (client.getPlayers ()).thenReturn (players);
		
		final MultiplayerSessionUtils multiplayerSessionUtils = mock (MultiplayerSessionUtils.class);
		when (multiplayerSessionUtils.findPlayerWithID (players, 3, "getModifiedImage")).thenReturn (pd);
		
		// Wizard details
		final FogOfWarMemory mem = new FogOfWarMemory ();
		
		final MomPersistentPlayerPrivateKnowledge ppk = new MomPersistentPlayerPrivateKnowledge ();
		ppk.setFogOfWarMemory (mem);
		
		when (client.getOurPersistentPlayerPrivateKnowledge ()).thenReturn (ppk);
		
		final KnownWizardDetails wizardDetails = new KnownWizardDetails ();
		wizardDetails.setWizardID ("WZ01");
		
		final KnownWizardUtils knownWizardUtils = mock (KnownWizardUtils.class);
		when (knownWizardUtils.findKnownWizardDetails (mem.getWizardDetails (), 3, "getModifiedImage")).thenReturn (wizardDetails);
		
		// Set up object to test
		final ModifiedImageCacheImpl modifiedImageCache = new ModifiedImageCacheImpl ();
		modifiedImageCache.setUtils (utils);
		
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setUtils (utils);
		gen.setClient (client);
		gen.setMultiplayerSessionUtils (multiplayerSessionUtils);
		gen.setKnownWizardUtils (knownWizardUtils);
		gen.setModifiedImageCache (modifiedImageCache);
		
		// Set up a dummy panel
		final Dimension panelSize = new Dimension (doubleSize ? 810 : 410, doubleSize ? 380 : 190);
		
		final JPanel panel = new JPanel ()
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				super.paintComponent (g);
				
				int x = 5;
				int y = 5;
				int rowCount = 0;
				
				for (final UnitEx unitDef : units)
					try
					{
						final Image image = gen.getOverlandUnitImage (unitDef, 3, doubleSize, false);
						g.drawImage (image, x, y, null);
				
						rowCount++;
						if (rowCount == 20)
						{
							x = 5;
							rowCount = 0;
							y = y + image.getHeight (null) + (doubleSize ? 4 : 2);
						}
						else
							x = x + image.getWidth (null) + (doubleSize ? 4 : 2);
					}
					catch (final IOException e)
					{
						log.error (e, e);
					}
			}
		};
		panel.setMinimumSize (panelSize);
		panel.setMaximumSize (panelSize);
		panel.setPreferredSize (panelSize);
		
		// Set up a dummy frame
		final JFrame frame = new JFrame ();
		frame.setContentPane (panel);
		frame.setResizable (false);
		frame.pack ();
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo (null);
		
		frame.setVisible (true);
		Thread.sleep (5000);
		frame.setVisible (false);
	}
	
	/**
	 * Tests all the normal overland unit images at default size
	 * Really to do this we need data from the server XML, which we can't access, so this is a bit of a hack hard coding data from the server XML that we need here
	 * 
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetOverlandUnitImage_RegularSize () throws Exception
	{
		testGetOverlandUnitImage_Original (false);
	}

	/**
	 * Tests all the normal overland unit images at double size
	 * Really to do this we need data from the server XML, which we can't access, so this is a bit of a hack hard coding data from the server XML that we need here
	 * 
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetOverlandUnitImage_DoubleSize () throws Exception
	{
		testGetOverlandUnitImage_Original (true);
	}
	
	/**
	 * Tests all the upscaled overland unit images
	 * Really to do this we need data from the server XML, which we can't access, so this is a bit of a hack hard coding data from the server XML that we need here
	 * 
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetOverlandUnitImage_Upscaled () throws Exception
	{
		// Set up unit data
		final List<UnitEx> units = new ArrayList<UnitEx> ();
		for (int n = 1; n <= 199; n++)
			if ((n != 132) && (n != 199))
		{
			String s = Integer.toString (n);
			while (s.length () < 3)
				s = "0" + s;
			
			final String unitID = "UN" + s;
			
			final UnitEx unitDef = new UnitEx ();
			unitDef.setUnitOverlandImageFileUP ("/momime.client.graphics/units/" + unitID + "/overland-UP.png");

			if (unitID.equals ("UN001"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-01.png");
			else if (unitID.equals ("UN002"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-02.png");
			else if (unitID.equals ("UN003"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-03.png");
			else if (unitID.equals ("UN004"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-04.png");
			else if (unitID.equals ("UN005"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-05.png");
			else if ((unitID.equals ("UN006")) || (unitID.equals ("UN024")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-06.png");
			else if ((unitID.equals ("UN007")) || (unitID.equals ("UN008")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-07.png");
			else if (unitID.equals ("UN009"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-08.png");
			else if ((unitID.equals ("UN010")) || (unitID.equals ("UN013")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-09.png");
			else if (unitID.equals ("UN011"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-10.png");
			else if (unitID.equals ("UN012"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-11.png");
			else if (unitID.equals ("UN014"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-12.png");
			else if (unitID.equals ("UN015"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-13.png");
			else if (unitID.equals ("UN016"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-14.png");
			else if (unitID.equals ("UN017"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-15.png");
			else if (unitID.equals ("UN018"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-16.png");
			else if (unitID.equals ("UN019"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-17.png");
			else if (unitID.equals ("UN020"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-18.png");
			else if (unitID.equals ("UN022"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-19.png");
			else if (unitID.equals ("UN023"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-20.png");
			else if (unitID.equals ("UN025"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-21.png");
			else if (unitID.equals ("UN026"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-22.png");
			else if ((unitID.equals ("UN021")) || (unitID.equals ("UN027")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-23.png");
			else if (unitID.equals ("UN028"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-24.png");
			else if (unitID.equals ("UN029"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-25.png");
			else if (unitID.equals ("UN030"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-26.png");
			else if (unitID.equals ("UN031"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-27.png");
			else if ((unitID.equals ("UN032")) || (unitID.equals ("UN035")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-28.png");
			else if (unitID.equals ("UN033"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-29.png");
			else if (unitID.equals ("UN034"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHeroFlagUP-30.png");
			else if (unitID.equals ("UN036"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBoatFlagUP-1.png");
			else if (unitID.equals ("UN037"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBoatFlagUP-2.png");
			else if (unitID.equals ("UN038"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandCatapaultFlagUP.png");
			else if (unitID.equals ("UN039"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBoatFlagUP-3.png");
			else if ((unitID.equals ("UN045")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandSettlerFlagUP.png");
			else if ((unitID.equals ("UN047")) || (unitID.equals ("UN048")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-1.png");
			else if (unitID.equals ("UN049"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-2.png");
			else if (unitID.equals ("UN050"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-3.png");
			else if (unitID.equals ("UN051"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-4.png");
			else if (unitID.equals ("UN052"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-5.png");
			else if (unitID.equals ("UN053"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlagUP-6.png");
			else if (unitID.equals ("UN061"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDarkElfFlagUP.png");
			else if ((unitID.equals ("UN067")) || (unitID.equals ("UN072")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDraconianFlagUP-1.png");
			else if (unitID.equals ("UN068"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/combatBeastmenFlag-d3-01.png");
			else if ((unitID.equals ("UN069")) || (unitID.equals ("UN070")) || (unitID.equals ("UN071")) || (unitID.equals ("UN073")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDraconianFlagUP-2.png");
			else if (unitID.equals ("UN076"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDraconianFlagUP-3.png");
			else if (unitID.equals ("UN077"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDwarfFlagUP-1.png");
			else if (unitID.equals ("UN078"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDwarfFlagUP-2.png");
			else if (unitID.equals ("UN080"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDwarfFlagUP-3.png");
			else if (unitID.equals ("UN081"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDwarfFlagUP-4.png");
			else if ((unitID.equals ("UN084")) || (unitID.equals ("UN086")) || (unitID.equals ("UN087")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandGnollFlagUP-1.png");
			else if (unitID.equals ("UN085"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandBeastmenFlag-1.png");
			else if (unitID.equals ("UN089"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandGnollFlagUP-2.png");
			else if (unitID.equals ("UN099"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighElfFlagUP-1.png");
			else if (unitID.equals ("UN100"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighElfFlagUP-2.png");
			else if (unitID.equals ("UN104"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighElfFlagUP-3.png");
			else if (unitID.equals ("UN105"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-1.png");
			else if (unitID.equals ("UN106"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-2.png");
			else if (unitID.equals ("UN107"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-3.png");
			else if (unitID.equals ("UN109"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-4.png");
			else if (unitID.equals ("UN110"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-5.png");
			else if (unitID.equals ("UN113"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHighMenFlagUP-6.png");
			else if (unitID.equals ("UN127"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandLizardmenFlagUP.png");
			else if ((unitID.equals ("UN138")) || (unitID.equals ("UN139")))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandOrcFlagUP-1.png");
			else if (unitID.equals ("UN142"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandOrcFlagUP-2.png");
			else if (unitID.equals ("UN143"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandOrcFlagUP-3.png");
			else if (unitID.equals ("UN147"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandOrcFlagUP-4.png");
			else if (unitID.equals ("UN164"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandHydraFlagUP.png");
			else if (unitID.equals ("UN172"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandShadowDemonsFlagUP.png");
			else if (unitID.equals ("UN173"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDeathKnightsFlagUP.png");
			else if (unitID.equals ("UN174"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandDemonLordFlagUP.png");
			else if (unitID.equals ("UN184"))
				unitDef.setUnitOverlandImageFlagUP ("/momime.client.graphics/flags/overlandGiantSpidersFlagUP.png");

			if ((unitID.equals ("UN127")) || (unitID.equals ("UN184")))
				unitDef.setFlagOffsetXUP (4);
			else if (unitID.equals ("UN164"))
				unitDef.setFlagOffsetXUP (5);
			else if ((unitID.equals ("UN021")) || (unitID.equals ("UN022")) || (unitID.equals ("UN027")) || (unitID.equals ("UN028")))
				unitDef.setFlagOffsetXUP (9);
			else if ((unitID.equals ("UN009")) || (unitID.equals ("UN039")) || (unitID.equals ("UN110")) || (unitID.equals ("UN113")))
				unitDef.setFlagOffsetXUP (10);
			else if ((unitID.equals ("UN002")) || (unitID.equals ("UN004")) || (unitID.equals ("UN005")) || (unitID.equals ("UN006")) || (unitID.equals ("UN007")) || (unitID.equals ("UN008")) || (unitID.equals ("UN010")) || (unitID.equals ("UN012")) || (unitID.equals ("UN013")) || (unitID.equals ("UN014")) || (unitID.equals ("UN016")) || (unitID.equals ("UN024")) || (unitID.equals ("UN026")) || (unitID.equals ("UN029")) || (unitID.equals ("UN030")) || (unitID.equals ("UN032")) || (unitID.equals ("UN033")) || (unitID.equals ("UN034")) || (unitID.equals ("UN035")) || (unitID.equals ("UN037")))
				unitDef.setFlagOffsetXUP (11);
			else if ((unitID.equals ("UN011")) || (unitID.equals ("UN015")) || (unitID.equals ("UN017")) || (unitID.equals ("UN019")) || (unitID.equals ("UN038")) || (unitID.equals ("UN067")) || (unitID.equals ("UN068")) || (unitID.equals ("UN069")) || (unitID.equals ("UN070")) || (unitID.equals ("UN071")) || (unitID.equals ("UN072")) || (unitID.equals ("UN174")))
				unitDef.setFlagOffsetXUP (12);
			else if ((unitID.equals ("UN001")) || (unitID.equals ("UN003")) || (unitID.equals ("UN023")) || (unitID.equals ("UN025")) || (unitID.equals ("UN073")) || (unitID.equals ("UN104")))
				unitDef.setFlagOffsetXUP (13);
			else if ((unitID.equals ("UN020")) || (unitID.equals ("UN076")) || (unitID.equals ("UN080")) || (unitID.equals ("UN099")) || (unitID.equals ("UN106")) || (unitID.equals ("UN107")))
				unitDef.setFlagOffsetXUP (14);
			else if ((unitID.equals ("UN031")) || (unitID.equals ("UN036")) || (unitID.equals ("UN048")) || (unitID.equals ("UN051")) || (unitID.equals ("UN052")) || (unitID.equals ("UN084")) || (unitID.equals ("UN087")) || (unitID.equals ("UN109")))
				unitDef.setFlagOffsetXUP (15);
			else if ((unitID.equals ("UN049")) || (unitID.equals ("UN050")) || (unitID.equals ("UN053")) || (unitID.equals ("UN077")) || (unitID.equals ("UN089")) || (unitID.equals ("UN100")) || (unitID.equals ("UN105")) || (unitID.equals ("UN142")) || (unitID.equals ("UN143")) || (unitID.equals ("UN173")))
				unitDef.setFlagOffsetXUP (16);
			else if ((unitID.equals ("UN047")) || (unitID.equals ("UN085")) || (unitID.equals ("UN086")) || (unitID.equals ("UN138")) || (unitID.equals ("UN139")) || (unitID.equals ("UN147")) || (unitID.equals ("UN172")))
				unitDef.setFlagOffsetXUP (17);
			else if (unitID.equals ("UN078"))
				unitDef.setFlagOffsetXUP (18);
			else if (unitID.equals ("UN061"))
				unitDef.setFlagOffsetXUP (19);
			else if ((unitID.equals ("UN045")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN081")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setFlagOffsetXUP (22);
			else if (unitID.equals ("UN018"))
				unitDef.setFlagOffsetXUP (23);

			if ((unitID.equals ("UN018")) || (unitID.equals ("UN031")))
				unitDef.setFlagOffsetYUP (1);
			else if ((unitID.equals ("UN021")) || (unitID.equals ("UN001")) || (unitID.equals ("UN002")) || (unitID.equals ("UN003")) || (unitID.equals ("UN004")) || (unitID.equals ("UN005")) || (unitID.equals ("UN006")) || (unitID.equals ("UN007")) || (unitID.equals ("UN008")) || (unitID.equals ("UN009")) || (unitID.equals ("UN010")) || (unitID.equals ("UN011")) || (unitID.equals ("UN012")) || (unitID.equals ("UN013")) || (unitID.equals ("UN014")) || (unitID.equals ("UN015")) || (unitID.equals ("UN016")) || (unitID.equals ("UN019")) || (unitID.equals ("UN020")) || (unitID.equals ("UN022")) || (unitID.equals ("UN023")) || (unitID.equals ("UN024")) || (unitID.equals ("UN025")) || (unitID.equals ("UN026")) || (unitID.equals ("UN027")) || (unitID.equals ("UN029")) || (unitID.equals ("UN030")) || (unitID.equals ("UN032")) || (unitID.equals ("UN033")) || (unitID.equals ("UN034")) || (unitID.equals ("UN035")) || (unitID.equals ("UN036")))
				unitDef.setFlagOffsetYUP (3);
			else if ((unitID.equals ("UN076")) || (unitID.equals ("UN081")))
				unitDef.setFlagOffsetYUP (4);
			else if ((unitID.equals ("UN028")) || (unitID.equals ("UN037")))
				unitDef.setFlagOffsetYUP (5);
			else if ((unitID.equals ("UN017")) || (unitID.equals ("UN045")) || (unitID.equals ("UN051")) || (unitID.equals ("UN054")) || (unitID.equals ("UN063")) || (unitID.equals ("UN074")) || (unitID.equals ("UN083")) || (unitID.equals ("UN088")) || (unitID.equals ("UN094")) || (unitID.equals ("UN101")) || (unitID.equals ("UN112")) || (unitID.equals ("UN119")) || (unitID.equals ("UN126")) || (unitID.equals ("UN133")) || (unitID.equals ("UN146")) || (unitID.equals ("UN152")))
				unitDef.setFlagOffsetYUP (6);
			else if ((unitID.equals ("UN039")) || (unitID.equals ("UN052")))
				unitDef.setFlagOffsetYUP (7);
			else if ((unitID.equals ("UN053")) || (unitID.equals ("UN172")))
				unitDef.setFlagOffsetYUP (8);
			else if (unitID.equals ("UN038"))
				unitDef.setFlagOffsetYUP (9);
			else if ((unitID.equals ("UN099")) || (unitID.equals ("UN100")))
				unitDef.setFlagOffsetYUP (10);
			else if ((unitID.equals ("UN104")) || (unitID.equals ("UN106")) || (unitID.equals ("UN107")))
				unitDef.setFlagOffsetYUP (11);
			else if ((unitID.equals ("UN061")) || (unitID.equals ("UN089")) || (unitID.equals ("UN142")) || (unitID.equals ("UN164")))
				unitDef.setFlagOffsetYUP (12);
			else if ((unitID.equals ("UN077")) || (unitID.equals ("UN110")))
				unitDef.setFlagOffsetYUP (13);
			else if ((unitID.equals ("UN105")) || (unitID.equals ("UN113")) || (unitID.equals ("UN173")))
				unitDef.setFlagOffsetYUP (14);
			else if (unitID.equals ("UN147"))
				unitDef.setFlagOffsetYUP (15);
			else if (unitID.equals ("UN080"))
				unitDef.setFlagOffsetYUP (16);
			else if ((unitID.equals ("UN050")) || (unitID.equals ("UN069")) || (unitID.equals ("UN071")) || (unitID.equals ("UN073")))
				unitDef.setFlagOffsetYUP (17);
			else if ((unitID.equals ("UN049")) || (unitID.equals ("UN067")) || (unitID.equals ("UN070")) || (unitID.equals ("UN072")) || (unitID.equals ("UN084")) || (unitID.equals ("UN086")) || (unitID.equals ("UN087")) || (unitID.equals ("UN109")) || (unitID.equals ("UN184")))
				unitDef.setFlagOffsetYUP (18);
			else if ((unitID.equals ("UN068")) || (unitID.equals ("UN085")) || (unitID.equals ("UN174")))
				unitDef.setFlagOffsetYUP (19);
			else if ((unitID.equals ("UN047")) || (unitID.equals ("UN048")) || (unitID.equals ("UN127")) || (unitID.equals ("UN143")))
				unitDef.setFlagOffsetYUP (20);
			else if ((unitID.equals ("UN138")) || (unitID.equals ("UN139")))
				unitDef.setFlagOffsetYUP (22);
			else if (unitID.equals ("UN078"))
				unitDef.setFlagOffsetYUP (23);
			
			units.add (unitDef);
		}

		// Set look and feel
		final NdgUIUtils utils = new NdgUIUtilsImpl ();
		utils.useNimbusLookAndFeel ();
		
		// Player details
		final MomClient client = mock (MomClient.class);
		
		final MomTransientPlayerPublicKnowledge trans = new MomTransientPlayerPublicKnowledge ();
		trans.setFlagColour ("E0218A");
		
		final PlayerPublicDetails pd = new PlayerPublicDetails (null, null, trans);
		
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		when (client.getPlayers ()).thenReturn (players);
		
		final MultiplayerSessionUtils multiplayerSessionUtils = mock (MultiplayerSessionUtils.class);
		when (multiplayerSessionUtils.findPlayerWithID (players, 3, "getModifiedImage")).thenReturn (pd);
		
		// Wizard details
		final FogOfWarMemory mem = new FogOfWarMemory ();
		
		final MomPersistentPlayerPrivateKnowledge ppk = new MomPersistentPlayerPrivateKnowledge ();
		ppk.setFogOfWarMemory (mem);
		
		when (client.getOurPersistentPlayerPrivateKnowledge ()).thenReturn (ppk);
		
		final KnownWizardDetails wizardDetails = new KnownWizardDetails ();
		wizardDetails.setWizardID ("WZ01");
		
		final KnownWizardUtils knownWizardUtils = mock (KnownWizardUtils.class);
		when (knownWizardUtils.findKnownWizardDetails (mem.getWizardDetails (), 3, "getModifiedImage")).thenReturn (wizardDetails);
		
		// Set up object to test
		final ModifiedImageCacheImpl modifiedImageCache = new ModifiedImageCacheImpl ();
		modifiedImageCache.setUtils (utils);
		
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setUtils (utils);
		gen.setClient (client);
		gen.setMultiplayerSessionUtils (multiplayerSessionUtils);
		gen.setKnownWizardUtils (knownWizardUtils);
		gen.setModifiedImageCache (modifiedImageCache);
		
		// Set up a dummy panel
		final Dimension panelSize = new Dimension (810, 380);
		
		final JPanel panel = new JPanel ()
		{
			@Override
			protected final void paintComponent (final Graphics g)
			{
				super.paintComponent (g);
				
				int x = 5;
				int y = 5;
				int rowCount = 0;
				
				for (final UnitEx unitDef : units)
					try
					{
						final Image image = gen.getOverlandUnitImage (unitDef, 3, true, true);
						g.drawImage (image, x, y, null);
				
						rowCount++;
						if (rowCount == 20)
						{
							x = 5;
							rowCount = 0;
							y = y + image.getHeight (null) + 4;
						}
						else
							x = x + image.getWidth (null) + 4;
					}
					catch (final IOException e)
					{
						log.error (e, e);
					}
			}
		};
		panel.setMinimumSize (panelSize);
		panel.setMaximumSize (panelSize);
		panel.setPreferredSize (panelSize);
		
		// Set up a dummy frame
		final JFrame frame = new JFrame ();
		frame.setContentPane (panel);
		frame.setResizable (false);
		frame.pack ();
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo (null);
		
		frame.setVisible (true);
		Thread.sleep (5000);
		frame.setVisible (false);
	}
	
	/**
	 * Tests the getModifiedImage method when we don't ask for any modifications to the base image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_unmodified () throws Exception
	{
		// Base image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final NdgUIUtils utils = mock (NdgUIUtils.class);
		when (utils.loadImage ("test.png")).thenReturn (src);
		
		// Set up object to test
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setUtils (utils);
		
		// Call method
		final Image image = gen.getModifiedImage ("test.png", false, null, null, null, null, null, null);
		
		// Check results
		assertSame (src, image);
	}

	/**
	 * Tests the getModifiedImage method colouring an entire image, like a wizard gem
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourEntireImage () throws Exception
	{
		// Player's colour
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		
		final MomTransientPlayerPublicKnowledge trans = new MomTransientPlayerPublicKnowledge ();
		trans.setFlagColour ("C");
		
		final PlayerPublicDetails player = new PlayerPublicDetails (null, null, trans);
		
		final MultiplayerSessionUtils multiplayerSessionUtils = mock (MultiplayerSessionUtils.class);
		when (multiplayerSessionUtils.findPlayerWithID (players, 1, "getModifiedImage")).thenReturn (player);
		
		// Wizard
		final FogOfWarMemory mem = new FogOfWarMemory ();
		
		final MomPersistentPlayerPrivateKnowledge priv = new MomPersistentPlayerPrivateKnowledge ();
		priv.setFogOfWarMemory (mem);
		
		final KnownWizardDetails wizardDetails = new KnownWizardDetails ();
		wizardDetails.setWizardID ("WZ01");
		
		final KnownWizardUtils knownWizardUtils = mock (KnownWizardUtils.class);
		when (knownWizardUtils.findKnownWizardDetails (mem.getWizardDetails (), 1, "getModifiedImage")).thenReturn (wizardDetails);

		// Client
		final MomClient client = mock (MomClient.class);
		when (client.getPlayers ()).thenReturn (players);
		when (client.getOurPersistentPlayerPrivateKnowledge ()).thenReturn (priv);
		
		// Mock returned image
		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final ModifiedImageCache cache = mock (ModifiedImageCache.class);
		when (cache.getModifiedImage ("test.png", null, false, null, null, Arrays.asList ("C"), null, false)).thenReturn (dest);
				
		// Set up object to test
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setClient (client);
		gen.setMultiplayerSessionUtils (multiplayerSessionUtils);
		gen.setKnownWizardUtils (knownWizardUtils);
		gen.setModifiedImageCache (cache);
		
		// Call method
		final Image image = gen.getModifiedImage ("test.png", true, null, null, null, 1, null, null);
		
		// Check results
		assertSame (dest, image);
	}

	/**
	 * Tests the getModifiedImage method colouring an entire image then applying shading to it
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourAndShadeEntireImage () throws Exception
	{
		// Player's colour
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		
		final MomTransientPlayerPublicKnowledge trans = new MomTransientPlayerPublicKnowledge ();
		trans.setFlagColour ("C");
		
		final PlayerPublicDetails player = new PlayerPublicDetails (null, null, trans);
		
		final MultiplayerSessionUtils multiplayerSessionUtils = mock (MultiplayerSessionUtils.class);
		when (multiplayerSessionUtils.findPlayerWithID (players, 1, "getModifiedImage")).thenReturn (player);
		
		// Wizard
		final FogOfWarMemory mem = new FogOfWarMemory ();
		
		final MomPersistentPlayerPrivateKnowledge priv = new MomPersistentPlayerPrivateKnowledge ();
		priv.setFogOfWarMemory (mem);

		final KnownWizardDetails wizardDetails = new KnownWizardDetails ();
		wizardDetails.setWizardID ("WZ01");
		
		final KnownWizardUtils knownWizardUtils = mock (KnownWizardUtils.class);
		when (knownWizardUtils.findKnownWizardDetails (mem.getWizardDetails (), 1, "getModifiedImage")).thenReturn (wizardDetails);
		
		// Client
		final MomClient client = mock (MomClient.class);
		when (client.getPlayers ()).thenReturn (players);
		when (client.getOurPersistentPlayerPrivateKnowledge ()).thenReturn (priv);
				
		// Mock returned image
		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final ModifiedImageCache cache = mock (ModifiedImageCache.class);
		when (cache.getModifiedImage ("test.png", null, false, null, null, Arrays.asList ("C", "D", "E"), null, false)).thenReturn (dest);
		
		// Set up object to test
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setClient (client);
		gen.setMultiplayerSessionUtils (multiplayerSessionUtils);
		gen.setKnownWizardUtils (knownWizardUtils);
		gen.setModifiedImageCache (cache);
		
		// Call method
		final Image image = gen.getModifiedImage ("test.png", true, null, null, null, 1, Arrays.asList ("D", "E"), null);
		
		// Check results
		assertSame (dest, image);
	}

	/**
	 * Tests the getModifiedImage method colouring the flag portion of an image
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_colourFlag () throws Exception
	{
		// Player's colour
		final List<PlayerPublicDetails> players = new ArrayList<PlayerPublicDetails> ();
		
		final MomTransientPlayerPublicKnowledge trans = new MomTransientPlayerPublicKnowledge ();
		trans.setFlagColour ("C");
		
		final PlayerPublicDetails player = new PlayerPublicDetails (null, null, trans);
		
		final MultiplayerSessionUtils multiplayerSessionUtils = mock (MultiplayerSessionUtils.class);
		when (multiplayerSessionUtils.findPlayerWithID (players, 1, "getModifiedImage")).thenReturn (player);
		
		// Wizard
		final FogOfWarMemory mem = new FogOfWarMemory ();
		
		final MomPersistentPlayerPrivateKnowledge priv = new MomPersistentPlayerPrivateKnowledge ();
		priv.setFogOfWarMemory (mem);

		final KnownWizardDetails wizardDetails = new KnownWizardDetails ();
		wizardDetails.setWizardID ("WZ01");
		
		final KnownWizardUtils knownWizardUtils = mock (KnownWizardUtils.class);
		when (knownWizardUtils.findKnownWizardDetails (mem.getWizardDetails (), 1, "getModifiedImage")).thenReturn (wizardDetails);
		
		// Client
		final MomClient client = mock (MomClient.class);
		when (client.getPlayers ()).thenReturn (players);
		when (client.getOurPersistentPlayerPrivateKnowledge ()).thenReturn (priv);
		
		// Base image
		final BufferedImage src = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		src.setRGB (0, 0, 0xFF000080);
		src.setRGB (1, 0, 0xFF000081);
		
		// Mock returned image
		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final ModifiedImageCache cache = mock (ModifiedImageCache.class);
		when (cache.getModifiedImage ("test.png", "flag.png", true, 1, 0, Arrays.asList ("C"), null, false)).thenReturn (dest);
		
		// Set up object to test
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setClient (client);
		gen.setMultiplayerSessionUtils (multiplayerSessionUtils);
		gen.setKnownWizardUtils (knownWizardUtils);
		gen.setModifiedImageCache (cache);
		
		// Call method
		final Image image = gen.getModifiedImage ("test.png", false, "flag.png", 1, 0, 1, null, null);
		
		// Check results
		assertSame (dest, image);
	}

	/**
	 * Tests the getModifiedImage method applying shading to a base image that doesn't need any parts in the player's colour
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetModifiedImage_shadingOnly () throws Exception
	{
		// Mock returned image
		final BufferedImage dest = new BufferedImage (2, 1, BufferedImage.TYPE_INT_ARGB);
		
		final ModifiedImageCache cache = mock (ModifiedImageCache.class);
		when (cache.getModifiedImage ("test.png", null, true, null, null, Arrays.asList ("D", "E"), null, false)).thenReturn (dest);
		
		// Set up object to test
		final PlayerColourImageGeneratorImpl gen = new PlayerColourImageGeneratorImpl ();
		gen.setModifiedImageCache (cache);
		
		// Call method
		final Image image = gen.getModifiedImage ("test.png", true, null, null, null, null, Arrays.asList ("D", "E"), null);
		
		// Check results
		assertSame (dest, image);
	}
}