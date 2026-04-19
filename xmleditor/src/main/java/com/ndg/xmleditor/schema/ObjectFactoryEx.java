package com.ndg.xmleditor.schema;

import org.w3._2001.xmlschema.Attribute;
import org.w3._2001.xmlschema.LocalComplexType;
import org.w3._2001.xmlschema.LocalElement;
import org.w3._2001.xmlschema.ObjectFactory;
import org.w3._2001.xmlschema.Schema;
import org.w3._2001.xmlschema.TopLevelComplexType;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3._2001.xmlschema.TopLevelSimpleType;

/**
 * Create descendants of some classes during unmarshalling
 */
public final class ObjectFactoryEx extends ObjectFactory
{
	/**
	 * Newly created complex type
	 */
	@Override
	public final TopLevelComplexType createTopLevelComplexType ()
	{
		return new TopLevelComplexTypeEx ();
	}

	/**
	 * Newly created complex type
	 */
	@Override
	public final LocalComplexType createLocalComplexType ()
	{
		return new LocalComplexTypeEx ();
	}
	
	/**
	 * Newly created simple type
	 */
	@Override
	public final TopLevelSimpleType createTopLevelSimpleType ()
	{
		return new SimpleTypeEx ();
	}

	/**
	 * Newly created schema
	 */
	@Override
	public final Schema createSchema ()
	{
		return new SchemaEx ();
	}

	/**
	 * Newly created element
	 */
	@Override
	public final LocalElement createLocalElement ()
	{
		return new LocalElementEx ();
	}

	/**
	 * Newly created element
	 */
	@Override
	public final TopLevelElement createTopLevelElement ()
	{
		return new TopLevelElementEx ();
	}
	
	/**
	 * Newly created attribute
	 */
	@Override
	public final Attribute createAttribute ()
	{
		return new AttributeEx ();
	}
}