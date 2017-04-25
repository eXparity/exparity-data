/*
 * 
 */

package org.exparity.io.internet;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * @author Stewart Bissett
 */
public class HttpHeaders
{
	private List<HttpHeader> headers = new ArrayList<HttpHeader>();

	public HttpHeaders(final Collection<HttpHeader> headers)
	{
		Validate.notNull(headers, "Headers collection cannot be null");
		this.headers.addAll(headers);
	}

	HttpHeaders()
	{}

	public String getHeaderValue(final String entryName)
	{
		String[] found = getHeaderValues(entryName);
		return found.length > 0 ? found[0] : null;
	}

	public String[] getHeaderValues(final String entryName)
	{
		Set<String> values = new HashSet<String>();
		for (HttpHeader entry : headers) {
			if (entryName.equals(entry.getKey())) {
				values.add(entry.getValue());
			}
		}
		return values.toArray(new String[] {});
	}

	public static HttpHeaders getFrom(final HttpURLConnection con)
	{
		Collection<HttpHeader> responseHeaders = new ArrayList<HttpHeader>();
		for (String key : con.getHeaderFields().keySet()) {
			for (String value : con.getHeaderFields().get(key)) {
				responseHeaders.add(new HttpHeader(key, value));
			}
		}
		return new HttpHeaders(responseHeaders);
	}

	public void setOn(final HttpURLConnection con)
	{
		for (HttpHeader header : headers) {
			con.setRequestProperty(header.getKey(), header.getValue());
		}
	}

	public List<HttpHeader> getHeaders()
	{
		return headers;
	}
}
