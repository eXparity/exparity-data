/*
 * 
 */

package org.exparity.data.xml;

/**
 * @author Stewart Bissett
 */
public class TransformFailedException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TransformFailedException(final Exception e)
	{
		super(e);
	}

}
