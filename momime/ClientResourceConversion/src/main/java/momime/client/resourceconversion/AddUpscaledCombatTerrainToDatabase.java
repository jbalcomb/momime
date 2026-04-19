package momime.client.resourceconversion;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import com.ndg.utils.Holder;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import momime.common.database.Animation;
import momime.common.database.AnimationFrame;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.CommonXsdResourceResolver;
import momime.common.database.MomDatabase;

/**
 * Copies all the fields that reference terrain tiles to also reference the upscaled versions
 */
public final class AddUpscaledCombatTerrainToDatabase
{
	/** Location of XML to read/update */
	private final static File XML_LOCATION = new File ("W:\\EclipseHome\\SF\\MoMIME\\Server\\src\\external\\resources\\momime.server.database\\Original Master of Magic 1.31 rules.momime.xml");
	
	/** Prefix of all overland terrain images that should have upscaled versions */
	private final static String COMBAT_TERRAIN_PREFIX = "/momime.client.graphics/combat/terrain/";

	/** Exclude any graphics starting with this */
	private final static String COMBAT_TERRAIN_PREFIX_EXCEPT = "/momime.client.graphics/combat/terrain/road";
	
	/**
	 * Copies all the fields that reference terrain tiles to also reference the upscaled versions
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

		// Copy animations
		final Set<String> copiedAnimationIDs = new HashSet<String> ();
		final List<Animation> newAnimations = db.getAnimation ().stream ().filter (a -> (!a.getFrame ().isEmpty ()) && (a.getFrame ().stream ().allMatch
			(f -> (f.getImageFile ().startsWith (COMBAT_TERRAIN_PREFIX)) && (!f.getImageFile ().startsWith (COMBAT_TERRAIN_PREFIX_EXCEPT))))).map (a ->
			{
				copiedAnimationIDs.add (a.getAnimationID ());
				
				final Animation c = new Animation ();
				c.setAnimationDescription (a.getAnimationDescription () + " upscaled");
				c.setAnimationID (a.getAnimationID () + "_UP");
				c.setAnimationSpeed (a.getAnimationSpeed ());
				c.setCombatCastOffsetX (a.getCombatCastOffsetX ());
				c.setCombatCastOffsetY (a.getCombatCastOffsetY ());
				c.setOverlandCastOffsetX (a.getOverlandCastOffsetX ());
				c.setOverlandCastOffsetY (a.getOverlandCastOffsetY ());
				c.getFrame ().addAll (a.getFrame ().stream ().map (f ->
				{
					final AnimationFrame r = new AnimationFrame ();
					r.setImageFile (f.getImageFile ().substring (0, f.getImageFile ().length () - 4) + "-UP.png");
					r.setImageFlag (f.getImageFlag ());
					r.setShadowImageFile (f.getShadowImageFile ());
					r.setShadowlessImageFile (f.getShadowlessImageFile ());
					r.setFlagOffsetX (r.getFlagOffsetX ());
					r.setFlagOffsetY (r.getFlagOffsetY ());
					return r;
				}).collect (Collectors.toList ()));
				return c;
			}).collect (Collectors.toList ());
		
		db.getAnimation ().addAll (newAnimations);
		System.out.println ("Copied " + newAnimations.size () + " animations");
		
		// Update tiles
		final Holder<Integer> tileFilesUpdated = new Holder<Integer> (0);
		final Holder<Integer> tileAnimationsUpdated = new Holder<Integer> (0);
		
		db.getTileSet ().forEach (ts ->
			ts.getSmoothedTileType ().forEach (tt ->
				tt.getSmoothedTile ().forEach (t ->
				{
					if ((t.getTileFile () != null) && (t.getTileFile ().startsWith (COMBAT_TERRAIN_PREFIX)) && (!t.getTileFile ().startsWith (COMBAT_TERRAIN_PREFIX_EXCEPT)))
					{
						t.setTileFileUP (t.getTileFile ().substring (0, t.getTileFile ().length () - 4) + "-UP.png");
						tileFilesUpdated.setValue (tileFilesUpdated.getValue () + 1);
					}
					
					if (copiedAnimationIDs.contains (t.getTileAnimation ()))
					{
						t.setTileAnimationUP (t.getTileAnimation () + "_UP");
						tileAnimationsUpdated.setValue (tileAnimationsUpdated.getValue () + 1);
					}
				})));
		
		System.out.println ("Set UP variant of " + tileFilesUpdated.getValue () + " terrain tiles");
		System.out.println ("Set UP animation of " + tileAnimationsUpdated.getValue () + " terrain tiles");
		
		// Save XML back out
		final Marshaller marshaller = JAXBContext.newInstance (MomDatabase.class).createMarshaller ();		
		marshaller.setSchema (schema);
		marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		marshaller.marshal (db, XML_LOCATION);
		
		System.out.println ("All done!");
	}
	
	/**
	 * Copies all the fields that reference terrain tiles to also reference the upscaled versions
	 * @param args Command line arguments (ignored)
	 */
	public final static void main (final String [] args)
	{
		if (args.length != 0)
			System.out.println ("Usage: AddUpscaledOverlandTerrainToDatabase");
		else
			try
			{
				new AddUpscaledCombatTerrainToDatabase ().run ();
			}
			catch (final Exception e)
			{
				e.printStackTrace ();
			}
	}
}