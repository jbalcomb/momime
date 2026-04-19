package com.ndg.xmleditor.constants;

import org.jdom2.Namespace;

/**
 * Tags used in .xsd files - excludes custom appinfo tags used by the XML editor or other applications
 */
public final class XsdConstants
{
	/** Path and name to locate the XML XSD file on the classpath */
	public final static String XML_XSD_LOCATION = "/com.ndg.xmleditor.schema/xml.xsd";
	
	/** Full URI of the xml: namespace */
	public final static String XML_XSD_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";

	/** Path and name to locate the XML Schema XSD file on the classpath */
	public final static String XML_SCHEMA_XSD_LOCATION = "/com.ndg.xmleditor.schema/XMLSchema.xsd";
	
	/** Full URI of the xsd: schema definition namespace */
	public final static String XML_SCHEMA_XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";

	/** Namespace of instances of XML documents */
	public final static Namespace INSTANCE_NAMESPACE = Namespace.getNamespace ("xsi", "http://www.w3.org/2001/XMLSchema-instance");

	/** Value indicating that an attribute is mandatory */
	public final static String VALUE_ATTRIBUTE_USE_REQUIRED = "required";

	/** Value indicating an element is a child entity */
	public final static String VALUE_MAX_OCCURS_UNBOUNDED = "unbounded";

	/** Data type for strings that cannot contain CR or LF, so are edited by a regular JTextField */
	public final static String VALUE_DATA_TYPE_SINGLE_LINE_STRING = "normalizedString";

	/** Date data type, in the format yyyy-MM-dd */
	public final static String VALUE_DATA_TYPE_DATE = "date";

	/**
	 * Prevent instatiation of this class
	 */
	private XsdConstants ()
	{
	}
}