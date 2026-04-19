package com.ndg.multiplayer.session;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.ndg.multiplayer.base.MultiplayerBaseConstants;

/**
 * Resolver to allow finding Multiplayer Session XSDs when they are referenced as an xsd:import statements from other XSDs
 */
public class MultiplayerSessionXsdResourceResolver implements LSResourceResolver
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (MultiplayerSessionXsdResourceResolver.class);
	
	/** Full URI of the xsd: namespace */
	protected final static String XSD_URI = "http://www.w3.org/2001/XMLSchema";

	/** DOM Implementation registry used to find the load/save implementation */
	private final DOMImplementationRegistry domImplRegistry;

	/**
	 * Creates a resolver to allow finding referenced MoM IME XSD files
	 * @param aDomImplRegistry DOM Implementation registry used to find the load/save implementation
	 */
	public MultiplayerSessionXsdResourceResolver (final DOMImplementationRegistry aDomImplRegistry)
	{
		super ();

		domImplRegistry = aDomImplRegistry;
	}

	/**
	 * Checks to see if the requested resource is a known MoM IME XSD file, returning it as an input stream
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

		// Multiplayer XSD
		if ((type.equals (XSD_URI)) && (namespaceURI.equals (MultiplayerBaseConstants.MULTIPLAYER_BASE_XSD_NAMESPACE_URI)))
		{
			log.debug ("Namespace matches multiplayer XSD");
			result = getClass ().getResourceAsStream (MultiplayerBaseConstants.MULTIPLAYER_BASE_XSD_LOCATION);
		}

		// Multiplayer session XSD
		else if ((type.equals (XSD_URI)) && (namespaceURI.equals (MultiplayerSessionBaseConstants.MULTIPLAYER_SESSION_BASE_XSD_NAMESPACE_URI)))
		{
			log.debug ("Namespace matches multiplayer session XSD");
			result = getClass ().getResourceAsStream (MultiplayerSessionBaseConstants.MULTIPLAYER_SESSION_BASE_XSD_LOCATION);
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
		if (stream == null)
			result = null;
		else
		{
			final DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplRegistry.getDOMImplementation ("LS");
			result = domImplementationLS.createLSInput ();

			result.setBaseURI (baseURI);
			result.setCertifiedText (false);
			result.setPublicId (publicId);
			result.setSystemId (systemId);
			result.setByteStream (stream);
		}

		return result;
	}
}