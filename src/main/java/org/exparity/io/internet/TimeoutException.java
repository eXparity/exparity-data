/*
 * 
 */

package org.exparity.io.internet;


/**
 * @author Stewart Bissett
 */
public class TimeoutException extends Exception
{
	private static final long serialVersionUID = 1L;

	public TimeoutException(final Exception e)
	{
		super(e);
	}

}
