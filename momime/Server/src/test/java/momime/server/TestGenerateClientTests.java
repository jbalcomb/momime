package momime.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import momime.common.database.CommonDatabase;

/**
 * Not really tests, but some of the client tests need data from the server XML, and I don't want to just have a complete
 * copy of the server XML copied to the client project, or use some hacky (hard coded) method of locating where the server XML is located.
 * 
 * What we really need is to generate Java code that contains data from the server XML
 */
@ExtendWith(MockitoExtension.class)
public final class TestGenerateClientTests extends ServerTestData
{
	/**
	 * Export flag data for original size overland units
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetOverlandUnitImage_Original () throws Exception
	{
		final CommonDatabase db = loadServerDatabase ();

		// Build maps
		final List<String> flagImagesOrder = new ArrayList<String> ();
		final Map<String, List<String>> flagImages = new HashMap<String, List<String>> ();
		final Map<Integer, List<String>> flagOffsetXs = new HashMap<Integer, List<String>> (); 
		final Map<Integer, List<String>> flagOffsetYs = new HashMap<Integer, List<String>> (); 
		
		db.getUnits ().stream ().filter (u -> u.getUnitOverlandImageFlag () != null).forEach (u ->
		{
			List<String> unitsWithThisFlagImage = flagImages.get (u.getUnitOverlandImageFlag ());
			if (unitsWithThisFlagImage == null)
			{
				unitsWithThisFlagImage = new ArrayList<String> ();
				flagImages.put (u.getUnitOverlandImageFlag (), unitsWithThisFlagImage);
				flagImagesOrder.add (u.getUnitOverlandImageFlag ());
			}
			unitsWithThisFlagImage.add (u.getUnitID ());
			
			List<String> unitsWithThisOffsetX = flagOffsetXs.get (u.getFlagOffsetX ());
			if (unitsWithThisOffsetX == null)
			{
				unitsWithThisOffsetX = new ArrayList<String> ();
				flagOffsetXs.put (u.getFlagOffsetX (), unitsWithThisOffsetX);
			}
			unitsWithThisOffsetX.add (u.getUnitID ());

			List<String> unitsWithThisOffsetY = flagOffsetYs.get (u.getFlagOffsetY ());
			if (unitsWithThisOffsetY == null)
			{
				unitsWithThisOffsetY = new ArrayList<String> ();
				flagOffsetYs.put (u.getFlagOffsetY (), unitsWithThisOffsetY);
			}
			unitsWithThisOffsetY.add (u.getUnitID ());
		});
		
		// Flag images
		boolean firstFlagImage = true;
		for (final String flagImage : flagImagesOrder)
		{
			final List<String> unitIDs = flagImages.get (flagImage); 
			
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstFlagImage)
				s.append ("else ");
			
			s.append ("if ");
			
			if (unitIDs.size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : unitIDs)
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (unitIDs.size () > 1)
				s.append (")");
			
			firstFlagImage = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setUnitOverlandImageFlag (\"" + flagImage + "\");");
		}
		System.out.println ("");
		
		// Flag offset X
		boolean firstOffsetX = true;
		for (final Entry<Integer, List<String>> flagOffsetX : flagOffsetXs.entrySet ().stream ().sorted ((f1, f2) -> f1.getKey () - f2.getKey ()).collect (Collectors.toList ()))
		{
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstOffsetX)
				s.append ("else ");
			
			s.append ("if ");
			
			if (flagOffsetX.getValue ().size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : flagOffsetX.getValue ())
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (flagOffsetX.getValue ().size () > 1)
				s.append (")");
			
			firstOffsetX = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setFlagOffsetX (" + flagOffsetX.getKey () + ");");
		}
		System.out.println ("");
		
		// Flag offset Y
		boolean firstOffsetY = true;
		for (final Entry<Integer, List<String>> flagOffsetY : flagOffsetYs.entrySet ().stream ().sorted ((f1, f2) -> f1.getKey () - f2.getKey ()).collect (Collectors.toList ()))
		{
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstOffsetY)
				s.append ("else ");
			
			s.append ("if ");
			
			if (flagOffsetY.getValue ().size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : flagOffsetY.getValue ())
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (flagOffsetY.getValue ().size () > 1)
				s.append (")");
			
			firstOffsetY = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setFlagOffsetY (" + flagOffsetY.getKey () + ");");
		}
	}

	/**
	 * Export flag data for upscaled overland units
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testGetOverlandUnitImage_Upscaled () throws Exception
	{
		final CommonDatabase db = loadServerDatabase ();

		// Build maps
		final List<String> flagImagesOrder = new ArrayList<String> ();
		final Map<String, List<String>> flagImages = new HashMap<String, List<String>> ();
		final Map<Integer, List<String>> flagOffsetXs = new HashMap<Integer, List<String>> (); 
		final Map<Integer, List<String>> flagOffsetYs = new HashMap<Integer, List<String>> (); 
		
		db.getUnits ().stream ().filter (u -> u.getUnitOverlandImageFlagUP () != null).forEach (u ->
		{
			List<String> unitsWithThisFlagImage = flagImages.get (u.getUnitOverlandImageFlagUP ());
			if (unitsWithThisFlagImage == null)
			{
				unitsWithThisFlagImage = new ArrayList<String> ();
				flagImages.put (u.getUnitOverlandImageFlagUP (), unitsWithThisFlagImage);
				flagImagesOrder.add (u.getUnitOverlandImageFlagUP ());
			}
			unitsWithThisFlagImage.add (u.getUnitID ());
			
			List<String> unitsWithThisOffsetX = flagOffsetXs.get (u.getFlagOffsetXUP ());
			if (unitsWithThisOffsetX == null)
			{
				unitsWithThisOffsetX = new ArrayList<String> ();
				flagOffsetXs.put (u.getFlagOffsetXUP (), unitsWithThisOffsetX);
			}
			unitsWithThisOffsetX.add (u.getUnitID ());

			List<String> unitsWithThisOffsetY = flagOffsetYs.get (u.getFlagOffsetYUP ());
			if (unitsWithThisOffsetY == null)
			{
				unitsWithThisOffsetY = new ArrayList<String> ();
				flagOffsetYs.put (u.getFlagOffsetYUP (), unitsWithThisOffsetY);
			}
			unitsWithThisOffsetY.add (u.getUnitID ());
		});
		
		// Flag images
		boolean firstFlagImage = true;
		for (final String flagImage : flagImagesOrder)
		{
			final List<String> unitIDs = flagImages.get (flagImage); 
			
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstFlagImage)
				s.append ("else ");
			
			s.append ("if ");
			
			if (unitIDs.size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : unitIDs)
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (unitIDs.size () > 1)
				s.append (")");
			
			firstFlagImage = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setUnitOverlandImageFlagUP (\"" + flagImage + "\");");
		}
		System.out.println ("");
		
		// Flag offset X
		boolean firstOffsetX = true;
		for (final Entry<Integer, List<String>> flagOffsetX : flagOffsetXs.entrySet ().stream ().sorted ((f1, f2) -> f1.getKey () - f2.getKey ()).collect (Collectors.toList ()))
		{
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstOffsetX)
				s.append ("else ");
			
			s.append ("if ");
			
			if (flagOffsetX.getValue ().size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : flagOffsetX.getValue ())
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (flagOffsetX.getValue ().size () > 1)
				s.append (")");
			
			firstOffsetX = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setFlagOffsetXUP (" + flagOffsetX.getKey () + ");");
		}
		System.out.println ("");
		
		// Flag offset Y
		boolean firstOffsetY = true;
		for (final Entry<Integer, List<String>> flagOffsetY : flagOffsetYs.entrySet ().stream ().sorted ((f1, f2) -> f1.getKey () - f2.getKey ()).collect (Collectors.toList ()))
		{
			final StringBuilder s = new StringBuilder ();
			s.append ("\t\t\t");
			
			if (!firstOffsetY)
				s.append ("else ");
			
			s.append ("if ");
			
			if (flagOffsetY.getValue ().size () > 1)
				s.append ("(");
			
			boolean firstUnitID = true;
			for (final String unitID : flagOffsetY.getValue ())
			{
				if (!firstUnitID)
					s.append (" || ");
				
				s.append ("(unitID.equals (\"" + unitID + "\"))");
				
				firstUnitID = false;
			}
			
			if (flagOffsetY.getValue ().size () > 1)
				s.append (")");
			
			firstOffsetY = false;
			
			System.out.println (s);
			System.out.println ("\t\t\t\tunitDef.setFlagOffsetYUP (" + flagOffsetY.getKey () + ");");
		}
	}
}