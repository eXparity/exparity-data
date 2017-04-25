/**
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 * 
 */
public class ContentConversionException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ContentConversionException(String exception)
	{
		super(exception);
	}

	public ContentConversionException(Exception exception)
	{
		super(exception);
	}
}
