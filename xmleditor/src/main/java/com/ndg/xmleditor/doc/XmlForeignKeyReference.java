package com.ndg.xmleditor.doc;

import org.jdom2.Element;
import org.w3._2001.xmlschema.Field;
import org.w3._2001.xmlschema.Keybase;

/**
 * Stores the xsd:key reference being referred to, and the container where the referred to records will be located; is output from
 * @see ListOfXmlDocuments#checkIfFieldIsForeignKey
 */
public final class XmlForeignKeyReference
{
	/** The xsd:key node from the XSD representing the primary key that this field refers to */
	private final Keybase primaryKey;

	/** The document where the definition for the referenced complex type is held */
	private final XmlDocument referencedDocument;

	/** The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document */
	private final Element container;

	/**
	 * Stores and fixes details about a foreign key reference
	 * @param aPrimaryKey The xsd:key node from the XSD representing the primary key that this field refers to
	 * @param aReferencedDocument The document where the definition for the referenced complex type is held
	 * @param aContainer The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document
	 */
	public XmlForeignKeyReference (final Keybase aPrimaryKey, final XmlDocument aReferencedDocument, final Element aContainer)
	{
		super ();

		primaryKey = aPrimaryKey;
		referencedDocument = aReferencedDocument;
		container = aContainer;
	}

	/**
	 * @return The xsd:key node from the XSD representing the primary key that this field refers to
	 */
	public final Keybase getPrimaryKey ()
	{
		return primaryKey;
	}

	/**
	 * @return The document where the definition for the referenced complex type is held
	 */
	public final XmlDocument getReferencedDocument ()
	{
		return referencedDocument;
	}

	/**
	 * @return The record in the XML document under which the referred to records will be located, or if it is a top level FK then this will be the root of the XML document
	 */
	public final Element getContainer ()
	{
		return container;
	}

	/**
	 * @return Details of this foreign key reference, useful for debugging
	 */
	@Override
	public final String toString ()
	{
		final StringBuilder fields = new StringBuilder ();
		for (final Field field : primaryKey.getField ())
		{
			if (fields.length () > 0)
				fields.append (", ");
			
			fields.append (field.getXpath ());
		}
		
		return "(PK[" + primaryKey.getName () + " with selector xpath " + primaryKey.getSelector ().getXpath () + " and fields [" + fields + "]], XSD[" +
			referencedDocument.getXsd ().getTargetNamespace () + "], Container[" + container.getName () + "])";
	}
}