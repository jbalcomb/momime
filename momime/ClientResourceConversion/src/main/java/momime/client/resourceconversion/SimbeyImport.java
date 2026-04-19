package momime.client.resourceconversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Searches for upscaled versions of graphics
 */
public final class SimbeyImport
{
	/**
	 * @param sourceFolder Source folder continaing original graphics
	 * @param upscaledFolder Folder that might contain upscaled graphics
	 * @param counts Keeps track of overall counts
	 * @throws Exception If there is a problem
	 */
	public final void scan (final File sourceFolder, final File upscaledFolder, final SimbeyImportCounts counts) throws Exception
	{
		for (final File sourceFile : sourceFolder.listFiles ())
			if (sourceFile.isDirectory ())
			{
				final File upscaledFile = new File (upscaledFolder, sourceFile.getName ());
				if ((upscaledFile.exists ()) && (upscaledFile.isDirectory ()))
					scan (sourceFile, upscaledFile, counts);
			}
			else if ((sourceFile.getName ().endsWith (".png")) && (!sourceFile.getName ().endsWith ("-UP.png")))
			{
				counts.filesSearched++;
				
				// Generate expected filenames
				final List<String> possibleFilenames = new ArrayList<String> ();
				possibleFilenames.add (sourceFile.getName ());
				
				if (sourceFile.getName ().contains ("-frame"))
					possibleFilenames.add (sourceFile.getName ().replaceAll ("-frame", "-frame0"));
				else if (sourceFile.getName ().equals ("00000000.png"))
				{
					possibleFilenames.add ("00000000a.png");
					possibleFilenames.add ("00000000a-frame1.png");
				}
				
				if (sourceFile.getName ().contains ("00000000-frame"))
					possibleFilenames.add (sourceFile.getName ().replaceAll ("00000000-frame", "00000000a-frame"));
				
				boolean found = false;
				for (final String possibleFilename : possibleFilenames)
					if (!found)
					{
						final File upscaledFile = new File (upscaledFolder, possibleFilename);
						if ((upscaledFile.exists ()) && (!upscaledFile.isDirectory ()))
						{
							//System.out.println ("Upscaled version of " + sourceFile + " found");
							found = true;
							counts.filesFound++;
							
							try (final FileInputStream in = new FileInputStream (upscaledFile))
							{
								try (final FileOutputStream out = new FileOutputStream (new File (sourceFolder, sourceFile.getName ().substring (0, sourceFile.getName ().length () - 4) + "-UP.png")))
								{
									IOUtils.copyLarge (in, out);
								}
							}
						}
					}
						
				if (!found)
					System.out.println ("Upscaled version of " + sourceFile + " not found");
			}
	}
	
	/**
	 * Searches for upscaled versions of graphics
	 * @param args Command line arguments
	 */
	public final static void main (final String [] args)
	{
		if (args.length != 2)
			System.out.println ("Usage: SimbeyImport <Source folder continaing original graphics> <Folder that might contain upscaled graphics>");
		else
			try
			{
				final SimbeyImportCounts counts = new SimbeyImportCounts ();
				new SimbeyImport ().scan (new File (args [0]), new File (args [1]), counts);
				System.out.println ("Found " + counts.filesFound + " of " + counts.filesSearched + " PNGs");
			}
			catch (final Exception e)
			{
				e.printStackTrace ();
			}
	}
}