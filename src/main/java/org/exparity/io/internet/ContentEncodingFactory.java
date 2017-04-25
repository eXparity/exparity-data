/**
 * 
 */

package org.exparity.io.internet;

/**
 * @author <a href="mailto:stewart@modular-it.co.uk">Stewart Bissett
 * @deprecated Use static factory methods on {@link ContentEncoding}
 */
@Deprecated
public class ContentEncodingFactory
{
	public ContentEncoding create(final String charsetName)
	{
		return ContentEncoding.forName(charsetName);
	}
}
