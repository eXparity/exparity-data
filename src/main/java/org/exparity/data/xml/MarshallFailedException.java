/*
 * 
 */

package org.exparity.data.xml;

/**
 * @author Stewart Bissett
 */
public class MarshallFailedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public MarshallFailedException(final Exception e)
	{
		super(e);
	}

}
