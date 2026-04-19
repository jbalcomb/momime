package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;

import org.w3._2001.xmlschema.Documentation;
import org.w3._2001.xmlschema.NoFixedFacet;
import org.w3._2001.xmlschema.TopLevelSimpleType;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class SimpleTypeEx extends TopLevelSimpleType
{
	/**
	 * @param includeBlankRecordAtStart Whether to insert an empty record at the start of the returned array
	 * @return Two dimensional array of value-description pairs for the found enumerations, or null if no enumerations were found
	 */
	public final String [] [] getEnumerations (final boolean includeBlankRecordAtStart)
	{
		final List<String []> enums = new ArrayList<String []> ();
		
		// Search for enums
		boolean foundAtLeastOneDescription = false;
		if (getRestriction () != null)
			for (final Object obj : getRestriction ().getFacets ())
				if (obj instanceof JAXBElement)
				{
					final JAXBElement<?> container = (JAXBElement<?>) obj;
					if (container.getValue () instanceof NoFixedFacet)
					{
						final NoFixedFacet value = (NoFixedFacet) container.getValue ();
						
						// Found one
						final String [] thisEnum = new String [2];
						thisEnum [0] = value.getValue ();
						
						// Is there a description for it?
						final StringBuilder desc = new StringBuilder ();
						if (value.getAnnotation () != null)
							for (final Object obj2 : value.getAnnotation ().getAppinfoOrDocumentation ())
								if (obj2 instanceof Documentation)
								{
									final Documentation doc = (Documentation) obj2;
									for (final Object obj3 : doc.getContent ())
										if (obj3 instanceof String)
											desc.append ((String) obj3);
								}
						
						if (desc.length () != 0)
						{
							thisEnum [1] = desc.toString ();
							foundAtLeastOneDescription = true;
						}
						
						enums.add (thisEnum);
					}
				}
		
		// If we didn't find a single description, only return a single dimension array
		final int enumCount = enums.size () + (includeBlankRecordAtStart ? 1 : 0);
		final String [] [] result;
		if (enums.size () == 0)
			result = null;
		else if (foundAtLeastOneDescription)
		{
			// Output 2*n array including descriptions
			result = new String [2] [enumCount];
			int enumNo = includeBlankRecordAtStart ? 1 : 0;
			for (final String [] thisEnum : enums)
			{
				result [0] [enumNo] = thisEnum [0];
				result [1] [enumNo] = (thisEnum [1] != null) ? thisEnum [1] : thisEnum [0];
				enumNo++;
			}
		}
		else
		{
			// Output 1*n array without descriptions
			result = new String [1] [enumCount];
			int enumNo = includeBlankRecordAtStart ? 1 : 0;
			for (final String [] thisEnum : enums)
			{
				result [0] [enumNo] = thisEnum [0];
				enumNo++;
			}
		}
			
		return result;
	}
}