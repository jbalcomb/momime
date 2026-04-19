package com.ndg.xmleditor.schema;

/**
 * Locations of XML files can be given either as absolute paths, or relative to the location of the XSD
 */
public enum XmlFilenameType
{
	/** Filename is an absolute path */
	ABSOLUTE,
	
	/** Filename is taken relative to the location of the XSD */
	RELATIVE;
}
