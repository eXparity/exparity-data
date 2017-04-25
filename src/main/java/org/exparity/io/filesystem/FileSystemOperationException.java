/**
 * 
 */

package org.exparity.io.filesystem;


/**
 * @author Stewart Bissett
 * 
 */
public class FileSystemOperationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public FileSystemOperationException(String errorMessage)
	{
		super(errorMessage);
	}

	public FileSystemOperationException(Throwable exception)
	{
		super(exception);
	}

	public FileSystemOperationException(String errorMessage, Throwable exception)
	{
		super(errorMessage, exception);
	}

}
