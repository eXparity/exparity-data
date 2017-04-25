/**
 * 
 */

package org.exparity.io.internet;

/**
 * @author Stewart Bissett
 * 
 */
class BinaryContentType implements ContentType
{
	@Override
	public String getText(final byte[] data, final ContentEncoding encoding)
	{
		throw new ContentConversionException(this + " cannot be converted to text");
	}

	@Override
	public boolean isBinary()
	{
		return true;
	}

	@Override
	public boolean isText()
	{
		return false;
	}

}
