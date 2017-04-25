/*
 * 
 */

package org.exparity.io.internet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Stewart Bissett
 */
public class HttpURLConnectionFactory
{
	public HttpURLConnection newConnection(final URL url) throws IOException
	{
		return (HttpURLConnection) url.openConnection();
	}

}
