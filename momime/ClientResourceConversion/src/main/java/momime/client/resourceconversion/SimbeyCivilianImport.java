package momime.client.resourceconversion;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.CommonXsdResourceResolver;
import momime.common.database.MomDatabase;
import momime.common.database.Race;
import momime.common.database.RacePopulationTask;

/**
 * Fills in the civilianImageFileUP values 
 */
public final class SimbeyCivilianImport
{
	/** Location of XML to read/update*/
	private final static File XML_LOCATION = new File ("W:\\EclipseHome\\SF\\MoMIME\\Server\\src\\external\\resources\\momime.server.database\\Original Master of Magic 1.31 rules.momime.xml");
	
	/**
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

		// Process each race
		for (final Race race : db.getRace ())
			for (final RacePopulationTask task : race.getRacePopulationTask ())
				task.setCivilianImageFileUP (task.getCivilianImageFile ().substring (0, task.getCivilianImageFile ().length () - 4) + "UP.png");

		// Save out the updated XML
		final Marshaller marshaller = JAXBContext.newInstance (MomDatabase.class).createMarshaller ();		
		marshaller.setSchema (schema);
		marshaller.setProperty (Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal (db, XML_LOCATION);
		
		System.out.print ("All done!");
	}
	
	/**
	 * @param args Command line arguments, ignored
	 */
	public final static void main (final String [] args)
	{
		try
		{
			new SimbeyCivilianImport ().run ();
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
		}
	}
}