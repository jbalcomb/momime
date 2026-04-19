package momime.client.resourceconversion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.CommonXsdResourceResolver;
import momime.common.database.MomDatabase;

/**
 * Goes through many images to make sure they're all referenced somewhere in the XML
 */
public final class UnusedImageChecker
{
	/** Location of XML to read */
	private final static File XML_LOCATION = new File ("W:\\EclipseHome\\SF\\MoMIME\\Server\\src\\external\\resources\\momime.server.database\\Original Master of Magic 1.31 rules.momime.xml");

	/** Root folder containing images */
	private final static File IMAGES_FOLDER = new File ("W:\\EclipseHome\\SF\\MoMIME\\Client\\src\\main\\resources\\momime.client.graphics");
	
	/** Prefix for the root folder */
	private final static String IMAGES_PREFIX = "/momime.client.graphics";
	
	/**
	 * @param folder Folder to scan for images
	 * @param prefix Classpath style prefix of this folder location
	 * @param images Set of image names to add to
	 * @throws IOException If there is a problem
	 */
	private final void scan (final File folder, final String prefix, final Set<String> images) throws IOException
	{
		final File [] files = folder.listFiles ();
		for (final File file : files)
			if (file.isDirectory ())
				scan (file, prefix + "/" + file.getName (), images);
			else
				images.add (prefix + "/" + file.getName ());
	}
	
	/**
	 * Finds the PNGs files we want to check
	 * @throws Exception If there is a problem
	 */
	public final void run () throws Exception
	{
		// Read XML
		final URL xsdResource = getClass ().getResource (CommonDatabaseConstants.COMMON_XSD_LOCATION);

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		schemaFactory.setResourceResolver (new CommonXsdResourceResolver (DOMImplementationRegistry.newInstance ()));
		
		final Schema schema = schemaFactory.newSchema (xsdResource);

		final Unmarshaller unmarshaller = JAXBContext.newInstance (MomDatabase.class).createUnmarshaller ();		
		unmarshaller.setSchema (schema);
		
		final MomDatabase db = (MomDatabase) unmarshaller.unmarshal (XML_LOCATION);
		
		// Get a list of all images we expect to be referenced by the XML
		final Set<String> images = new HashSet<String> ();
		for (final String folder : new String [] {"units", "wizards", "flags"})
			scan (new File (IMAGES_FOLDER, folder), IMAGES_PREFIX + "/" + folder, images);
		
		System.out.println (images.size () + " images found");
		
		// Now go through places in the XML where images are referenced
		db.getAnimation ().forEach (a -> a.getFrame ().forEach (f ->
		{
			images.remove (f.getImageFile ());
			images.remove (f.getImageFlag ());
			images.remove (f.getShadowlessImageFile ());
			images.remove (f.getShadowImageFile ());
		}));
		System.out.println (images.size () + " images remaining after checking animations");
		
		db.getUnit ().forEach (u ->
		{
			images.remove (u.getUnitOverlandImageFile ());
			images.remove (u.getUnitOverlandImageFileUP ());
			images.remove (u.getUnitOverlandImageFlag ());
			images.remove (u.getUnitOverlandImageFlagUP ());
			images.remove (u.getHeroPortraitImageFile ());
			images.remove (u.getUnitSummonImageFile ());
		});
		System.out.println (images.size () + " images remaining after checking units");
		
		db.getWizard ().forEach (w ->
		{
			images.remove (w.getPortraitImageFile ());
			images.remove (w.getHappyImageFile ());
			images.remove (w.getMadImageFile ());
			images.remove (w.getStandingImageFile ());
			images.remove (w.getBanishingImageFile ());
			images.remove (w.getBanishingHandImageFile ());
			images.remove (w.getWorldHandsImageFile ());
		});
		System.out.println (images.size () + " images remaining after checking wizards");
		
		// List out any images that look to be unused
		int n = 0;
		for (final String image : images)
		{
			n++;
			System.out.println (n + " / " + images.size () + " = " + image);
		}
		
		System.out.println ("All done!");
	}
	
	/**
	 * @param args Command line arguments, ignored
	 */
	public final static void main (final String [] args)
	{
		try
		{
			new UnusedImageChecker ().run ();
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
		}
	}

}