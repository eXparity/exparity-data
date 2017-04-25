/*
 * 
 */

package org.exparity.data.html;

import org.exparity.data.types.KeyValue;

/**
 * The {@link Attribute} class represents a HTML attribute
 * 
 * @author Stewart Bissett
 */
public class Attribute extends KeyValue<String, String>
{
	/**
	 * Construct an {@link Attribute} instance with the given name and value
	 */
	public Attribute(final String name, final String value)
	{
		super(name, value);
	}
}
