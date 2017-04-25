/*
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 */
public class FileNotSentException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public FileNotSentException(final String message)
	{
		super(message);
	}

}
