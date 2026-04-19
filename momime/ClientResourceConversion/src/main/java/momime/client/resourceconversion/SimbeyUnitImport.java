package momime.client.resourceconversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import momime.common.database.Animation;
import momime.common.database.AnimationFrame;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.CommonXsdResourceResolver;
import momime.common.database.MomDatabase;
import momime.common.database.Unit;
import momime.common.database.UnitCombatAction;
import momime.common.database.UnitCombatImage;

/**
 * Simbey's upscaled unit graphics are all in named folders which makes copying them across non trivial
 */
public final class SimbeyUnitImport
{
	/** Location of XML to read/update */
	private final static File XML_LOCATION = new File ("W:\\EclipseHome\\SF\\MoMIME\\Server\\src\\external\\resources\\momime.server.database\\Original Master of Magic 1.31 rules.momime.xml");
	
	/** List of Simbey's folder names corresponding to each unit */
	private static final List<String> UNIT_FOLDERS = Arrays.asList
		("heroes\\Brax", "heroes\\Gunther", "heroes\\Zaldron", "heroes\\B'Shan", "heroes\\Rakir", "heroes\\Valana", "heroes\\Bahgtru", "heroes\\Serena",
		 "heroes\\Shuri", "heroes\\Theria", "heroes\\Greyfairer", "heroes\\Taki", "heroes\\Reywind", "heroes\\Malleus", "heroes\\Tumu", "heroes\\Jaer",
		 "heroes\\Marcus", "heroes\\Fang", "heroes\\Morgana", "heroes\\Aureus", "heroes\\Shin Bo", "heroes\\Spyder", "heroes\\Shalla", "heroes\\Yramrag",
		 
		 "champions\\Mystic X", "champions\\Aerie", "champions\\Deth Stryke", "champions\\Elana", "champions\\Roland",
		 "champions\\Mortu", "champions\\Alorra", "champions\\Sir Harold", "champions\\Ravashack", "champions\\Warrax", "champions\\Torin",
		 
		 "shared\\Trireme", "shared\\Galley", "shared\\Catapult", "shared\\Warship",
		 
		 "barbarians\\Spearmen", "barbarians\\Swordsmen", "barbarians\\Bowmen", "barbarians\\Cavalry", "barbarians\\Shamans", "shared\\Settlers", "barbarians\\Berserkers",
		 
		 "beastmen\\Spearmen", "beastmen\\Swordsmen", "beastmen\\Halberdiers", "beastmen\\Bowmen", "beastmen\\Priests", "beastmen\\Magicians",
		 "beastmen\\Engineers", "shared\\Settlers", "beastmen\\Centaurs", "beastmen\\Manticores", "beastmen\\Minotaurs",
		 
		 "dark elves\\Spearmen", "dark elves\\Swordsmen", "dark elves\\Halberdiers", "dark elves\\Cavalry", "dark elves\\Priests", "shared\\Settlers",
		 "dark elves\\Nightblades", "dark elves\\Warlocks", "dark elves\\Nightmares",
		 
		 "draconians\\Spearmen", "draconians\\Swordsmen", "draconians\\Halberdiers", "draconians\\Bowmen", "draconians\\Shamans", "draconians\\Magicians",
		 "draconians\\Engineers", "shared\\Settlers", "draconians\\Doom Drakes", "draconians\\Air Ship",
		 
		 "dwarves\\Swordsmen", "dwarves\\Halberdiers", "dwarves\\Engineers", "dwarves\\Hammerhands", "dwarves\\Steam Cannon", "dwarves\\Golem", "shared\\Settlers",
		 
		 "gnolls\\Spearmen", "gnolls\\Swordsmen", "gnolls\\Halberdiers", "gnolls\\Bowmen", "shared\\Settlers", "gnolls\\Wolf Riders",
		 
		 "halflings\\Spearmen", "halflings\\Swordsmen", "halflings\\Bowmen", "halflings\\Shamans", "shared\\Settlers", "halflings\\Slingers",
		 
		 "high elves\\Spearmen", "high elves\\Swordsmen", "high elves\\Halberdiers", "high elves\\Cavalry", "high elves\\Magicians", "shared\\Settlers",
		 "high elves\\Longbowmen", "high elves\\Elven Lords", "high elves\\Pegasai",
		 
		 "high men\\Spearmen", "high men\\Swordsmen", "high men\\Bowmen", "high men\\Cavalry", "high men\\Priests", "high men\\Magicians",
		 "high men\\Engineers", "shared\\Settlers", "high men\\Pikemen", "high men\\Paladins",
		 
		 "klackons\\Spearmen", "klackons\\Swordsmen", "klackons\\Halberdiers", "klackons\\Engineers", "shared\\Settlers", "klackons\\Stag Beetle",
		 
		 "lizardmen\\Spearmen", "lizardmen\\Swordsmen", "lizardmen\\Halberdiers", "lizardmen\\Javelineers", "lizardmen\\Shamans", "shared\\Settlers", "lizardmen\\Dragon Turtle",
		 
		 "nomads\\Spearmen", "nomads\\Swordsmen", "nomads\\Bowmen", "nomads\\Priests", "nomads\\Magicians", "shared\\Settlers",
		 "nomads\\Horsebowmen", "nomads\\Pikemen", "nomads\\Rangers", "nomads\\Griffins",
		 
		 "orcs\\Spearmen", "orcs\\Swordsmen", "orcs\\Halberdiers", "orcs\\Bowmen", "orcs\\Cavalry", "orcs\\Shamans",
		 "orcs\\Magicians", "orcs\\Engineers", "shared\\Settlers", "orcs\\Wyvern Riders",
		 
		 "trolls\\Spearmen", "trolls\\Swordsmen", "trolls\\Halberdiers", "trolls\\Shamans", "shared\\Settlers", "trolls\\War Trolls", "trolls\\War Mammoths",
		 
		 "arcane\\Magic Spirit",
		 
		 "chaos\\Hell Hounds", "chaos\\Gargoyles", "chaos\\Fire Giant", "chaos\\Fire Elemental", "chaos\\Chaos Spawn",
		 "chaos\\Chimeras", "chaos\\Doom Bat", "chaos\\Efreet", "chaos\\Hydra", "chaos\\Great Drake",
		 
		 "death\\Skeletons", "death\\Ghouls", "death\\Night Stalker", "death\\Werewolves", "death\\Demons", "death\\Wraiths",
		 "death\\Shadow Demons", "death\\Death Knights", "death\\Demon Lord", "death\\Zombies",
		 
		 "life\\Unicorns", "life\\Guardian Spirit", "life\\Angel", "life\\Arch Angel",
		 
		 "nature\\War Bears", "nature\\Sprites", "nature\\Cockatrices", "nature\\Basilisk", "nature\\Giant Spiders", "nature\\Stone Giant",
		 "nature\\Colossus", "nature\\Gorgons", "nature\\Earth Elemental", "nature\\Behemoth", "nature\\Great Wyrm",
		 
		 "sorcery\\Floating Island", "sorcery\\Phantom Beast", "sorcery\\Phantom Warriors", "sorcery\\Storm Giant", "sorcery\\Air Elemental", "sorcery\\Djinn", "sorcery\\Sky Drake", "sorcery\\Nagas");
	
	/**
	 * Searches for upscaled versions of unit graphics
	 * @throws Exception If there is a problem
	 */
	public final void run () throws Exception
	{
		// XSD
		final URL xsdResource = getClass ().getResource (CommonDatabaseConstants.COMMON_XSD_LOCATION);

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (new CommonXsdResourceResolver (DOMImplementationRegistry.newInstance ()));
		
		final Schema schema = schemaFactory.newSchema (xsdResource);

		final Unmarshaller unmarshaller = JAXBContext.newInstance (MomDatabase.class).createUnmarshaller ();		
		unmarshaller.setSchema (schema);
		
		// XML
		final MomDatabase db = (MomDatabase) unmarshaller.unmarshal (XML_LOCATION);
		final Map<String, Unit> units = db.getUnit ().stream ().collect (Collectors.toMap (u -> u.getUnitID (), u -> u));

		// Make a list of all combat image files we expect to find
		final List<String> combatImageFilenames = new ArrayList<String> ();
		for (int d = 1; d <= 8; d++)
		{
			combatImageFilenames.add ("d" + d + "-attack");
			combatImageFilenames.add ("d" + d + "-move-frame1");
			combatImageFilenames.add ("d" + d + "-move-frame3");
			combatImageFilenames.add ("d" + d + "-stand");
		}
		
		// Look for upscaled unit graphics
		int unitNo = 0;
		for (final String unitFolder : UNIT_FOLDERS)
		{
			unitNo++;

			String unitNbr = Integer.toString (unitNo);
			while (unitNbr.length () < 3)
				unitNbr = "0" + unitNbr;

			final String unitID = "UN" + unitNbr;
			final Unit unit = units.get (unitID);
			
			// Only update the XML if the overland image exists
			final File overlandSrc = new File ("W:\\EclipseHome\\simbey\\resources\\MoM\\units\\" + unitFolder + "\\overland.png");
			if (!overlandSrc.exists ())
				System.out.println (overlandSrc.getPath () + " not found");
			else if (unit == null)
				System.out.println ("Found upscaled overland graphics for " + unitID + " but can't find the unit in the XML");
			else
			{
				// Update XML
				unit.setUnitOverlandImageFileUP (unit.getUnitOverlandImageFile ().substring (0, unit.getUnitOverlandImageFile ().length () - 4) + "-UP.png");
				
				// Copy image files
				final File dest = new File ("W:\\EclipseHome\\SF\\MoMIME\\Client\\src\\main\\resources\\momime.client.graphics\\units\\" + unitID + "\\overland-UP.png");
				copyFile (overlandSrc, dest);
			}
			
			// Only update the XML if all the combat images exist
			final List<File> combatSrcList = new ArrayList<File> ();
			for (final String combatImageFilename : combatImageFilenames)
			{
				final File combatSrc = new File ("W:\\EclipseHome\\simbey\\resources\\MoM\\units\\" + unitFolder + "\\" + combatImageFilename + ".png");
				if (!combatSrc.exists ())
					System.out.println (combatSrc.getPath () + " not found");
				else
					combatSrcList.add (combatSrc);
			}
			
			if (combatSrcList.size () == combatImageFilenames.size ())
			{
				if (unit == null)
					System.out.println ("Found upscaled combat graphics for " + unitID + " but can't find the unit in the XML");
				else
				{
					// Update unit XML
					for (final UnitCombatAction action : unit.getUnitCombatAction ())
						for (final UnitCombatImage image : action.getUnitCombatImage ())
						{
							if (image.getUnitCombatImageFile () != null)
								image.setUnitCombatImageFileUP (image.getUnitCombatImageFile ().substring (0, image.getUnitCombatImageFile ().length () - 4) + "-UP.png");
							
							if (image.getUnitCombatAnimation () != null)
								image.setUnitCombatAnimationUP (image.getUnitCombatAnimation () + "_UP");
						}
					
					// Add new animations
					final List<Animation> animsToCopy = db.getAnimation ().stream ().filter (a -> a.getAnimationID ().startsWith (unitID)).collect (Collectors.toList ());
					for (final Animation srcAnim : animsToCopy)
					{
						final Animation destAnim = new Animation ();
						destAnim.setAnimationID (srcAnim.getAnimationID () + "_UP");
						destAnim.setAnimationDescription (srcAnim.getAnimationDescription () + " upscaled");
						destAnim.setAnimationSpeed (srcAnim.getAnimationSpeed ());
						
						for (final AnimationFrame srcFrame : srcAnim.getFrame ())
						{
							final AnimationFrame destFrame = new AnimationFrame ();
							destFrame.setImageFile (srcFrame.getImageFile ().substring (0, srcFrame.getImageFile ().length () - 4) + "-UP.png");
							
							destAnim.getFrame ().add (destFrame);
						}
						
						db.getAnimation ().add (destAnim);
					}
					
					// Copy image files
					for (int n = 0; n < combatSrcList.size (); n++)
					{
						final File combatSrc = combatSrcList.get (n);
						final String combatImageFilename = combatImageFilenames.get (n);
						
						final File dest = new File ("W:\\EclipseHome\\SF\\MoMIME\\Client\\src\\main\\resources\\momime.client.graphics\\units\\" + unitID + "\\" + combatImageFilename + "-UP.png");
						copyFile (combatSrc, dest);
					}
				}
			}
		}
		
		// Save XML back out
		final Marshaller marshaller = JAXBContext.newInstance (MomDatabase.class).createMarshaller ();		
		marshaller.setSchema (schema);
		marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		marshaller.marshal (db, XML_LOCATION);
		
		System.out.println ("All done!");
	}
	
	/**
	 * @param src File to copy from
	 * @param dest File to copy to
	 * @throws IOException If there is a problem
	 */
	private final void copyFile (final File src, final File dest) throws IOException
	{
		try (final FileInputStream in = new FileInputStream (src))
		{
			try (final FileOutputStream out = new FileOutputStream (dest))
			{
				IOUtils.copyLarge (in, out);
			}
		}
	}
	
	/**
	 * Searches for upscaled versions of unit graphics
	 * @param args Command line arguments, ignored
	 */
	public final static void main (final String [] args)
	{
		try
		{
			new SimbeyUnitImport ().run ();
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
		}
	}
}