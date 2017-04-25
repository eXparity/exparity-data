/*
 * 
 */

package org.exparity.io.internet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.exparity.data.types.KeyValue;

/**
 * @author Stewart Bissett
 */
public class HttpCommandParameter extends KeyValue<String, String>
{
	public HttpCommandParameter(final String key, final String value) throws UnsupportedEncodingException
	{
		super(key, URLEncoder.encode(value, "UTF-8"));
	}

	public HttpCommandParameter(final String key, final byte[] value)
	{
		super(key, Base64.encodeBase64String(value));
	}
}
