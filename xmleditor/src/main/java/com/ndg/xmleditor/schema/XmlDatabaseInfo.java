package com.ndg.xmleditor.schema;

/**
 * Stores the info held about each XML database under the top level <xsd:appinfo>
 */
public final class XmlDatabaseInfo
{
	/** Title to display for this XML file; if null, the namespaceURI will be displayed instead */
	private String title;
	
	/** Location of this XML file */
	private String filename;
	
	/** Locations of XML files can be given either as absolute paths, or relative to the location of the XSD */
	private XmlFilenameType filenameType;

	/**
	 * @return Title to display for this XML file; if null, the namespaceURI will be displayed instead
	 */
	public final String getTitle ()
	{
		return title;
	}

	/**
	 * @param tit Title to display for this XML file; if null, the namespaceURI will be displayed instead
	 */
	public final void setTitle (final String tit)
	{
		title = tit;
	}

	/**
	 * @return Location of this XML file
	 */
	public final String getFilename ()
	{
		return filename;
	}

	/**
	 * @param name Location of this XML file
	 */
	public final void setFilename (final String name)
	{
		filename = name;
	}

	/**
	 * @return Locations of XML files can be given either as absolute paths, or relative to the location of the XSD
	 */
	public final XmlFilenameType getFilenameType ()
	{
		return filenameType;
	}

	/**
	 * @param ft Locations of XML files can be given either as absolute paths, or relative to the location of the XSD
	 */
	public final void setFilenameType (final XmlFilenameType ft)
	{
		filenameType = ft;
	}
}