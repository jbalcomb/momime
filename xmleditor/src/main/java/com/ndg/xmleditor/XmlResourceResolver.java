package com.ndg.xmleditor;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.ndg.xmleditor.constants.XsdConstants;

/**
 * Resolver to allow finding XML and XML Schema XSDs when they are referenced as an xsd:import statements from other XSDs
 */
public class XmlResourceResolver implements LSResourceResolver
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (XmlResourceResolver.class);
	
	/** DOM Implementation registry used to find the load/save implementation */
	private DOMImplementationRegistry domImplementationRegistry;
	
	/** Secondary resource resolver, to resolve application specific XSDs */
	private LSResourceResolver secondaryResourceResolver;

	/**
	 * Checks to see if the requested resource is a known XSD file, returning it as an input stream
	 * @param type Type of resource to locate - expect this to be "http://www.w3.org/2001/XMLSchema"
	 * @param namespaceURI Namespace of the resource to locate - which is what we used to locate it
	 * @param publicId The public identifier of the external entity being referenced (in this case, null)
	 * @param systemId The location suggested for the resource (e.g. the schemaLocation specified for the Server XSD in the Language XSD)
	 * @param baseURI The absolute base URI of the resource being parsed (in this case, null)
	 * @return Stream to the located resource
	 */
	@SuppressWarnings ({"resource", "unused"})
	protected InputStream resolveResourceToStream (final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI)
	{
		final InputStream result;

		// XML XSD
		if ((type.equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)) && (namespaceURI.equals (XsdConstants.XML_XSD_NAMESPACE_URI)))
		{
			log.debug ("Namespace matches XML XSD");
			result = getClass ().getResourceAsStream (XsdConstants.XML_XSD_LOCATION);
		}

		// XML Schema XSD
		else if ((type.equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)) && (namespaceURI.equals (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI)))
		{
			log.debug ("Namespace matches XML Schema XSD");
			result = getClass ().getResourceAsStream (XsdConstants.XML_SCHEMA_XSD_LOCATION);
		}

		else
			result = null;

		return result;
	}

	/**
	 * Checks to see if the requested resource is a known MoM IME XSD file, returning it as an LSInput
	 * @param type Type of resource to locate - expect this to be "http://www.w3.org/2001/XMLSchema"
	 * @param namespaceURI Namespace of the resource to locate - which is what we used to locate it
	 * @param publicId The public identifier of the external entity being referenced (in this case, null)
	 * @param systemId The location suggested for the resource (e.g. the schemaLocation specified for the Server XSD in the Language XSD)
	 * @param baseURI The absolute base URI of the resource being parsed (in this case, null)
	 * @return Located XSD file
	 */
	@Override
	@SuppressWarnings ("resource")
	public final LSInput resolveResource (final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI)
	{
		final InputStream stream = resolveResourceToStream (type, namespaceURI, publicId, systemId, baseURI);

		final LSInput result;
		if (stream != null)
		{
			final DOMImplementationLS domImplementationLS = (DOMImplementationLS) getDomImplementationRegistry ().getDOMImplementation ("LS");
			result = domImplementationLS.createLSInput ();

			result.setBaseURI (baseURI);
			result.setCertifiedText (false);
			result.setPublicId (publicId);
			result.setSystemId (systemId);
			result.setByteStream (stream);
		}
		else if (getSecondaryResourceResolver () != null)
			result = getSecondaryResourceResolver ().resolveResource (type, namespaceURI, publicId, systemId, baseURI);
		else
			result = null;

		return result;
	}

	/**
	 * @return DOM Implementation registry used to find the load/save implementation
	 */
	public final DOMImplementationRegistry getDomImplementationRegistry ()
	{
		return domImplementationRegistry;
	}

	/**
	 * @param reg DOM Implementation registry used to find the load/save implementation
	 */
	public final void setDomImplementationRegistry (final DOMImplementationRegistry reg)
	{
		domImplementationRegistry = reg;
	}

	/**
	 * @return Secondary resource resolver, to resolve application specific XSDs
	 */
	public final LSResourceResolver getSecondaryResourceResolver ()
	{
		return secondaryResourceResolver;
	}

	/**
	 * @param res Secondary resource resolver, to resolve application specific XSDs
	 */
	public final void setSecondaryResourceResolver (final LSResourceResolver res)
	{
		secondaryResourceResolver = res;
	}
}