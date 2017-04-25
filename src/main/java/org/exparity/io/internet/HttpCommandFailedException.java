/*
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 */
public class HttpCommandFailedException extends Exception
{
	@SuppressWarnings("unused")
	private final int code;
	private final String message;

	public HttpCommandFailedException(final int code, final String message)
	{
		this.code = code;
		this.message = message;
	}

	@Override
	public String toString()
	{
		return message;
	}

	private static final long serialVersionUID = 1L;

}
