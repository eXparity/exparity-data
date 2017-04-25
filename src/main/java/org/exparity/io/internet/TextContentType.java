/**
 * 
 */

package org.exparity.io.internet;

import java.nio.charset.CharacterCodingException;

/**
 * @author Stewart Bissett
 * 
 */
class TextContentType implements ContentType
{
	public String getText(final byte[] data, final ContentEncoding encoding) throws ContentConversionException
	{
		try {
			return encoding.decode(data);
		}
		catch (CharacterCodingException e) {
			throw new ContentConversionException(e);
		}
	}

	@Override
	public boolean isBinary()
	{
		return false;
	}

	@Override
	public boolean isText()
	{
		return true;
	}
}
