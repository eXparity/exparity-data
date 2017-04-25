/**
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 * 
 */
public interface ContentType
{
	public String getText(byte[] data, ContentEncoding encoding) throws ContentConversionException;

	public boolean isText();

	public boolean isBinary();
}
