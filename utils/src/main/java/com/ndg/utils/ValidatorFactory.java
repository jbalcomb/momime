package com.ndg.utils;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

/**
 * Workaround for problem with Schema.newValidator with JDK 17.
 */
public final class ValidatorFactory
{
	/**
	 * @param schema XML Schema
	 * @return New Validator
	 */
	public final Validator newValidator (final Schema schema)
	{
		return schema.newValidator ();
	}
}