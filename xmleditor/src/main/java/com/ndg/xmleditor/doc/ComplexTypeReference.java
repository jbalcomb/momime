package com.ndg.xmleditor.doc;

import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * Stores an xsd:complexType element and which XSD the complex type is defined in.
 * The xsd:complexType may be a separately named one (TopLevelComplexTypeEx) or locally defined under an element (LocalComplexTypeEx)
 */
public final class ComplexTypeReference
{
	/** Specifies which XSD this complex type is defined in */
	private final XmlDocument xmlDocument;

	/** The xsd:complexType element */
	private final ComplexTypeEx complexTypeDefinition;

	/**
	 * @param anXmlDocument Specifies which XSD this complex type is defined in
	 * @param aComplexTypeDefinition The xsd:complexType element
	 */
	public ComplexTypeReference (final XmlDocument anXmlDocument, final ComplexTypeEx aComplexTypeDefinition)
	{
		super ();
		xmlDocument = anXmlDocument;
		complexTypeDefinition = aComplexTypeDefinition;
	}

	/**
	 * @return Specifies which XSD this complex type is defined in
	 */
	public final XmlDocument getXmlDocument ()
	{
		return xmlDocument;
	}

	/**
	 * @return The xsd:complexType element
	 */
	public final ComplexTypeEx getComplexTypeDefinition ()
	{
		return complexTypeDefinition;
	}
}